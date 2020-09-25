<template>
  <span v-if="igvUrl">
    <a :href="igvUrl" @click.prevent="loadIGV" target="igv">{{ position }}</a>
    <sup><v-icon class="pa-1" size="small">mdi-open-in-new</v-icon></sup>
    <iframe
      ref="igvIframe"
      name="igv"
      src="about:blank"
      style="display:none"
    ></iframe>
  </span>
  <span v-else>{{ position }}</span>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'IgvLink',

  props: {
    position: {
      type: Number,
      required: true,
    },
  },

  computed: {
    ...mapGetters(['igvHost', 'bamFile']),

    igvUrl() {
      if (!this.igvHost || !this.bamFile) {
        return null
      }

      const result = `${this.igvHost}/load?file=file://${encodeURIComponent(
        this.bamFile
      )}&locus=chrM:${this.position}`
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
