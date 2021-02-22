package mitoreport.haplogrep

import spock.lang.Specification
import spock.lang.Unroll

class GnomadBaseHaplogroupTest extends Specification {

    @Unroll
    def "Given rawGnomadHapIndexedValues #rawGnomadHapIndexedValues, mapToBaseHaplogroup is #expResult"() {
        expect:
        GnomadBaseHaplogroup.mapToBaseHaplogroup(rawGnomadHapIndexedValues) == expResult

        where:
        rawGnomadHapIndexedValues << [
                null,
                [],
                [2680, 1537, 865],
                [2680, 1537, 865, 602, 34, 282, 91, 14777, 701, 934, 3144, 2732, 663, 2977, 4724, 5670, 126, 1, 1298, 366, 7, 393, 3079, 6037, 1234, 819, 546, 12, 89],
                [0, 0, 0.00289017, 0.00598802, 0, 0, 0, 0.000262812, 0, 0, 0.00144928, 0, 0, 0, 0.000775795, 0.000695894, 0, 0, 0, 0, 0, 0, 0.00112486, 0.00116618, 0, 0, 0, 0, 0],
        ]
        expResult << [
                Collections.emptySortedMap(),
                Collections.emptySortedMap(),
                Collections.emptySortedMap(),
                new TreeMap<>([
                        (GnomadBaseHaplogroup.A) : 2680,
                        (GnomadBaseHaplogroup.B) : 1537,
                        (GnomadBaseHaplogroup.C) : 865,
                        (GnomadBaseHaplogroup.D) : 602,
                        (GnomadBaseHaplogroup.E) : 34,
                        (GnomadBaseHaplogroup.F) : 282,
                        (GnomadBaseHaplogroup.G) : 91,
                        (GnomadBaseHaplogroup.H) : 14777,
                        (GnomadBaseHaplogroup.HV): 701,
                        (GnomadBaseHaplogroup.I) : 934,
                        (GnomadBaseHaplogroup.J) : 3144,
                        (GnomadBaseHaplogroup.K) : 2732,
                        (GnomadBaseHaplogroup.L0): 663,
                        (GnomadBaseHaplogroup.L1): 2977,
                        (GnomadBaseHaplogroup.L2): 4724,
                        (GnomadBaseHaplogroup.L3): 5670,
                        (GnomadBaseHaplogroup.L4): 126,
                        (GnomadBaseHaplogroup.L5): 1,
                        (GnomadBaseHaplogroup.M) : 1298,
                        (GnomadBaseHaplogroup.N) : 366,
                        (GnomadBaseHaplogroup.P) : 7,
                        (GnomadBaseHaplogroup.R) : 393,
                        (GnomadBaseHaplogroup.T) : 3079,
                        (GnomadBaseHaplogroup.U) : 6037,
                        (GnomadBaseHaplogroup.V) : 1234,
                        (GnomadBaseHaplogroup.W) : 819,
                        (GnomadBaseHaplogroup.X) : 546,
                        (GnomadBaseHaplogroup.Y) : 12,
                        (GnomadBaseHaplogroup.Z) : 89,
                ]),
                new TreeMap<>([
                        (GnomadBaseHaplogroup.A) : 0,
                        (GnomadBaseHaplogroup.B) : 0,
                        (GnomadBaseHaplogroup.C) : 0.00289017,
                        (GnomadBaseHaplogroup.D) : 0.00598802,
                        (GnomadBaseHaplogroup.E) : 0,
                        (GnomadBaseHaplogroup.F) : 0,
                        (GnomadBaseHaplogroup.G) : 0,
                        (GnomadBaseHaplogroup.H) : 0.000262812,
                        (GnomadBaseHaplogroup.HV): 0,
                        (GnomadBaseHaplogroup.I) : 0,
                        (GnomadBaseHaplogroup.J) : 0.00144928,
                        (GnomadBaseHaplogroup.K) : 0,
                        (GnomadBaseHaplogroup.L0): 0,
                        (GnomadBaseHaplogroup.L1): 0,
                        (GnomadBaseHaplogroup.L2): 0.000775795,
                        (GnomadBaseHaplogroup.L3): 0.000695894,
                        (GnomadBaseHaplogroup.L4): 0,
                        (GnomadBaseHaplogroup.L5): 0,
                        (GnomadBaseHaplogroup.M) : 0,
                        (GnomadBaseHaplogroup.N) : 0,
                        (GnomadBaseHaplogroup.P) : 0,
                        (GnomadBaseHaplogroup.R) : 0,
                        (GnomadBaseHaplogroup.T) : 0.00112486,
                        (GnomadBaseHaplogroup.U) : 0.00116618,
                        (GnomadBaseHaplogroup.V) : 0,
                        (GnomadBaseHaplogroup.W) : 0,
                        (GnomadBaseHaplogroup.X) : 0,
                        (GnomadBaseHaplogroup.Y) : 0,
                        (GnomadBaseHaplogroup.Z) : 0,
                ])
        ]
    }
}
