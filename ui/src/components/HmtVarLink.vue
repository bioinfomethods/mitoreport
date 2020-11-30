<template>
  <span v-if="hmtVarSearchResultsUrl">
    <a
      id="hmtVarSearchResultsUrlLink"
      :href="hmtVarSearchResultsUrl"
      target="_blank"
    >
      HmtVar</a
    >
  </span>
  <span v-else>{{ alleleVariant }}</span>
</template>

<script>
import { mapGetters } from 'vuex'
import { stringifyUrl } from 'query-string'

export default {
  name: 'HmtVarLink',

  props: {
    position: {
      type: Number,
      required: true,
    },
    refAllele: {
      type: String,
      required: true,
    },
    altAllele: {
      type: String,
      required: true,
    },
    includeAltInSearch: {
      type: Boolean,
      required: false,
      default: false,
    },
  },

  computed: {
    ...mapGetters(['getHmtVarUrlPrefix']),

    hmtVarSearchResultsUrl() {
      if (!this.getHmtVarUrlPrefix) {
        return null
      }

      const mutation = this.includeAltInSearch
        ? `${this.refAllele}${this.position}${this.altAllele}`
        : `${this.refAllele}${this.position}`

      const result = stringifyUrl({
        url: this.getHmtVarUrlPrefix,
        query: {
          mutation: mutation,
        },
      })

      return result
    },

    alleleVariant() {
      return `${this.refAllele}/${this.altAllele}`
    },
  },
}
</script>
