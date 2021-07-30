package mitoreport

import gngs.Variant

class MitoUtils {

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

    static String extractMitoHgvsg(Variant variant) {
        String chr = variant.chr
        int pos = variant.pos
        String ref = variant.ref
        String alt = variant.alt

        if (!chr || !(chr.toUpperCase() in ['CHRM']) || !pos || !ref | !alt) {
            return null
        } else {
            alt == 'del' ? "m.${pos}${ref}del" : "m.${pos}${ref}>${alt}"
        }
    }
}
