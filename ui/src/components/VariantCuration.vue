<template>
  <v-form>
    <v-textarea
      v-model="variantNote"
      @input="debounceSave"
      auto-grow
      label="Notes"
      outlined
      dense
    ></v-textarea>

    <table class="curationDetailsTable" v-if="variant.disease">
      <tbody>
        <tr>
          <td><v-icon>mdi-biohazard</v-icon> Disease:</td>
          <td>{{ variant.disease }}</td>
        </tr>
      </tbody>
    </table>
  </v-form>
</template>

<style lang="scss">
table.curationDetailsTable td {
  vertical-align: top;
}
</style>
<script>
import { mapGetters } from 'vuex'
import * as _ from 'lodash'
import { DEBOUNCE_DELAY } from '@/shared/constants'

export default {
  name: 'VariantCuration',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  mounted() {
    this.selectedTags = this.getVariantTags.filter(vt => {
      return this.curation?.selectedTagNames?.includes(vt.name)
    })
    this.variantNote = this.curation.variantNote
  },

  data: () => {
    return {
      selectedTags: [],
      variantNote: '',
    }
  },

  computed: {
    ...mapGetters([
      'getVariantTags',
      'getVariantById',
      'getCurationByVariantId',
    ]),
    variant() {
      return this.getVariantById(this.variantId)
    },
    curation() {
      return this.getCurationByVariantId(this.variantId)
    },
  },

  methods: {
    removeSelectedTag: function(toRemove) {
      this.selectedTags = this.selectedTags.filter(
        selected => selected !== toRemove
      )
      this.debounceSave()
    },

    debounceSave: _.debounce(function() {
      this.$store.dispatch('saveCuration', {
        variantId: this.variantId,
        variantNote: this.variantNote,
      })
    }, DEBOUNCE_DELAY.MEDIUM),
  },
}
</script>

<style lang="css" scoped></style>
