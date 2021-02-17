package mitoreport.haplogrep

import spock.lang.Specification
import spock.lang.Unroll

import static mitoreport.TestUtils.hr

class HaplogrepClassificationTest extends Specification {

    @Unroll
    def "Given haplogrep results #haplogrepResults, hasMultipleBaseHaplogroups is #expResult"() {
        expect:
        HaplogrepClassification underTest = new HaplogrepClassification(
                haplogrepResults: haplogrepResults
        )
        underTest.hasMultipleBaseHaplogroups == expResult

        where:
        haplogrepResults                                                           | expResult
        null                                                                       | false
        Collections.emptySortedMap()                                               | false
        new TreeMap<>([1: hr('HV0a1a', 1), 2: hr('HV0a1', 2)])                     | false
        new TreeMap<>([1: hr('HV0a1a', 1), 2: hr('HV0a1', 2), 3: hr('1junk', 3)])  | false
        new TreeMap<>([1: hr('HV0a1a', 1), 2: hr('HV0a1', 2), 3: hr('T2f1a1', 3)]) | true
    }
}
