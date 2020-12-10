<template>
  <span>
    <v-tooltip top v-if="header.tooltip">
      <template v-slot:activator="{ on, attrs }">
        <span v-bind="attrs" v-on="on">{{ header.text }}</span>
      </template>
      <span class="text-caption">{{ header.tooltip }}</span>
    </v-tooltip>
    <span v-else>{{ header.text }}</span>
  </span>
</template>

<script>
export default {
  name: 'VariantTableHeader',

  props: {
    header: {
      type: Object,
      required: true,
    },
  },
}
</script>

<style lang="css" scoped></style>
