package mitoreport

import mitoreport.haplogrep.HaplogrepResult

class TestUtils {

    static HaplogrepResult hr(String haplogroup, Integer rank, BigDecimal quality = 0.99) {
        return new HaplogrepResult(haplogroup, rank, quality)
    }
}
