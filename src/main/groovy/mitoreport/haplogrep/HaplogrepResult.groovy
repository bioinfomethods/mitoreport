package mitoreport.haplogrep

import groovy.transform.Immutable

@Immutable
class HaplogrepResult {

    String haplogroup
    Integer rank
    BigDecimal quality

    /**
     * Naive implementation that uses haplogroup prefix matching.  Ideally, the haplogroup should
     * also be checked against valid haplogroups according to Haplogroup nomenclature
     * https://onlinelibrary.wiley.com/doi/abs/10.1002/humu.20921
     *
     * For example, "Hjunk", "Foo" incorrectly return a valid base haplogroup (H and F respectively)
     * This is not an issue because haplogroup property is assumed to be from results of Haplogrep.
     */
    GnomadBaseHaplogroup getBaseHaplogroup() {
        if (haplogroup == null) {
            return GnomadBaseHaplogroup.UNKNOWN
        }

        GnomadBaseHaplogroup firstTwoDigitMatch = GnomadBaseHaplogroup.twoDigitBaseGroups()
                .find { haplogroup.toUpperCase().startsWith(it.name()) } ?: GnomadBaseHaplogroup.UNKNOWN

        if (firstTwoDigitMatch == GnomadBaseHaplogroup.UNKNOWN) {
            GnomadBaseHaplogroup firstDigitMatch = GnomadBaseHaplogroup.singleDigitBaseGroups()
                    .find { haplogroup.toUpperCase().startsWith(it.name()) } ?: GnomadBaseHaplogroup.UNKNOWN

            return firstDigitMatch
        } else {
            return firstTwoDigitMatch
        }
    }
}
