package mitoreport

import gngs.*
import graxxia.CSV
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import static gngs.VEPConsequences.RANKED_CONSEQUENCES

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

    MitoMapPolymorphismsLoader mitoMapLoader

    /**
     * Index of consequences, ordered by severity
     */
    List<Map> consequencesWithRank = RANKED_CONSEQUENCES.withIndex(1).collect { String consequence, Integer index -> [id: consequence, name: consequence, rank: index] }
    
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

        log.info "Loading annotations from $opts.ann"
        Map<String, Map> annotations = new CSV(opts.ann).toListMap().collectEntries { [it.Allele, it] }
        log.info "Loaded ${annotations.size()} functional annotations"

        List<MitoMapPolymorphismAnnotation> mitoMapAnnotations = (opts.mann && mitoMapLoader) ? mitoMapLoader.getAnnotations(opts.mann) : []
        log.info "Loaded ${mitoMapAnnotations.size()} MitoMap annotations"
        
        int total = 0
        int gnomADVariantCount = 0

        List results = []

        vcf.each { Variant v ->
            
            ++total

            int sampleIndex = 0

            Map<String, String> compacted = getCompactVariantRepresentation(v)
            String compactAllele = compacted.compactAllele

            Map variantInfo = [
                    id     : "${v.chr}-${v.pos}-${v.ref}-${v.alt}",
                    chr    : v.chr,
                    pos    : v.pos,
                    ref    : v.ref,
                    alt    : v.alt,
                    type   : v.type,
                    qual   : v.qual,
                    dosages: v.getDosages()
            ]

            Map vepInfo = extractMitoVEPInfo(v)

            Map variantAnnotations =
                    annotations.getOrDefault(compactAllele, [:]).collectEntries { key, value ->
                        def result = value
                        if (value instanceof String)
                            result = value.tokenize('|+').grep { it != 'NA' }*.trim().join(', ')

                        [key, result]
                    }

            MitoMapPolymorphismAnnotation mitoAnnotation = mitoMapAnnotations.find { it.compactAllele == compactAllele }
            variantAnnotations.gbFreqPct = mitoAnnotation?.gbFreqPct ?: 0.0
            variantAnnotations.gbFreq = mitoAnnotation?.gbFreq ?: 0.0
            variantAnnotations.curatedRef = mitoAnnotation ? [
                    'count': mitoAnnotation?.curatedRefsCount ?: 0,
                    'url'  : mitoAnnotation?.curatedRefsUrl,
            ] : Collections.emptyMap()

            Map infoField = v.parsedInfo
            infoField.remove('ANN')
            
            // If there is a locus annotation from mito map, we always prefer that
            if(mitoAnnotation && mitoAnnotation.locus) {
                vepInfo.symbol = mitoAnnotation.locus
            }
            else 
            if(variantAnnotations && variantAnnotations.Locus) {
                vepInfo.symbol = variantAnnotations.Locus
            }
            // else stick with what VEP put there
            
            Map resultInfo = variantInfo + vepInfo + variantAnnotations + infoField + [genotypes: v.parsedGenotypes]
            
            Variant gnomADVariant = gnomAD.find(v)
            if(gnomADVariant) {
                MitoGnomAD gnomADInfo = MitoGnomAD.parse(gnomADVariant.info)
                
                resultInfo.gnomAD = gnomADInfo // native object serializes to JSON OK

                ++gnomADVariantCount
            }
            else {
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

        log.info "Annotated ${gnomADVariantCount} variants with gnomAD annotations (${Utils.perc(gnomADVariantCount/(total+1))})"
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
        if(vep.Consequence in EXCLUDE_CONSEQUENCES) {
            return [
                symbol : null,
                consequence: null,
                hgvsp : null,
                hgvsc : null
            ]
        }

        Map vepInfo = [
            symbol     : vep.SYMBOL,
            consequence: consequencesWithRank.find { it.id == vep.Consequence },
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

    static Map<String, String> getCompactVariantRepresentation(Variant variant) {
        if (!variant) {
            return Collections.emptyMap()
        }

        if ('DEL' == variant?.type?.toUpperCase()) {
            return [
                    'compactAllele': "${variant.ref.toUpperCase()}${variant.pos}del",
                    'change'       : "${variant.ref.toUpperCase()}-del"
            ]
        } else {
            return [
                    'compactAllele': "${variant.ref.toUpperCase()}${variant.pos}${variant.alt.toUpperCase()}",
                    'change'       : "${variant.ref.toUpperCase()}-${variant.alt.toUpperCase()}"
            ]
        }
    }
}
