package mitoreport

import groovy.transform.MapConstructor
import groovy.util.logging.Slf4j

import java.util.regex.Matcher
import java.util.regex.Pattern

@Slf4j
@MapConstructor
class MitoMapAnnotation {

    // private fields will not be serialized when using JsonOutput.toJson()

    private static final Pattern HTML_ANCHOR_PATTERN = Pattern.compile(/<a.*?href='(.*?)'.*?>(\d+?\.?\d*?%*)<\/a>/)
    private static final Pattern HTML_TEXT_ANCHOR_PATTERN = Pattern.compile(/<a.*?href=['"](.*?)["'].*?>(.*?)<\/a>/)
//    private static final Pattern CONTROL_GB_FREQ_PATTERN = Pattern.compile(/(\d+\.\d*%)<br>\((\d+\.\d*%)\)/)
    private static final Pattern CONTROL_GB_FREQ_PATTERN = Pattern.compile(/\(.*?(\d+\.\d*?)%.*?\)/)
    private static final Pattern ALLELE_CHANGE_PATTERN = Pattern.compile(/([ATCGU]+?)-([ATCGU|del]*)/)

    String mitoMapHost
    String regionType = 'CODING'
    String positionStr
    String locusAnchor
    String alleleChange
    String codonNumber
    String codonPosition
    String aminoAcidChange
    String gbFreqStr
    String gbSeqsAnchor
    String curatedRefsAnchor
    String diseaseAaChange
    String disease
    String diseaseStatus
    BigDecimal mitoTipScore
    MitoTipQuartile mitoTipQuartile
    Integer mitoTipCount
    BigDecimal mitoTipFreqPct

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
        if (regionType == 'CODING') {
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
        } else if (regionType == 'CONTROL') {
            Matcher controlMatcher = gbFreqStr =~ CONTROL_GB_FREQ_PATTERN
            if (!controlMatcher.find()) {
                return 0.0
            } else {
                if (controlMatcher[0].size() != 2) {
                    return 0.0
                } else {
                    String numberStr = (controlMatcher[0][1]).replaceAll(/%/, '')
                    return numberStr && numberStr.isBigDecimal() ? new BigDecimal(numberStr) : 0.0
                }
            }
        } else {
            log.warn('Unexpected regionType when parsing gbFreqPct, regionType={}, gbFreqStr={}', regionType, gbFreqStr)
            return 0.0
        }
    }

    BigDecimal getGbFreq() {
        return (this.gbFreqPct ?: 0.0) / 100.0
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

    Map getCuratedRef() {
        return ['count': curatedRefsCount, 'url': curatedRefsUrl]
    }

    String getLocus() {
        Matcher anchorMatcher = locusAnchor =~ HTML_TEXT_ANCHOR_PATTERN
        if (!anchorMatcher.find()) {
            return null
        } else {
            return anchorMatcher[0][2] // locus
        }
    }

    List<String> getDiseases() {
        disease?.split(/\+/)?.collect { it.trim() }?.findAll() ?: Collections.emptyList() as List<String>
    }

    Boolean getDiseaseConfirmedPathogenic() {
        diseaseStatus?.toUpperCase() == 'CFRM'
    }

    BigDecimal getMitoTipFreq() {
        if (this.mitoTipFreqPct == null) {
            return this.mitoTipFreqPct
        } else {
            return this.mitoTipFreqPct / 100.0
        }
    }
}

enum MitoTipQuartile {
    Q1,
    Q2,
    Q3,
    Q4,
    UNKNOWN
    ;

    static MitoTipQuartile safeValueOf(String value) {
        values().find { it.name() == value } ?: UNKNOWN
    }
}
