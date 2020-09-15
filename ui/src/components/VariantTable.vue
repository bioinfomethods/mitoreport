<template>
  <v-data-table
    :headers="headers"
    :items="variants"
    :items-per-page="20"
    class="elevation-1"
    multi-sort
    dense
  >
    <template v-slot:item.alt="{ item }">
      <td>{{ item.ref }}/{{ item.alt }}</td>
    </template>

    <template v-slot:item.hgvsc="{ item }">
      <td v-html="item.hgvsc"></td>
    </template>
  </v-data-table>
</template>

<script>
export default {
  name: 'VariantTable',
  props: {},
  data: () => {
    return {
      variants: window.variants,

      headers: [
        { text: 'Position', align: 'start', value: 'pos' },
        { text: 'Allele', value: 'alt' },
        { text: 'Type', value: 'type' },
        { text: 'Gene', value: 'symbol' },
        { text: 'Consequence', value: 'consequence' },
        { text: 'VAF', value: 'genotypes[0].AF' },
        { text: 'Depth', value: 'genotypes[0].DP' },
        { text: 'Disease', value: 'Disease' },
        { text: 'MitoMap', value: 'Status_MitoMap' },
        { text: 'Curated Refs', value: 'Curated References' },
        { text: 'HGVS.p', value: 'hgvsp' },
        { text: 'HGVS.c', value: 'hgvsc' },
        // { text: 'Effect', value: 'Effect' },
        { text: 'HGVS', value: 'HGVS' },
      ],
    }
  },
}
</script>

<style scoped></style>
