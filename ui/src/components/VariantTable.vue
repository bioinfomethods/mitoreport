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
          <v-col md="2">
            <span>Haplogroup is: {{ getFirstHaplogroup }}</span>
            <v-switch
              id="displayHaplodata"
              :items="displayHaplodata"
              v-model="displayHaplodata"
              ><template v-slot:label>Toggle Haplogroup</template></v-switch
            >
            <!-- <span>{{ hetRatio }}</span> -->
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
              <v-text-field
                v-model="filterConfig.hgvsg"
                type="text"
                label="Contains"
                single-line
                dense
              ></v-text-field>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedGenes"
                :items="genes"
                type="text"
                label="Select Genes"
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
                  {{ gbFreqTicks[filterConfig.gbFreqTickIndex] }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gbFreqTickIndex"
                :value="gbFreqTicks"
                min="0"
                :max="gbFreqLastTickIndex"
                hide-details
              >
              </v-slider>
            </td>
            <!-- 9 gnomad het freq -->
            <td>
              <v-row class="px-4 justify-center">
                <span class="grey--text text--darken-1">
                  {{ filterConfig.gnomADHetFreqMax }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gnomADHetFreqMax"
                step="0.0001"
                min="0"
                max="1"
                hide-details
              >
              </v-slider>
            </td>
            <!-- 10 gnomad hom freq -->
            <td>
              <v-row class="px-4 justify-center">
                <span class="grey--text text--darken-1">
                  {{ filterConfig.gnomADHomFreqMax }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gnomADHomFreqMax"
                step="0.0001"
                min="0"
                max="1"
                hide-details
              >
              </v-slider>
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
            <td></td>
            <td></td>
          </tr>
        </template>

        <!-- Dynamically iterate through all header slots v-slot:header.${header.value} to include VariantTableHeader component -->
        <template v-for="h in headers" :slot="`header.${h.value}`">
          <VariantTableHeader :key="h.value" :header="h"></VariantTableHeader>
        </template>

        <!-- Override row values where necessary using slot v-slot:item.${header.value} -->
        <template v-slot:item.hgvsg="{ item }">
          <!-- Todo: test this regex? Also confirm it is a good idea. -->
          <a
            :id="`varlink-${item.pos}-${item.ref}-${item.alt}`"
            @click.stop="activeVariant = item"
            >{{
              item.HGVS || item.id.replace(/chrM-(\d+)-(\w)-(\w+)/, 'm.$1$2>$3')
            }}
          </a>
        </template>

        <template v-slot:item.symbols="{ item }">
          <GeneCardsLink
            v-for="gene in item.symbols"
            :key="gene"
            :gene="gene"
          ></GeneCardsLink>
        </template>
        <template v-slot:item.consequence="{ item }">
          {{ item.consequence ? item.consequence.name : '' }}
        </template>
        <template v-slot:item.gbFreq="{ item }">
          <span v-if="item.gbFreq > 0">{{ item.gbFreq | precisionTo }}</span>
        </template>

        <template v-slot:item.hapWeight="{ item }">
          <span v-if="hapRatios[item.id] && hapRatios[item.id].hapWeight">
            {{ hapRatios[item.id].hapWeight | precisionTo }}</span
          >
        </template>

        <!-- gnomAD Het -->
        <template v-slot:item.gnomAD.af_het="{ item }">
          <span class="gnomADspan" v-if="item.gnomAD && item.gnomAD.af_het > 0">
            {{ item.gnomAD.af_het | precisionTo }}</span
          >
          <v-icon class="worldIcon">mdi-earth</v-icon>

          <span
            v-if="
              item.gnomAD &&
                item.gnomAD.af_het > 0 &&
                item.gnomAD.hap_af_het_map &&
                displayHaplodata &&
                getFirstHaplogroup
            "
          >
            <br />
            <span class="haplogroupIcon">{{ getFirstHaplogroup }}</span>
            {{ item.gnomAD.hap_af_het_map[getFirstHaplogroup] | precisionTo }}
          </span>
        </template>

        <!-- gnomAD Hom -->
        <template v-slot:item.gnomAD.af_hom="{ item }">
          <span class="gnomADspan" v-if="item.gnomAD && item.gnomAD.af_hom > 0">
            {{ item.gnomAD.af_hom | precisionTo }}</span
          >
          <v-icon class="worldIcon">mdi-earth</v-icon>

          <span
            v-if="
              item.gnomAD &&
                item.gnomAD.af_hom > 0 &&
                item.gnomAD.hap_af_hom_map &&
                displayHaplodata &&
                getFirstHaplogroup
            "
          >
            <br />
            <span class="haplogroupIcon">{{ getFirstHaplogroup }}</span>
            {{ item.gnomAD.hap_af_hom_map[getFirstHaplogroup] | precisionTo }}
          </span>
        </template>

        <!-- Heteroplasmy Distribution -->
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

        <!-- Age Distribution Homoplasmic -->
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
          <CurationCell :variantId="item.id" :key="item.id"></CurationCell>
        </template>
        <template v-slot:item.curatedRef="{ item }">
          <CuratedRefLink
            :href="item.curatedRef.url"
            :count="item.curatedRef.count"
          ></CuratedRefLink>
        </template>
        <template v-slot:expanded-item="{ headers, item }">
          <td
            :colspan="headers.length"
            :class="{
              'ma-0 pa-0': true,
              'expanded-closing': !transitioned[item.id],
            }"
            style="height: auto"
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
            <li>
              <IgvLink :position="activeVariant.pos"></IgvLink>
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
        gnomADHap: [],
        selectedConsequence: {},
        vafRange: [0, 1],
        gbFreqTickIndex: 6,
        gnomADHetFreqMax: 1.0,
        gnomADHomFreqMax: 1.0,
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
      gbFreqTicks: [0.0, 0.001, 0.002, 0.005, 0.01, 0.1, 1.0],
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
      displayHaplodata: false,
    }
  },

  computed: {
    ...mapState(['filteredVariants', 'maxReadDepth', 'settings']),
    ...mapGetters([
      'getSettingsBamFile',
      'getSampleSettings',
      'getVariantById',
      'getCurationByVariantId',
      'getHaplogroups',
      'getFirstHaplogroup',
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
          text: 'HGVS.g',
          value: 'hgvsg',
          width: '100',
          filter: this.hgvsgFilter,
        },
        {
          text: 'Gene',
          // tooltip: 'Gene tooltip',
          value: 'symbols',
          width: '150',
          filter: this.genesFilter,
        },
        {
          text: 'Curation',
          value: 'curation',
          width: '180',
          filter: (value, search, item) => this.curationFilter(item),
        },
        {
          text: 'Heteroplasmy',
          tooltip: 'Heteroplasmy freq of variant in sample',
          value: 'genotypes[0].AF',
          width: '120',
          filter: this.vafFilter,
        },
        {
          text: 'Genbank',
          // tooltip: 'Genbank % tooltip',
          value: 'gbFreq',
          width: '100',
          filter: this.gbFreqFilter,
        },

        {
          text: 'gnomAD Heteroplasmy',
          tooltip:
            'Proportion of individuals with variant at heteroplasmy between 0.10 - 0.95 in gnomAD',
          value: 'gnomAD.af_het',
          width: '100',
          filter: this.gnomADHetFreqFilter,
        },

        {
          text: 'gnomAD Homoplasmy',
          tooltip:
            'Proportion of individuals with variant at homoplasmy (heteroplasmy >= 0.95) in gnomAD',
          value: 'gnomAD.af_hom',
          width: '100',
          filter: this.gnomADHomFreqFilter,
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
          text: 'Disease',
          tooltip: 'Disease association from MitoMap',
          value: 'Disease',
          width: 100,
          filter: this.diseaseFilter,
        },
        {
          text: 'MitoMap Refs',
          tooltip: 'MitoMap curated references',
          value: 'curatedRef',
          align: 'center',
          sortable: false,
          width: '100',
          filter: this.curatedRefsFilter,
        },
        {
          text: 'Heteroplasmy Distribution',
          tooltip: 'Heteroplasmy Distribution of variant from gnomAD',
          align: 'center',
          value: 'gnomAD.hl_hist',
          sortable: false,
          width: '120',
        },
        {
          text: 'Age Distribution (Homoplasmic)',
          tooltip: 'Age Distribution (Homoplasmic) from gnomAD',
          align: 'center',
          value: 'gnomAD.age_hist_hom',
          sortable: false,
          width: '120',
        },
      ]
    },

    types() {
      return [...new Set(this.filteredVariants.map(row => row.type))]
    },

    // TODO: #31
    // hapOptions() {
    //   return [...new Set(['true', 'false'])]
    // },

    hapRatios() {
      return this.filteredVariants.reduce((map, variant) => {
        if (variant.gnomAD) {
          map[variant.id] = {
            hapWeight: 0,
          }
          if (
            variant.gnomAD.af_het &&
            variant.gnomAD.hap_af_het_map[this.getFirstHaplogroup]
          ) {
            let hetRatio = (map[variant.id].hetRatio =
              variant.gnomAD.hap_af_het_map[this.getFirstHaplogroup] /
              variant.gnomAD.af_het)

            map[variant.id].hapWeight = Math.log(hetRatio) * Math.log(hetRatio)
          }
          if (
            variant.gnomAD.af_hom &&
            variant.gnomAD.hap_af_hom_map[this.getFirstHaplogroup]
          ) {
            let homRatio = (map[variant.id].homRatio =
              variant.gnomAD.hap_af_hom_map[this.getFirstHaplogroup] /
              variant.gnomAD.af_hom)
            map[variant.id].hapWeight += Math.log(homRatio) * Math.log(homRatio)
          }
        }
        return map
      }, {})
    },

    genes() {
      const genes = [
        ...new Set(
          this.filteredVariants
            .filter(row => row.symbols)
            .map(row => row.symbols)
            .flat()
        ),
      ]

      return genes
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

    gbFreqLastTickIndex() {
      return this.gbFreqTicks.length - 1
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
    toggleHaplodata() {
      document.getElementById('app').classList.toggle('showHaplodata')
    },

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
      let upper = this.gbFreqTicks[this.filterConfig.gbFreqTickIndex]

      return filters.rangeTextFilter(`${lower}-${upper}`, value || 0.0)
    },

    gnomADHetFreqFilter: function(value) {
      let lower = 0
      let upper = this.filterConfig.gnomADHetFreqMax

      return filters.rangeTextFilter(`${lower}-${upper}`, value || 0.0)
    },

    gnomADHomFreqFilter: function(value) {
      let lower = 0
      let upper = this.filterConfig.gnomADHomFreqMax

      return filters.rangeTextFilter(`${lower}-${upper}`, value || 0.0)
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

    gnomADHapFilter: function(value) {
      return filters.trioFilter(this.filterConfig.gnomADHap, value)
    },

    genesFilter: function(value) {
      return filters.setInSetFilter(this.filterConfig.selectedGenes, value)
    },

    removeSelectedHaploFilter: function(toRemove) {
      this.filterConfig.gnomADHap = this.filterConfig.gnomADHap.filter(
        selected => selected !== toRemove
      )
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

    hapWeightSort: function(l, r) {
      if (this.hapRatios[l]) {
        if (this.hapRatios[r]) {
          return this.hapRatios[l].hapWeight - this.hapRatios[r].hapWeight
        } else {
          return 1
        }
      } else {
        if (this.hapRatios[r]) {
          return -1
        } else {
          return 0
        }
      }
    },

    hetRatioSort: function(l, r) {
      if (this.hapRatios[l]) {
        if (this.hapRatios[r]) {
          return this.hapRatios[l].hetRatio - this.hapRatios[r].hetRatio
        } else {
          return 1
        }
      } else {
        if (this.hapRatios[r]) {
          return -1
        } else {
          return 0
        }
      }
    },

    homRatioSort: function(l, r) {
      if (this.hapRatios[l]) {
        if (this.hapRatios[r]) {
          return this.hapRatios[l].homRatio - this.hapRatios[r].homRatio
        } else {
          return 1
        }
      } else {
        if (this.hapRatios[r]) {
          return -1
        } else {
          return 0
        }
      }
    },

    gnomADHombundleSort: function(l, r) {
      if (l && l.af_hom) {
        if (r && r.af_hom) {
          if (this.displayHaplodata) {
            return (
              l.hap_af_hom_map[this.getFirstHaplogroup] / l.af_hom -
              r.hap_af_hom_map[this.getFirstHaplogroup] / r.af_hom
            )
          } else {
            return l.af_hom - r.af_hom
          }
        } else {
          return 1
        }
      } else {
        if (r && r.af_hom) {
          return -1
        } else {
          return 0
        }
      }
    },

    gnomADHapSort: function(l, r) {
      return weight(l) - weight(r)

      function weight(gnomAD) {
        return gnomAD !== undefined ? (gnomAD ? 1 : 0) : -1
      }
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
    displayHaplodata: function() {
      console.log('Toggling haplodata', this.displayHaplodata)

      this.toggleHaplodata(this.displayHaplodata)
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
