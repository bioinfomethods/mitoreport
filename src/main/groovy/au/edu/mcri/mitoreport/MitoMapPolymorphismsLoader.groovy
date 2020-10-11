package au.edu.mcri.mitoreport

import groovy.json.JsonSlurper
import groovy.transform.MapConstructor
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

@Slf4j
@TupleConstructor(includes = 'mitoMapHost, pagePath')
@MapConstructor
class MitoMapPolymorphismsLoader {

    static final Map<String, String> TITLE_TO_PROPERTY_NAMES = Collections.unmodifiableMap([
            'Position'                                 : 'positionStr',
            'Locus'                                    : 'locusAnchor',
            'Nucleotide Change'                        : 'alleleChange',
            'Codon Number'                             : 'codonNumber',
            'Codon Position'                           : 'codonPosition',
            'Amino Acid Change'                        : 'aminoAcidChange',
            "GB Freq<span class='mark'>&Dagger;</span>": 'gbFreqStr',
            'GB Seqs'                                  : 'gbSeqsAnchor',
            'Curated References'                       : 'curatedRefsAnchor',
    ])

    String mitoMapHost = 'https://mitomap.org'
    String pagePath = '/foswiki/bin/view/MITOMAP/PolymorphismsCoding'

    List<MitoMapPolymorphismAnnotation> getAnnotations() {
        String fileName = 'mito_map_polymorphisms.html'
        String htmlText = downloadPolymorphismsCoding(fileName)

        Pattern matchData = Pattern.compile(/"data":(\[\s*?\[.*?\]\])/, Pattern.DOTALL)
        Pattern matchColumns = Pattern.compile(/"columns": (.*?}])/, Pattern.DOTALL)
        def dataMatcher = htmlText =~ matchData
        String dataJson = dataMatcher[0][1]
        def columnsMatcher = htmlText =~ matchColumns
        String columnsJson = columnsMatcher[0][1]

        def data = new JsonSlurper().parse(dataJson.bytes)
        def columns = new JsonSlurper().parse(columnsJson.bytes)

        List<MitoMapPolymorphismAnnotation> result = data.collect { def row ->
            Map<String, String> transformedRow = ['mitoMapHost': mitoMapHost]
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

    String downloadPolymorphismsCoding(String fileName, String workingDir = System.getProperty('user.dir')) {
        Path annotationsFilePath = Paths.get(workingDir, fileName)
        if (!Files.exists(annotationsFilePath)) {
            String pageUrl = "$mitoMapHost$pagePath"
            log.info("No MitoMap Polymorphisms downloaded HTML found, downloading $pageUrl")

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
            writeToFile(annotationsFilePath, resultHtml)

            return resultHtml
        } else {
            log.info("Found MitoMap Polymorphisms at ${annotationsFilePath.toString()}")
            return annotationsFilePath.toFile().text
        }
    }

    private static void writeToFile(Path annotationsFilePath, String fileContents) {
        Path temp = Files.createTempFile('mitoreport', null)
        temp.toFile().withWriter { BufferedWriter w ->
            w.write(fileContents)
        }

        Files.move(temp, annotationsFilePath, ATOMIC_MOVE, REPLACE_EXISTING)
    }
}
