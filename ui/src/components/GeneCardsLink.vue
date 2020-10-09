<template>
  <span v-if="geneCardsUrl">
    <a id="geneCardsUrlLink" :href="geneCardsUrl" target="_blank">{{ gene }}</a>
    <sup><v-icon class="pa-1" size="small">mdi-open-in-new</v-icon></sup>
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
    ...mapGetters(['geneCardsUrlPrefix']),

    geneCardsUrl() {
      if (!this.geneCardsUrlPrefix) {
        return null
      }

      const result = stringifyUrl({
        url: this.geneCardsUrlPrefix,
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
