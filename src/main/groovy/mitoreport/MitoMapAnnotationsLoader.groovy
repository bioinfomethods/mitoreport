package mitoreport

import groovy.json.JsonGenerator
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.MapConstructor
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpBuilder

import javax.inject.Singleton
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

import static groovyx.net.http.util.SslUtils.ignoreSslIssues
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING

@Slf4j
@TupleConstructor(includes = 'mitoMapHost, codingsPagePath, controlsPagePath')
@MapConstructor
@Singleton
class MitoMapAnnotationsLoader {

    static final Map<String, String> TITLE_TO_PROPERTY_NAMES = Collections.unmodifiableMap([
            'Position'                                                                                                                : 'positionStr',
            'Locus'                                                                                                                   : 'locusAnchor',
            'Nucleotide Change'                                                                                                       : 'alleleChange',
            'Allele'                                                                                                                  : 'alleleStr',
            'Codon Number'                                                                                                            : 'codonNumber',
            'Codon Position'                                                                                                          : 'codonPosition',
            'Amino Acid Change'                                                                                                       : 'aminoAcidChange',
            "GB Freq<span class='mark'>&Dagger;</span>"                                                                               : 'gbFreqStr',
            "GB Freq<br><span style='white-space:nowrap;'>FL&nbsp;(CR)<span class='mark'>&ast;&Dagger;</span></span>"                 : 'gbFreqStr',
            "GB&nbsp;Freq&nbsp;&nbsp;<br><span style='white-space:nowrap;'>FL&nbsp;(CR)<span class='mark'>&ast;&Dagger;</span></span>": 'gbFreqStr',
            'GB Seqs'                                                                                                                 : 'gbSeqsAnchor',
            "GB Seqs<br><span style='white-space:nowrap;'>total&nbsp;(FL/CR)<span class='mark'>&ast;</span></span>"                   : 'gbSeqsAnchor',
            "GB&nbsp;Seqs&nbsp;<br><span style='white-space:nowrap;'>FL&nbsp;(CR)<span class='mark'>&ast;</span></span>"              : 'gbSeqsAnchor',
            'Curated References'                                                                                                      : 'curatedRefsAnchor',
            'References'                                                                                                              : 'curatedRefsAnchor',
            'Disease'                                                                                                                 : 'disease',
    ])

    String mitoMapHost = 'https://mitomap.org'
    String codingsPagePath = '/foswiki/bin/view/MITOMAP/VariantsCoding'
    String controlsPagePath = '/foswiki/bin/view/MITOMAP/VariantsControl'
    String rnaMutationsPagePath = '/foswiki/bin/view/MITOMAP/MutationsRNA'
    String diseasesPagePath = '/cgi-bin/disease.cgi'
    String mitoTipsPagePath = '/downloads/mitotip_scores.txt'

    void downloadAnnotations(Path outputPath) {
        if (Files.notExists(outputPath) || outputPath.toFile().text.empty) {
            String codingsHtml = downloadPage("$mitoMapHost$codingsPagePath")
            String controlsHtml = downloadPage("$mitoMapHost$controlsPagePath")
            String rnaMutationsHtml = downloadPage("$mitoMapHost$rnaMutationsPagePath")
            List<MitoMapAnnotation> codings = parseVariantsHtmlPage(codingsHtml, 'CODING')
            List<MitoMapAnnotation> controls = parseVariantsHtmlPage(controlsHtml, 'CONTROL')
            List<MitoMapAnnotation> rnaMutations = parseVariantsHtmlPage(rnaMutationsHtml, 'RNA_MUTATIONS')
            Set<MitoMapAnnotation> allAnnotations = new HashSet<>(rnaMutations + codings + controls)

            String diseasesTsv = downloadPage("$mitoMapHost$diseasesPagePath")
            Map<String, Object> diseasesAnnotations = diseasesTsv.split(/\n/)
                    .tail()
                    .collectEntries { String line ->
//                        id	pos	ref	alt	aachange	homoplasmy	heteroplasmy	disease	status	pubmed_ids	gbcnt	gbfreq
//                        205	114	C	T	noncoding	+	-	BD-associated	Reported	19290059	229	0.44

                        // lineItems[5], lineItems[6], lineItems[9], lineItems[10], lineItems[11]
                        // unused as they're already included in other annotations
                        def lineItems = line.split(/\t/)
                        Integer lineItemsSize = lineItems.size()
                        String pos = lineItemsSize > 1 ? lineItems[1] : null
                        String ref = lineItemsSize > 2 ? lineItems[2] : null
                        String alt = lineItemsSize > 3 ? lineItems[3] : null
                        String aaChange = lineItemsSize > 4 ? lineItems[4] : null
//                        String homoplasmy = lineItemsSize > 5 ? lineItems[5] : null
//                        String heteroplasmy = lineItemsSize > 6 ? lineItems[6] : null
                        String disease = lineItemsSize > 7 ? lineItems[7] : null
                        String mitoMapStatus = lineItemsSize > 8 ? lineItems[8] : null
//                        String pubmedIds = lineItemsSize > 9 ? lineItems[9] : null
//                        String gbCount = lineItemsSize > 10 ? lineItems[10] : null
//                        String gbFreq = lineItemsSize > 11 ? lineItems[11] : null
                        String compactAllele = "$ref$pos$alt"
                        [(compactAllele): ['diseaseAaChange': aaChange, 'disease': disease, 'mitoMapStatus': mitoMapStatus]]
                    }

            String mitoTipsTsv = downloadPage("$mitoMapHost$mitoTipsPagePath")
            Map<String, Object> mitoTipsAnnotations = parseMitoTipsTsv(mitoTipsTsv)

            allAnnotations.each { MitoMapAnnotation annotation ->
                def diseaseAnnotation = diseasesAnnotations.getOrDefault(annotation.compactAllele, Collections.emptyMap())
                annotation.diseaseAaChange = diseaseAnnotation.getOrDefault('diseaseAaChange', null)
                annotation.disease = diseaseAnnotation.getOrDefault('disease', null)
                annotation.diseaseStatus = diseaseAnnotation.getOrDefault('mitoMapStatus', null)

                def mitoTipAnnotation = mitoTipsAnnotations.getOrDefault(annotation.compactAllele, Collections.emptyMap())
                annotation.mitoTipScore = mitoTipAnnotation.getOrDefault('mitoTipScore', null)
                annotation.mitoTipScorePercentile = mitoTipAnnotation.getOrDefault('mitoTipScorePercentile', null)
                annotation.mitoTipQuartile = mitoTipAnnotation.getOrDefault('mitoTipQuartile', MitoTipQuartile.UNKNOWN)
                annotation.mitoTipCount = mitoTipAnnotation.getOrDefault('mitoTipCount', null)
                annotation.mitoTipFreqPct = mitoTipAnnotation.getOrDefault('mitoTipFreqPct', null)
            }

            JsonGenerator generator = new JsonGenerator.Options()
                    .excludeFieldsByName('parsedAllele')
                    .build()
            String json = JsonOutput.prettyPrint(generator.toJson(allAnnotations))
            writeToFile(outputPath, json)
        } else {
            log.info("Skipping download, MitoMap Variants already exists at ${outputPath.toString()}")
        }
    }

    private List<MitoMapAnnotation> parseVariantsHtmlPage(String htmlText, String regionType) {
        Pattern matchData = Pattern.compile(/"data":(\[\s*?\[.*?\]\])/, Pattern.DOTALL)
        Pattern matchColumns = Pattern.compile(/"columns": (.*?}])/, Pattern.DOTALL)
        def dataMatcher = htmlText =~ matchData
        String dataJson = dataMatcher[0][1]
        def columnsMatcher = htmlText =~ matchColumns
        String columnsJson = columnsMatcher[0][1]

        def data = new JsonSlurper().parse(dataJson.bytes)
        String singleQuoteFixedColumns = new String(columnsJson.bytes).replaceAll(/\\'/, '\'')
        def columns = new JsonSlurper().parse(singleQuoteFixedColumns.bytes)

        List<MitoMapAnnotation> result = data.collect { def row ->
            Map<String, String> transformedRow = ['mitoMapHost': mitoMapHost, 'regionType': regionType]
            columns.eachWithIndex { def column, int index ->
                String title = column.title?.trim() ?: ''
                String propertyName = TITLE_TO_PROPERTY_NAMES.get(title)
                String propertyValue = row[index]
                if (propertyName) {
                    transformedRow["$propertyName".toString()] = propertyValue?.trim()
                }
            }
            new MitoMapAnnotation(transformedRow)
        }.findAll()

        return result
    }

    static Map<String, MitoMapAnnotation> getAnnotations(Path annotationsFilePath = Paths.get(System.getProperty('user.dir'), "mito_map_annotations_${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}.json")) {
        if (annotationsFilePath == null || !Files.exists(annotationsFilePath)) {
            log.error("Annotations file ${annotationsFilePath?.toString()} does not exist.")

            return Collections.emptyMap()
        }

        Map<String, MitoMapAnnotation> result = new JsonSlurper()
                .parse(annotationsFilePath.toFile())
                .collectEntries { def obj ->
                    MitoMapAnnotation annotation = new MitoMapAnnotation(obj)
                    [(annotation.compactAllele): annotation]
                }

        return result
    }

    static String downloadPage(String pageUrl) {
        log.info("Downloading MitoMap page from $pageUrl")

        HttpBuilder client = HttpBuilder.configure {
            request.uri = pageUrl
            request.headers['User-Agent'] = 'https://www.mcri.edu.au'
            ignoreSslIssues execution
        }

        def respBytes = client.get() {
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

    protected static Map<String, Object> parseMitoTipsTsv(String mitoTipsTsv) {
        Map<String, Object> rawMitoTips = mitoTipsTsv.split(/\n/)
                .tail()
                .collectEntries { String line ->
//                        Position	rCRS	Alt	MitoTIP_Score	Quartile	Count	Percentage	Mitomap_Status
//                        577	G	:	20.3833	Q1	0	0.000	Absent

                    // lineItems[7] unused as it is already included in other annotations
                    def lineItems = line.split(/\t/)
                    Integer lineItemsSize = lineItems.size()
                    String pos = lineItemsSize > 0 ? lineItems[0] : null
                    String ref = lineItemsSize > 1 ? lineItems[1] : null
                    String alt = lineItemsSize > 2 ? lineItems[2] : null
                    BigDecimal mitoTipScore = lineItemsSize > 3 && lineItems[3].isNumber() ? new BigDecimal(lineItems[3]) : 0.0
                    MitoTipQuartile mitoTipQuartile = lineItemsSize > 4 ? MitoTipQuartile.safeValueOf(lineItems[4]) : MitoTipQuartile.UNKNOWN
                    BigDecimal mitoTipCount = lineItemsSize > 5 && lineItems[5].isNumber() ? new BigDecimal(lineItems[5]) : 0.0
                    BigDecimal mitoTipFreqPct = lineItemsSize > 6 && lineItems[6].isNumber() ? new BigDecimal(lineItems[6]) : 0.0
//                        String mitoTipStatus = lineItemsSize > 7 && lineItems[7].isNumber() ? new BigDecimal(lineItems[7]) : 0.0
                    String compactAllele = "$ref$pos${alt == ':' ? 'del' : alt}"
                    [(compactAllele): ['mitoTipScore': mitoTipScore, 'mitoTipQuartile': mitoTipQuartile, 'mitoTipCount': mitoTipCount, 'mitoTipFreqPct': mitoTipFreqPct]]
                }

        int totalCount = rawMitoTips.size()
        Map<String, Object> withPercentileResult = rawMitoTips.sort { a, b -> b.value.mitoTipScore <=> a.value.mitoTipScore ?: a.key <=> b.key }
        withPercentileResult.eachWithIndex { String allele, def mtAnnotation, int i ->
            BigDecimal percentile = ((totalCount - i - 1) / totalCount * 100).setScale(2, RoundingMode.HALF_EVEN)
            mtAnnotation['mitoTipScorePercentile'] = percentile
        }

        return withPercentileResult
    }
}
