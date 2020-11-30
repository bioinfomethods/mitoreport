<template>
  <span v-if="curatedRefUrl">
    <a id="curatedRefLink" :href="curatedRefUrl" target="_blank">{{ count }}</a>
  </span>
  <span id="curatedRefSpan" v-else>{{ count }}</span>
</template>

<script>
import { stringifyUrl } from 'query-string'

export default {
  name: 'CuratedRefLink',

  props: {
    count: {
      type: Number,
      required: false,
    },
    href: {
      type: String,
      required: false,
    },
  },

  computed: {
    curatedRefUrl() {
      if (this.count && this.href) {
        const result = stringifyUrl({
          url: this.href,
        })

        return result
      } else {
        return null
      }
    },
  },
}
</script>
