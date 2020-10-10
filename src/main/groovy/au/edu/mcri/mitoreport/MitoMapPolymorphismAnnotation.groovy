package au.edu.mcri.mitoreport

import groovy.transform.Immutable
import groovy.transform.MapConstructor

@Immutable
@MapConstructor
class MitoMapPolymorphismAnnotation {

    String positionStr
    String locusAnchor
    String alleleChange
    String codonNumber
    String codonPosition
    String aminoAcidChange
    String gbFreqPctStr
    String gbSeqsAnchor
    String curatedRefsAnchor

}
