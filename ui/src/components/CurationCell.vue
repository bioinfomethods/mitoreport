<template>
  <div>
    <v-icon v-show="hasSelectedTags">mdi-tag-multiple</v-icon>
    <v-icon v-show="hasNote">mdi-note-text</v-icon>
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
    ...mapGetters(['getCurationByVariantId']),

    curation() {
      return this.getCurationByVariantId(this.variantId)
    },

    hasNote() {
      return !_.isEmpty(this.curation.variantNote)
    },

    hasSelectedTags() {
      return !_.isEmpty(this.curation.selectedTagNames)
    },
  },
}
</script>
