<template>
  <v-card>
    <v-card-title>
      chrM:{{ activeVariant.pos }} {{ activeVariant.ref }}/{{
        activeVariant.alt
      }}
    </v-card-title>
    <v-card-text>
      <ul>
        <li>
          <a
            :href="
              `https://gnomad.broadinstitute.org/region/M-${activeVariant.pos -
                5}-${activeVariant.pos + 5}?dataset=gnomad_r3`
            "
            target="mrgnomadvariant"
            >gnomAD (Region)</a
          >
        </li>
        <li>
          <a
            :href="
              `https://gnomad.broadinstitute.org/variant/M-${activeVariant.pos}-${activeVariant.ref}-${activeVariant.alt}?dataset=gnomad_r3`
            "
            target="mrgnomadregion"
            >gnomAD (Variant)</a
          >
        </li>
        <li>
          <HmtVarLink
            :position="activeVariant.pos"
            :refAllele="activeVariant.ref"
            :altAllele="activeVariant.alt"
          ></HmtVarLink>
        </li>
        <li>
          <IgvLink :position="activeVariant.pos"></IgvLink>
        </li>
        <li>
          <a target="_blank" :href="ucscLink(activeVariant)"
            >UCSC Genome Browser</a
          >
        </li>
        <li>
          <a
            :href="
              `https://mitomap.org/cgi-bin/search_allele?starting=${activeVariant.pos}&ending=${activeVariant.pos}`
            "
            target="_blank"
            >MITOMAP {{ activeVariant.pos }}</a
          >
        </li>
      </ul>
    </v-card-text>
  </v-card>
</template>

<script>
import HmtVarLink from '@/components/HmtVarLink'
import IgvLink from '@/components/IgvLink'
import * as $ from 'jquery'

export default {
  props: {
    activeVariant: {
      type: Object,
      required: true,
    },
  },
  components: {
    HmtVarLink,
    IgvLink,
  },
  methods: {
    ucscLink(activeVariant) {
      const queryString = {
        // Original stuff
        db: 'hg38',
        highlight: `chrM:${activeVariant.pos}-${activeVariant.pos}`,
        position: `chrM:${parseInt(activeVariant.pos) - 10}-${parseInt(
          activeVariant.pos
        ) + 10}`,

        // Kat's tracks
        ruler: 'full',
        knownGene: 'pack',
        refSeqComposite: 'pack',
        omimAvSnp: 'full',
        clinvar: 'squish',
        hgmd: 'squish',
        lovdComp: 'squish',
        omimGene2: 'squish',
        cons100way: 'full',
        dbSnp153Composite: 'pack',
        gnomadVariants: 'show',
        gnomadGenomesVariantsV3_1_1: 'pack',
        rmsk: 'dense',

        // Hide all other tracks
        hideTracks: 1,

        // Override the user's previous settings
        ignoreCookie: 1,
      }

      return `http://genome.ucsc.edu/cgi-bin/hgTracks?${$.param(queryString)}`
    },
  },
}
</script>
