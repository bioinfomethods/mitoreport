package au.edu.mcri.mitoreport

import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.MicronautTest
import spock.lang.Specification

import javax.inject.Inject

@MicronautTest
class MitoMapPolymorphismsLoaderTest extends Specification {

    @Inject
    ResourceLoader resourceLoader

    MitoMapPolymorphismsLoader underTest

    def 'getAnnotations'() {
        given: 'Downloaded Polymorphisms from MitoMap'

        underTest = Spy(class: MitoMapPolymorphismsLoader, constructorArgs: ['https://mitomap.org', '/foswiki/bin/view/MITOMAP/PolymorphismsCoding']) {
            String htmlTest = resourceLoader.getResourceAsStream('classpath:mito_map_polymorphisms_small.html').get().text
            downloadPolymorphismsCoding(_) >> htmlTest
        }

        when:
        List<MitoMapPolymorphismAnnotation> actualResult = underTest.getAnnotations()

        then:
        actualResult.size() == 2
        actualResult == [
                new MitoMapPolymorphismAnnotation(
                        positionStr: '577',
                        locusAnchor: "<a href='/MITOMAP/GenomeLoci#MTTF'>MT-TF</a>",
                        alleleChange: 'G-A',
                        codonNumber: '-',
                        codonPosition: '-',
                        aminoAcidChange: 'tRNA',
                        gbFreqPctStr: '0.000%',
                        gbSeqsAnchor: '0',
                        curatedRefsAnchor: "<a href='/cgi-bin/print_ref_list?refs=10433&title=Coding+Polymorphism+G-A+at+577' target='_blank'>1</a>",
                ),
                new MitoMapPolymorphismAnnotation(
                        positionStr: '16023',
                        locusAnchor: "<a href='/MITOMAP/GenomeLoci#MTTP'>MT-TP</a>",
                        alleleChange: 'G-T',
                        codonNumber: '-',
                        codonPosition: '-',
                        aminoAcidChange: 'tRNA',
                        gbFreqPctStr: '0.010%',
                        gbSeqsAnchor: "<a href='/cgi-bin/index_mitomap.cgi?title=Coding+Polymorphism+G-T+at+rCRS+position+16023&pos=16023&ref=G&alt=T&purge_type=' target='_blank'>5</a>",
                        curatedRefsAnchor: "<a href='/cgi-bin/print_ref_list?refs=91704&title=Coding+Polymorphism+G-T+at+16023' target='_blank'>1</a>",
                ),
        ]
    }
}
