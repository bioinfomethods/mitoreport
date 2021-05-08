package mitoreport.haplogrep

import core.SampleFile
import core.TestSample
import genepi.haplogrep.util.HgClassifier
import groovy.transform.Canonical
import groovy.transform.MapConstructor
import groovy.util.logging.Slf4j
import importer.VcfImporter
import search.ranking.results.RankedResult
import util.ExportUtils
import vcf.Sample

import java.util.concurrent.Callable

import static java.math.RoundingMode.HALF_EVEN

/**
 * Worker class (thread safe) for classifying Haplogroup of given VCF and Sample.
 * Note that only results of given sampleId are returned for multi-sample VCFs.
 */
@Slf4j
@Canonical(includes = 'vcfFile, sampleId')
@MapConstructor
class HaplogroupClassifier implements Callable<HaplogrepClassification> {

    static final Integer DEFAULT_HAPLOGREP_QUALITY_SCALE = 4
    static final BigDecimal DEFAULT_HAPLOGREP_HET_LEVEL = 0.9 as BigDecimal
    static final String DEFAULT_PHYLOTREE_FILE_NAME = 'phylotree17.xml'
    static final String DEFAULT_PHYLOGENETIC_WEIGHTS_FILE_NAME = 'weights17.txt'
    static final String DEFAULT_RANKING_METHOD = 'kulczynski'
    static final Integer DEFAULT_RESULTS_COUNT = 5
    static final Boolean DEFAULT_FIX_NOMENCLATURE = false

    File vcfFile
    String sampleId

    @Override
    HaplogrepClassification call() throws Exception {
        log.info('Running Haplogrep classification on VCF file={}, sampleId={}', vcfFile?.absolutePath, sampleId)
        log.debug('Running Haplogrep with following params: Heteroplasmy level={}, Phylotree version file={}, Phylotree weights file={}, ranking method={}, results count={}, fix nomenclature={}',
                DEFAULT_HAPLOGREP_HET_LEVEL,
                DEFAULT_PHYLOTREE_FILE_NAME,
                DEFAULT_PHYLOGENETIC_WEIGHTS_FILE_NAME,
                DEFAULT_RANKING_METHOD,
                DEFAULT_RESULTS_COUNT,
                DEFAULT_FIX_NOMENCLATURE,
        )
        VcfImporter vcfImporter = new VcfImporter()
        Map<String, Sample> vcfSamples = vcfImporter.load(vcfFile, false)
        List<String> hsdLines = ExportUtils.vcfTohsd(vcfSamples, DEFAULT_HAPLOGREP_HET_LEVEL)
        SampleFile hsdSampleFile = new SampleFile(hsdLines)

        HgClassifier classifier = new HgClassifier()
        classifier.run(hsdSampleFile, DEFAULT_PHYLOTREE_FILE_NAME, DEFAULT_PHYLOGENETIC_WEIGHTS_FILE_NAME, DEFAULT_RANKING_METHOD, DEFAULT_RESULTS_COUNT, DEFAULT_FIX_NOMENCLATURE)
        TestSample applicTestSample = hsdSampleFile.testSamples.find { sampleId.startsWith(it.sampleID) }

        SortedMap<Integer, HaplogrepResult> haplogrepResults = new TreeMap<>(applicTestSample.getResults().withIndex(1).collectEntries { RankedResult rr, Integer index ->
            [(index): new HaplogrepResult(
                    haplogroup: rr.haplogroup.toString(),
                    rank: index,
                    quality: new BigDecimal(rr.distance).setScale(DEFAULT_HAPLOGREP_QUALITY_SCALE, HALF_EVEN),
            )]
        })

        HaplogrepClassification result = new HaplogrepClassification(this.sampleId, haplogrepResults)

        return result
    }
}
