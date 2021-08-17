<template>
  <span v-if="hmtVarSearchResultsUrl">
    <a
      id="hmtVarSearchResultsUrlLink"
      :href="hmtVarSearchResultsUrl"
      :target="createHrefTarget()"
    >
      HmtVar</a
    >
  </span>
  <span v-else>{{ alleleVariant }}</span>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
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
    ...mapState(['sampleId']),
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

  methods: {
    createHrefTarget() {
      return `mitoreport_${this.sampleId}_hmt`
    },
  },
}
</script>
