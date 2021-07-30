package mitoreport

import gngs.Variant
import spock.lang.Specification
import spock.lang.Unroll

class MitoUtilsTest extends Specification {

    @Unroll
    def 'Given variant has chr=#chr, pos=#pos, ref=#ref, alt=#alt then hgvsg is #expResult'() {
        given:
        Variant mockVariant = Mock(Variant)
        mockVariant.chr >> chr
        mockVariant.pos >> pos
        mockVariant.ref >> ref
        mockVariant.alt >> alt

        expect:
        MitoUtils.extractMitoHgvsg(mockVariant) == expResult

        where:
        pos  | chr    | ref   | alt   | expResult
        8888 | null   | null  | null  | null
        8888 | ''     | null  | null  | null
        null | 'chrM' | 'T'   | 'C'   | null
        8888 | 'chrM' | 'A'   | 'G'   | 'm.8888A>G'
        8888 | 'chrM' | 'T'   | 'TT'  | 'm.8888T>TT'
        8888 | 'chrM' | 'GAA' | 'G'   | 'm.8888GAA>G'
        8888 | 'chrM' | 'A'   | 'del' | 'm.8888Adel'
        8888 | 'chrM' | 'A'   | null  | null
        8888 | 'chrM' | null  | null  | null
        8888 | 'chrM' | null  | null  | null
    }
}
