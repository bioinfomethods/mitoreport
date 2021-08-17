<template>
  <span v-if="igvUrl">
    <a
      id="igvUrlLink"
      :href="igvUrl"
      @click.prevent="loadIGV"
      :target="createHrefTarget()"
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
import { mapState, mapGetters } from 'vuex'
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
    ...mapState(['sampleId']),
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
    createHrefTarget() {
      return `mitoreport_${this.sampleId}_igv`
    },
  },
}
</script>
