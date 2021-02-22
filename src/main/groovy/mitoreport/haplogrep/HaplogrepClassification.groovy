package mitoreport.haplogrep

import groovy.transform.Immutable
import groovy.transform.MapConstructor

@Immutable
@MapConstructor
class HaplogrepClassification {

    String sampleId
    SortedMap<Integer, HaplogrepResult> haplogrepResults = Collections.emptySortedMap()

    Boolean getHasMultipleBaseHaplogroups() {
        haplogrepResults?.values()
                ?.collect { it.baseHaplogroup }
                ?.findAll { it != GnomadBaseHaplogroup.UNKNOWN }
                ?.unique(false)
                ?.size() > 1
    }
}
