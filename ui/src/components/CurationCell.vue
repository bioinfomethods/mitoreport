<template>
  <div>
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
  },
}
</script>
