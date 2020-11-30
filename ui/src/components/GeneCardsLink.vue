<template>
  <span v-if="geneCardsUrl">
    <a id="geneCardsUrlLink" :href="geneCardsUrl" target="_blank">{{ gene }}</a>
  </span>
  <span v-else>{{ gene }}</span>
</template>

<script>
import { mapGetters } from 'vuex'
import { stringifyUrl } from 'query-string'

export default {
  name: 'GeneCardsLink',

  props: {
    gene: {
      type: String,
      required: true,
    },
  },

  computed: {
    ...mapGetters(['getGeneCardsUrlPrefix']),

    geneCardsUrl() {
      if (!this.getGeneCardsUrlPrefix) {
        return null
      }

      const result = stringifyUrl({
        url: this.getGeneCardsUrlPrefix,
        query: {
          gene: this.gene,
        },
        fragmentIdentifier: 'diseases',
      })

      return result
    },
  },
}
</script>
