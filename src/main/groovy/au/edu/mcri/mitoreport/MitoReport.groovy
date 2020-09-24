package au.edu.mcri.mitoreport

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

import javax.inject.Inject

import gngs.CliOptions
import gngs.tools.DeletionPlot
import groovy.json.JsonOutput
import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.core.io.ResourceLoader
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

@Command(name = 'mito-report', description = 'Mito Report', mixinStandardHelpOptions = true)
class MitoReport implements Runnable {

  private static final String MITO_REPORT_PATH_NAME = 'mitoreport'

  @Option(names = ['-s', '-sample', '--sample'], required = true, description = 'Sample ID')
  String sample

  @Option(names = ['-r', '-region', '--region'], required = true, description = 'Chromosome region')
  String region

  @Option(names = ['-ann', '-annotations', '--annotations'], required = true, description = 'Annotation file to apply to VCF')
  String annotations

  @Option(names = ['-vcf'], required = true, description = 'VCF file for sample')
  File vcfFile

  @Parameters(paramLabel = "BAMS", arity = '1..*', description = "One or more BAM files")
  List<File> bamFiles

  @Inject
  ResourceLoader resourceLoader

  static void main(String[] args) throws Exception {
    PicocliRunner.run(MitoReport.class, args)
  }

  void run() {
    (bamFiles + [vcfFile]).each { assert it.exists(), "${it.absolutePath} does not exist." }

    writeOutUi()

    Map<String, File> deletionsResult = createDeletionsPlot()
    File deletionsJson = deletionsResult.deletionsJsonFile
    File variantsJson = runReport(deletionsJson)

    writeOutUiDataAndSettings(deletionsJson, deletionsResult.bamFile, variantsJson)
  }

  Map<String, File> createDeletionsPlot() {
    File result = new File(Paths.get(MITO_REPORT_PATH_NAME, 'deletions.json').toUri())

    CliOptions dpOpts = new CliOptions(overrides: [
      'region'   : region,
      'covo'     : Paths.get(MITO_REPORT_PATH_NAME, 'covo.tsv').toString(),
      'sample'   : sample,
      'covplot'  : Paths.get(MITO_REPORT_PATH_NAME, 'covo.png').toString(),
      'srplot'   : Paths.get(MITO_REPORT_PATH_NAME, 'sr.png').toString(),
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
      'vcf': vcfFile.absolutePath,
      'del': deletionsJson.absolutePath,
      'ann': annotations,
      'o'  : MITO_REPORT_PATH_NAME,
    ])

    Report mitoReport = new Report(opts: reportOpts)
    mitoReport.run()

    return mitoReport.variantsResultJson
  }

  void writeOutUi() {
    File mitoreport = new File(Paths.get(MITO_REPORT_PATH_NAME).toUri())
    FileUtils.deleteQuietly(mitoreport)
    FileUtils.forceMkdir(mitoreport)

    String runningContextPath = this.class.protectionDomain.codeSource.location.toURI().path

    // Special handling for writing out contents in jar file.
    if (runningContextPath.endsWith('jar')) {
      List<Path> mitoReportPaths = Collections.emptyList()
      URI jarFsUri = URI.create("jar:file:" + runningContextPath)
      try (FileSystem fs = FileSystems.newFileSystem(jarFsUri, Collections.emptyMap())) {
        mitoReportPaths = Files.walk(fs.getPath(MITO_REPORT_PATH_NAME))
          .filter(Files::isRegularFile)
          .collect(Collectors.toList())
      }

      // Each reportFilePath should already be in 'mitoreport' directory
      mitoReportPaths.each { Path reportFilePath ->
        String reportFilePathStr = reportFilePath.toString()
        String jarResourcePath = reportFilePathStr.startsWith('/') ? reportFilePathStr.takeAfter('/') : reportFilePathStr
        String outFileName = Paths.get('.', jarResourcePath)
        String containingDir = FilenameUtils.getFullPathNoEndSeparator(outFileName)
        FileUtils.forceMkdir(new File(containingDir))
        resourceLoader.getResourceAsStream("classpath:$jarResourcePath").ifPresent { InputStream is ->
          new File(outFileName).withOutputStream { OutputStream os ->
            os.write(is.bytes)
          }
        }
      }
    }
    else {
      resourceLoader.getResources("classpath:$MITO_REPORT_PATH_NAME").collect(Collectors.toList()).each { URL reportDir ->
        FileUtils.copyDirectory(new File(reportDir.toURI()), new File(mitoreport.toURI()))
      }
    }
  }

  void writeOutUiDataAndSettings(File deletionsJson, File sampleBamFile, File variantsJson) {
    new File(Paths.get(MITO_REPORT_PATH_NAME, 'deletions.js').toUri())
      .withWriter { it << 'window.deletions = ' + deletionsJson.text }

    new File(Paths.get(MITO_REPORT_PATH_NAME, 'variants.js').toUri())
      .withWriter { it << 'window.variants = ' + variantsJson.text }

    String bamDir = FilenameUtils.getFullPath(sampleBamFile.absolutePath)
    String bamFileName = FilenameUtils.getName(sampleBamFile.absolutePath)
    String sampleVcfDir = FilenameUtils.getFullPath(vcfFile.absolutePath)
    String sampleVcfFileName = FilenameUtils.getName(vcfFile.absolutePath)

    def settings = [
      'igvHost'            : 'http://localhost:60151',
      'sampleBamDir'       : bamDir,
      'sampleBamFilename'  : bamFileName,
      'sampleVcfDir'       : sampleVcfDir,
      'sampleVcfFilename'  : sampleVcfFileName,
      'maternalVcfDir'     : null,
      'maternalVcfFilename': null,
    ]

    String settingsJson = JsonOutput.prettyPrint(JsonOutput.toJson(settings))

    new File(Paths.get(MITO_REPORT_PATH_NAME, 'defaultSettings.js').toUri())
      .withWriter { it << 'window.defaultSettings = ' + settingsJson }
  }
}
