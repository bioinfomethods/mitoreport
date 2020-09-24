<template>
  <span>
    <a :href="igvUrl" @click.prevent="loadIGV" target="igv">{{ position }}</a>
    <sup><v-icon class="pa-1" size="small">mdi-open-in-new</v-icon></sup>
    <iframe
      ref="igvIframe"
      name="igv"
      src="about:blank"
      style="display:none"
    ></iframe>
  </span>
</template>

<script>
export default {
  name: 'IgvLink',
  props: {
    igvHost: {
      type: String,
      required: true,
    },
    bamFile: {
      type: String,
      required: true,
    },
    position: {
      type: Number,
      required: true,
    },
  },

  computed: {
    igvUrl() {
      return `${this.igvHost}/load?file=${this.bamFile}&locus=chrM:${this.position}`
    },
  },
  methods: {
    loadIGV() {
      this.$refs.igvIframe.src = this.igvUrl
    },
  },
}
</script>
