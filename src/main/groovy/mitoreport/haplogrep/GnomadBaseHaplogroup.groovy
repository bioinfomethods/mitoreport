package mitoreport.haplogrep

import static mitoreport.haplogrep.Lineage.*

/**
 * Modelled from https://gnomad.broadinstitute.org/faq#what-is-the-distribution-of-haplogroups-in-gnomad-v3-1
 */
enum GnomadBaseHaplogroup {

    A('A', EURASIAN),
    B('B', EURASIAN),
    C('C', ASIAN),
    D('D', ASIAN),
    E('E', ASIAN),
    F('F', EURASIAN),
    G('G', ASIAN),
    H('H', EURASIAN),
    HV('HV', EURASIAN),
    I('I', EURASIAN),
    J('J', EURASIAN),
    K('K', EURASIAN),
    L0('L0', AFRICAN),
    L1('L1', AFRICAN),
    L2('L2', AFRICAN),
    L3('L3', AFRICAN),
    L4('L4', AFRICAN),
    L5('L5', AFRICAN),
    M('M', ASIAN),
    N('N', EURASIAN),
    P('P', EURASIAN),
    R('R', EURASIAN),
    T('T', EURASIAN),
    U('U', EURASIAN),
    V('V', EURASIAN),
    W('W', EURASIAN),
    X('X', EURASIAN),
    Y('Y', EURASIAN),
    Z('Z', ASIAN),
    UNKNOWN('UNKNOWN', Lineage.UNKNOWN),

    private String id
    private Lineage lineage

    GnomadBaseHaplogroup(String id, Lineage lineage) {
        this.id = id
        this.lineage = lineage
    }

    static List<GnomadBaseHaplogroup> singleDigitBaseGroups() {
        return Collections.unmodifiableList(Arrays.asList(values()) - twoDigitBaseGroups())
    }

    static List<GnomadBaseHaplogroup> twoDigitBaseGroups() {
        return Collections.unmodifiableList([HV, L0, L1, L2, L3, L4, L5])
    }

    static <T> SortedMap<GnomadBaseHaplogroup, T> mapToBaseHaplogroup(List<T> rawGnomadHapIndexedValues) {
        List<GnomadBaseHaplogroup> gnomadBaseHaplogroups = values() - UNKNOWN
        if (gnomadBaseHaplogroups.size() != (rawGnomadHapIndexedValues ?: []).size()) {
            return Collections.emptySortedMap()
        } else {
            SortedMap<GnomadBaseHaplogroup, T> result = new TreeMap<>(
                    [
                            gnomadBaseHaplogroups,
                            rawGnomadHapIndexedValues
                    ]
                            .transpose()
                            .collectEntries {
                                [(it[0]): it[1]]
                            })

            return result
        }
    }
}
