<template>
  <div class="text-center">
    <v-menu bottom>
      <template v-slot:activator="{ on, attrs }">
        <a v-bind="attrs" v-on="on" @click="toggleVariantExpansion(variant)">
          {{ convertHGVSg(variant, true) }}
        </a>
      </template>

      <v-list style="padding: 5px;">
        <h3>{{ convertHGVSg(variant, true) }}</h3>
        <ul>
          <li v-for="(item, index) in links" :key="index">
            <a :target="createHrefTarget(item.name)" :href="item.link">{{
              item.title
            }}</a>
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
          <li>
            <a
              :target="createHrefTarget('ucsc')"
              :href="this.ucscLink(this.variant, 0)"
            >
              UCSC Genome Browser
            </a>
            -
            <a
              :target="createHrefTarget('ucsc_new_session')"
              :href="this.ucscLink(this.variant, 1)"
            >
              (USCS new session)
            </a>
          </li>
        </ul>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import HmtVarLink from '@/components/HmtVarLink'
import IgvLink from '@/components/IgvLink'
import * as $ from 'jquery'

export default {
  props: {
    variant: {
      type: Object,
      required: true,
    },
    toggleVariantExpansion: { type: Function },
  },
  components: {
    HmtVarLink,
    IgvLink,
  },
  data: () => {
    return {
      mitovisualize: [
        'MT-RNR1',
        'MT-RNR2',
        'MT-TA',
        'MT-TC',
        'MT-TD',
        'MT-TE',
        'MT-TF',
        'MT-TG',
        'MT-TH',
        'MT-TI',
        'MT-TK',
        'MT-TL1',
        'MT-TL2',
        'MT-TM',
        'MT-TN',
        'MT-TP',
        'MT-TQ',
        'MT-TR',
        'MT-TS1',
        'MT-TS2',
        'MT-TT',
        'MT-TV',
        'MT-TW',
        'MT-TY',
      ],
    }
  },
  mounted() {
    this.toggleVariantExpansion
  },
  computed: {
    ...mapState(['sampleId']),
    links() {
      const links = [
        {
          name: 'gnomad_region',
          title: 'gnomAD (Region)',
          link: `https://gnomad.broadinstitute.org/region/M-${this.variant.pos -
            5}-${this.variant.pos + 5}?dataset=gnomad_r3`,
        },
        {
          name: 'gnomad_variant',
          title: 'gnomAD (Variant)',
          link: `https://gnomad.broadinstitute.org/variant/M-${this.variant.pos}-${this.variant.ref}-${this.variant.alt}?dataset=gnomad_r3`,
        },
        {
          name: 'mitomap',
          title: `MITOMAP ${this.variant.pos}`,
          link: `https://mitomap.org/cgi-bin/search_allele?pstns=${this.variant.pos}`,
        },
      ]
      if (
        this.mitovisualize.indexOf(this.variant.locus) >= 0 &&
        this.variant.alleleChange
      ) {
        links.push({
          title: `MitoVisualize ${this.variant.locus} ${this.convertHGVSg(
            this.variant
          )}`,
          link: `https://www.mitovisualize.org/variant/m-${this.variant.pos}-${this.variant.alleleChange}`,
        })
      }
      return links
    },
  },
  methods: {
    createHrefTarget(name) {
      return `mitoreport_${this.sampleId}_${name}`
    },
    ucscLink(variant, ignoreCookie) {
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
        ignoreCookie: ignoreCookie,
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
  },
}
</script>
