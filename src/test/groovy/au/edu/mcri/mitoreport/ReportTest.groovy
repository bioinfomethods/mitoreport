package au.edu.mcri.mitoreport

import spock.lang.Specification
import spock.lang.Unroll

class ReportTest extends Specification {

    @Unroll
    def 'toPrecision(#number, #precision) = #expResult'() {
        expect:
        Report.toPrecision(number, precision) == expResult

        where:
        number | precision | expResult
        null   | 2         | 0
        0.0    | 2         | 0.0
        5      | 2         | 5.0
        5.0    | 2         | 5.0
        5.01   | 2         | 5.0
        0.5    | 2         | 0.50
        0.0001 | 2         | 0.00010
        26.72  | 2         | 27
        264.2  | 2         | 260
        99.99  | 2         | 100
    }
}
