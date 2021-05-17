<template>
  <span v-if="igvUrl">
    <a id="igvUrlLink" :href="igvUrl" @click.prevent="loadIGV" target="igv"
      >IGV Link: {{ position }}</a
    >
    <iframe
      ref="igvIframe"
      name="igv"
      src="about:blank"
      style="display: none"
    ></iframe>
  </span>
  <span v-else>{{ position }}</span>
</template>

<script>
import { mapGetters } from 'vuex'
import { stringifyUrl } from 'query-string'

export default {
  name: 'IgvLink',

  props: {
    position: {
      type: Number,
      required: true,
    },
  },

  computed: {
    ...mapGetters(['getIgvHost', 'getSettingsBamFile']),

    igvUrl() {
      if (!this.getIgvHost || !this.getSettingsBamFile) {
        return null
      }

      const result = stringifyUrl({
        url: `${this.getIgvHost}/load`,
        query: {
          file: `file://${this.getSettingsBamFile}`,
          locus: `chrM:${this.position}`,
        },
      })

      return result
    },
  },

  methods: {
    loadIGV() {
      this.$refs.igvIframe.src = this.igvUrl
    },
  },
}
</script>
