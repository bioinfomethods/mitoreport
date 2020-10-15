package au.edu.mcri.mitoreport

import spock.lang.Specification
import spock.lang.Unroll

class MitoMapPolymorphismAnnotationTest extends Specification {

    MitoMapPolymorphismAnnotation underTest

    @Unroll
    def 'Given position=#positionStr, alleleChange=#alleleChange then ref=#expRef, alt=#expAlt, compactAllele=#expCompactAllele'() {
        given:
        underTest = new MitoMapPolymorphismAnnotation(positionStr: positionStr, alleleChange: alleleChange)

        expect:
        underTest.refAllele == expRef
        underTest.altAllele == expAlt
        underTest.compactAllele == expCompactAllele

        where:
        positionStr | alleleChange | expRef | expAlt | expCompactAllele
        8888        | null         | null   | null   | null
        8888        | ''           | null   | null   | null
        null        | 'T-C'        | 'T'    | 'C'    | null
        ''          | 'T-C'        | 'T'    | 'C'    | null
        8888        | 'A-G'        | 'A'    | 'G'    | 'A8888G'
        8888        | 'T-TT'       | 'T'    | 'TT'   | 'T8888TT'
        8888        | 'GAA-G'      | 'GAA'  | 'G'    | 'GAA8888G'
        8888        | 'A-del'      | 'A'    | 'del'  | 'A8888del'
        8888        | 'A-'         | 'A'    | null   | 'A8888'
        8888        | 'B-D'        | null   | null   | null
        8888        | '-A'         | null   | null   | null
    }

    @Unroll
    def 'Given regionType=#regionType, gbFreqStr=#gbFreqStr then gbFreqPct is #expGbFreqPct'() {
        given:
        underTest = new MitoMapPolymorphismAnnotation(regionType: regionType, gbFreqStr: gbFreqStr)

        expect:
        underTest.gbFreqPct == expGbFreqPct

        where:
        regionType | gbFreqStr                                                                                                                                                                                                                                                                                          | expGbFreqPct
        'CODING'   | null                                                                                                                                                                                                                                                                                               | 0.0
        'CODING'   | ''                                                                                                                                                                                                                                                                                                 | 0.0
        'CODING'   | 'abc'                                                                                                                                                                                                                                                                                              | 0.0
        'CODING'   | '0'                                                                                                                                                                                                                                                                                                | 0.0
        'CODING'   | '0%'                                                                                                                                                                                                                                                                                               | 0.0
        'CODING'   | '10.0'                                                                                                                                                                                                                                                                                             | 10.0
        'CODING'   | '98.7%'                                                                                                                                                                                                                                                                                            | 98.7
        'CODING'   | '150.8%'                                                                                                                                                                                                                                                                                           | 150.8
        'CODING'   | "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/A15951G.html' target=_blank>0.010%</a>&nbsp;<img src='/images/flag.png'></span>"                                                                                                                                                    | 0.01
        'CODING'   | "<a href='/haploinfo_genbank/A15951G.html' target=_blank>0.011%</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                             | 0.011
        'CODING'   | "<a href='' target=_blank>0.012%</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                                                            | 0.012
        'CODING'   | "<a href='/haploinfo_genbank/A15951G.html' target=_blank>-</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                                  | 0.0
        'CODING'   | "<a href='/haploinfo_genbank/A15951G.html' target=_blank></a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                                   | 0.0
        'CODING'   | "0.000%<br>(0.001%)"                                                                                                                                                                                                                                                                               | 0.0
        'CONTROL'  | null                                                                                                                                                                                                                                                                                               | 0.0
        'CONTROL'  | "0"                                                                                                                                                                                                                                                                                                | 0.0
        'CONTROL'  | "0%"                                                                                                                                                                                                                                                                                               | 0.0
        'CONTROL'  | "0.000%<br>(0.001%)"                                                                                                                                                                                                                                                                               | 0.001
        'CONTROL'  | "<a href='/haploinfo_genbank/A15951G.html' target=_blank>0.011%</a>&nbsp;<img src='/images/flag.png'>"                                                                                                                                                                                             | 0.0
        'CONTROL'  | "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/G203A.html' target=_blank>0.459%</a>&nbsp;<img src='/images/flag.png'></span><br>(0.166%)"                                                                                                                                          | 0.166
        'CONTROL'  | "<span style='white-space:nowrap;'><a href='/haploinfo_genbank/T152C.html' target=_blank>25.942%</a>&nbsp;<img src='/images/flag.png'></span><br><span style='white-space:nowrap;'>(<a href='/haploinfo_gbcontrol/T152C.html' target=_blank>17.494%</a>)&nbsp;<img src='/images/flag.png'></span>" | 17.494
    }

    @Unroll
    def 'Given curatedRefsAnchor=#curatedRefsAnchor then curatedRefsCount is #expCountResult, curatedRefsUrl is #expUrlResult'() {
        given:
        underTest = new MitoMapPolymorphismAnnotation(mitoMapHost: 'https://mitomap.org', curatedRefsAnchor: curatedRefsAnchor)

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
}
