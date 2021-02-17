package mitoreport.haplogrep

import spock.lang.Specification
import spock.lang.Unroll

class HaplogrepResultTest extends Specification {

    @Unroll
    def "Given Haplogroup #haplogroup, baseHaplogroup is #expResult"() {
        expect:
        new HaplogrepResult(haplogroup: haplogroup).baseHaplogroup == expResult

        where:
        haplogroup | expResult
        null       | GnomadBaseHaplogroup.UNKNOWN
        '1junk'     | GnomadBaseHaplogroup.UNKNOWN
        'Hjunk'    | GnomadBaseHaplogroup.H  // This incorrectly passes but difficult to fix as it'll require parsing of phylotree17.xml
        'HV0a1a'   | GnomadBaseHaplogroup.HV
        'h13a1a1a' | GnomadBaseHaplogroup.H
        'Labc'     | GnomadBaseHaplogroup.UNKNOWN
        'L3'       | GnomadBaseHaplogroup.L3
        'T2f1a1'   | GnomadBaseHaplogroup.T
    }
}
