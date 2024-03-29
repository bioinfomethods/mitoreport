package mitoreport

import groovy.json.JsonSlurper
import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@MicronautTest
class MitoMapAnnotationsLoaderTest extends Specification {

    @Inject
    ResourceLoader resourceLoader

    @Inject
    MitoMapAnnotationsLoader underTest

    def 'downloadAnnotations() saves annotations to JSON file correctly'() {
        given: 'Downloaded annotations from MitoMap'
        underTest = GroovySpy(global: true)
        String codingsHtml = resourceLoader.getResourceAsStream('classpath:mito_map_variants_codings_small.html').get().text
        String controlsHtml = resourceLoader.getResourceAsStream('classpath:mito_map_variants_controls_small.html').get().text
        String rnaMutationsHtml = resourceLoader.getResourceAsStream('classpath:mito_map_variants_rna-mutations_small.html').get().text
        String diseasesTsv = resourceLoader.getResourceAsStream('classpath:mito_map_diseases_small.tsv').get().text
        String mitoTipsTsv = resourceLoader.getResourceAsStream('classpath:mitotip_scores_small.txt').get().text
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/foswiki/bin/view/MITOMAP/VariantsCoding') >> codingsHtml
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/foswiki/bin/view/MITOMAP/VariantsControl') >> controlsHtml
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/foswiki/bin/view/MITOMAP/MutationsRNA') >> rnaMutationsHtml
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/cgi-bin/disease.cgi') >> diseasesTsv
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/downloads/mitotip_scores.txt') >> mitoTipsTsv

        when:
        Path actualResult = Files.createTempFile(MitoMapAnnotationsLoaderTest.name, null)
        underTest.downloadAnnotations(actualResult)

        then:
        def actualJson = new JsonSlurper().parse(actualResult.toFile())
        def expJson = new JsonSlurper().parse(resourceLoader.getResourceAsStream('classpath:MitoMapAnnotationsLoaderTest_expected.json').get())
        actualJson == expJson
    }

    def 'getAnnotations() correctly parses JSON and returns annotations'() {
        given:
        Path annotationsJsonFile = Paths.get(resourceLoader.getResource('classpath:MitoMapAnnotationsLoaderTest_expected.json').get().path)

        when:
        Map<String, MitoMapAnnotation> actualResult = MitoMapAnnotationsLoader.getAnnotations(annotationsJsonFile)

        then:
        actualResult.size() == 6

        MitoMapAnnotation actualResult1 = actualResult.get('G577A')
        actualResult1.mitoMapHost == 'https://mitomap.org'
        actualResult1.regionType == 'CODING'
        actualResult1.positionStr == '577'
        actualResult1.alleleChange == 'G-A'
        actualResult1.gbFreqStr == '0.000%'
        actualResult1.curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=10433&title=Coding+Polymorphism+G-A+at+577' target='_blank'>1</a>"
        actualResult1.disease == 'Hearing loss patient'
        actualResult1.diseaseStatus == 'Reported'
        !actualResult1.diseaseConfirmedPathogenic
        actualResult1.mitoTipScore == 17.75
        actualResult1.mitoTipScorePercentile == 82.00
        actualResult1.mitoTipQuartile == MitoTipQuartile.Q1
        actualResult1.mitoTipCount == 0
        actualResult1.mitoTipFreq == 0

        MitoMapAnnotation actualResult2 = actualResult.get('G16023T')
        actualResult2.mitoMapHost == 'https://mitomap.org'
        actualResult2.regionType == 'CODING'
        actualResult2.positionStr == '16023'
        actualResult2.alleleChange == 'G-T'
        actualResult2.gbFreqStr == "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/A15951G.html' target=_blank>0.010%</a>&nbsp;<img src='/images/flag.png'></span>"
        actualResult2.curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=91704&title=Coding+Polymorphism+G-T+at+16023' target='_blank'>1</a>"
        actualResult2.disease == 'Migraine +pigmentary retinopathy +deafness +leukariosis'
        actualResult2.diseaseStatus == 'Reported'
        !actualResult2.diseaseConfirmedPathogenic
        actualResult2.mitoTipScore == 20.1973
        actualResult2.mitoTipScorePercentile == 94.00
        actualResult2.mitoTipQuartile == MitoTipQuartile.Q1
        actualResult2.mitoTipCount == 5
        actualResult2.mitoTipFreq == 0.0001

        MitoMapAnnotation actualResult3 = actualResult.get('T152C')
        actualResult3.mitoMapHost == 'https://mitomap.org'
        actualResult3.regionType == 'CONTROL'
        actualResult3.positionStr == '152'
        actualResult3.alleleChange == 'T-C'
        actualResult3.gbFreqStr == "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/T152C.html' target=_blank>25.942%</a>&nbsp;<img src='/images/flag.png'></span><br><span style='white-space:nowrap;'>(<a href='/haploinfo_gbcontrol/T152C.html' target=_blank>17.494%</a>)&nbsp;<img src='/images/flag.png'></span>"
        actualResult3.curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=1986,4,140,233,414,394,521,572,911,924,928,968,989,1739,2285,2882,3082,3311,3314,3502,3690,3779,3922,4022,4946,5348,6221,6169,6421,6432,6623,6687,8200,8405,8492,8510,90260,9531,10433,20043,20059,90335,20264,20429,90123,90254,90272,90296,90302,90311,90350,90680,90365,90398,90429,90435,90452,90453,90477,90501,90534,90537,90550,90562,90572,90592,90625,90628,90653,90687,90695,90749,90762,90764,90771,90773,90805,90814,90941,90962,90963,90965,90966,90977,91049,91021,91026,91030,91068,91077,91091,91214,91215,91256,91275,91288,91311,91312,91318,91343,91394,91423,91431,91468,91483,91612,91660,91687,91737,91755,91775,91793,91806,92045,92031,92043,92063,92100,92191,92268,99016&title=Control+Polymorphism+T-C+at+152' target='_blank'>121</a>"
        actualResult3.disease == null
        actualResult3.diseaseStatus == null
        !actualResult3.diseaseConfirmedPathogenic
        actualResult3.mitoTipScore == null
        actualResult3.mitoTipQuartile == MitoTipQuartile.UNKNOWN
        actualResult3.mitoTipScorePercentile == null
        actualResult3.mitoTipCount == null
        actualResult3.mitoTipFreq == null

        MitoMapAnnotation actualResult4 = actualResult.get('C16565T')
        actualResult4.mitoMapHost == 'https://mitomap.org'
        actualResult4.regionType == 'CONTROL'
        actualResult4.positionStr == '16565'
        actualResult4.alleleChange == 'C-T'
        actualResult4.gbFreqStr == "0.000%<br>(0.001%)"
        actualResult4.curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=20180104&title=Control+Polymorphism+C-T+at+16565' target='_blank'>1</a>"
        actualResult4.disease == null
        actualResult4.diseaseStatus == null
        !actualResult4.diseaseConfirmedPathogenic
        actualResult4.mitoTipScore == null
        actualResult4.mitoTipQuartile == MitoTipQuartile.UNKNOWN
        actualResult4.mitoTipScorePercentile == null
        actualResult4.mitoTipCount == null
        actualResult4.mitoTipFreq == null

        MitoMapAnnotation actualResult5 = actualResult.get('A3243G')
        actualResult5.mitoMapHost == 'https://mitomap.org'
        actualResult5.regionType == 'RNA_MUTATIONS'
        actualResult5.positionStr == '3243'
        actualResult5.alleleChange == null
        actualResult5.alleleStr == 'A3243G'

        MitoMapAnnotation actualResult6 = actualResult.get('T16032TTCTCTGTTCTTTCAT')
        actualResult6.mitoMapHost == 'https://mitomap.org'
        actualResult6.regionType == 'RNA_MUTATIONS'
        actualResult6.positionStr == '16032'
        actualResult6.alleleChange == null
        actualResult6.alleleStr == 'T16032TTCTCTGTTCTTTCAT'
    }

    def 'parseMitoTipsTsv() returns correct results'() {
        given:
        String mitoTipsTsv = resourceLoader.getResource('classpath:mitotip_scores_small.txt').get().text

        when:
        Map<String, Object> actualResult = MitoMapAnnotationsLoader.parseMitoTipsTsv(mitoTipsTsv)

        then:
        actualResult.size() == 50
        def actualResultFirst = actualResult.values().first()
        assert actualResultFirst.mitoTipScore == 20.3833
        assert actualResultFirst.mitoTipScorePercentile == 98.00
        assert actualResultFirst.mitoTipQuartile == MitoTipQuartile.Q1
        assert actualResultFirst.mitoTipCount == 0.0
        assert actualResultFirst.mitoTipFreqPct == 0.0
        def actualResultLast = actualResult.values().last()
        assert actualResultLast.mitoTipScore == 3.73435
        assert actualResultLast.mitoTipScorePercentile == 0.0
        assert actualResultLast.mitoTipQuartile == MitoTipQuartile.Q4
        assert actualResultLast.mitoTipCount == 0.0
        assert actualResultLast.mitoTipFreqPct == 0.0
    }
}
