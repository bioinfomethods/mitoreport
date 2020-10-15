<template>
  <v-form>
    <v-select
      id="inputSelectVariantTags"
      v-model="selectedVariantTags"
      :items="getVariantTags"
      item-text=".name"
      item-value=".name"
      type="text"
      label="Tags"
      return-object
      multiple
      dense
    >
      <template v-slot:selection="{ item, index }">
        <v-chip
          v-if="index <= 3"
          close
          @click:close="removeSelectedTag(item)"
          x-small
        >
          <span>{{ item.name }}</span>
        </v-chip>
        <span v-if="index === 4" class="grey--text caption"
          >(+{{ getVariantTags.length - 4 }} others)</span
        >
      </template>
    </v-select>
    <span>Variant Curation for {{ curation }}</span>
  </v-form>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'VariantCuration',

  props: {
    curation: {
      type: Object,
      required: false,
    },
  },

  data: () => {
    return {
      selectedVariantTags: [],
    }
  },

  computed: {
    ...mapGetters(['getVariantTags', 'getCurationByVariantId']),
  },

  mounted() {
    console.log(
      `VariantCuration route=${JSON.stringify(
        this.$route.path
      )},params=${JSON.stringify(this.$route.params)},curation=${this.curation}`
    )
  },
}
</script>
