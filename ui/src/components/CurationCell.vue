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
        selectedTagNames && selectedTagNames.indexOf(tagName) >= 0
          ? `selected tag ${tagName}`
          : `tag ${tagName}`
      "
      v-for="tagName in getVariantTags.map(d => d.name || d.tag)"
      v-bind:key="tagName"
      @click="toggleTag(tagName)"
    >
      {{ tagName }}
    </span>
    <span
      v-for="tag in readOnlyTags"
      v-bind:key="tag.tag"
      :class="`tag ${tag.tag}`"
      :style="{ backgroundColor: tag.color }"
    >
      {{ tag.tag }}
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
tr.v-data-table__expanded.v-data-table__expanded__row .autoTag {
  display: none;
}
</style>
<script>
import { mapGetters, mapState } from 'vuex'
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
    tagRepo: {
      type: Object,
      required: true,
    },
    tagStore: {
      type: Object,
      required: true,
    },
    notesMaxLength: {
      type: Number,
      default: 80,
    },
    showQuickTags: Boolean,
    expanded: Boolean,
  },

  data: () => {
    return {
      selectedTags: [],
      readOnlyTags: [],
    }
  },

  methods: {
    toggleTag: function(tagName) {
      if (this.showQuickTags || this.expanded) {
        event.stopPropagation()
        var el = d3.select(this.$el).select(`.${tagName}`)

        if (this.selectedTagNames?.indexOf(tagName) >= 0) {
          this.selectedTags = this.selectedTags.filter(
            selected => selected.tag !== tagName
          )
          el.classed('selected', false)
        } else {
          this.selectedTags.push({
            tag: tagName,
            color: 'purple',
            notes: '',
          })
          el.classed('selected', true)
        }
        this.debounceSave()
      }
    },

    debounceSave: _.debounce(async function() {
      for (const tagName of this.allMitoTagNames) {
        this.tagRepo.removeTag(this.variantId, tagName)
      }

      for (const tag of this.selectedTags) {
        await this.tagRepo.saveTag({
          entityName: this.variantId,
          tag: tag.tag || tag.name,
          notes: tag.notes ? tag.notes.trim() : '',
          color: tag.color ? tag.color : 'purple',
          type: 'variant',
        })
      }
    }, DEBOUNCE_DELAY.LONG),

    updateTagsFromTagRepo: function(tagRepo) {
      if (tagRepo && typeof tagRepo.get === 'function') {
        const entity = tagRepo.get(this.variantId)
        const repoTags = entity?.tags
        this.selectedTags = []
        this.readOnlyTags = []

        if (!_.isEmpty(repoTags)) {
          for (const repoTag of Object.values(repoTags)) {
            if (this.allMitoTagNames.includes(repoTag.tag)) {
              this.selectedTags = _.uniqBy(
                this.selectedTags.concat({
                  tag: repoTag.tag,
                  color: repoTag.color,
                  notes: repoTag.notes,
                }),
                'tag'
              )
            } else {
              this.readOnlyTags = _.uniqBy(
                this.readOnlyTags.concat({
                  tag: repoTag.tag,
                  color: repoTag.color,
                  notes: repoTag.notes,
                }),
                'tag'
              )
            }
          }
        }
      }
    },
  },

  mounted() {
    this.updateTagsFromTagRepo(this.tagRepo)
  },

  computed: {
    ...mapGetters(['getVariantById', 'getFirstHaplogroup', 'getVariantTags']),

    ...mapState(['sampleId']),

    variant() {
      return this.getVariantById(this.variantId)
    },

    allMitoTagNames() {
      return this.getVariantTags.map(t => t.name)
    },

    selectedTagNames() {
      return this.selectedTags.map(t => t.tag)
    },

    readOnlyTagNames() {
      return this.readOnlyTags.map(t => t.tag)
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

  watch: {
    tagRepo: {
      handler(updatedTagRepo) {
        this.updateTagsFromTagRepo(updatedTagRepo)
      },
      deep: true,
    },
  },
}
</script>
