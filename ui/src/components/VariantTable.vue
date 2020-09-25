<template>
  <div>
    <v-card>
      <v-card-title>Saved Search</v-card-title>
      <v-select
        v-model="selectedSavedSearch"
        :items="allSavedSearches"
        type="text"
        label="Presets"
        @change="onSavedSearchChange"
      ></v-select>
    </v-card>
    <v-card>
      <v-card-title></v-card-title>
      <v-data-table
        :headers="headers"
        :items="variants"
        :options="tableOptions"
        :footer-props="tableFooterProps"
        class="elevation-1"
        dense
      >
        <template v-slot:body.prepend>
          <tr>
            <td>
              <v-row class="px-4 justify-space-between">
                <span class="grey--text text--darken-1">{{
                  filterConfig.posRange[0]
                }}</span>
                <span class="grey--text text--darken-1">{{
                  filterConfig.posRange[1]
                }}</span>
              </v-row>
              <v-range-slider
                v-model="filterConfig.posRange"
                :min="0"
                :max="MAX_POS"
                step="100"
                hide-details
              >
              </v-range-slider>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.allele"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedTypes"
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
                v-model="filterConfig.selectedGenes"
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
                    >(+{{ filterConfig.selectedGenes.length - 4 }} others)</span
                  >
                </template>
              </v-select>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedConsequences"
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
                    >(+{{
                      filterConfig.selectedConsequences.length - 4
                    }}
                    others)</span
                  >
                </template>
              </v-select>
            </td>
            <td>
              <v-row class="px-4 justify-space-between">
                <span class="grey--text text--darken-1">{{
                  vafTicks[vafIndexRange[0]]
                }}</span>
                <span class="grey--text text--darken-1">{{
                  vafTicks[vafIndexRange[1]]
                }}</span>
              </v-row>
              <v-range-slider
                v-model="vafIndexRange"
                :value="vafTicks"
                min="0"
                :max="vafLastTickIndex"
                hide-details
              >
              </v-range-slider>
            </td>
            <td>
              <v-row class="px-4 justify-space-between">
                <span class="grey--text text--darken-1">{{
                  filterConfig.depthRange[0]
                }}</span>
                <span class="grey--text text--darken-1">{{
                  filterConfig.depthRange[1]
                }}</span>
              </v-row>
              <v-range-slider
                v-model="filterConfig.depthRange"
                :min="0"
                :max="MAX_READ_DEPTH"
                step="100"
                hide-details
              >
              </v-range-slider>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.disease"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.mitoMap"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.curatedRefs"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.hgvsp"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.hgvsc"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.hgvs"
                type="text"
                label="Contains"
              ></v-text-field>
            </td>
          </tr>
        </template>
        <template v-slot:item.pos="{ item }">
          <td>
            <IgvLink
              :igvHost="igvHost"
              :bamFile="bamFile"
              :position="item.pos"
            ></IgvLink>
          </td>
        </template>
        <template v-slot:item.consequence="{ item }">
          <td>{{ item.consequence.name }}</td>
        </template>
        <template v-slot:item.hgvsc="{ item }">
          <td v-html="item.hgvsc"></td>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import IgvLink from '@/components/IgvLink'
import * as filters from '@/shared/variantFilters'
import * as _ from 'lodash'
import {
  MIN_POS,
  MAX_POS,
  MAX_READ_DEPTH,
  DEFAULT_VARIANT_SEARCH,
} from '@/shared/constants'

export default {
  name: 'VariantTable',
  props: {},

  components: {
    IgvLink,
  },

  data: () => {
    return {
      filterConfig: {
        posRange: [0, MAX_POS],
        allele: '',
        selectedTypes: [],
        selectedGenes: [],
        selectedConsequences: [],
        vafRange: [0, 1],
        depthRange: [0, MAX_READ_DEPTH],
        disease: '',
        mitoMap: '',
        curatedRefs: '',
        hgvsp: '',
        hgvsc: '',
        hgvs: '',
      },
      selectedSavedSearch: DEFAULT_VARIANT_SEARCH.name,
      vafTicks: [
        0,
        0.00001,
        0.0005,
        0.0001,
        0.005,
        0.01,
        0.02,
        0.03,
        0.04,
        0.05,
        0.1,
        1,
      ],
      vafIndexRange: [0, 11],
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
    ...mapState(['variants', 'settings']),
    ...mapGetters(['igvHost', 'bamFile', 'sampleSettings']),

    allSavedSearches() {
      const result = [DEFAULT_VARIANT_SEARCH.name].concat(
        (this.sampleSettings.variantSearches || []).map(vs => vs.name)
      )

      return result
    },

    MIN_POS() {
      return MIN_POS
    },

    MAX_POS() {
      return MAX_POS
    },

    MAX_READ_DEPTH() {
      return MAX_READ_DEPTH
    },

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
          width: '130',
          filter: this.vafFilter,
        },
        {
          text: 'Depth',
          value: 'genotypes[0].DP',
          width: '130',
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
      return [
        ...new Set(
          _.sortBy(
            this.variants.map(row => row.consequence),
            ['rank']
          )
        ),
      ]
    },

    vafLastTickIndex() {
      return this.vafTicks.length - 1
    },
  },

  methods: {
    onSavedSearchChange: function(value) {
      let savedSearch =
        (this.sampleSettings.variantSearches || []).find(vs => {
          return vs.name === value
        }) || DEFAULT_VARIANT_SEARCH

      let newFilterConfig = Object.assign(
        {},
        DEFAULT_VARIANT_SEARCH.filterConfig,
        savedSearch.filterConfig
      )
      this.filterConfig = Object.assign({}, this.filterConfig, newFilterConfig)

      const lowerIndex = this.vafTicks.indexOf(newFilterConfig.vafRange[0])
      const upperIndex = this.vafTicks.indexOf(newFilterConfig.vafRange[1])
      this.vafIndexRange = Object.assign([], this.vafIndexRange, [
        lowerIndex === -1 ? 0 : lowerIndex,
        upperIndex === -1 ? this.vafLastTickIndex : upperIndex,
      ])
    },

    posFilter: function(value) {
      return filters.rangeTextFilter(
        `${this.filterConfig.posRange[0]}-${this.filterConfig.posRange[1]}`,
        value
      )
    },

    vafFilter: function(value) {
      let lower = this.vafTicks[this.vafIndexRange[0]]
      let upper = this.vafTicks[this.vafIndexRange[1]]
      return filters.rangeTextFilter(`${lower - upper}`, value)
    },

    depthFilter: function(value) {
      return filters.rangeTextFilter(
        `${this.filterConfig.depthRange[0]}-${this.filterConfig.depthRange[1]}`,
        value
      )
    },

    alleleFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.allele, value)
    },

    typesFilter: function(value) {
      return filters.inSetFilter(this.filterConfig.selectedTypes, value)
    },

    genesFilter: function(value) {
      return filters.inSetFilter(this.filterConfig.selectedGenes, value)
    },

    removeSelectedGene: function(toRemove) {
      this.selectedGenes = this.filterConfig.selectedGenes.filter(
        selected => selected !== toRemove
      )
    },

    consequencesFilter: function(value) {
      return filters.inSetFilter(this.filterConfig.selectedConsequences, value)
    },

    removeSelectedConsequence: function(toRemove) {
      this.filterConfig.selectedConsequences = this.filterConfig.selectedConsequences.filter(
        selected => selected !== toRemove
      )
    },

    consequenceSort: function(l, r) {
      return l.rank - r.rank
    },

    diseaseFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.disease, value)
    },

    mitoMapFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.mitoMap, value)
    },

    curatedRefsFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.curatedRefs, value)
    },

    hgvspFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.hgvsp, value)
    },

    hgvscFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.hgvsc, value)
    },

    hgvsFilter: function(value) {
      return filters.iContainsFilter(this.filterConfig.hgvs, value)
    },
  },
}
</script>

<style scoped></style>
