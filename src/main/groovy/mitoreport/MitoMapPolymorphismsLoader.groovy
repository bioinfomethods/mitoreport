package mitoreport

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.MapConstructor
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder

import javax.inject.Singleton
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

@Slf4j
@TupleConstructor(includes = 'mitoMapHost, codingsPagePath, controlsPagePath')
@MapConstructor
@Singleton
class MitoMapPolymorphismsLoader {

    static final Map<String, String> TITLE_TO_PROPERTY_NAMES = Collections.unmodifiableMap([
            'Position'                                                                                               : 'positionStr',
            'Locus'                                                                                                  : 'locusAnchor',
            'Nucleotide Change'                                                                                      : 'alleleChange',
            'Codon Number'                                                                                           : 'codonNumber',
            'Codon Position'                                                                                         : 'codonPosition',
            'Amino Acid Change'                                                                                      : 'aminoAcidChange',
            "GB Freq<span class='mark'>&Dagger;</span>"                                                              : 'gbFreqStr',
            "GB Freq<br><span style='white-space:nowrap;'>FL&nbsp;(CR)<span class='mark'>&ast;&Dagger;</span></span>": 'gbFreqStr',
            'GB Seqs'                                                                                                : 'gbSeqsAnchor',
            "GB Seqs<br><span style='white-space:nowrap;'>total&nbsp;(FL/CR)<span class='mark'>&ast;</span></span>"  : 'gbSeqsAnchor',
            'Curated References'                                                                                     : 'curatedRefsAnchor',
    ])

    String mitoMapHost = 'https://mitomap.org'
    String codingsPagePath = '/foswiki/bin/view/MITOMAP/PolymorphismsCoding'
    String controlsPagePath = '/foswiki/bin/view/MITOMAP/PolymorphismsControl'

    void downloadPolymorphisms(Path outputPath) {
        if (Files.notExists(outputPath) || outputPath.toFile().text.empty) {
            String codingsHtml = downloadPolymorphismsPage("$mitoMapHost$codingsPagePath")
            String controlsHtml = downloadPolymorphismsPage("$mitoMapHost$controlsPagePath")
            List<MitoMapPolymorphismAnnotation> codings = parseHtml(codingsHtml, 'CODING')
            List<MitoMapPolymorphismAnnotation> controls = parseHtml(controlsHtml, 'CONTROL')
            List<MitoMapPolymorphismAnnotation> allAnnotations = codings + controls

            String json = JsonOutput.prettyPrint(JsonOutput.toJson(allAnnotations))
            writeToFile(outputPath, json)
        } else {
            log.info("Skipping download, MitoMap Polymorphisms already exists at ${outputPath.toString()}")
        }
    }

    private List<MitoMapPolymorphismAnnotation> parseHtml(String htmlText, String regionType) {
        Pattern matchData = Pattern.compile(/"data":(\[\s*?\[.*?\]\])/, Pattern.DOTALL)
        Pattern matchColumns = Pattern.compile(/"columns": (.*?}])/, Pattern.DOTALL)
        def dataMatcher = htmlText =~ matchData
        String dataJson = dataMatcher[0][1]
        def columnsMatcher = htmlText =~ matchColumns
        String columnsJson = columnsMatcher[0][1]

        def data = new JsonSlurper().parse(dataJson.bytes)
        def columns = new JsonSlurper().parse(columnsJson.bytes)

        List<MitoMapPolymorphismAnnotation> result = data.collect { def row ->
            Map<String, String> transformedRow = ['mitoMapHost': mitoMapHost, 'regionType': regionType]
            columns.eachWithIndex { def column, int index ->
                String title = column.title?.trim() ?: ''
                String propertyName = TITLE_TO_PROPERTY_NAMES.get(title)
                String propertyValue = row[index]
                transformedRow["$propertyName".toString()] = propertyValue
            }

            new MitoMapPolymorphismAnnotation(transformedRow)
        }

        return result
    }

    static List<MitoMapPolymorphismAnnotation> getAnnotations(Path annotationsFilePath = Paths.get(System.getProperty('user.dir'), "mito_map_annotations_${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.json")) {
        if (annotationsFilePath == null || !Files.exists(annotationsFilePath)) {
            log.warn("Annotations file ${annotationsFilePath?.toString()} does not exist.")

            return Collections.emptyList()
        }

        List<MitoMapPolymorphismAnnotation> result = new JsonSlurper()
                .parse(annotationsFilePath.toFile())
                .collect { def obj ->
                    new MitoMapPolymorphismAnnotation(obj)
                }

        return result
    }

    static String downloadPolymorphismsPage(String pageUrl) {
        log.info("Downloading MitoMap Polymorphisms HTML page from $pageUrl")

        def respBytes = HttpBuilder.configure {
            request.uri = pageUrl
            request.headers['User-Agent'] = 'https://www.mcri.edu.au'
        }.get() {
            response.exception { Exception e ->
                log.error("Error downloading page $pageUrl", e)
                throw new RuntimeException(e)
            }
        }

        String resultHtml = new String(respBytes as byte[])

        return resultHtml
    }

    private static void writeToFile(Path outputPath, String fileContents) {
        Path temp = Files.createTempFile('mitoreport', null)
        temp.toFile().withWriter { BufferedWriter w ->
            w.write(fileContents)
        }

        Files.move(temp, outputPath, REPLACE_EXISTING)
    }
}
