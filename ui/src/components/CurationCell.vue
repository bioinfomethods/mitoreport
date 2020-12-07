<template>
  <div>
    <v-icon v-if="hasSelectedTags" :color="tagColor">mdi-tag-multiple</v-icon>
    <v-icon v-if="hasNote">mdi-note-text</v-icon>
  </div>
</template>

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
    ]),

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