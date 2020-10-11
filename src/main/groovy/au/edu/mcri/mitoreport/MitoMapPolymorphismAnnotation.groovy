package au.edu.mcri.mitoreport

import groovy.transform.Immutable
import groovy.transform.MapConstructor

import java.util.regex.Matcher
import java.util.regex.Pattern

@Immutable
@MapConstructor
class MitoMapPolymorphismAnnotation {

    static final Pattern HTML_ANCHOR_PATTERN = Pattern.compile(/<a.*?href='(.*?)'.*?>(\d+?\.?\d*?%*)<\/a>/)
    static final Pattern ALLELE_CHANGE_PATTERN = Pattern.compile(/([ATCGU]+?)-([ATCGU|del]*)/)

    String mitoMapHost
    String positionStr
    String locusAnchor
    String alleleChange
    String codonNumber
    String codonPosition
    String aminoAcidChange
    String gbFreqStr
    String gbSeqsAnchor
    String curatedRefsAnchor

    Long getPosition() {
        positionStr && positionStr.isLong() ? Long.valueOf(positionStr) : 0L
    }

    String getRefAllele() {
        Matcher alleleChangeMatcher = alleleChange =~ ALLELE_CHANGE_PATTERN
        if (!alleleChangeMatcher.find()) {
            return null
        } else {
            return alleleChangeMatcher[0][1]
        }
    }

    String getAltAllele() {
        Matcher alleleChangeMatcher = alleleChange =~ ALLELE_CHANGE_PATTERN
        if (!alleleChangeMatcher.find() || alleleChangeMatcher[0][2] == '') {
            return null
        } else {
            return alleleChangeMatcher[0][2]
        }
    }

    String getCompactAllele() {
        if (!refAllele || !position) {
            return null
        } else {
            return "$refAllele$position${altAllele ?: ''}"
        }
    }

    BigDecimal getGbFreqPct() {
        Matcher anchorMatcher = gbFreqStr =~ HTML_ANCHOR_PATTERN
        if (!anchorMatcher.find()) {
            String numberStr = gbFreqStr?.replaceAll(/%/, '')

            return numberStr && numberStr.isBigDecimal() ? new BigDecimal(numberStr) : 0.0
        } else {
            if (anchorMatcher[0].size() != 3) {
                return 0.0
            } else {
                String numberStr = (anchorMatcher[0][2]).replaceAll(/%/, '')
                return numberStr && numberStr.isBigDecimal() ? new BigDecimal(numberStr) : 0.0
            }
        }
    }

    Integer getCuratedRefsCount() {
        Matcher anchorMatcher = curatedRefsAnchor =~ HTML_ANCHOR_PATTERN
        if (!anchorMatcher.find()) {
            return 0
        } else {
            if (anchorMatcher[0].size() != 3 || !(anchorMatcher[0][2]).isInteger()) {
                return 0
            } else {
                Integer result = Integer.parseInt(anchorMatcher[0][2])

                return result
            }
        }
    }

    String getCuratedRefsUrl() {
        Matcher anchorMatcher = curatedRefsAnchor =~ HTML_ANCHOR_PATTERN
        if (!anchorMatcher.find()) {
            return mitoMapHost
        } else {
            String href = anchorMatcher[0][1]

            return "$mitoMapHost$href"
        }
    }
}
