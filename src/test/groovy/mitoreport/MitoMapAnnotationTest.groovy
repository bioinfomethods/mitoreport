package mitoreport

import spock.lang.Specification
import spock.lang.Unroll

class MitoMapAnnotationTest extends Specification {

    MitoMapAnnotation underTest

    @Unroll
    def 'Given position=#positionStr, alleleChange=#alleleChange then ref=#expRef, alt=#expAlt, compactAllele=#expCompactAllele'() {
        given:
        underTest = new MitoMapAnnotation(positionStr: positionStr, alleleChange: alleleChange)

        expect:
        underTest.refAllele == expRef
        underTest.altAllele == expAlt
        underTest.compactAllele == expCompactAllele
        underTest.hgvs == expHgvs

        where:
        positionStr | alleleChange | expRef | expAlt | expCompactAllele | expHgvs
        8888        | null         | null   | null   | null             | null
        8888        | ''           | null   | null   | null             | null
        null        | 'T-C'        | 'T'    | 'C'    | null             | null
        ''          | 'T-C'        | 'T'    | 'C'    | null             | null
        8888        | 'A-G'        | 'A'    | 'G'    | 'A8888G'         | 'm.8888A>G'
        8888        | 'T-TT'       | 'T'    | 'TT'   | 'T8888TT'        | 'm.8888T>TT'
        8888        | 'GAA-G'      | 'GAA'  | 'G'    | 'GAA8888G'       | 'm.8888GAA>G'
        8888        | 'A-del'      | 'A'    | 'del'  | 'A8888del'       | 'm.8888Adel'
        8888        | 'A-'         | 'A'    | null   | 'A8888'          | null
        8888        | 'B-D'        | null   | null   | null             | null
        8888        | '-A'         | null   | null   | null             | null
    }

    @Unroll
    def 'Given regionType=#regionType, gbFreqStr=#gbFreqStr then gbFreqPct is #expGbFreqPct'() {
        given:
        underTest = new MitoMapAnnotation(regionType: regionType, gbFreqStr: gbFreqStr)

        expect:
        underTest.gbFreqPct == expGbFreqPct
        underTest.gbFreq == expGbFreq

        where:
        regionType | gbFreqStr                                                                                                                                                                                                                                                                                          | expGbFreqPct | expGbFreq
        'CODING'   | null                                                                                                                                                                                                                                                                                               | 0.0          | 0.0
        'CODING'   | ''                                                                                                                                                                                                                                                                                                 | 0.0          | 0.0
        'CODING'   | 'abc'                                                                                                                                                                                                                                                                                              | 0.0          | 0.0
        'CODING'   | '0'                                                                                                                                                                                                                                                                                                | 0.0          | 0.0
        'CODING'   | '0%'                                                                                                                                                                                                                                                                                               | 0.0          | 0.0
        'CODING'   | '10.0'                                                                                                                                                                                                                                                                                             | 10.0         | 0.1
        'CODING'   | '98.7%'                                                                                                                                                                                                                                                                                            | 98.7         | 0.987
        'CODING'   | '150.8%'                                                                                                                                                                                                                                                                                           | 150.8        | 1.508
        'CODING'   | "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/A15951G.html' target=_blank>0.010%</a>&nbsp;<img src='/images/flag.png'></span>"                                                                                                                                                    | 0.01         | 0.0001
        'CODING'   | "<a href='/haploinfo_genbank/A15951G.html' target=_blank>0.011%</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                             | 0.011        | 0.00011
        'CODING'   | "<a href='' target=_blank>0.012%</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                                                            | 0.012        | 0.00012
        'CODING'   | "<a href='/haploinfo_genbank/A15951G.html' target=_blank>-</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                                  | 0.0          | 0.0
        'CODING'   | "<a href='/haploinfo_genbank/A15951G.html' target=_blank></a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                                   | 0.0          | 0.0
        'CODING'   | "0.000%<br>(0.001%)"                                                                                                                                                                                                                                                                               | 0.0          | 0.0
        'CONTROL'  | null                                                                                                                                                                                                                                                                                               | 0.0          | 0.0
        'CONTROL'  | "0"                                                                                                                                                                                                                                                                                                | 0.0          | 0.0
        'CONTROL'  | "0%"                                                                                                                                                                                                                                                                                               | 0.0          | 0.0
        'CONTROL'  | "0.000%<br>(0.001%)"                                                                                                                                                                                                                                                                               | 0.001        | 0.00001
        'CONTROL'  | "<a href='/haploinfo_genbank/A15951G.html' target=_blank>0.011%</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                             | 0.0          | 0.0
        'CONTROL'  | "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/G203A.html' target=_blank>0.459%</a>&nbsp;<img src='/images/flag.png'></span><br>(0.166%)"                                                                                                                                          | 0.166        | 0.00166
        'CONTROL'  | "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/T152C.html' target=_blank>25.942%</a>&nbsp;<img src='/images/flag.png'></span><br><span style='white-space:nowrap;'>(<a href='/haploinfo_gbcontrol/T152C.html' target=_blank>17.494%</a>)&nbsp;<img src='/images/flag.png'></span>" | 17.494       | 0.17494
    }

    @Unroll
    def 'Given curatedRefsAnchor=#curatedRefsAnchor then curatedRefsCount is #expCountResult, curatedRefsUrl is #expUrlResult'() {
        given:
        underTest = new MitoMapAnnotation(mitoMapHost: 'https://mitomap.org', curatedRefsAnchor: curatedRefsAnchor)

        expect:
        underTest.curatedRefsCount == expCountResult
        underTest.curatedRefsUrl == expUrlResult

        where:
        curatedRefsAnchor                                                                                           | expCountResult | expUrlResult
        "<a href='/cgi-bin/print_ref_list?refs=91704&title=Coding+Polymorphism+G-T+at+16023' target='_blank'>1</a>" | 1              | "https://mitomap.org/cgi-bin/print_ref_list?refs=91704&title=Coding+Polymorphism+G-T+at+16023"
        null                                                                                                        | 0              | 'https://mitomap.org'
        ''                                                                                                          | 0              | 'https://mitomap.org'
        'abc'                                                                                                       | 0              | 'https://mitomap.org'
        "<a href='' target='_blank'>3</a>"                                                                          | 3              | 'https://mitomap.org'
    }

    @Unroll
    def 'Given disease=#disease then diseases=#expDiseases'() {
        given:
        underTest = new MitoMapAnnotation(disease: disease)

        expect:
        underTest.diseases == expDiseases

        where:
        disease                                                   | expDiseases
        null                                                      | Collections.emptyList()
        ''                                                        | Collections.emptyList()
        'deafness'                                                | ['deafness']
        'Migraine +pigmentary retinopathy +deafness +leukariosis' | ['Migraine', 'pigmentary retinopathy', 'deafness', 'leukariosis']
    }

    @Unroll
    def 'Given diseaseStatus=#diseaseStatus then diseaseConfirmedPathogenic=#expDiseaseConfirmedPathogenic'() {
        given:
        underTest = new MitoMapAnnotation(diseaseStatus: diseaseStatus)

        expect:
        underTest.diseaseConfirmedPathogenic == expDiseaseConfirmedPathogenic

        where:
        diseaseStatus | expDiseaseConfirmedPathogenic
        null          | false
        ''            | false
        'Reported'    | false
        'cfRm'        | true
    }

    @Unroll
    def 'Given mitoTipFreqPct=#mitoTipFreqPct then mitoTipFreq=#expMitoTipFreq'() {
        given:
        underTest = new MitoMapAnnotation(mitoTipFreqPct: mitoTipFreqPct)

        expect:
        underTest.mitoTipFreq == expMitoTipFreq

        where:
        mitoTipFreqPct | expMitoTipFreq
        null           | null
        0.0            | 0.0
        8.8            | 0.088
    }
}
