<template>
  <div>
    <v-tooltip top>
      <template v-slot:activator="{ on, attrs }">
        <span v-bind="attrs" v-on="on" class="haploWeightIcon">
          <span v-if="hapRatio && hapRatio.hapWeight > 1">
            <v-icon>mdi-contrast-box</v-icon></span
          >
        </span>
      </template>
      <span class="text-caption">
        <span
          >This variant has a high discordance between the observed "global"
          vs<br />
          "haplogroup" gnomAD Heteroplasmy frequency and/or Homoplasmy
          frequency.<br />
          Weight: {{ hapRatio.hapWeight | precisionTo }}</span
        >
      </span>
    </v-tooltip>

    <span v-if="variant.Disease" class="autoTag">
      <v-icon>mdi-biohazard</v-icon>&nbsp;{{ variant.Disease }}
    </span>

    <span v-if="hasSelectedTags">
      <v-tooltip top>
        <template v-slot:activator="{ on, attrs }">
          <span v-bind="attrs" v-on="on">
            <v-icon v-if="hasSelectedTags" :color="tagColor"
              >mdi-tag-multiple</v-icon
            >
          </span>
        </template>
        <span class="text-caption">
          {{ curation.selectedTagNames.join(', ') }}
        </span>
      </v-tooltip>
    </span>

    <span v-if="hasNote">
      <v-icon v-if="hasNote">mdi-note-text</v-icon>
      {{
        curation.variantNote.substring(0, 60) +
          (curation.variantNote.length > 60 ? '…' : '')
      }}
    </span>
  </div>
</template>
<style lang="scss">
span.autoTag {
  border: 1px solid grey;
  line-height: 1.5em;
  padding: 2px;
  border-radius: 5px;
  white-space: nowrap;
  background: #fafafafa;
  .v-icon {
    top: -2px;
    font-size: 18px;
  }
}
</style>
<script>
import { mapGetters } from 'vuex'
import * as _ from 'lodash'
import * as vueFilters from '@/shared/vueFilters'

export default {
  name: 'CurationCell',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  computed: {
    ...mapGetters([
      'getCurationByVariantId',
      'getImportantVariantTags',
      'getSampleSettings',
      'getVariantById',
      'getFirstHaplogroup',
    ]),

    variant() {
      return this.getVariantById(this.variantId)
    },

    curation() {
      return this.getCurationByVariantId(this.variantId)
    },

    hasNote() {
      return !_.isEmpty(this.curation.variantNote)
    },

    hasSelectedTags() {
      return !_.isEmpty(this.curation.selectedTagNames)
    },

    tagColor() {
      const hasImportantTag = this.getImportantVariantTags.some(impTag =>
        (this.curation.selectedTagNames || []).includes(impTag.name)
      )

      return hasImportantTag ? 'red' : ''
    },

    hapRatio() {
      var variant = this.variant
      var map = {}
      if (variant.gnomAD) {
        map = {
          hapWeight: 0,
        }
        if (
          variant.gnomAD.af_het &&
          variant.gnomAD.hap_af_het_map[this.getFirstHaplogroup]
        ) {
          let hetRatio = (map.hetRatio =
            variant.gnomAD.hap_af_het_map[this.getFirstHaplogroup] /
            variant.gnomAD.af_het)

          map.hapWeight = Math.log(hetRatio) * Math.log(hetRatio)
        }
        if (
          variant.gnomAD.af_hom &&
          variant.gnomAD.hap_af_hom_map[this.getFirstHaplogroup]
        ) {
          let homRatio = (map.homRatio =
            variant.gnomAD.hap_af_hom_map[this.getFirstHaplogroup] /
            variant.gnomAD.af_hom)
          map.hapWeight += Math.log(homRatio) * Math.log(homRatio)
        }
      }
      return map
    },
  },

  filters: {
    precisionTo: vueFilters.precisionTo,
  },
}
</script>
