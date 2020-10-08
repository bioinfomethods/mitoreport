<template>
  <span v-if="igvUrl">
    <a id="igvUrlLink" :href="igvUrl" @click.prevent="loadIGV" target="igv">{{
      position
    }}</a>
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
    ...mapGetters(['igvHost', 'settingsBamFile']),

    igvUrl() {
      if (!this.igvHost || !this.settingsBamFile) {
        return null
      }

      const result = stringifyUrl({
        url: `${this.igvHost}/load`,
        query: {
          file: `file://${this.settingsBamFile}`,
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
