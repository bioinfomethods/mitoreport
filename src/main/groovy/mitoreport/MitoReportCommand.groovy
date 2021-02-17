package mitoreport

import gngs.CliOptions
import gngs.tools.DeletionPlot
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import io.micronaut.core.io.ResourceLoader
import mitoreport.haplogrep.HaplogrepClassification
import mitoreport.haplogrep.HaplogroupClassifier
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
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

@Slf4j
@Command(name = 'mito-report', description = 'Mito Report', mixinStandardHelpOptions = true)
class MitoReportCommand implements Runnable {

    private String mitoReportPathName = null

    private static final String DEFAULT_IGV_HOST = 'http://localhost:60151'
    private static final String DEFAULT_GENE_CARDS_URL_PREFIX = 'https://www.genecards.org/cgi-bin/carddisp.pl'
    private static final String DEFAULT_HMT_VAR_URL_PREFIX = 'https://www.hmtvar.uniba.it/results'
    private static final String JAR_UI_DIR = 'mitoui'

    @Option(names = ['-s', '-sample', '--sample'], required = true, description = 'Sample ID')
    String sample

    @Option(names = ['-ann', '-annotations', '--annotations'], required = true, description = 'Annotation file to apply to VCF')
    String annotations

    @Option(names = ['-mann', '--mito-annotations'], required = true, description = 'Annotations from MitoMap to apply to VCF')
    Path mitoMapAnnotations

    @Option(names = ['-gnomad', '--gnomad-vcf'], required = true, description = 'gnomAD mtDNA sites VCF')
    Path gnomADVCF

    @Option(names = ['-vcf'], required = true, description = 'VCF file for sample')
    File vcfFile

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
    MitoMapPolymorphismsLoader mitoMapLoader

    void run() {

        (bamFiles + [vcfFile, new File(annotations), mitoMapAnnotations.toFile(), gnomADVCF.toFile()])
                .each { assert it.exists(), "${it.absolutePath} does not exist." }

        // Default to a directory named after the sample
        if (!this.outputDir) {
            this.outputDir = new File("mitoreport-$sample")
        }

        this.mitoReportPathName = this.outputDir.absolutePath

        writeOutUiAssets()

        Map<String, File> deletionsResult = createDeletionsPlot()
        File deletionsJson = deletionsResult.deletionsJsonFile
        File variantsJson = runReport(deletionsJson)

        BasicFileAttributes fileAttr = Files.readAttributes(gnomADVCF, BasicFileAttributes)

        Map<String, String> metadata = [
                mitoreportVersion: this.getClass().getPackage().getImplementationVersion(),
                absolutePath     : gnomADVCF.toAbsolutePath().toString(),
                fileName         : gnomADVCF.fileName.toString(),
                created          : timestampStrToLocal(fileAttr.creationTime().toString()),
                modified         : timestampStrToLocal(fileAttr.lastModifiedTime().toString()),
                accessed         : timestampStrToLocal(fileAttr.lastAccessTime().toString())
//            gitTag      : ("git describe".execute().text), // Use tags for release / version number?
//            gitHash     : ("git rev-parse --short HEAD".execute().text).trim(),
//            gitBranch   : ("git status".execute().text).split("\n").first().split(" ").last(),
//            gitDate     : ("git show -s --format=%cD".execute().text).trim()
        ]

        HaplogrepClassification haplogrepClassification = new HaplogroupClassifier(vcfFile, sample).call()
        writeOutUiDataAndSettings(deletionsJson, deletionsResult.bamFile, variantsJson, haplogrepClassification, metadata)
    }

    Map<String, File> createDeletionsPlot() {
        File result = new File(Paths.get(mitoReportPathName, 'deletions.json').toUri())

        CliOptions dpOpts = new CliOptions(overrides: [
                'region'   : region,
                'covo'     : Paths.get(mitoReportPathName, 'covo.tsv').toString(),
                'sample'   : sample,
                'covplot'  : Paths.get(mitoReportPathName, 'covo.png').toString(),
                'srplot'   : Paths.get(mitoReportPathName, 'sr.png').toString(),
                'json'     : result.absolutePath,
                'arguments': bamFiles.collect { it.absolutePath }
        ])

        DeletionPlot deletionPlot = new DeletionPlot(opts: dpOpts)
        deletionPlot.run()

        // TODO - This bamFile location is required to generate IGV links in the UI
        File bamFile = deletionPlot.getBam().samFile

        return ['bamFile': bamFile, 'deletionsJsonFile': result]
    }

    File runReport(File deletionsJson) {
        CliOptions reportOpts = new CliOptions(overrides: [
                'vcf'   : vcfFile.absolutePath,
                'del'   : deletionsJson.absolutePath,
                'ann'   : annotations,
                'mann'  : mitoMapAnnotations,
                'gnomad': gnomADVCF.toFile().absolutePath,
                'o'     : mitoReportPathName,
        ])

        Report mitoReport = new Report(opts: reportOpts, mitoMapLoader: mitoMapLoader)
        mitoReport.run()

        return mitoReport.variantsResultJson
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
        indexHtml.text = indexHtml.text.replaceFirst('mitoSettings.js', "mitoSettings_${sample}.js")
    }

    private void writeOutUiDataAndSettings(File deletionsJson, File sampleBamFile, File variantsJson, HaplogrepClassification haplogrepClassification, Map metadata) {
        new File(Paths.get(mitoReportPathName, 'deletions.js').toUri())
                .withWriter { it << 'window.deletions = ' + deletionsJson.text }

        new File(Paths.get(mitoReportPathName, 'variants.js').toUri())
                .withWriter { it << 'window.variants = ' + variantsJson.text }

        String bamDir = FilenameUtils.getFullPath(sampleBamFile.absolutePath)
        String bamFileName = FilenameUtils.getName(sampleBamFile.absolutePath)
        String sampleVcfDir = FilenameUtils.getFullPath(vcfFile.absolutePath)
        String sampleVcfFileName = FilenameUtils.getName(vcfFile.absolutePath)

        def defaultSettings = [
                'igvHost'           : DEFAULT_IGV_HOST,
                'geneCardsUrlPrefix': DEFAULT_GENE_CARDS_URL_PREFIX,
                'hmtVarUrlPrefix'   : DEFAULT_HMT_VAR_URL_PREFIX,
                'samples'           : [
                        [
                                'id'                     : sample,
                                'metadata'               : metadata,
                                'haplogrepClassification': haplogrepClassification,
                                'bamDir'                 : bamDir,
                                'bamFilename'            : bamFileName,
                                'vcfDir'                 : sampleVcfDir,
                                'vcfFilename'            : sampleVcfFileName,
                                'maternalVcfDir'         : null,
                                'maternalVcfFilename'    : null,
                                'variantSearches'        : [
                                        [
                                                'name'        : 'Preset Filter 1',
                                                'description' : 'Filter 1 description',
                                                'custom'      : false,
                                                'filterConfig': [
                                                        'posRange'           : [200, 16300],
                                                        'allele'             : 'A/C',
                                                        'selectedTypes'      : ['SNP', 'INS', 'DEL'],
                                                        'selectedConsequence': [:],
                                                        'vafRange'           : [0.00001, 0.1],
                                                        'importantCuration'  : false,
                                                ]
                                        ],
                                        [
                                                'name'        : 'Preset Filter 2',
                                                'description' : 'Filter 2 description',
                                                'custom'      : false,
                                                'filterConfig': [
                                                        'posRange'           : [0, 16500],
                                                        'selectedTypes'      : ['SNP', 'DEL'],
                                                        'selectedConsequence': [:],
                                                        'vafRange'           : [0.00001, 0.05],
                                                        'depthRange'         : [500, 12000],
                                                        'importantCuration'  : false,
                                                ],
                                        ]
                                ],
                                variantTags              : [
                                        ['name': 'Review', important: true, custom: false],
                                        ['name': 'Excluded', important: false, custom: false],
                                        ['name': 'FalsePositive', important: false, custom: false],
                                        ['name': 'Likely', important: true, custom: false],
                                        ['name': 'Match', important: true, custom: false],
                                        ['name': 'Mismatch', important: false, custom: false],
                                ],
                                curations                : [],
                        ]
                ]
        ]

        String defaultSettingsJson = JsonOutput.prettyPrint(JsonOutput.toJson(defaultSettings))
        String settingsJson = JsonOutput.prettyPrint(JsonOutput.toJson([:]))

        new File(Paths.get(mitoReportPathName, 'defaultSettings.js').toUri())
                .withWriter { it << 'window.defaultSettings = ' + defaultSettingsJson }
        String mitoSettingsFileName = "mitoSettings_${sample}.js"
        new File(Paths.get(mitoReportPathName, mitoSettingsFileName).toUri())
                .withWriter { it << 'window.settings = ' + settingsJson }

        if (devMode) {
            log.info('Running in developer mode')
            List<String> fileNamesToCopy = [
                    'defaultSettings.js',
                    mitoSettingsFileName,
                    'deletions.js',
                    'variants.js'
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
