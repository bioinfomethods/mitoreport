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
import { state } from '@/store'
import { tag_state } from '@/store'
import { mapGetters } from 'vuex'
import * as _ from 'lodash'
import { DEBOUNCE_DELAY } from '@/shared/constants'
import { TagRepository, RepositoryEntities } from 'tagmesh'

export default {
  name: 'VariantCuration',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  data: () => {
    return {

      variantNote : '',

      tag_state,

      state,
    }
  },
  
  mounted: function() {
    console.log('calling loadVariantNote from ', this)
    this.loadVariantNote()
  },
  
  watch: {
    storedTags() {
        this.loadVariantNote()
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
    
    /**
     * @returns {TagRepository}
     */
    tagRepository() {
        return this.tag_state.tags
    },

    storedTags() {

        let tagRepository = this.tagRepository
        
        if(!tagRepository)
            return {}

        return tagRepository.get(this.variantId).tags
    },
  },

  methods: {
    debounceSave: _.debounce(function() {

        /**@type {TagRepository} */
        this.tagRepository.saveTag({ entityName : this.variantId, tag: 'Notes', notes: this.variantNote, color: 'none' })

    }, DEBOUNCE_DELAY.MEDIUM),
    
    loadVariantNote() {
        console.log("Loading variant notes from: ", this.storedTags)
        this.variantNote = this.storedTags['Notes']?.notes
    }
  },
}
</script>

<style lang="css" scoped></style>
