package mitoreport

import gngs.*
import graxxia.CSV
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Log

@Log
class Report extends ToolBase {
    
    VCF vcf
    
    Map deletion

    @Override
    public void run() {
        
        log.info "Loading vcf $opts.vcf"
        vcf = VCF.parse(opts.vcf)
        
        log.info "Loaded ${vcf.size()} variants from $opts.vcf"
        
        deletion = new JsonSlurper().parse(new File(opts.del))
        
        log.info "Parsed deletion report $opts.del"
        
        log.info "Parsing annotations from $opts.ann"
        
        writeAnnotations(opts.o)
    }
    
    void writeAnnotations(String dir) {
        
        log.info "Loading annotations from $opts.ann"
        Map<String,Map> annotations = new CSV(opts.ann).toListMap().collectEntries { [it.Allele, it ] }
        
        List results = []
        
        vcf.each { Variant v ->
            
            int sampleIndex = 0
            
            String compactAllele = v.ref.toUpperCase() + v.pos + v.alt.toUpperCase()
            
            Map variantInfo = [
                chr : v.chr,
                pos: v.pos,
                ref: v.ref,
                alt : v.alt, 
                type: v.type,
                qual: v.qual,
                dosages: v.getDosages()
            ]
            
            Map variantAnnotations = 
                annotations.getOrDefault(compactAllele, [:]).collectEntries { key, value ->
                    def result = value
                    if(value instanceof String) 
                        result = value.tokenize('|+').grep { it != 'NA' }*.trim().join(', ')

                    [key, result]
                }

            
            results << variantInfo + variantAnnotations + v.parsedInfo + [genotypes: v.parsedGenotypes]
        }
        
        String json = JsonOutput.prettyPrint(JsonOutput.toJson(results))
        
        File dirFile = new File(dir)
        if(!dirFile.exists()) {
            log.info "Creating directory $dir"
            dirFile.mkdirs()
        }
        
        def outputPath = "$dir/variants.js"
        Utils.writer(outputPath).withWriter {  it << "window.variants = " + json; it << '\n' }
        
        log.info "Wrote annotated variants to $outputPath"
    }
    
   
    static void main(String[] args) {
        cli('Report -o <report dir> -vcf <vcf> [-vcf <vcf2>]... -del <del> [-del <del2>]...', 'Mitochondrial Analysis Report', args) {
            o 'Directory to save report assets to', args:1, required: true
            vcf 'VCF file for sample', args: Cli.UNLIMITED, required: true
            del 'Deletion analysis for sample', args: Cli.UNLIMITED, required: true
            ann 'Annotation file to apply to VCF', args: 1, required: true
        }
    }
}
