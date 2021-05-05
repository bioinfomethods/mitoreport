<template>
  <span :class="filterActive ? 'filterActive' : ''">
    <v-tooltip top v-if="header.tooltip || filterActive">
      <template v-slot:activator="{ on, attrs }">
        <span v-bind="attrs" v-on="on">{{ header.text }}</span>
      </template>
      <span class="text-caption">{{ header.tooltip }}</span>
      <div class="filteredItemCount" v-if="filterActive">
        <span
          >Active Filter: {{ filteredItemCount }} of
          {{ filteredVariants.length }}</span
        >
      </div>
    </v-tooltip>
    <span v-else>{{ header.text }}</span>
  </span>
</template>

<script>
import { mapState } from 'vuex'
import * as _ from 'lodash'

export default {
  name: 'VariantTableHeader',

  props: {
    header: {
      type: Object,
      required: true,
    },
  },
  computed: {
    ...mapState(['filteredVariants']),

    filterActive() {
      return this.filteredItemCount !== this.filteredVariants.length
    },

    filteredItemCount() {
      if (this.header.filter) {
        const filteredList = this.filteredVariants.filter(variant => {
          if (variant) {
            const value = _.get(variant, this.header.value)
            return this.header.filter(value, null, variant)
          } else {
            return false
          }
        })
        return filteredList.length
      } else {
        return this.filteredVariants.length
      }
    },
  },
}
</script>

<style lang="css" scoped></style>
