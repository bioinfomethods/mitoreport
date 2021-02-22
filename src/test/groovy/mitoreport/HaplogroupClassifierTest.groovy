package mitoreport

import io.micronaut.core.io.ResourceLoader
import io.micronaut.test.annotation.MicronautTest
import mitoreport.haplogrep.HaplogrepClassification
import mitoreport.haplogrep.HaplogroupClassifier
import spock.lang.Specification

import javax.inject.Inject

import static mitoreport.TestUtils.hr

@MicronautTest
class HaplogroupClassifierTest extends Specification {

    @Inject
    ResourceLoader resourceLoader

    HaplogroupClassifier underTest

    def 'Given VCF classifier returns expected results'() {
        given:
        File vcfFile = new File(resourceLoader.getResource('classpath:HG00097.vcf').get().toURI())
        underTest = new HaplogroupClassifier(vcfFile: vcfFile, sampleId: 'HG00097')

        when:
        HaplogrepClassification actualResult = underTest.call()

        then:
        actualResult.sampleId == 'HG00097'
        !actualResult.hasMultipleBaseHaplogroups
        actualResult.haplogrepResults == new TreeMap<>([
                1: hr('T2f1a1', 1, 0.9189),
                2: hr('T2+16189', 2, 0.9069),
                3: hr('T2', 3, 0.9032),
                4: hr('T2f1a', 4, 0.8980),
                5: hr('T2+150', 5, 0.8972),
        ])
    }
}
