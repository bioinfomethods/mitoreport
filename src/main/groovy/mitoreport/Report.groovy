package mitoreport

import gngs.*
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import mitoreport.haplogrep.GnomadBaseHaplogroup

/**
 * Creates a comprehensive report for interpreting Mitochondrial variants based on:
 *
 * - output VCF from Broad Mitochondrial analysis pipeline
 * - annotations from VEP
 * - coverage plot computed by DeletionPlot tool
 *
 * @author Simon Sadedin
 */
@Slf4j
class Report extends ToolBase {

    VCF vcf

    VCF gnomAD

    Map deletion

    File variantsResultJson

    MitoMapAnnotationsLoader mitoMapLoader

    final static Set<String> EXCLUDE_CONSEQUENCES = [
            "upstream_gene_variant",
            "downstream_gene_variant",
    ] as Set

    @Override
    public void run() {

        log.info "Loading vcf $opts.vcf"
        vcf = VCF.parse(opts.vcf)

        gnomAD = VCF.parse(opts.gnomad)
        log.info "Loaded ${gnomAD.size()} variants from gnomAD VCF ${opts.vcf}"

        log.info "Loaded ${vcf.size()} variants from $opts.vcf"

        deletion = new JsonSlurper().parse(new File(opts.del))

        log.info "Parsed deletion report $opts.del"

        log.info "Parsing annotations from $opts.ann"

        writeAnnotations(opts.o)
    }

    void writeAnnotations(String dir) {

        Map<String, MitoMapAnnotation> mitoMapAnnotations = (opts.mann && mitoMapLoader)
                ? mitoMapLoader.getAnnotations(opts.mann)
                : Collections.emptyMap() as Map<String, MitoMapAnnotation>
        log.info "Loaded ${mitoMapAnnotations.size()} MitoMap annotations"

        int total = 0
        int gnomADVariantCount = 0

        List results = []

        vcf.each { Variant v ->

            ++total

            Map<String, String> compacted = MitoUtils.getCompactVariantRepresentation(v)
            String compactAllele = compacted.compactAllele

            Map variantInfo = [
                    id     : "${v.chr}-${v.pos}-${v.ref}-${v.alt}",
                    hgvsg  : MitoUtils.extractMitoHgvsg(v),
                    chr    : v.chr,
                    pos    : v.pos,
                    ref    : v.ref,
                    alt    : v.alt,
                    type   : v.type,
                    qual   : v.qual,
                    dosages: v.getDosages()
            ]

            Map vepInfo = extractMitoVEPInfo(v)
            Map<String, Object> mitoMapAnnotation = (mitoMapAnnotations.getOrDefault(compactAllele, null)?.toMap() ?: [:]) as Map<String, Object>

            // Keep annotations file backwards compatible with older UI, remove this if and else
            // clause once UI is updated to use newly named properties instead
            if (mitoMapAnnotation) {
                mitoMapAnnotation.put('Status_MitoMap', mitoMapAnnotation.getOrDefault('diseaseStatus', null))
                mitoMapAnnotation.put('HGVS', mitoMapAnnotation.getOrDefault('hgvs', null))
                mitoMapAnnotation.put('Disease', mitoMapAnnotation.getOrDefault('disease', null))
            } else {
                mitoMapAnnotation.put('curatedRef', ['count': 0, 'url': null])
            }

            Map infoField = v.parsedInfo
            infoField.remove('ANN')

            // If there is a locus annotation from mito map, we always prefer that
            if (mitoMapAnnotation && mitoMapAnnotation.locus) {
                vepInfo.symbol = mitoMapAnnotation.locus
                vepInfo.symbols = vepInfo.symbol.split(", ")
            }
            // else stick with what VEP put there

            Map resultInfo = variantInfo + vepInfo + mitoMapAnnotation + infoField + [genotypes: v.parsedGenotypes]

            Variant gnomADVariant = gnomAD.find(v)
            if (gnomADVariant) {
                MitoGnomAD gnomADInfo = MitoGnomAD.parse(gnomADVariant.info)
                gnomADInfo.metaClass.getHap_ac_het_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_ac_het)
                }
                gnomADInfo.metaClass.getHap_ac_hom_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_ac_hom)
                }
                gnomADInfo.metaClass.getHap_af_het_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_af_het)
                }
                gnomADInfo.metaClass.getHap_af_hom_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_af_hom)
                }
                gnomADInfo.metaClass.getHap_an_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_an)
                }
                gnomADInfo.metaClass.getHap_faf_hom_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_faf_hom)
                }
                gnomADInfo.metaClass.getHap_hl_hist_map << { ->
                    GnomadBaseHaplogroup.mapToBaseHaplogroup(gnomADInfo.hap_hl_hist)
                }
                resultInfo.gnomAD = gnomADInfo
                // native object serializes to JSON OK including metaClass properties

                ++gnomADVariantCount
            } else {
                resultInfo.gnomAD = null
            }

            results << resultInfo
        }

        String json = JsonOutput.prettyPrint(JsonOutput.toJson(results))

        File dirFile = new File(dir)
        if (!dirFile.exists()) {
            log.info "Creating directory $dir"
            dirFile.mkdirs()
        }

        variantsResultJson = new File("$dir/variants.json")
        Utils.writer(variantsResultJson).withWriter { it << json; it << '\n' }

        log.info "Annotated ${gnomADVariantCount} variants with gnomAD annotations (${Utils.perc(gnomADVariantCount / (total + 1))})"
        log.info "Wrote annotated variants to $variantsResultJson"
    }

    /**
     * Extract relevant VEP information for a mtDNA variant
     * <p>
     * This process filters out annotations that are less interpretable for mtDNA variants,
     * for example, we prefer not to annotate with 'upstream' or 'downstream' since the variant is
     * not actually inside the gene of interest, and by VEP's interpretation of upstream and downstream,
     * every mtDNA variant would be upstream or downstream of every gene in the mtDNA.
     *
     * @param v
     * @return
     */
    private Map extractMitoVEPInfo(Variant v) {

        Map vep = v.maxVep
        if (vep.Consequence in EXCLUDE_CONSEQUENCES) {
            return [
                symbol : null,
                symbols : null,
                consequence: null,
                hgvsp : null,
                hgvsc : null
            ]
        }

        Map vepInfo = [
            symbol     : vep.SYMBOL,
            symbols    : vep.SYMBOL?.split(", "),
            consequence: VepConsequence.fromTerm(vep.Consequence).toMap(),
            hgvsp      : URLDecoder.decode(vep.HGVSp?.replaceAll('^.*:', ''), "UTF-8"),
            hgvsc      : vep.HGVSc?.replaceAll('^.*:', '')
        ]

        return vepInfo
    }


    static void main(String[] args) {
        cli('Report -o <report dir> -vcf <vcf> [-vcf <vcf2>]... -del <del> [-del <del2>]...', 'Mitochondrial Analysis Report', args) {
            o 'Directory to save report assets to', args: 1, required: true
            vcf 'VCF file for sample', args: Cli.UNLIMITED, required: true
            del 'Deletion analysis for sample', args: Cli.UNLIMITED, required: true
            ann 'Annotation file to apply to VCF', args: 1, required: true
            mann 'MitoMap Annotations file in JSON format (see command to download)', args: 1, required: true
            gnomad 'gnomAD mitochondrial VCF file', args: 1, required: true
        }
    }
}
