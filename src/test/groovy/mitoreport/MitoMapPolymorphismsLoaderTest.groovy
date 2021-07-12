package mitoreport

import groovy.json.JsonSlurper
import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@MicronautTest
class MitoMapPolymorphismsLoaderTest extends Specification {

    @Inject
    ResourceLoader resourceLoader

    @Inject
    MitoMapAnnotationsLoader underTest

    def 'downloadPolymorphisms() saves annotations to JSON file correctly'() {
        given: 'Downloaded Polymorphisms from MitoMap'
        underTest = GroovySpy(global: true)
        String codingsHtml = resourceLoader.getResourceAsStream('classpath:mito_map_polymorphisms_codings_small.html').get().text
        String controlsHtml = resourceLoader.getResourceAsStream('classpath:mito_map_polymorphisms_controls_small.html').get().text
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/foswiki/bin/view/MITOMAP/PolymorphismsCoding') >> codingsHtml
        1 * MitoMapAnnotationsLoader.downloadPage('https://mitomap.org/foswiki/bin/view/MITOMAP/PolymorphismsControl') >> controlsHtml

        when:
        Path actualResult = Files.createTempFile('MitoMapPolymorphismsLoaderTest', null)
        underTest.downloadPolymorphisms(actualResult)

        then:
        def actualJson = new JsonSlurper().parse(actualResult.toFile())
        def expJson = new JsonSlurper().parse(resourceLoader.getResourceAsStream('classpath:MitoMapPolymorphismsLoaderTest_expected.json').get())
        actualJson == expJson
    }

    def 'getAnnotations() correctly parses JSON and returns annotations'() {
        given:
        Path annotationsJsonFile = Paths.get(resourceLoader.getResource('classpath:MitoMapPolymorphismsLoaderTest_expected.json').get().path)

        when:
        List<MitoMapPolymorphismAnnotation> actualResult = MitoMapAnnotationsLoader.getAnnotations(annotationsJsonFile)

        then:
        actualResult.size() == 4
        
        actualResult[0].mitoMapHost == 'https://mitomap.org'
        actualResult[0].regionType == 'CODING'
        actualResult[0].positionStr == '577'
        actualResult[0].alleleChange == 'G-A'
        actualResult[0].gbFreqStr == '0.000%'
        actualResult[0].curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=10433&title=Coding+Polymorphism+G-A+at+577' target='_blank'>1</a>"

        actualResult[1].mitoMapHost == 'https://mitomap.org'
        actualResult[1].regionType == 'CODING'
        actualResult[1].positionStr == '16023'
        actualResult[1].alleleChange == 'G-T'
        actualResult[1].gbFreqStr == "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/A15951G.html' target=_blank>0.010%</a>&nbsp;<img src='/images/flag.png'></span>"
        actualResult[1].curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=91704&title=Coding+Polymorphism+G-T+at+16023' target='_blank'>1</a>"

        actualResult[2].mitoMapHost == 'https://mitomap.org'
        actualResult[2].regionType == 'CONTROL'
        actualResult[2].positionStr == '152'
        actualResult[2].alleleChange == 'T-C'
        actualResult[2].gbFreqStr == "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/T152C.html' target=_blank>25.942%</a>&nbsp;<img src='/images/flag.png'></span><br><span style='white-space:nowrap;'>(<a href='/haploinfo_gbcontrol/T152C.html' target=_blank>17.494%</a>)&nbsp;<img src='/images/flag.png'></span>"
        actualResult[2].curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=1986,4,140,233,414,394,521,572,911,924,928,968,989,1739,2285,2882,3082,3311,3314,3502,3690,3779,3922,4022,4946,5348,6221,6169,6421,6432,6623,6687,8200,8405,8492,8510,90260,9531,10433,20043,20059,90335,20264,20429,90123,90254,90272,90296,90302,90311,90350,90680,90365,90398,90429,90435,90452,90453,90477,90501,90534,90537,90550,90562,90572,90592,90625,90628,90653,90687,90695,90749,90762,90764,90771,90773,90805,90814,90941,90962,90963,90965,90966,90977,91049,91021,91026,91030,91068,91077,91091,91214,91215,91256,91275,91288,91311,91312,91318,91343,91394,91423,91431,91468,91483,91612,91660,91687,91737,91755,91775,91793,91806,92045,92031,92043,92063,92100,92191,92268,99016&title=Control+Polymorphism+T-C+at+152' target='_blank'>121</a>"

        actualResult[3].mitoMapHost == 'https://mitomap.org'
        actualResult[3].regionType == 'CONTROL'
        actualResult[3].positionStr == '16565'
        actualResult[3].alleleChange == 'C-T'
        actualResult[3].gbFreqStr == "0.000%<br>(0.001%)"
        actualResult[3].curatedRefsAnchor == "<a href='/cgi-bin/print_ref_list?refs=20180104&title=Control+Polymorphism+C-T+at+16565' target='_blank'>1</a>"
    }
}
