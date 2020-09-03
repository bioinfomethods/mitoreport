<template>
<v-card>
    <v-card-text>
      <v-data-table
        :headers="headers"
        :items="variants"
        :items-per-page="20"
        class="elevation-1"
        multi-sort
       dense 
      >
      <template v-slot:item.alt='{ item }'>
          <td>{{item.ref}}/{{item.alt}}</td>
      </template>      

      <template v-slot:item.hgvsc='{ item }'>
          <td v-html='item.hgvsc'></td>
      </template>      


      </v-data-table>
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  name: 'VariantTable',
  props: {
  },
  data: () => { return {
    variants: window.variants,

    headers: [
          { text: 'Position', align: 'start', value: 'pos', },
          { text: 'Allele', value: 'alt' },
          { text: 'Type', value: 'type' },
          { text: 'Gene', value: 'symbol' },
          { text: 'Consequence', value: 'consequence' },
          { text: 'VAF', value: 'genotypes[0].AF' },
          { text: 'Depth', value: 'genotypes[0].DP' },
          { text: 'Disease', value: 'Disease' },
          { text: 'MitoMap', value: 'Status_MitoMap' },
          { text: 'Curated Refs', value: 'Curated References' },
          { text: 'HGVS.p', value: 'hgvsp'},
          { text: 'HGVS.c', value: 'hgvsc'},
          // { text: 'Effect', value: 'Effect' },
          { text: 'HGVS', value: 'HGVS' },
    ]
  }}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
