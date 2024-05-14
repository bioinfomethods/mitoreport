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
    tagRepo: {
      type: Object,
      required: true,
    },
    tagStore: {
      type: Object,
      required: true,
    },
  },

  mounted() {
    this.initTagsFromTagRepo(this.tagRepo)
  },

  data: () => {
    return {
      selectedTags: [],
      readOnlyTags: [],
      variantNote: '',
    }
  },

  computed: {
    ...mapGetters(['getVariantTags', 'getVariantById']),
    variant() {
      return this.getVariantById(this.variantId)
    },
    allMitoTagNames() {
      return this.getVariantTags.map(t => t.name)
    },
    mergedNotes() {
      return this.selectedTags
        .filter(t => t.notes)
        .map(t => t.notes.trim())
        .concat(this.readOnlyTags.filter(t => t.notes).map(t => t.notes.trim()))
        .join('\n')
    },
    hasNote() {
      return !_.isEmpty(this.mergedNotes)
    },
  },

  methods: {
    debounceSave: _.debounce(function() {
      let existingReviewTag = this.selectedTags.find(t => t.name === 'Review')
      if (existingReviewTag) {
        existingReviewTag.notes = this.variantNote
      } else {
        existingReviewTag = {
          name: 'Review',
          color: 'purple',
          notes: this.variantNote,
          type: 'variant',
        }
        this.selectedTags.push(existingReviewTag)
      }
      this.tagRepo.saveTag({
        entityName: this.variantId,
        tag: existingReviewTag.name,
        notes: existingReviewTag.notes ? existingReviewTag.notes.trim() : '',
        color: existingReviewTag.color ? existingReviewTag.color : 'purple',
        type: 'variant',
      })
    }, DEBOUNCE_DELAY.LONG),

    initTagsFromTagRepo: function(tagRepo) {
      if (tagRepo && typeof tagRepo.get === 'function') {
        const entity = tagRepo.get(this.variantId)
        const repoTags = entity?.tags
        if (!_.isEmpty(repoTags)) {
          for (const repoTag of Object.values(repoTags)) {
            if (repoTag.tag === 'Review') {
              this.variantNote = repoTag.notes
            }
            if (this.allMitoTagNames.includes(repoTag.tag)) {
              this.selectedTags = _.uniqBy(
                this.selectedTags.concat({
                  name: repoTag.tag,
                  color: repoTag.color,
                  notes: repoTag.notes,
                }),
                'name'
              )
            } else {
              this.readOnlyTags = _.uniqBy(
                this.readOnlyTags.concat({
                  name: repoTag.tag,
                  color: repoTag.color,
                  notes: repoTag.notes,
                }),
                'name'
              )
            }
          }
        }
      }
    },
  },

  watch: {
    tagRepo: function(updatedTagRepo) {
      this.initTagsFromTagRepo(updatedTagRepo)
    },
  },
}
</script>

<style lang="css" scoped></style>
