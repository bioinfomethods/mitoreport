package mitoreport

import core.SampleFile
import core.TestSample
import genepi.haplogrep.util.HgClassifier
import groovy.transform.Canonical
import groovy.transform.TupleConstructor
import importer.VcfImporter
import util.ExportUtils
import vcf.Sample

import java.util.concurrent.Callable

@Canonical(includes = 'vcfFile, sampleId')
@TupleConstructor(includes = 'vcfFile, sampleId')
class HaplogroupClassifier implements Callable<HaplogroupResult> {

    static final BigDecimal DEFAULT_HAPLOGREP_HET_LEVEL = 0.9
    static final String DEFAULT_PHYLOTREE_FILE_NAME = 'phylotree17.xml'
    static final String DEFAULT_PHYLOGENETIC_WEIGHTS_FILE_NAME = 'weights17.txt'
    static final String DEFAULT_RANKING_METHOD = 'kulczynski'
    static final Integer DEFAULT_RESULTS_COUNT = 1
    static final Boolean DEFAULT_FIX_NOMENCLATURE = false

    File vcfFile
    String sampleId

    @Override
    HaplogroupResult call() throws Exception {
        VcfImporter vcfImporter = new VcfImporter()
        Map<String, Sample> vcfSamples = vcfImporter.load(vcfFile, false)
        List<String> hsdLines = ExportUtils.vcfTohsd(vcfSamples, DEFAULT_HAPLOGREP_HET_LEVEL)
        SampleFile hsdSampleFile = new SampleFile(hsdLines)

        HgClassifier classifier = new HgClassifier()
        classifier.run(hsdSampleFile, DEFAULT_PHYLOTREE_FILE_NAME, DEFAULT_PHYLOGENETIC_WEIGHTS_FILE_NAME, DEFAULT_RANKING_METHOD, DEFAULT_RESULTS_COUNT, DEFAULT_FIX_NOMENCLATURE)
        TestSample applicTestSample = hsdSampleFile.testSamples.find { sampleId.startsWith(it.sampleID) }

        return null
    }
}
