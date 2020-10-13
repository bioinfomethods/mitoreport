package au.edu.mcri.mitoreport

import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject
import java.nio.file.Files
import java.nio.file.Path

@MicronautTest
class MitoMapPolymorphismsLoaderTest extends Specification {

    @Inject
    ResourceLoader resourceLoader

    @Inject
    MitoMapPolymorphismsLoader underTest

    def 'getAnnotations() parses HTML and returns correct results'() {
        given: 'Downloaded Polymorphisms from MitoMap'
        String htmlTest = resourceLoader.getResourceAsStream('classpath:mito_map_polymorphisms_small.html').get().text
        Path testMitoMapHtml = Files.createTempFile('MitoMapPolymorphismsLoaderTest', null)
        testMitoMapHtml.toFile().withWriter { Writer w -> w.write(htmlTest) }

        when:
        List<MitoMapPolymorphismAnnotation> actualResult = underTest.getAnnotations(testMitoMapHtml)

        then:
        actualResult.size() == 2
        actualResult == [
                new MitoMapPolymorphismAnnotation(
                        mitoMapHost: 'https://mitomap.org',
                        positionStr: '577',
                        locusAnchor: "<a href='/MITOMAP/GenomeLoci#MTTF'>MT-TF</a>",
                        alleleChange: 'G-A',
                        codonNumber: '-',
                        codonPosition: '-',
                        aminoAcidChange: 'tRNA',
                        gbFreqStr: '0.000%',
                        gbSeqsAnchor: '0',
                        curatedRefsAnchor: "<a href='/cgi-bin/print_ref_list?refs=10433&title=Coding+Polymorphism+G-A+at+577' target='_blank'>1</a>",
                ),
                new MitoMapPolymorphismAnnotation(
                        mitoMapHost: 'https://mitomap.org',
                        positionStr: '16023',
                        locusAnchor: "<a href='/MITOMAP/GenomeLoci#MTTP'>MT-TP</a>",
                        alleleChange: 'G-T',
                        codonNumber: '-',
                        codonPosition: '-',
                        aminoAcidChange: 'tRNA',
                        gbFreqStr: "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/A15951G.html' target=_blank>0.010%</a>&nbsp;<img src='/images/flag.png'></span>",
                        gbSeqsAnchor: "<a href='/cgi-bin/index_mitomap.cgi?title=Coding+Polymorphism+G-T+at+rCRS+position+16023&pos=16023&ref=G&alt=T&purge_type=' target='_blank'>5</a>",
                        curatedRefsAnchor: "<a href='/cgi-bin/print_ref_list?refs=91704&title=Coding+Polymorphism+G-T+at+16023' target='_blank'>1</a>",
                ),
        ]
    }
}
