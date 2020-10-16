<template>
  <v-form>
    <v-select
      id="inputSelectVariantTags"
      v-model="selectedTags"
      :items="getVariantTags"
      item-text=".name"
      item-value=".name"
      @change="debounceSave"
      type="text"
      label="Select tags"
      return-object
      multiple
      dense
    >
      <template v-slot:selection="{ item, index }">
        <v-chip
          v-if="index <= 6"
          close
          @click:close="removeSelectedTag(item)"
          x-small
        >
          <span>{{ item.name }}</span>
        </v-chip>
        <span v-if="index === 7" class="grey--text caption"
          >(+{{ filterConfig.selectedGenes.length - 4 }} others)</span
        >
      </template>
    </v-select>
    <v-textarea
      v-model="variantNote"
      @input="debounceSave"
      auto-grow
      label="Notes"
      outlined
      dense
    ></v-textarea>
  </v-form>
</template>

<script>
import { mapGetters } from 'vuex'
import * as _ from 'lodash'
import { DEBOUNCE_DELAY_MS } from '@/shared/constants'

export default {
  name: 'VariantCuration',

  props: {
    curation: {
      type: Object,
      required: false,
    },
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
      variantNote: 'Lorem ipsum',
    }
  },

  computed: {
    ...mapGetters(['getVariantTags', 'getCurationByVariantId']),
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
        selectedTags: this.selectedTags,
        variantNote: this.variantNote,
      })
    }, DEBOUNCE_DELAY_MS.MEDIUM),
  },
}
</script>
