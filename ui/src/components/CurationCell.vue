<template>
  <div :class="showQuickTags ? 'showQuickTags' : ''">
    <v-tooltip top>
      <template v-slot:activator="{ on, attrs }">
        <span v-bind="attrs" v-on="on" class="haploWeightIcon">
          <span v-if="hapRatio && hapRatio.hapWeight > 0.5">
            <v-icon>mdi-contrast-box</v-icon></span
          >
        </span></template
      >
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

    <span
      :class="
        curation &&
        curation.selectedTagNames &&
        curation.selectedTagNames.indexOf(tag) >= 0
          ? `selected tag ${tag}`
          : `tag ${tag}`
      "
      v-for="tag in getVariantTags.map(d => d.name)"
      v-bind:key="tag"
      @click="toggleTag(tag)"
    >
      {{ tag }}
    </span>

    <span class="curationCellVariantNote" v-if="hasNote">
      {{
        curation.variantNote.substring(0, 100) +
          (curation.variantNote.length > 100 ? '…' : '')
      }}
      <br />
    </span>

    <span v-if="variant.disease" class="autoTag" :title="variant.disease">
      <v-icon>mdi-biohazard</v-icon>&nbsp;{{ shortDisease }}
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
span.tag {
  border: solid 1px lightgrey;
  display: none;
  color: grey;
  background-color: white;
  margin-right: 3px;
  padding: 2px;
  border-radius: 4px;
  font-size: 10px;
  &.selected {
    border: none;
    display: inline;
    color: white;
    background-color: purple;
  }
}
tr.v-data-table__expanded.v-data-table__expanded__row span.tag,
.showQuickTags span.tag {
  display: inline;
  cursor: pointer;
  &:hover {
    border: solid 1px black;
    color: black;
    background-color: rgba(128, 0, 128, 0.5);
  }
}
tr.v-data-table__expanded.v-data-table__expanded__row .curationCellVariantNote,
tr.v-data-table__expanded.v-data-table__expanded__row .autoTag {
  display: none;
}
</style>
<script>
import { mapGetters } from 'vuex'
import * as _ from 'lodash'
import * as vueFilters from '@/shared/vueFilters'
import { DEBOUNCE_DELAY } from '@/shared/constants'
import d3 from 'd3'

export default {
  name: 'CurationCell',

  props: {
    variantId: {
      type: String,
      required: false,
    },
    showQuickTags: Boolean,
    expanded: Boolean,
  },
  data: () => {
    return {
      selectedTags: [],
    }
  },
  methods: {
    toggleTag: function(tag) {
      if (this.showQuickTags || this.expanded) {
        event.stopPropagation()
        var el = d3.select(this.$el).select(`.${tag}`)

        if (this.curation?.selectedTagNames.indexOf(tag) >= 0) {
          this.selectedTags = this.selectedTags.filter(
            selected => selected.name !== tag
          )
          el.classed('selected', false)
          this.debounceSave()
        } else {
          this.selectedTags.push(this.getVariantTags.find(d => d.name == tag))
          el.classed('selected', true)
          this.debounceSave()
        }
      }
    },

    debounceSave: _.debounce(function() {
      this.$store.dispatch('saveCuration', {
        variantId: this.variantId,
        selectedTags: this.selectedTags,
        // variantNote: this.variantNote,
      })
    }, DEBOUNCE_DELAY.MEDIUM),
  },
  mounted() {
    this.selectedTags = this.getVariantTags.filter(vt => {
      return this.curation?.selectedTagNames?.includes(vt.name)
    })
  },
  computed: {
    ...mapGetters([
      'getCurationByVariantId',
      'getImportantVariantTags',
      'getSampleSettings',
      'getVariantById',
      'getFirstHaplogroup',
      'getVariantTags',
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

    shortDisease() {
      if (!this.variant.disease) return ''

      let result = this.variant.disease
      if (result.length > 30) {
        if (result.split(',').length > 2) {
          result =
            result.split(',')[0] + ` +${result.split(',').length - 1} more`
        } else {
          result = result.substring(0, 30) + '…'
        }
      }
      return result
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
