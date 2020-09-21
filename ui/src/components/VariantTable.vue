<template>
  <v-card>
    <v-card-title>
      <v-text-field
        v-model="search"
        append-icon="search"
        label="Search"
        single-line
        hide-details
      ></v-text-field>
    </v-card-title>
    <v-data-table
      :headers="headers"
      :items="variants"
      :options="tableOptions"
      :footer-props="tableFooterProps"
      :search="search"
      class="elevation-1"
      dense
    >
      <template v-slot:body.prepend>
        <tr>
          <td>
            <v-text-field
              v-model="pos"
              type="string"
              label="lower:upper"
            ></v-text-field>
          </td>
          <td>
            <v-text-field
              v-model="allele"
              type="string"
              label="Contains"
            ></v-text-field>
          </td>
        </tr>
      </template>
    </v-data-table>
  </v-card>
</template>
Ø
<script>
import { mapState } from 'vuex'
import * as _ from 'lodash'

export default {
  name: 'VariantTable',
  props: {},

  data: () => {
    return {
      search: '',
      pos: '',
      allele: '',
      tableOptions: {
        page: 1,
        itemsPerPage: 20,
        sortBy: ['pos'],
        multiSort: true,
      },
      tableFooterProps: {
        itemsPerPageOptions: [5, 10, 20, 50, -1],
      },
    }
  },

  computed: {
    ...mapState(['variants']),

    headers() {
      return [
        {
          text: 'Position',
          align: 'start',
          value: 'pos',
          filter: this.posFilter,
        },
        { text: 'Allele', value: 'alt', filter: this.alleleFilter },
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
        { text: 'HGVS', value: 'HGVS' },
      ]
    },
  },

  methods: {
    posFilter: function(value) {
      if (!this.pos) return true

      return value < parseInt(this.pos)
    },

    alleleFilter: function(value) {
      if (!this.allele) return true

      return _.upperCase(value).includes(_.upperCase(this.allele))
    },
  },
}
</script>

<style scoped></style>
