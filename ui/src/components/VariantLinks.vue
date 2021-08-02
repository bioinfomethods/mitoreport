<template>
  <div class="text-center">
    <!-- <v-switch v-model="closeOnClick" label="Close on click"></v-switch> -->
    <v-menu bottom>
      <template v-slot:activator="{ on, attrs }">
        <a v-bind="attrs" v-on="on">
          {{ convertHGVSg(variant, true) }}
        </a>
      </template>

      <v-list style="padding: 5px;">
        <h3>{{ convertHGVSg(variant, true) }}</h3>
        <ul>
          <li v-for="(item, index) in links" :key="index">
            <a target="_blank" :href="item.link">{{ item.title }}</a>
          </li>
          <li>
            <HmtVarLink
              :position="variant.pos"
              :refAllele="variant.ref"
              :altAllele="variant.alt"
            ></HmtVarLink>
          </li>
          <li>
            <IgvLink :position="variant.pos"></IgvLink>
          </li>
        </ul>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import HmtVarLink from '@/components/HmtVarLink'
import IgvLink from '@/components/IgvLink'
import * as $ from 'jquery'

export default {
  props: {
    variant: {
      type: Object,
      required: true,
    },
  },
  components: {
    HmtVarLink,
    IgvLink,
  },
  computed: {
    links() {
      return [
        {
          title: 'gnomAD (Region)',
          link: `https://gnomad.broadinstitute.org/region/M-${this.variant.pos -
            5}-${this.variant.pos + 5}?dataset=gnomad_r3`,
        },
        {
          title: 'gnomAD (Variant)',
          link: `https://gnomad.broadinstitute.org/variant/M-${this.variant.pos}-${this.variant.ref}-${this.variant.alt}?dataset=gnomad_r3`,
        },
        {
          title: 'UCSC Genome Browser',
          link: this.ucscLink(this.variant),
        },
        {
          title: `MITOMAP ${this.variant.pos}`,
          link: `https://mitomap.org/cgi-bin/search_allele?starting=${this.variant.pos}&ending=${this.variant.pos}`,
        },
      ]
    },
  },
  methods: {
    ucscLink(variant) {
      const queryString = {
        // Original stuff
        db: 'hg38',
        highlight: `chrM:${variant.pos}-${variant.pos}`,
        position: `chrM:${parseInt(variant.pos) - 10}-${parseInt(variant.pos) +
          10}`,

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
    convertHGVSg: function(item, limited) {
      return (
        item.HGVS ||
        item.id.replace(/chrM-(\d+)-(\w+)-(\w+)/, function(original, a, b, c) {
          if (limited && c.length > 3) {
            c = `${c[0]}…${c[c.length - 1]}`
          }
          return `m.${a}${b}>${c}`
        })
      )
    },
    closeOnClick: function() {},
  },
}
</script>
