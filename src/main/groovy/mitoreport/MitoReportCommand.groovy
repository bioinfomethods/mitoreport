package mitoreport

import gngs.CliOptions
import gngs.Utils
import gngs.VCF
import gngs.Variant
import gngs.tools.DeletionPlot
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import io.micronaut.core.io.ResourceLoader
import mitoreport.haplogrep.HaplogrepClassification
import mitoreport.haplogrep.HaplogroupClassifier
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import picocli.CommandLine.ArgGroup
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

import javax.inject.Inject
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

import static java.time.LocalDateTime.parse as ld
import static mitoreport.MitoUtils.getManifestInfo

@Slf4j
@Command(name = 'mito-report', description = 'Mito Report', mixinStandardHelpOptions = true)
class MitoReportCommand implements Runnable {

    private String mitoReportPathName = null

    private static final String DEFAULT_IGV_HOST = 'http://localhost:60151'
    private static final String DEFAULT_GENE_CARDS_URL_PREFIX = 'https://www.genecards.org/cgi-bin/carddisp.pl'
    private static final String DEFAULT_HMT_VAR_URL_PREFIX = 'https://www.hmtvar.uniba.it/results'
    private static final String JAR_UI_DIR = 'mitoui'

    @Option(names = ['-v', '-vcf', '--vcf'], required = true, description = 'VCF file for sample')
    File vcfFile

    @Option(names = ['-s', '-sample', '--sample'], required = true, description = 'Sample ID')
    String sample

    @Option(names = ['-so', '-sample-output', '--sample-output'], required = false, description = 'Provide replacement Sample ID')
    String sampleOutput

    @ArgGroup(exclusive = false, multiplicity = "0..1")
    Maternal maternal

    @Option(names = ['-ann', '-annotations', '--annotations'], required = false, description = 'DEPRECATED, -mann replaces this.  Annnotation file to apply to VCF')
    String annotations

    @Option(names = ['-mann', '--mito-annotations'], required = true, description = 'Annotations from MitoMap to apply to VCF')
    Path mitoMapAnnotations

    @Option(names = ['-gnomad', '--gnomad-vcf'], required = true, description = 'gnomAD mtDNA sites VCF')
    Path gnomADVCF

    @Option(names = ['-r', '-region', '--region'], defaultValue = 'chrM:200-16300', required = true, description = 'Chromosome region, defaults to chrM:200-16300')
    String region

    @Option(names = ['-o', '--output-dir'], required = false, description = 'Directory to write output to, defaults to mitoreport-<sample>')
    File outputDir

    @Option(names = ['-d', '-dev', '--dev'], required = false, description = 'Developer Mode, copies .js files to ui folder')
    Boolean devMode

    @Parameters(paramLabel = "BAMS", arity = '1..*', description = "One or more BAM files, one of them must be the sample BAM and the rest are control BAMs")
    List<File> bamFiles

    @Inject
    ResourceLoader resourceLoader

    @Inject
    MitoMapAnnotationsLoader mitoMapLoader

    static class Maternal {
        @Option(names = ['-w', '--maternal-vcf'], required = true, description = 'VCF file with maternal sample')
        File vcfFile

        @Option(names = ['-m', '--maternal-sample'], required = false, description = 'Provide maternal sample ID if provided VCF is as multi sample VCF, defaults to use first sample')
        String sample
    }

    void run() {
        this.sampleOutput = this.sampleOutput ?: sample

        (bamFiles + [vcfFile, mitoMapAnnotations.toFile(), gnomADVCF.toFile()] + (maternal ? [maternal.vcfFile] : []))
                .each { assert it.exists(), "${it.absolutePath} does not exist." }

        // Default to a directory named after the sample
        if (!this.outputDir) {
            this.outputDir = new File("mitoreport-$sampleOutput")
        }

        this.mitoReportPathName = this.outputDir.absolutePath

        writeOutUiAssets()

        Map<String, File> deletionsResult = createDeletionsPlot()
        File deletionsJson = deletionsResult.deletionsJsonFile
        File coverageStatsJson = deletionsResult.coverageStatsJsonFile
        File variantsJson = runReport(deletionsJson)

        def maternalVariantsResults = createMaternalVariantsResults(mitoReportPathName, maternal)

        Optional<URL> maybeManifestUrl = resourceLoader.getResource('classpath:META-INF/MANIFEST.MF')
        Map<String, Object> manifestInfo = getManifestInfo(maybeManifestUrl)
        BasicFileAttributes gnomadVcfFileAttrs = Files.readAttributes(gnomADVCF, BasicFileAttributes)

        Map<String, Object> metadata = [
                applicationName  : manifestInfo.get('Implementation-Title'),
                version          : manifestInfo.get('Implementation-Version'),
                vendor           : manifestInfo.get('Implementation-Vendor'),
                buildTimestamp   : manifestInfo.get('Build-Timestamp'),
                buildCommit      : manifestInfo.get('Build-Commit'),
                mitoreportVersion: manifestInfo.get('Implementation-Version'),
                gnomadSource     : [
                        absolutePath: gnomADVCF.toAbsolutePath().toString(),
                        fileName    : gnomADVCF.fileName.toString(),
                        created     : timestampStrToLocal(gnomadVcfFileAttrs.creationTime().toString()),
                        modified    : timestampStrToLocal(gnomadVcfFileAttrs.lastModifiedTime().toString()),
                        accessed    : timestampStrToLocal(gnomadVcfFileAttrs.lastAccessTime().toString())
                ],
                absolutePath     : gnomADVCF.toAbsolutePath().toString(),
                fileName         : gnomADVCF.fileName.toString(),
                created          : timestampStrToLocal(gnomadVcfFileAttrs.creationTime().toString()),
                modified         : timestampStrToLocal(gnomadVcfFileAttrs.lastModifiedTime().toString()),
                accessed         : timestampStrToLocal(gnomadVcfFileAttrs.lastAccessTime().toString())
        ]

        HaplogrepClassification haplogrepClassification = new HaplogroupClassifier(vcfFile, sample).call()
        writeOutUiDataAndSettings(
                deletionsJson,
                deletionsResult.bamFile,
                variantsJson,
                haplogrepClassification,
                coverageStatsJson,
                metadata,
                maternalVariantsResults.get('variantsFile') as File,
                maternalVariantsResults.get('haplogrepClassification') as HaplogrepClassification
        )
    }

    Map<String, File> createDeletionsPlot() {
        File deletionsFile = new File(Paths.get(mitoReportPathName, 'deletions.json').toUri())
        File coverageStatsJsonFile = new File(Paths.get(mitoReportPathName, 'covo_stats.json').toUri())

        CliOptions dpOpts = new CliOptions(overrides: [
                'region'      : region,
                'covo'        : Paths.get(mitoReportPathName, 'covo.tsv').toString(),
                'covoStats'   : coverageStatsJsonFile.absolutePath,
                'sample'      : sample,
                'sampleOutput': sampleOutput,
                'covplot'     : Paths.get(mitoReportPathName, 'covo.png').toString(),
                'srplot'      : Paths.get(mitoReportPathName, 'sr.png').toString(),
                'json'        : deletionsFile.absolutePath,
                'arguments'   : bamFiles.collect { it.absolutePath }
        ])

        DeletionPlot deletionPlot = new DeletionPlot(opts: dpOpts)
        deletionPlot.run()

        // TODO - This bamFile location is required to generate IGV links in the UI
        File bamFile = deletionPlot.getBam().samFile

        return ['bamFile': bamFile, 'deletionsJsonFile': deletionsFile, 'coverageStatsJsonFile': coverageStatsJsonFile]
    }

    File runReport(File deletionsJson) {
        CliOptions reportOpts = new CliOptions(overrides: [
                'vcf'   : vcfFile.absolutePath,
                'del'   : deletionsJson.absolutePath,
                'mann'  : mitoMapAnnotations,
                'gnomad': gnomADVCF.toFile().absolutePath,
                'o'     : mitoReportPathName,
        ])

        Report mitoReport = new Report(opts: reportOpts, mitoMapLoader: mitoMapLoader)
        mitoReport.run()

        return mitoReport.variantsResultJson
    }

    private static def createMaternalVariantsResults(String pathname, Maternal maternal) {
        def result = Collections.emptyMap()
        if (maternal) {
            VCF vcf = VCF.parse(maternal.vcfFile)
            String sampleId = maternal.sample ?: vcf.samples[0]
            assert sampleId in vcf.samples, "${maternal.sample} not in VCF"
            Integer sampleIdIndex = vcf.samples.indexOf(sampleId)
            def maternalVariants = VCF.parse(maternal.vcfFile).collect { Variant v ->
                Map<String, Object> infoField = v.parsedInfo
                Map<String, Object> res = [
                        id       : "${v.chr}-${v.pos}-${v.ref}-${v.alt}",
                        hgvsg    : MitoUtils.extractMitoHgvsg(v),
                        chr      : v.chr,
                        pos      : v.pos,
                        ref      : v.ref,
                        alt      : v.alt,
                        type     : v.type,
                        qual     : v.qual,
                        dosages  : v.getDosages(),
                        genotypes: [v.parsedGenotypes[sampleIdIndex]],
                ]
                res << infoField
            }

            String maternalVariantsJson = JsonOutput.prettyPrint(JsonOutput.toJson(maternalVariants))

            File dirFile = new File(pathname)
            if (!dirFile.exists()) {
                log.info "Creating directory $pathname"
                dirFile.mkdirs()
            }

            File file = new File("$pathname/maternalVariants.json")
            Utils.writer(file).withWriter { it << maternalVariantsJson; it << '\n' }

            HaplogrepClassification haplogrepClassification = new HaplogroupClassifier(maternal.vcfFile, sampleId).call()

            result = Collections.unmodifiableMap([
                    'sampleId'               : sampleId,
                    'variantsFile'           : file,
                    'haplogrepClassification': haplogrepClassification,
            ])
        }

        return result
    }

    private void writeOutUiAssets() {
        File mitoreport = new File(Paths.get(mitoReportPathName).toUri())
        FileUtils.deleteQuietly(mitoreport)
        FileUtils.forceMkdir(mitoreport)

        String runningContextPath = this.class.protectionDomain.codeSource.location.toURI().path

        // Special handling for writing out contents in jar file.
        if (runningContextPath.endsWith('jar')) {
            List<Path> mitoReportOutputPaths = Collections.emptyList()
            URI jarFsUri = URI.create("jar:file:" + runningContextPath)
            FileSystem fs = null
            try {
                fs = FileSystems.newFileSystem(jarFsUri, Collections.emptyMap())
                mitoReportOutputPaths = Files.walk(fs.getPath(JAR_UI_DIR))
                        .collect(Collectors.toList())
                        .findAll { Files.isRegularFile(it) }
            }
            finally {
                fs.close()
            }

            // Each reportFilePath should already be in 'mitoreport' directory
            mitoReportOutputPaths.each { Path reportFilePath ->
                String reportFilePathStr = reportFilePath.toString()
                String jarResourcePath = reportFilePathStr.startsWith('/') ? reportFilePathStr[1..-1] : reportFilePathStr
                String outFileName = Paths.get(jarResourcePath.replace(JAR_UI_DIR, mitoReportPathName)).toString()
                String containingDir = FilenameUtils.getFullPathNoEndSeparator(outFileName)
                FileUtils.forceMkdir(new File(containingDir))
                resourceLoader.getResourceAsStream("classpath:$jarResourcePath").ifPresent { InputStream is ->
                    new File(outFileName).withOutputStream { OutputStream os ->
                        os.write(is.bytes)
                    }
                }
            }
        } else {
            resourceLoader.getResources("classpath:$JAR_UI_DIR").collect(Collectors.toList()).each { URL reportDir ->
                FileUtils.copyDirectory(new File(reportDir.toURI()), new File(mitoReportPathName))
            }
        }

        contextualiseReportSettings(mitoReportPathName)
    }

    private void contextualiseReportSettings(String reportPathName) {
        File indexHtml = new File(Paths.get(reportPathName, 'index.html').toString())
        indexHtml.text = indexHtml.text.replaceFirst('mitoSettings.js', "mitoSettings_${sampleOutput}.js")
    }

    private void writeOutUiDataAndSettings(File deletionsJson, File sampleBamFile, File variantsJson, HaplogrepClassification haplogrepClassification, File coverageStatsJson, Map metadata, File maternalVariantsJson = null, HaplogrepClassification maternalHaplogrepClassification = null) {
        new File(Paths.get(mitoReportPathName, 'deletions.js').toUri())
                .withWriter { it << 'window.deletions = ' + deletionsJson.text }

        new File(Paths.get(mitoReportPathName, 'variants.js').toUri())
                .withWriter { it << 'window.variants = ' + variantsJson.text }

        if (maternalVariantsJson && maternalVariantsJson.exists()) {
            new File(Paths.get(mitoReportPathName, 'maternalVariants.js').toUri())
                    .withWriter { it << 'window.maternalVariants = ' + maternalVariantsJson.text }
        } else {
            new File(Paths.get(mitoReportPathName, 'maternalVariants.js').toUri())
                    .withWriter { it << 'window.maternalVariants = []' }
        }

        String bamDir = FilenameUtils.getFullPath(sampleBamFile.absolutePath)
        String bamFileName = FilenameUtils.getName(sampleBamFile.absolutePath)
        String sampleVcfDir = FilenameUtils.getFullPath(vcfFile.absolutePath)
        String sampleVcfFileName = FilenameUtils.getName(vcfFile.absolutePath)
        String maternalVcfDir = maternal?.vcfFile ? FilenameUtils.getFullPath(maternal.vcfFile.absolutePath) : null
        String maternalVcfFileName = maternal?.vcfFile ? FilenameUtils.getName(maternal.vcfFile.absolutePath) : null
        def coverageStats = new JsonSlurper().parseText(coverageStatsJson.text)
        def qc = ['coverageStats': coverageStats]

        def defaultSettings = [
                'igvHost'           : DEFAULT_IGV_HOST,
                'geneCardsUrlPrefix': DEFAULT_GENE_CARDS_URL_PREFIX,
                'hmtVarUrlPrefix'   : DEFAULT_HMT_VAR_URL_PREFIX,
                'sample'            : [
                        'id'                     : sampleOutput,
                        'metadata'               : metadata,
                        'qc'                     : qc,
                        'haplogrepClassification': new HaplogrepClassification(sampleOutput, haplogrepClassification.haplogrepResults),
                        'couchDbUrl'             : 'http://localhost:5984/mitoreport',
                        'bamDir'                 : bamDir,
                        'bamFilename'            : bamFileName,
                        'vcfDir'                 : sampleVcfDir,
                        'vcfFilename'            : sampleVcfFileName,
                        'maternalVcfDir'         : maternalVcfDir,
                        'maternalVcfFilename'    : maternalVcfFileName,
                        'variantSearches'        : [
                                [
                                        'name'        : 'All',
                                        'description' : 'No filters applied',
                                        'custom'      : false,
                                        'filterConfig': [
                                                posRange              : [0, 16569],
                                                allele                : '',
                                                selectedMitoTIP       : [],
                                                selectedTypes         : [],
                                                selectedGenes         : [],
                                                selectedMasks         : [],
                                                selectedConsequences  : [],
                                                gnomADHap             : [],
                                                vafRange              : [0, 1],
                                                gbFreqTickIndex       : 6,
                                                gnomADHetFreqTickIndex: 8,
                                                gnomADHomFreqTickIndex: 8,
                                                disease               : '',
                                                diseaseShowBlank      : false,
                                                curationSearch        : '',
                                                importantCuration     : false,
                                                mitoMap               : '',
                                                mitoMapShowBlank      : false,
                                                selectedCuratedRefName: '',
                                                hgvsp                 : '',
                                                hgvspShowBlank        : false,
                                                hgvsc                 : '',
                                                hgvscShowBlank        : false,
                                                hgvs                  : '',
                                                hgvsShowBlank         : false,
                                        ]
                                ],
                        ],
                        variantTags              : [
                                ['name': 'Review', important: true, custom: false],
                                ['name': 'Excluded', important: false, custom: false],
                                ['name': 'FalsePositive', important: false, custom: false],
                                ['name': 'Likely', important: true, custom: false],
                                ['name': 'Match', important: true, custom: false],
                                ['name': 'Mismatch', important: false, custom: false],
                        ],
                        curations                : [:],
                ],
        ]

        if (maternalVariantsJson && maternalHaplogrepClassification) {
            defaultSettings['sample']['maternalHaplogrepClassification'] = new HaplogrepClassification(maternal.sample, maternalHaplogrepClassification.haplogrepResults)
        }
        String defaultSettingsJson = JsonOutput.prettyPrint(JsonOutput.toJson(defaultSettings))
        String settingsJson = JsonOutput.prettyPrint(JsonOutput.toJson([:]))

        new File(Paths.get(mitoReportPathName, 'defaultSettings.js').toUri())
                .withWriter { it << 'window.defaultSettings = ' + defaultSettingsJson }
        String mitoSettingsFileName = "mitoSettings_${sampleOutput}.js"
        new File(Paths.get(mitoReportPathName, mitoSettingsFileName).toUri())
                .withWriter { it << 'window.settings = ' + settingsJson }

        if (devMode) {
            log.info('Running in developer mode')
            List<String> fileNamesToCopy = [
                    'defaultSettings.js',
                    mitoSettingsFileName,
                    'deletions.js',
                    'variants.js',
                    'maternalVariants.js',
            ]

            fileNamesToCopy.each { fileName ->
                Files.copy(
                        Paths.get(mitoReportPathName, fileName),
                        Paths.get(mitoReportPathName, "..", "ui", "public", fileName),
                        StandardCopyOption.REPLACE_EXISTING
                )
            }
        }
    }

    private static String timestampStrToLocal(String timestamp) {
        String result = ld(timestamp, DateTimeFormatter.ISO_DATE_TIME)
                .atOffset(ZoneOffset.of("+10:00"))
                .toLocalDateTime()
                .toString()

        return result
    }
}
