<template>
  <div>
    <v-card>
      <v-card-text class="py-0 my-0">
        <v-row align="start" justify="start">
          <v-col md="1">
            <span class="text-subtitle-1">Saved Search:</span>
          </v-col>
          <v-col md="2">
            <v-select
              id="inputSelectSavedSearch"
              v-model="selectedSavedSearch"
              :items="allSavedSearches"
              item-text=".name"
              item-value=".name"
              @change="onSavedSearchChange"
              type="text"
              label="Presets"
              return-object
              dense
            >
              <template v-slot:selection="{ item }">
                <span>{{ item.name }}</span>
              </template>
            </v-select>
          </v-col>
          <v-col md="1">
            <v-menu
              v-model="searchFormMenu"
              :close-on-content-click="false"
              :nudge-width="500"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-on="on" v-bind="attrs">
                  <v-icon>mdi-content-save</v-icon>
                </v-btn>
              </template>
              <v-card>
                <v-form
                  id="save-search-form"
                  v-model="searchForm.valid"
                  @submit.prevent="onSaveSearch"
                >
                  <v-text-field
                    v-model="searchForm.name"
                    label="Name"
                    class="px-4 pt-8 pb-4"
                    maxlength="50"
                    :rules="[rules.required]"
                    persistent-hint
                    :hint="searchFormNameHint"
                  ></v-text-field>
                  <v-text-field
                    v-model="searchForm.description"
                    label="Description"
                    class="px-4"
                    maxlength="500"
                  ></v-text-field>
                  <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn text @click="searchFormMenu = false">Cancel</v-btn>
                    <v-btn
                      type="submit"
                      form="save-search-form"
                      color="primary"
                      text
                      :disabled="saveSearchDisabled"
                      >Save</v-btn
                    >
                  </v-card-actions>
                </v-form>
              </v-card>
            </v-menu>
            <v-btn
              v-if="selectedSavedSearch.custom"
              icon
              @click.prevent="onDeleteSearch"
            >
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <v-card>
      <v-card-title></v-card-title>
      <v-data-table
        ref="variantTable"
        :headers="headers"
        :items="filteredVariants"
        :options="tableOptions"
        :footer-props="tableFooterProps"
        :expanded.sync="expandedVariants"
        item-key="id"
        @click:row="toggleVariantExpansion"
        class="elevation-1 variant-expanded"
        dense
      >
        <!-- v-slot:body.prepend includes all components for each column's filtering -->
        <template v-slot:body.prepend>
          <tr class="paddedrow">
            <td>
              <v-row class="px-4 justify-space-between">
                <span class="grey--text text--darken-1 text-caption">{{
                  filterConfig.posRange[0]
                }}</span>
                <span class="grey--text text--darken-1 text-caption">{{
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
                dense
              ></v-text-field>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedTypes"
                :items="types"
                type="text"
                label="Select"
                multiple
                dense
              >
                <template v-slot:selection="{ item, index }">
                  <v-chip
                    v-if="index <= 3"
                    close
                    @click:close="removeSelectedType(item)"
                    x-small
                  >
                    <span>{{ item }}</span>
                  </v-chip>
                  <span v-if="index === 4" class="grey--text caption"
                    >(+{{ filterConfig.selectedTypes.length - 4 }} others)</span
                  >
                </template>
              </v-select>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedGenes"
                :items="genes"
                type="text"
                label="Select"
                multiple
                dense
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
                v-model="filterConfig.selectedConsequence"
                :items="consequences"
                item-text=".name"
                item-value=".name"
                return-object
                type="text"
                label="Select up to severity"
                dense
                class="variant-table-v-select"
              >
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
              <v-row class="px-4 justify-center">
                <span class="grey--text text--darken-1">
                  {{ filterConfig.gbFreqMax }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gbFreqMax"
                step="5"
                min="0"
                max="100"
                hide-details
              >
              </v-slider>
            </td>
            <td>
              <v-row class="px-4 justify-center">
                <span class="grey--text text--darken-1">
                  {{ filterConfig.gnomADHetFreqMax }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gnomADHetFreqMax"
                step="5"
                min="0"
                max="100"
                hide-details
              >
              </v-slider>
            </td>
            <td>
              <v-row class="px-4 justify-center">
                <span class="grey--text text--darken-1">
                  {{ filterConfig.gnomADHomFreqMax }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gnomADHomFreqMax"
                step="5"
                min="0"
                max="100"
                hide-details
              >
              </v-slider>
            </td>
            <td></td>
            <td></td>
            <td>
              <v-row class="justify-space-between">
                <v-text-field
                  v-model="filterConfig.curationSearch"
                  type="text"
                  label="Search notes or tag names"
                  class="pl-3 pr-1 pt-2 curation-search"
                  dense
                ></v-text-field>
                <v-checkbox
                  v-model="filterConfig.importantCuration"
                  on-icon="mdi-tag-multiple"
                  off-icon="mdi-tag-multiple-outline"
                  @click="toggleImportantCuration"
                  color="red"
                  class="pa-0"
                ></v-checkbox>
              </v-row>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.disease"
                type="text"
                label="Contains"
                dense
              ></v-text-field>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedCuratedRefName"
                :items="curatedRefs"
                item-text=".name"
                item-value=".name"
                type="text"
                no-data-text="Select"
                dense
              >
              </v-select>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.hgvsp"
                type="text"
                label="Contains"
                single-line
                dense
              ></v-text-field>
            </td>
            <td>
              <v-text-field
                v-model="filterConfig.hgvsc"
                type="text"
                label="Contains"
                dense
              ></v-text-field>
            </td>
          </tr>
        </template>

        <!-- Dynamically iterate through all header slots v-slot:header.${header.value} to include VariantTableHeader component -->
        <template v-for="h in headers" :slot="`header.${h.value}`">
          <VariantTableHeader :key="h.value" :header="h"></VariantTableHeader>
        </template>

        <!-- Override row values where necessary using slot v-slot:item.${header.value} -->
        <template v-slot:item.pos="{ item }">
          <td>
            <IgvLink :position="item.pos"></IgvLink>
          </td>
        </template>
        <template v-slot:item.ref_alt="{ item }">
          <td>
            <a
              :id="`varlink-${item.pos}-${item.ref}-${item.alt}`"
              @click.stop="activeVariant = item"
              >{{ item.ref }}/{{ item.alt }}</a
            >
          </td>
        </template>
        <template v-slot:item.symbol="{ item }">
          <td>
            <GeneCardsLink
              v-if="item.symbol"
              :gene="item.symbol"
            ></GeneCardsLink>
          </td>
        </template>
        <template v-slot:item.consequence="{ item }">
          <td>{{ item.consequence ? item.consequence.name : '' }}</td>
        </template>
        <template v-slot:item.gbFreqPct="{ item }">
          <td>
            <span v-if="item.gbFreqPct > 0"
              >{{ item.gbFreqPct | precisionTo }}%</span
            >
          </td>
        </template>
        <template v-slot:item.gnomAD.af_het="{ item }">
          <td>
            <span v-if="item.gnomAD && item.gnomAD.af_het > 0"
              >{{ (100 * item.gnomAD.af_het) | precisionTo }}%</span
            >
          </td>
        </template>
        <template v-slot:item.gnomAD.af_hom="{ item }">
          <td>
            <span v-if="item.gnomAD && item.gnomAD.af_hom > 0"
              >{{ (100 * item.gnomAD.af_hom) | precisionTo }}%</span
            >
          </td>
        </template>
        <template v-slot:item.gnomAD.hl_hist="{ item }">
          <span v-if="heteroplasmyDistExists(item)">
            <v-sparkline
              :value="heteroplasmyDistHeights(item)"
              type="bar"
              :gradient="[COLORS.PRIMARY, COLORS.PRIMARY_LIGHT]"
              radius="10"
              padding="1"
              smooth="1"
              gradient-direction="top"
              auto-line-width
            >
              <template v-slot:label>
                {{ heteroplasmyDistLabel(item) }}
              </template>
            </v-sparkline>
          </span>
        </template>
        <template v-slot:item.gnomAD.age_hist_hom="{ item }">
          <span v-if="ageDistExists(item)">
            <v-sparkline
              :value="ageDistHomoHeights(item)"
              type="bar"
              :gradient="[COLORS.PRIMARY, COLORS.PRIMARY_LIGHT]"
              radius="10"
              padding="1"
              smooth="1"
              gradient-direction="top"
              auto-line-width
            >
              <template v-slot:label>
                {{ ageDistLabel(item) }}
              </template>
            </v-sparkline>
          </span>
        </template>
        <template v-slot:item.curation="{ item }">
          <td>
            <CurationCell :variantId="item.id" :key="item.id"></CurationCell>
          </td>
        </template>
        <template v-slot:item.curatedRef="{ item }">
          <td>
            <CuratedRefLink
              :href="item.curatedRef.url"
              :count="item.curatedRef.count"
            ></CuratedRefLink>
          </td>
        </template>
        <template v-slot:item.hgvsc="{ item }">
          <td v-html="item.hgvsc"></td>
        </template>
        <template v-slot:expanded-item="{ headers, item }">
          <td
            :colspan="headers.length"
            :class="{
              'ma-0 pa-0': true,
              'expanded-closing': !transitioned[item.id],
            }"
            style="height: auto;"
          >
            <v-expand-transition>
              <VariantDetails
                v-show="transitioned[item.id]"
                :variantId="item.id"
              ></VariantDetails>
            </v-expand-transition>
          </td>
        </template>
      </v-data-table>
    </v-card>

    <v-dialog v-if="activeVariant" v-model="activeVariant" width="400">
      <v-card>
        <v-card-title>
          chrM:{{ activeVariant.pos }} {{ activeVariant.ref }}/{{
            activeVariant.alt
          }}
        </v-card-title>
        <v-card-text>
          <ul>
            <li>
              <a
                :href="
                  `https://gnomad.broadinstitute.org/region/M-${activeVariant.pos -
                    5}-${activeVariant.pos + 5}?dataset=gnomad_r3`
                "
                target="mrgnomadvariant"
                >gnomAD (Region)</a
              >
            </li>
            <li>
              <a
                :href="
                  `https://gnomad.broadinstitute.org/variant/M-${activeVariant.pos}-${activeVariant.ref}-${activeVariant.alt}?dataset=gnomad_r3`
                "
                target="mrgnomadregion"
                >gnomAD (Variant)</a
              >
            </li>
            <li>
              <HmtVarLink
                :position="activeVariant.pos"
                :refAllele="activeVariant.ref"
                :altAllele="activeVariant.alt"
              ></HmtVarLink>
            </li>
          </ul>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import CuratedRefLink from '@/components/CuratedRefLink'
import GeneCardsLink from '@/components/GeneCardsLink'
import HmtVarLink from '@/components/HmtVarLink'
import IgvLink from '@/components/IgvLink'
import CurationCell from '@/components/CurationCell'
import VariantDetails from '@/components/VariantDetails'
import VariantTableHeader from '@/components/VariantTableHeader'
import * as filters from '@/shared/variantFilters'
import * as vueFilters from '@/shared/vueFilters'
import * as _ from 'lodash'
import {
  DEFAULT_VARIANT_SEARCH,
  MAX_POS,
  MIN_POS,
  COLORS,
} from '@/shared/constants'

export default {
  name: 'VariantTable',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  components: {
    CurationCell,
    CuratedRefLink,
    GeneCardsLink,
    HmtVarLink,
    IgvLink,
    VariantDetails,
    VariantTableHeader,
  },

  mounted() {
    this.toggleVariantById(this.variantId)
  },

  data: () => {
    return {
      filterConfig: {
        posRange: [0, MAX_POS],
        allele: '',
        selectedTypes: [],
        selectedGenes: [],
        selectedConsequence: {},
        vafRange: [0, 1],
        gbFreqMax: 5.0,
        gnomADHetFreqMax: 5.0,
        gnomADHomFreqMax: 5.0,
        disease: '',
        diseaseShowBlank: false,
        curationSearch: '',
        importantCuration: false,
        mitoMap: '',
        mitoMapShowBlank: false,
        selectedCuratedRefName: '',
        hgvsp: '',
        hgvspShowBlank: false,
        hgvsc: '',
        hgvscShowBlank: false,
        hgvs: '',
        hgvsShowBlank: false,
      },
      searchForm: {
        valid: true,
        name: '',
        description: '',
      },
      searchFormMenu: false,
      selectedSavedSearch: DEFAULT_VARIANT_SEARCH,
      vafTicks: [0, 0.01, 0.03, 0.05, 0.1, 1],
      vafIndexRange: [1, 5],
      tableOptions: {
        page: 1,
        itemsPerPage: 20,
        multiSort: true,
      },
      tableFooterProps: {
        itemsPerPageOptions: [5, 10, 20, 50, -1],
      },
      rules: {
        required: value => !!value || 'Required.',
      },
      expandedVariants: [],
      transitioned: [],
      closeTimeouts: {},
      activeVariant: null,
    }
  },

  computed: {
    ...mapState(['filteredVariants', 'maxReadDepth', 'settings']),
    ...mapGetters([
      'getSettingsBamFile',
      'getSampleSettings',
      'getVariantById',
      'getCurationByVariantId',
    ]),

    saveSearchDisabled() {
      return !this.searchForm.valid
    },

    allSavedSearches() {
      const all = [DEFAULT_VARIANT_SEARCH].concat(
        this.getSampleSettings.variantSearches
      )
      const result = _.sortBy(all, ['custom', 'name'])

      return result
    },

    searchFormNameHint() {
      if (this.selectedSavedSearch?.custom) {
        return 'Enter a new name to save as new search'
      }

      return ''
    },

    MIN_POS() {
      return MIN_POS
    },

    MAX_POS() {
      return MAX_POS
    },
    COLORS() {
      return COLORS
    },
    headers() {
      return [
        {
          text: 'Position',
          // tooltip: 'Position tooltip',
          align: 'start',
          value: 'pos',
          width: '120',
          filter: this.posFilter,
        },
        {
          text: 'Allele',
          // tooltip: 'Allele tooltip',
          value: 'ref_alt',
          width: '180',
          filter: this.alleleFilter,
        },
        {
          text: 'Type',
          // tooltip: 'Type tooltip',
          value: 'type',
          width: '100',
          filter: this.typesFilter,
        },
        {
          text: 'Gene',
          // tooltip: 'Gene tooltip',
          value: 'symbol',
          width: '150',
          filter: this.genesFilter,
        },
        {
          text: 'Consequence',
          tooltip: 'Sorting is on severity of consequence',
          value: 'consequence',
          width: '180',
          sort: this.consequenceSort,
          filter: this.consequenceFilter,
        },
        {
          text: 'Heteroplasmy',
          // tooltip: 'Heteroplasmy tooltip',
          value: 'genotypes[0].AF',
          width: '120',
          filter: this.vafFilter,
        },
        {
          text: 'Genbank %',
          // tooltip: 'Genbank % tooltip',
          value: 'gbFreqPct',
          width: '130',
          filter: this.gbFreqFilter,
        },
        {
          text: 'gnomAD Het %',
          // tooltip: 'gnomAD Het % tooltip',
          value: 'gnomAD.af_het',
          width: '130',
          filter: this.gnomADHetFreqFilter,
        },
        {
          text: 'gnomAD Hom %',
          // tooltip: 'gnomAD Hom % tooltip',
          value: 'gnomAD.af_hom',
          width: '130',
          filter: this.gnomADHomFreqFilter,
        },
        {
          text: 'Heteroplasmy Distribution',
          // tooltip: 'Heteroplasmy Distribution tooltip',
          align: 'center',
          value: 'gnomAD.hl_hist',
          sortable: false,
          width: '120',
        },
        {
          text: 'Age Distribution (Homoplasmic)',
          // tooltip: 'Age Distribution (Homoplasmic) tooltip',
          align: 'center',
          value: 'gnomAD.age_hist_hom',
          sortable: false,
          width: '120',
        },
        {
          text: 'Curation',
          // tooltip: 'Curation tooltip',
          value: 'curation',
          width: '180',
          filter: (value, search, item) => this.curationFilter(item),
        },
        {
          text: 'Disease',
          // tooltip: 'Disease tooltip',
          value: 'Disease',
          filter: this.diseaseFilter,
        },
        {
          text: 'MitoMap Refs',
          // tooltip: 'MitoMap Refs tooltip',
          value: 'curatedRef',
          align: 'center',
          sortable: false,
          width: '100',
          filter: this.curatedRefsFilter,
        },
        {
          text: 'HGVS.p',
          // tooltip: 'HGVS.p tooltip',
          value: 'hgvsp',
          width: '100',
          filter: this.hgvspFilter,
        },
        {
          text: 'HGVS.c',
          // tooltip: 'HGVS.c tooltip',
          value: 'hgvsc',
          width: '100',
          filter: this.hgvscFilter,
        },
      ]
    },

    types() {
      return [...new Set(this.filteredVariants.map(row => row.type))]
    },

    genes() {
      return [...new Set(this.filteredVariants.map(row => row.symbol))]
    },

    consequences() {
      const allUniqConsequences = _.uniqWith(
        this.filteredVariants.map(row => row.consequence),
        _.isEqual
      )
      const nullsExcluded = _.filter(allUniqConsequences, c => c !== null)

      return _.sortBy(nullsExcluded, ['rank'])
    },

    vafLastTickIndex() {
      return this.vafTicks.length - 1
    },

    curatedRefs() {
      return [
        {
          name: 'All',
          predicate: () => true,
        },
        {
          name: '0',
          predicate: value => !value || value === 0,
        },
        {
          name: '> 0',
          predicate: value => value > 0,
        },
      ]
    },
  },

  methods: {
    toggleVariantById: function(variantId) {
      const variant = this.getVariantById(variantId)
      if (!_.isEmpty(variant)) {
        this.toggleVariantExpansion(variant)
      }
    },

    toggleVariantExpansion: function(variant) {
      const id = variant.id
      const isExpanded = this.$refs.variantTable.isExpanded
      if (isExpanded && this.transitioned[id]) {
        this.closeVariantExpansion(variant)
      } else {
        clearTimeout(this.closeTimeouts[id])
        this.$refs.variantTable.expand(variant, true)
        this.$nextTick(() => this.$set(this.transitioned, id, true))
        this.$nextTick(() =>
          this.expandedVariants.forEach(
            ev => ev !== variant && this.closeVariantExpansion(ev)
          )
        )

        if (this.$route.params.variantId !== id) {
          this.$router.push({
            name: 'variantDetails',
            params: { variantId: variant.id },
          })
        }
      }
    },

    closeVariantExpansion: function(variant) {
      const id = variant.id
      this.$set(this.transitioned, id, false)
      this.closeTimeouts[id] = setTimeout(
        () => this.$refs.variantTable.expand(variant, false),
        300
      )
    },

    onSavedSearchChange: function(value) {
      let savedSearch =
        (this.getSampleSettings.variantSearches || []).find(vs => {
          return vs.name === value.name
        }) || DEFAULT_VARIANT_SEARCH.name

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
      if (value?.custom) {
        this.searchForm.name = value.name
        this.searchForm.description = value.description
      } else {
        this.searchForm.name = ''
        this.searchForm.description = ''
      }
      this.toggleImportantCuration()
    },

    onSaveSearch: function() {
      const toSave = {
        name: this.searchForm.name,
        description: this.searchForm.description,
        custom: true,
        filterConfig: { ...this.filterConfig },
      }
      toSave.filterConfig.vafRange = [
        this.vafTicks[this.vafIndexRange[0]],
        this.vafTicks[this.vafIndexRange[1]],
      ]
      this.$store.dispatch('saveSearch', toSave)
      this.selectedSavedSearch =
        this.allSavedSearches.find(ss => {
          return ss.name === this.searchForm.name
        }) || DEFAULT_VARIANT_SEARCH
      this.searchFormMenu = false
    },

    onDeleteSearch: function() {
      this.$store.dispatch('deleteSearch', this.selectedSavedSearch)
      this.selectedSavedSearch = this.allSavedSearches.find(ss => {
        return ss.name === DEFAULT_VARIANT_SEARCH.name
      })
      this.onSavedSearchChange(this.selectedSavedSearch)
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

      return filters.rangeTextFilter(`${lower}-${upper}`, value)
    },

    gbFreqFilter: function(value) {
      let lower = 0
      let upper = this.filterConfig.gbFreqMax

      return filters.rangeTextFilter(`${lower}-${upper}`, value || 0.0)
    },

    gnomADHetFreqFilter: function(value) {
      let lower = 0
      let upper = this.filterConfig.gnomADHetFreqMax

      return filters.rangeTextFilter(
        `${lower / 100}-${upper / 100}`,
        value || 0.0
      )
    },

    gnomADHomFreqFilter: function(value) {
      let lower = 0
      let upper = this.filterConfig.gnomADHomFreqMax

      return filters.rangeTextFilter(
        `${lower / 100}-${upper / 100}`,
        value || 0.0
      )
    },

    heteroplasmyDistExists: function(item) {
      return _.some(this.heteroplasmyDistHeights(item), Boolean)
    },

    heteroplasmyDistHeights: function(item) {
      return item?.gnomAD?.hl_hist || []
    },

    heteroplasmyDistLabel: function(item) {
      return this.heteroplasmyDistHeights(item)
        .map(() => '-')
        .join()
    },

    ageDistExists: function(item) {
      return _.some(this.ageDistHomoHeights(item), Boolean)
    },

    ageDistHomoHeights: function(item) {
      const smaller = item?.gnomAD?.age_hist_hom?.smaller || 0
      const larger = item?.gnomAD?.age_hist_hom?.larger || 0

      return [smaller].concat(item?.gnomAD?.age_hist_hom?.bins || [], [larger])
    },

    ageDistLabel: function(item) {
      return this.ageDistHomoHeights(item)
        .map(() => '-')
        .join()
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

    removeSelectedType: function(toRemove) {
      this.filterConfig.selectedTypes = this.filterConfig.selectedTypes.filter(
        selected => selected !== toRemove
      )
    },

    removeSelectedGene: function(toRemove) {
      this.filterConfig.selectedGenes = this.filterConfig.selectedGenes.filter(
        selected => selected !== toRemove
      )
    },

    consequenceFilter: function(value) {
      return filters.consequenceFilter(
        this.filterConfig.selectedConsequence,
        value
      )
    },

    consequenceSort: function(l, r) {
      return l.rank - r.rank
    },

    curationFilter: function(item) {
      const curation = this.getCurationByVariantId(item.id)

      return filters.curationFilter(this.filterConfig.curationSearch, curation)
    },

    toggleImportantCuration: function() {
      this.$store.dispatch(
        'filterImportantVariants',
        this.filterConfig.importantCuration
      )
    },

    diseaseFilter: function(value) {
      return filters.iContainsFilter(
        this.filterConfig.disease,
        value,
        this.filterConfig.diseaseShowBlank
      )
    },

    mitoMapFilter: function(value) {
      return filters.iContainsFilter(
        this.filterConfig.mitoMap,
        value,
        this.filterConfig.mitoMapShowBlank
      )
    },

    curatedRefsFilter: function(value) {
      const selectedCuratedRef =
        this.curatedRefs.find(
          cr => cr.name === this.filterConfig.selectedCuratedRefName
        ) || this.curatedRefs[0]
      return filters.predicateFilter(
        selectedCuratedRef.predicate,
        value?.count || 0
      )
    },

    hgvspFilter: function(value) {
      return filters.iContainsFilter(
        this.filterConfig.hgvsp,
        value,
        this.filterConfig.hgvspShowBlank
      )
    },

    hgvscFilter: function(value) {
      return filters.iContainsFilter(
        this.filterConfig.hgvsc,
        value,
        this.filterConfig.hgvscShowBlank
      )
    },

    hgvsFilter: function(value) {
      return filters.iContainsFilter(
        this.filterConfig.hgvs,
        value,
        this.filterConfig.hgvsShowBlank
      )
    },
  },

  watch: {
    consequences: function() {
      this.filterConfig.selectedConsequence = this.consequences.slice(-1)[0]
    },
    filteredVariants: function() {
      this.toggleVariantById(this.variantId)
    },
    $route: function() {
      this.toggleVariantById(this.variantId)
    },
  },

  filters: {
    precisionTo: vueFilters.precisionTo,
  },
}
</script>

<style lang="css" scoped>
.variant-table-v-select {
  font-size: 0.8em;
}

.variant-expanded >>> .v-data-table__expanded__row {
  background-color: rgba(0, 0, 0, 0.12);
}

.curation-search {
  width: 100px;
}
</style>
