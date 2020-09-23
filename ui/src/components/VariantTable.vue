<template>
  <v-card>
    <v-card-title></v-card-title>
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
            <!-- <v-text-field
              v-model="pos"
              type="text"
              label="lower-upper"
            ></v-text-field> -->
            <v-row class="px-4 justify-space-between">
              <span>{{ posRange[0] }}</span>
              <span>{{ posRange[1] }}</span>
            </v-row>
            <v-range-slider
              v-model="posRange"
              :min="0"
              :max="16500"
              step="100"
              hide-details
            >
            </v-range-slider>
          </td>
          <td>
            <v-text-field
              v-model="allele"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
          <td>
            <v-select
              v-model="selectedTypes"
              :items="types"
              type="text"
              chips
              label="Select"
              multiple
              deletable-chips
              small-chips
            ></v-select>
          </td>
          <td>
            <v-select
              v-model="selectedGenes"
              :items="genes"
              type="text"
              label="Select"
              multiple
            >
              <template v-slot:selection="{ item, index }">
                <v-chip
                  v-if="index <= 3"
                  close
                  @click:close="removeSelectedGene(item)"
                  x-small
                >
                  <span>{{ item }}</span>
                </v-chip>
                <span v-if="index === 4" class="grey--text caption"
                  >(+{{ selectedGenes.length - 4 }} others)</span
                >
              </template>
            </v-select>
          </td>
          <td>
            <v-select
              v-model="selectedConsequences"
              :items="consequences"
              item-text=".name"
              item-value=".name"
              return-object
              type="text"
              label="Select"
              multiple
            >
              <template v-slot:selection="{ item, index }">
                <v-chip
                  v-if="index <= 3"
                  close
                  @click:close="removeSelectedConsequence(item)"
                  x-small
                >
                  <span>{{ item.name }}</span>
                </v-chip>
                <span v-if="index === 4" class="grey--text caption"
                  >(+{{ selectedConsequences.length - 4 }} others)</span
                >
              </template>
            </v-select>
          </td>
          <td>
            <v-row class="px-4 justify-space-between">
              <span>{{ vafRange[0] }}</span>
              <span>{{ vafRange[1] }}</span>
            </v-row>
            <v-range-slider
              v-model="vafRange"
              :min="0"
              :max="1"
              step="0.01"
              hide-details
            >
            </v-range-slider>
          </td>
          <td>
            <v-row class="px-4 justify-space-between">
              <span>{{ depthRange[0] }}</span>
              <span>{{ depthRange[1] }}</span>
            </v-row>
            <v-range-slider
              v-model="depthRange"
              :min="0"
              :max="10000"
              step="100"
              hide-details
            >
            </v-range-slider>
          </td>
          <td>
            <v-text-field
              v-model="disease"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
          <td>
            <v-text-field
              v-model="mitoMap"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
          <td>
            <v-text-field
              v-model="curatedRefs"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
          <td>
            <v-text-field
              v-model="hgvsp"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
          <td>
            <v-text-field
              v-model="hgvsc"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
          <td>
            <v-text-field
              v-model="hgvs"
              type="text"
              label="Contains"
            ></v-text-field>
          </td>
        </tr>
      </template>
      <template v-slot:item.consequence="{ item }">
        <td>{{ item.consequence.name }}</td>
      </template>
      <template v-slot:item.hgvsc="{ item }">
        <td v-html="item.hgvsc"></td>
      </template>
    </v-data-table>
  </v-card>
</template>

<script>
import { mapState } from 'vuex'
import * as filters from '@/shared/variantFilters'

export default {
  name: 'VariantTable',
  props: {},

  data: () => {
    return {
      search: '',
      pos: '',
      posRange: [0, 16500],
      allele: '',
      selectedTypes: [],
      selectedGenes: [],
      selectedConsequences: [],
      vafRange: [0, 1],
      depthRange: [0, 10000],
      disease: '',
      mitoMap: '',
      curatedRefs: '',
      hgvsp: '',
      hgvsc: '',
      hgvs: '',
      tableOptions: {
        page: 1,
        itemsPerPage: 20,
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
          width: '150',
          filter: this.posFilter,
        },
        {
          text: 'Allele',
          value: 'ref_alt',
          width: '180',
          filter: this.alleleFilter,
        },
        { text: 'Type', value: 'type', width: '200', filter: this.typesFilter },
        {
          text: 'Gene',
          value: 'symbol',
          width: '250',
          filter: this.genesFilter,
        },
        {
          text: 'Consequence',
          value: 'consequence',
          width: '250',
          sort: this.consequenceSort,
          filter: this.consequencesFilter,
        },
        {
          text: 'VAF',
          value: 'genotypes[0].AF',
          width: '120',
          filter: this.vafFilter,
        },
        {
          text: 'Depth',
          value: 'genotypes[0].DP',
          width: '120',
          filter: this.depthFilter,
        },
        {
          text: 'Disease',
          value: 'Disease',
          width: '150',
          filter: this.diseaseFilter,
        },
        {
          text: 'MitoMap',
          value: 'Status_MitoMap',
          width: '150',
          filter: this.mitoMapFilter,
        },
        {
          text: 'Curated Refs',
          value: 'Curated References',
          width: '100',
          filter: this.curatedRefsFilter,
        },
        {
          text: 'HGVS.p',
          value: 'hgvsp',
          width: '100',
          filter: this.hgvspFilter,
        },
        {
          text: 'HGVS.c',
          value: 'hgvsc',
          width: '100',
          filter: this.hgvscFilter,
        },
        { text: 'HGVS', value: 'HGVS', width: '100', filter: this.hgvsFilter },
      ]
    },

    types() {
      return [...new Set(this.variants.map(row => row.type))]
    },

    genes() {
      return [...new Set(this.variants.map(row => row.symbol))]
    },

    consequences() {
      return [...new Set(this.variants.map(row => row.consequence))]
    },
  },

  methods: {
    posFilter: function(value) {
      return filters.posFilter(`${this.posRange[0]}-${this.posRange[1]}`, value)
    },

    vafFilter: function(value) {
      return filters.posFilter(`${this.vafRange[0]}-${this.vafRange[1]}`, value)
    },

    depthFilter: function(value) {
      return filters.posFilter(
        `${this.depthRange[0]}-${this.depthRange[1]}`,
        value
      )
    },

    alleleFilter: function(value) {
      return filters.iContainsFilter(this.allele, value)
    },

    typesFilter: function(value) {
      return filters.inSetFilter(this.selectedTypes, value)
    },

    genesFilter: function(value) {
      return filters.inSetFilter(this.selectedGenes, value)
    },

    removeSelectedGene: function(toRemove) {
      this.selectedGenes = this.selectedGenes.filter(
        selected => selected !== toRemove
      )
    },

    consequencesFilter: function(value) {
      return filters.inSetFilter(this.selectedConsequences, value)
    },

    removeSelectedConsequence: function(toRemove) {
      this.selectedConsequences = this.selectedConsequences.filter(
        selected => selected !== toRemove
      )
    },

    consequenceSort: function(l, r) {
      return l.rank - r.rank
    },

    diseaseFilter: function(value) {
      return filters.iContainsFilter(this.disease, value)
    },

    mitoMapFilter: function(value) {
      return filters.iContainsFilter(this.mitoMap, value)
    },

    curatedRefsFilter: function(value) {
      return filters.iContainsFilter(this.curatedRefs, value)
    },

    hgvspFilter: function(value) {
      return filters.iContainsFilter(this.hgvsp, value)
    },

    hgvscFilter: function(value) {
      return filters.iContainsFilter(this.hgvsc, value)
    },

    hgvsFilter: function(value) {
      return filters.iContainsFilter(this.hgvs, value)
    },
  },
}
</script>

<style scoped></style>
