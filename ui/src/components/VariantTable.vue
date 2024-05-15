<template>
  <div>
    <v-card>
      <v-card-text class="py-0 my-0">
        <v-row>
          <v-col md="1">
            <span class="text-subtitle-1">Saved Search:</span>
            <br />
            <span
              >Filtered to {{ currentVariants.length }} of
              {{ tableFilteredVariants.length }} variants</span
            >
          </v-col>
          <v-col md="2">
            <v-select
              id="inputSelectSavedSearch"
              v-model="selectedSavedSearch"
              :items="allSavedSearchesDisplay"
              item-text=".name"
              item-value=".name"
              @input="onSavedSearchChange"
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
            <span
              >Haplogroup is: {{ getFirstHaplogroup }}
              <span class="secondary-info"
                >({{ getFirstFullHaplogroup }})</span
              ></span
            >
            <v-switch
              id="displayHaplodata"
              :items="displayHaplodata"
              v-model="displayHaplodata"
              ><template v-slot:label
                >{{ displayHaplodata ? 'Hide' : 'Show' }} Haplogroup
                Data</template
              ></v-switch
            >
          </v-col>

          <v-col md="2">
            <span>Toggle Curation Tags</span>
            <v-switch
              id="quickTagSwitch"
              :items="showQuickTags"
              v-model="showQuickTags"
              ><template v-slot:label
                >{{ showQuickTags ? 'Hide' : 'Show' }} Tags</template
              ></v-switch
            >
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <v-card>
      <v-data-table
        ref="variantTable"
        id="variantTable"
        :headers="headers"
        :items="tableFilteredVariants"
        :options="tableOptions"
        :footer-props="tableFooterProps"
        :expanded.sync="expandedVariants"
        item-key="id"
        @current-items="currentItems"
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

              <v-row class="px-4 justify-space-between posSlider">
                <span class="grey--text text--darken-1 text-caption">{{
                  filterConfig.posRange[0]
                }}</span>
                <span class="grey--text text--darken-1 text-caption">{{
                  filterConfig.posRange[1]
                }}</span>
              </v-row>
              <v-range-slider
                class="posSlider"
                v-model="filterConfig.posRange"
                :min="0"
                :max="MAX_POS"
                step="100"
                hide-details
              >
              </v-range-slider>
            </td>
            <td id="geneHeader">
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
                    >+{{ filterConfig.selectedGenes.length - 4 }} more</span
                  >
                </template>

                <template v-slot:item="{ item, attrs, on }">
                  <v-list-item
                    v-on="on"
                    v-bind="attrs"
                    #default="{ active }"
                    style="min-width: 180px"
                  >
                    <v-list-item-action>
                      <v-checkbox :input-value="active"></v-checkbox>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        <span>{{ item }}</span>
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </template>
              </v-select>

              <v-select
                v-model="filterConfig.selectedMasks"
                :items="allMasks.map(mask => mask.name)"
                type="text"
                label="Variant Masks"
                multiple
                dense
              >
                <template v-slot:prepend-item>
                  <v-list-item
                    ripple
                    @click="toggleAllMasks"
                    style="width: 330px"
                  >
                    <v-list-item-action>
                      <v-icon
                        :color="
                          filterConfig.selectedMasks.length > 0
                            ? 'indigo darken-4'
                            : ''
                        "
                      >
                        {{ maskSelectAllIcon }}
                      </v-icon>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        Select All variant masks to exclude
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-divider class="mt-2"></v-divider>
                </template>

                <template v-slot:item="{ item, attrs, on }">
                  <v-list-item v-on="on" v-bind="attrs" #default="{ active }">
                    <v-list-item-action>
                      <v-checkbox :input-value="active"></v-checkbox>
                    </v-list-item-action>
                    <v-list-item-content>
                      <v-list-item-title>
                        <span>{{ item }}</span>
                        <span class="float-right">{{
                          `Region: ${
                            allMasks.find(mask => mask.name == item).start
                          }-${allMasks.find(mask => mask.name == item).end}`
                        }}</span>
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </template>

                <template v-slot:selection="{ index }">
                  <span
                    v-if="
                      index === 1 &&
                        filterConfig.selectedMasks.length === allMasks.length
                    "
                    style="font-size: 12px; width: 53px;"
                    >All masks</span
                  >
                  <span v-else-if="index === 1" style="font-size: 12px;"
                    >{{ filterConfig.selectedMasks.length }}&nbsp;masks</span
                  >
                  <span
                    v-else-if="filterConfig.selectedMasks.length === 1"
                    style="font-size: 12px;"
                    >1&nbsp;mask</span
                  >
                </template>
              </v-select>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedConsequences"
                :items="consequences"
                item-text=".displayTerm"
                type="text"
                label="Select Consequences"
                multiple
                dense
              >
                <template v-slot:selection="{ item, index }">
                  <v-chip
                    v-if="index <= 3"
                    close
                    @click:close="removeSelectedConsequence(item)"
                    x-small
                  >
                    <span>{{
                      shortenConsequenceOntology(item.displayTerm)
                    }}</span>
                  </v-chip>
                  <span v-if="index === 4" class="grey--text caption"
                    >+{{
                      filterConfig.selectedConsequences.length - 4
                    }}
                    more</span
                  >
                </template>
              </v-select>
            </td>
            <td>
              <v-select
                v-model="filterConfig.selectedMitoTIP"
                :items="mitoTipQuartiles"
                type="text"
                label="Select Pathegenicity"
                multiple
                dense
              >
                <template v-slot:selection="{ item, index }">
                  <v-chip
                    v-if="index < 1"
                    close
                    @click:close="removeSelectedMitoQuartile(item)"
                    x-small
                  >
                    <span>{{ item }}</span>
                  </v-chip>
                  <span v-if="index === 1" class="grey--text caption"
                    >+{{ filterConfig.selectedMitoTIP.length - 1 }} more</span
                  >
                </template>
              </v-select>
            </td>
            <td>
              <v-row class="justify-space-between curation-search">
                <v-text-field
                  v-model="filterConfig.curationSearch"
                  type="text"
                  label="Search notes or tag names"
                  dense
                ></v-text-field>
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
            <!-- Maternal Variant Header -->
            <td v-if="hasMaternalVariants">
              <v-row class="px-4 justify-space-between">
                <span class="grey--text text--darken-1">{{
                  mafTicks[mafIndexRange[0]]
                }}</span>
                <span class="grey--text text--darken-1">{{
                  mafTicks[mafIndexRange[1]]
                }}</span>
              </v-row>
              <v-range-slider
                v-model="mafIndexRange"
                :value="mafTicks"
                min="0"
                :max="mafLastTickIndex"
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
                  {{ gnomADHetFreqTicks[filterConfig.gnomADHetFreqTickIndex] }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gnomADHetFreqTickIndex"
                :value="gnomADHetFreqTicks"
                min="0.0"
                :max="gnomADHetFreqLastTickIndex"
                hide-details
              >
              </v-slider>
            </td>
            <!-- 10 gnomad hom freq -->
            <td>
              <v-row class="px-4 justify-center">
                <span class="grey--text text--darken-1">
                  {{ gnomADHomFreqTicks[filterConfig.gnomADHomFreqTickIndex] }}
                </span>
              </v-row>
              <v-slider
                v-model="filterConfig.gnomADHomFreqTickIndex"
                :value="gnomADHomFreqTicks"
                min="0.0"
                :max="gnomADHomFreqLastTickIndex"
                hide-details
              >
              </v-slider>
            </td>
            <td></td>
            <td></td>
            <td></td>
          </tr>
        </template>

        <!-- Dynamically iterate through all header slots v-slot:header.${header.value} to include VariantTableHeader component -->
        <template v-for="h in headers" :slot="`header.${h.value}`">
          <VariantTableHeader :key="h.value" :header="h"></VariantTableHeader>
        </template>

        <!-- Override row values where necessary using slot v-slot:item.${header.value} -->
        <template v-slot:item.pos="{ item }">
          <VariantLinks
            :toggle-variant-expansion="toggleVariantExpansion"
            :variant="item"
          ></VariantLinks>
        </template>

        <template v-slot:item.symbols="{ item }">
          <GeneCardsLink
            v-for="gene in item.symbols"
            :key="gene"
            :gene="gene"
          ></GeneCardsLink>
        </template>
        <template v-slot:item.consequence="{ item }">
          <span v-if="item.consequence" :title="item.consequence.displayTerm">{{
            shortenConsequenceOntology(item.consequence.displayTerm)
          }}</span>
        </template>

        <template v-slot:item.mitotip="{ item }">
          <VariantTableMitoTip :item="item"></VariantTableMitoTip>
        </template>

        <template v-slot:item.gbFreq="{ item }">
          <span v-if="item.gbFreq > 0">{{ item.gbFreq | precisionTo }}</span>
        </template>

        <template v-slot:item.hapWeight="{ item }">
          <span v-if="hapRatios[item.id] && hapRatios[item.id].hapWeight">
            {{ hapRatios[item.id].hapWeight | precisionTo }}</span
          >
        </template>

        <!-- Maternal Variant -->
        <template v-slot:item.maternal="{ item }">
          <span v-if="maternalVariants[item.id]">
            <v-tooltip top>
              <template v-slot:activator="{ on, attrs }">
                <span v-bind="attrs" v-on="on">
                  {{ maternalVariants[item.id].genotypes[0].AF }}
                </span>
              </template>
              <span> Read depth: {{ maternalVariants[item.id].DP }} </span>
            </v-tooltip>
          </span>
        </template>

        <!-- gnomAD Het -->
        <template v-slot:item.gnomAD.af_het="{ item }">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <span v-bind="attrs" v-on="on">
                <span
                  v-if="item.gnomAD && item.gnomAD.af_het > 0"
                  :class="
                    'gnomADspan ' +
                      ((item.gnomAD.af_het > 0.0001 ||
                        item.gnomAD.hap_af_het_map[getFirstHaplogroup] >
                          0.0001) &&
                      (hapRatios[item.id].hetRatio > 2 ||
                        hapRatios[item.id].hetRatio < 0.5)
                        ? 'highlightMe'
                        : '')
                  "
                >
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
                  {{
                    item.gnomAD.hap_af_het_map[getFirstHaplogroup] | precisionTo
                  }}
                </span>
              </span>
            </template>
            <span class="text-caption">
              <span
                class="gnomADspan"
                v-if="item.gnomAD && item.gnomAD.af_het > 0"
              >
                Global gnomAD Freq: {{ item.gnomAD.af_het | precisionTo }}</span
              >
              <v-icon class="worldIcon tooltipIcon">mdi-earth</v-icon>

              <span
                v-if="
                  item.gnomAD &&
                    item.gnomAD.af_het > 0 &&
                    item.gnomAD.hap_af_het_map &&
                    getFirstHaplogroup
                "
              >
                <br />
                <span class="haplogroupIcon tooltipIcon">{{
                  getFirstHaplogroup
                }}</span>
                Haplogroup ({{ getFirstHaplogroup }}) Freq:
                {{
                  item.gnomAD.hap_af_het_map[getFirstHaplogroup] | precisionTo
                }}
                <br />
                <span
                  :class="
                    (item.gnomAD.af_het > 0.0001 ||
                      item.gnomAD.hap_af_het_map[getFirstHaplogroup] >
                        0.0001) &&
                    (hapRatios[item.id].hetRatio > 2 ||
                      hapRatios[item.id].hetRatio < 0.5)
                      ? 'highlightMe'
                      : ''
                  "
                >
                  <v-icon class="tooltipIcon">mdi-contrast-box</v-icon> Ratio
                  (Global / Haplogroup):
                  {{
                    (item.gnomAD.af_het /
                      item.gnomAD.hap_af_het_map[getFirstHaplogroup])
                      | precisionTo
                  }}
                  <span class="extraInfo">
                    <br />
                    Highlighted because the Ratio is smaller than 0.5 or larger
                    than 2<br />
                    and the Global Freq or Haplogroup Freq is larger than 0.0001
                  </span>
                </span>
              </span></span
            >
          </v-tooltip>
        </template>

        <!-- gnomAD Hom -->
        <template v-slot:item.gnomAD.af_hom="{ item }">
          <v-tooltip top>
            <template v-slot:activator="{ on, attrs }">
              <span v-bind="attrs" v-on="on">
                <span
                  v-if="item.gnomAD && item.gnomAD.af_hom > 0"
                  :class="
                    'gnomADspan ' +
                      ((item.gnomAD.af_hom > 0.0001 ||
                        item.gnomAD.hap_af_hom_map[getFirstHaplogroup] >
                          0.0001) &&
                      (hapRatios[item.id].homRatio > 2 ||
                        hapRatios[item.id].homRatio < 0.5)
                        ? 'highlightMe'
                        : '')
                  "
                >
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
                  {{
                    item.gnomAD.hap_af_hom_map[getFirstHaplogroup] | precisionTo
                  }}
                </span>
              </span>
            </template>
            <span class="text-caption">
              <span
                class="gnomADspan"
                v-if="item.gnomAD && item.gnomAD.af_hom > 0"
              >
                Global gnomAD Freq: {{ item.gnomAD.af_hom | precisionTo }}</span
              >
              <v-icon class="worldIcon tooltipIcon">mdi-earth</v-icon>

              <span
                v-if="
                  item.gnomAD &&
                    item.gnomAD.af_hom > 0 &&
                    item.gnomAD.hap_af_hom_map &&
                    getFirstHaplogroup
                "
              >
                <br />
                <span class="haplogroupIcon tooltipIcon">{{
                  getFirstHaplogroup
                }}</span>
                Haplogroup ({{ getFirstHaplogroup }}) Freq:
                {{
                  item.gnomAD.hap_af_hom_map[getFirstHaplogroup] | precisionTo
                }}
                <br />
                <span
                  :class="
                    (item.gnomAD.af_hom > 0.0001 ||
                      item.gnomAD.hap_af_hom_map[getFirstHaplogroup] >
                        0.0001) &&
                    (hapRatios[item.id].homRatio > 2 ||
                      hapRatios[item.id].homRatio < 0.5)
                      ? 'highlightMe'
                      : ''
                  "
                >
                  <v-icon class="tooltipIcon">mdi-contrast-box</v-icon> Ratio
                  (Global / Haplogroup):
                  {{
                    (item.gnomAD.af_hom /
                      item.gnomAD.hap_af_hom_map[getFirstHaplogroup])
                      | precisionTo
                  }}
                  <span class="extraInfo">
                    <br />
                    Highlighted because the Ratio is smaller than 0.5 or larger
                    than 2<br />
                    and the Global Freq or Haplogroup Freq is larger than 0.0001
                  </span>
                </span>
              </span></span
            >
          </v-tooltip>
        </template>

        <!-- Heteroplasmy Distribution -->
        <template v-slot:item.gnomAD.hl_hist="{ item }">
          <div v-if="heteroplasmyDistExists(item)" class="hist-wrapper">
            <v-sparkline
              :value="heteroplasmyDistHeights(item)"
              type="bar"
              :gradient="[COLORS.PRIMARY, COLORS.PRIMARY_LIGHT]"
              radius="10"
              padding="1"
              smooth="1"
              gradient-direction="top"
              auto-line-width
              height="45"
            >
            </v-sparkline>
          </div>
        </template>

        <!-- Age Distribution Homoplasmic -->
        <template v-slot:item.gnomAD.age_hist_hom="{ item }">
          <div v-if="ageDistExists(item)" class="hist-wrapper">
            <v-sparkline
              :value="ageDistHomoHeights(item)"
              type="bar"
              :gradient="[COLORS.PRIMARY, COLORS.PRIMARY_LIGHT]"
              radius="10"
              padding="3"
              smooth="1"
              gradient-direction="top"
              auto-line-width
              height="45"
            >
            </v-sparkline>
          </div>
        </template>

        <template v-slot:item.id="{ item }">
          <CurationCell
            :variantId="item.id"
            :key="item.id"
            :show-quick-tags="showQuickTags"
            :expanded="transitioned[item.id]"
            :tag-repo="tagRepo"
            :tag-store="tagStore"
            v-on:tags-updated="onTagsUpdated"
          ></CurationCell>
        </template>

        <template v-slot:item.Disease="{ item }">
          <span v-if="item.Disease">
            <v-icon>mdi-biohazard</v-icon>&nbsp;{{ item.Disease }}
          </span>
        </template>

        <template v-slot:expanded-item="{ headers }">
          <td colspan="4" class="expandedVariant">
            <VariantInfo :variantId="variantId"></VariantInfo>
          </td>
          <td class="expandedVariant">
            <VariantCuration
              :variantId="variantId"
              :tag-repo="tagRepo"
              :tag-store="tagStore"
            ></VariantCuration>
          </td>
          <td :colspan="headers.length - 4" class="expandedVariant">
            <VariantCharts :variantId="variantId"></VariantCharts>
          </td>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'

import GeneCardsLink from '@/components/GeneCardsLink'
import CurationCell from '@/components/CurationCell'
import VariantInfo from '@/components/VariantInfo'
import VariantCuration from '@/components/VariantCuration'
import VariantCharts from '@/components/VariantCharts'
import VariantLinks from '@/components/VariantLinks'
import VariantTableMitoTip from '@/components/VariantTableMitoTip'
import { mitoTIPsort, mitoTIPfilter } from '@/components/VariantTableMitoTip'

import VariantTableHeader from '@/components/VariantTableHeader'
import * as filters from '@/shared/variantFilters'
import * as vueFilters from '@/shared/vueFilters'
import * as _ from 'lodash'
import * as $ from 'jquery'
import Vue from 'vue'
import {
  DEFAULT_VARIANT_SEARCH,
  VARIANT_MASKS,
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
    tagRepo: {
      type: Object,
      required: true,
    },
    tagStore: {
      type: Object,
      required: true,
    },
  },

  components: {
    CurationCell,
    GeneCardsLink,
    VariantLinks,
    VariantInfo,
    VariantCuration,
    VariantCharts,
    VariantTableHeader,
    VariantTableMitoTip,
  },

  mounted() {
    this.toggleVariantById(this.variantId)

    if (this?.allSavedSearches) {
      const found = this.allSavedSearches.find(
        ss => ss && ss.name === 'Current'
      )

      if (found) {
        this.onSavedSearchChange(found)
      } else {
        this.onSavedSearchChange(DEFAULT_VARIANT_SEARCH)
        this.selectedSavedSearch = DEFAULT_VARIANT_SEARCH
      }
    }
  },

  beforeDestroy() {
    this.saveCurrentSearch()
  },

  data: () => {
    return {
      filterConfig: {
        posRange: [0, MAX_POS],
        allele: '',
        selectedTypes: [],
        selectedGenes: [],
        selectedMasks: [],
        selectedConsequences: [],
        selectedMitoTIP: [],
        gnomADHap: [],
        vafRange: [0, 1],
        gbFreqTickIndex: 6,
        gnomADHetFreqTickIndex: 8,
        gnomADHomFreqTickIndex: 8,
        disease: '',
        diseaseShowBlank: false,
        curationSearch: '',
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
      vafTicks: [0, 0.01, 0.02, 0.03, 0.05, 0.1, 1],
      vafIndexRange: [1, 5],
      mafTicks: [0, 0.01, 0.02, 0.03, 0.05, 0.1, 1],
      mafIndexRange: [0, 6],
      gbFreqTicks: [0.0, 0.001, 0.002, 0.005, 0.01, 0.1, 1.0],
      mitoTipQuartiles: [
        'Confirmed pathogenic',
        'Likely pathogenic',
        'Possibly pathogenic',
        'Possibly benign',
        'Likely benign',
      ],
      gnomADHetFreqTicks: [
        0.0,
        0.00005,
        0.0001,
        0.0002,
        0.0005,
        0.001,
        0.01,
        0.1,
        1.0,
      ],
      gnomADHomFreqTicks: [
        0.0,
        0.00005,
        0.0001,
        0.0002,
        0.0005,
        0.001,
        0.01,
        0.1,
        1.0,
      ],
      tableOptions: {
        // page: 1,
        itemsPerPage: 50,
        multiSort: true,
      },
      tableFooterProps: {
        itemsPerPageOptions: [10, 20, 50, 100, 200],
      },
      rules: {
        required: value => !!value || 'Required.',
      },
      expandedVariants: [],
      transitioned: [],
      closeTimeouts: {},
      activeVariant: null,
      displayHaplodata: false,
      showQuickTags: false,
      currentVariants: [],
    }
  },

  computed: {
    ...mapState([
      'filteredVariants',
      'maternalVariants',
      'maxReadDepth',
      'settings',
      'sampleId',
    ]),
    ...mapGetters([
      'getSettingsBamFile',
      'getSampleSettings',
      'getCurationByVariantId',
      'getHaplogroups',
      'getFirstHaplogroup',
      'getFirstFullHaplogroup',
      'hasMaternalVariants',
    ]),
    tableFilteredVariants() {
      return Object.values(this.filteredVariants)
    },
    allMasksSelected() {
      return this.filterConfig.selectedMasks.length === VARIANT_MASKS.length
    },
    someMasksSelected() {
      return (
        this.filterConfig.selectedMasks.length > 0 && !this.allMasksSelected
      )
    },
    maskSelectAllIcon() {
      if (this.allMasksSelected) return 'mdi-checkbox-marked'
      if (this.someMasksSelected) return 'mdi-minus-box'
      return 'mdi-checkbox-blank-outline'
    },

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

    allSavedSearchesDisplay() {
      const result = [DEFAULT_VARIANT_SEARCH]
        .concat(this.allSavedSearches)
        .filter(s => s && s.name !== 'Current')

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
      const allHeaders = [
        {
          text: 'HGVS.g',
          value: 'pos',
          width: '85',
          filter: this.hgvsgFilter,
        },
        {
          text: 'Gene',
          // tooltip: 'Gene tooltip',
          value: 'symbols',
          width: '85',
          filter: this.genesFilter,
        },
        {
          text: 'Consequence',
          tooltip: 'Sorting is on severity of consequence',
          value: 'consequence',
          width: '100',
          sort: this.consequenceSort,
          filter: this.consequenceFilter,
        },
        {
          text: 'MitoTIP',
          value: 'mitotip',
          width: '100',
          sort: mitoTIPsort,
          filter: (value, search, item) => {
            return mitoTIPfilter(this.filterConfig.selectedMitoTIP, item)
          },
        },
        {
          text: 'Curation',
          value: 'id',
          width: '50%',
          sort: this.curationSort,
          filter: (value, search, item) => this.curationFilter(item),
        },
        {
          text: 'Heteroplasmy of Sample\n',
          tooltip: 'Heteroplasmy freq of variant in sample',
          value: 'genotypes[0].AF',
          width: '80',
          filter: this.vafFilter,
        },
        {
          text: 'Maternal Heteroplasmy',
          tooltip: 'Maternal Info',
          value: 'maternal',
          width: '80',
          filter: this.mafFilter,
        },
        {
          text: 'Genbank Frequency\n',
          // tooltip: 'Genbank % tooltip',
          value: 'gbFreq',
          width: '80',
          filter: this.gbFreqFilter,
        },

        {
          text: 'gnomAD Freq (Heteroplasmic)',
          tooltip:
            'Proportion of individuals with variant at heteroplasmy between 0.10 - 0.95 in gnomAD',
          value: 'gnomAD.af_het',
          width: '80',
          filter: this.gnomADHetFreqFilter,
        },

        {
          text: 'gnomAD Freq (Homoplasmic)',
          tooltip:
            'Proportion of individuals with variant at homoplasmy (heteroplasmy >= 0.95) in gnomAD',
          value: 'gnomAD.af_hom',
          width: '80',
          filter: this.gnomADHomFreqFilter,
        },
        {
          text: 'Heteroplasmy Distribution',
          tooltip: 'Heteroplasmy Distribution of variant from gnomAD',
          value: 'gnomAD.hl_hist',
          sortable: false,
          width: '120',
        },
        {
          text: 'Age Distribution (Homoplasmic)',
          tooltip: 'Age Distribution (Homoplasmic) from gnomAD',
          value: 'gnomAD.age_hist_hom',
          sortable: false,
          width: '120',
        },
        {
          text: 'Read Depth\n',
          value: 'DP',
          sortable: true,
          width: '60',
        },
      ]
      const withoutMaternal = allHeaders.filter(h => h.value !== 'maternal')

      return this.hasMaternalVariants ? allHeaders : withoutMaternal
    },

    types() {
      return [
        ...new Set(Object.values(this.filteredVariants).map(row => row.type)),
      ]
    },

    hapRatios() {
      return Object.values(this.filteredVariants).reduce((map, variant) => {
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

    allMasks() {
      return VARIANT_MASKS
    },

    // Change to allGenes?
    genes() {
      const genes = [
        ...new Set(
          Object.values(this.filteredVariants)
            .filter(row => row.symbols)
            .map(row => row.symbols)
            .flat()
        ),
      ]

      return genes
    },

    consequences() {
      const consequences = [
        ...new Set(
          Object.values(this.filteredVariants)
            .filter(row => row.consequence)
            .map(row => row.consequence)
            .flat()
        ),
      ]

      return consequences
    },

    mafLastTickIndex() {
      return this.mafTicks.length - 1
    },

    vafLastTickIndex() {
      return this.vafTicks.length - 1
    },

    gbFreqLastTickIndex() {
      return this.gbFreqTicks.length - 1
    },

    gnomADHetFreqLastTickIndex() {
      return this.gnomADHetFreqTicks.length - 1
    },

    gnomADHomFreqLastTickIndex() {
      return this.gnomADHomFreqTicks.length - 1
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

    variantTagsForSearch() {
      let result = []
      for (const entity of Object.values(this.tagStore || {})) {
        const variantTags = entity?.tags
        if (!_.isEmpty(variantTags)) {
          result[entity.id] = variantTags
        }
      }

      return result
    },
    // initTagsFromTagRepo: function(tagRepo) {
    //   if (tagRepo && typeof tagRepo.get === 'function') {
    //     for (const item of Object.values(this.filteredVariants)) {
    //       const entity = tagRepo.get(item.id)
    //       const variantTags = entity?.tags
    //       if (!_.isEmpty(variantTags)) {
    //         Vue.set(this.variantTagsForSearch, item.id, variantTags)
    //       }
    //     }
    //   }
    // },
  },

  methods: {
    toggleAllMasks() {
      this.$nextTick(() => {
        if (this.allMasksSelected) {
          this.filterConfig.selectedMasks = []
        } else {
          this.filterConfig.selectedMasks = VARIANT_MASKS.map(mask => mask.name)
        }
      })
    },

    toggleHaplodata() {
      document.getElementById('app').classList.toggle('showHaplodata')
    },

    currentItems(items) {
      this.currentVariants = items
    },

    toggleVariantById: function(variantId) {
      const variant = this.filteredVariants[variantId]
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
        this.$refs.variantTable.expand(variant, true)
        this.$nextTick(() => {
          this.$set(this.transitioned, id, true)
          this.expandedVariants.forEach(ev => {
            ev !== variant && this.closeVariantExpansion(ev)
          })
        })

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
      this.$refs.variantTable.expand(variant, false)
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
    },

    saveCurrentSearch: function() {
      const toSave = {
        name: 'Current',
        description: 'Currently configured filters',
        custom: true,
        filterConfig: { ...this.filterConfig },
      }
      toSave.filterConfig.vafRange = [
        this.vafTicks[this.vafIndexRange[0]],
        this.vafTicks[this.vafIndexRange[1]],
      ]
      this.$store.dispatch('saveSearch', toSave)
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

    mafFilter: function(value) {
      if (value) {
        let lower = this.mafTicks[this.mafIndexRange[0]]
        let upper = this.mafTicks[this.mafIndexRange[1]]

        return filters.rangeTextFilter(`${lower}-${upper}`, value.heteroplasmy)
      } else {
        return true
      }
    },

    gbFreqFilter: function(value) {
      let lower = 0
      let upper = this.gbFreqTicks[this.filterConfig.gbFreqTickIndex]

      return filters.rangeTextFilter(`${lower}-${upper}`, value || 0.0)
    },

    gnomADHetFreqFilter: function(value) {
      let lower = 0
      let upper = this.gnomADHetFreqTicks[
        this.filterConfig.gnomADHetFreqTickIndex
      ]
      return filters.rangeTextFilter(`${lower}-${upper}`, value || 0.0)
    },

    gnomADHomFreqFilter: function(value) {
      let lower = 0
      let upper = this.gnomADHomFreqTicks[
        this.filterConfig.gnomADHomFreqTickIndex
      ]

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

    maskFilter: function(pos) {
      var result = true

      this.filterConfig.selectedMasks.forEach(activeMask => {
        var mask = VARIANT_MASKS?.find(d => d.name == activeMask)
        pos = parseInt(pos)

        if (!mask) {
          console.error('Error finding mask:', activeMask)
        }

        if (mask.end > mask.start) {
          if (mask.start < pos && pos < mask.end) {
            result = false
          }
        } else {
          if (pos < mask.end || mask.start < pos) {
            result = false
          }
        }
      })
      return result
    },

    genesFilter: function(value, search, item) {
      return (
        filters.setInSetFilter(this.filterConfig.selectedGenes, value) &&
        this.maskFilter(item.pos)
      )
    },

    convertHGVSg: function(item, limited) {
      return (
        item.HGVS ||
        item.id.replace(/chrM-(\d+)-(\w+)-(\w+)/, function(original, a, b, c) {
          if (limited && c.length > 3) {
            c = `${c[0]}…${c[c.length - 1]}`
          }
          return `m.${a}${b}>${c}`
        })
      )
    },

    hgvsgFilter: function(value, search, item) {
      return (
        filters.iContainsFilter(
          this.filterConfig.hgvsg,
          this.convertHGVSg(item)
        ) && this.posFilter(item.pos)
      )
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

    removeSelectedMask: function(toRemove) {
      this.filterConfig.selectedMasks = this.filterConfig.selectedMasks.filter(
        selected => selected !== toRemove
      )
    },

    removeSelectedGene: function(toRemove) {
      this.filterConfig.selectedGenes = this.filterConfig.selectedGenes.filter(
        selected => selected !== toRemove
      )
    },

    removeSelectedMitoQuartile: function(toRemove) {
      this.filterConfig.selectedMitoTIP = this.filterConfig.selectedMitoTIP.filter(
        selected => selected !== toRemove
      )
    },

    removeSelectedConsequence: function(toRemove) {
      this.filterConfig.selectedConsequences = this.filterConfig.selectedConsequences.filter(
        selected => selected !== toRemove.displayTerm
      )
    },

    consequenceFilter: function(value) {
      return filters.consequenceFilter(
        this.filterConfig.selectedConsequences,
        value
      )
    },

    onTagsUpdated: function(variantId, tags) {
      console.debug('VariantTable: onTagsUpdated', variantId, tags)
      // Vue.set(this.variantTagsForSearch, variantId, tags)
    },

    curationSort: function(l, r) {
      return this.curationWeight(l) - this.curationWeight(r)
    },

    curationWeight: function(id) {
      const variant = this.filteredVariants[id]
      const curation = this.getCurationByVariantId(id)
      let weight = 0

      if (variant.Disease) weight += 2
      if (curation.variantNote) weight++

      // Should hapWeight be added? Or just be given a floor of 1?
      if (this.displayHaplodata) {
        // if (this.hapRatios[id]?.hapWeight > 1)
        weight += this.hapRatios[id]?.hapWeight || 0
      }
      // Add weight for tag names here, still apply "selectedTagNames" in case custom names are used.
      if (curation?.selectedTagNames) weight++
      const blacklist = ['Excluded', 'Mismatch', 'FalsePositive']
      blacklist.forEach(item => {
        if (curation?.selectedTagNames?.indexOf(item) >= 0) weight = -1
      })

      const whitelist = ['Review', 'Likely', 'Match']
      whitelist.forEach(item => {
        if (curation?.selectedTagNames?.indexOf(item) >= 0) weight += 5
      })

      return weight
    },

    consequenceSort: function(l, r) {
      return l?.rank - r?.rank || -1
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

    shortenConsequenceOntology(consequence) {
      if (!consequence) return ''
      var result = consequence.replace('variant', '').trim()
      if (result.length > 25) {
        result = result
          .split(' ')
          .map(word => word.slice(0, 1))
          .join('')
          .toUpperCase()
      }
      return result
    },

    gnomADHapSort: function(l, r) {
      return weight(l) - weight(r)

      function weight(gnomAD) {
        return gnomAD !== undefined ? (gnomAD ? 1 : 0) : -1
      }
    },

    curationFilter: function(item) {
      const variantTags = this.variantTagsForSearch[item.id]
        ? Object.values(this.variantTagsForSearch[item.id])
        : []
      const disease = this.filteredVariants[item.id]?.Disease

      return filters.curationFilter(
        this.filterConfig.curationSearch,
        variantTags,
        disease
      )
    },

    diseaseFilter: function(value) {
      return filters.iContainsFilter(
        this.filterConfig.disease,
        value,
        this.filterConfig.diseaseShowBlank
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

    updateVariantTagsForSearch: function(tagRepo) {
      if (tagRepo && typeof tagRepo.get === 'function') {
        for (const item of Object.values(this.filteredVariants)) {
          const entity = tagRepo.get(item.id)
          const variantTags = entity?.tags
          if (!_.isEmpty(variantTags)) {
            Vue.set(this.variantTagsForSearch, item.id, variantTags)
          }
        }
      }
    },
  },

  watch: {
    filteredVariants: function() {
      this.toggleVariantById(this.variantId)
    },

    $route: function() {
      this.toggleVariantById(this.variantId)
    },

    displayHaplodata: function() {
      this.toggleHaplodata(this.displayHaplodata)
    },

    showQuickTags: function() {
      if (this.showQuickTags) {
        $('th.text-start.sortable.active.asc')
          .click()
          .click()
        $('th.text-start.sortable.active.desc').click()
      }
    },

    tagRepo: {
      handler(updatedTagRepo) {
        this.updateVariantTagsForSearch(updatedTagRepo)
      },
      deep: true,
    },
  },

  filters: {
    precisionTo: vueFilters.precisionTo,
  },
}
</script>

<style lang="scss" scoped>
.v-tooltip__content {
  background: rgba(97, 97, 97, 1) !important;
  opacity: 1 !important;
  ul {
    list-style: none;
    padding-left: 0;
  }
}

.variant-table-v-select {
  font-size: 0.8em;
}

.variant-expanded >>> .v-data-table__expanded__row {
  background-color: rgba(0, 0, 0, 0.12);
}

.row.curation-search {
  margin: 0;
  width: 100%;
}
.row {
  margin: 0 -12px;
}

.secondary-info {
  color: #aaa;
}
#variantTable .row {
  /* fix header alignment incorrect - seems vuetify issue changed .row to have -12px top margin in recent update causing vertical overlap */
  margin-top: auto;
}
.hist-wrapper {
  padding: 2px;
}
span.filterActive {
  text-decoration: 3px underline lightblue !important;
}
</style>
