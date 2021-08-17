<template>
  <div>
    <span v-if="geneCardsUrl">
      <a
        id="geneCardsUrlLink"
        :href="geneCardsUrl"
        :target="createHrefTarget()"
        >{{ gene }}</a
      >
    </span>
    <span v-else>{{ gene }}</span>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
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
    ...mapState(['sampleId']),
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

  methods: {
    createHrefTarget() {
      return `mitoreport_${this.sampleId}_genecards`
    },
  },
}
</script>
