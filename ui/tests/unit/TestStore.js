import { settingsSampleMerger } from '@/services/LocalDataService'
import { DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import * as _ from 'lodash'
import Vue from 'vue'
import Vuex from 'vuex'
import {
  DEFAULT_GENECARDS_URL_PREFIX,
  DEFAULT_HAPLOGROUPS,
  DEFAULT_FIRST_HAPLOGROUP,
  DEFAULT_HMT_VAR_URL_PREFIX,
  DEFAULT_IGV_HOST,
} from '../../src/shared/constants'
import { getters as realGetters } from '@/store'
import defaultSettings from '../fixtures/defaultSettings.json'
import deletions from '../fixtures/deletions.json'
import mitoSettings from '../fixtures/mitoSettings.json'
import variants from '../fixtures/variants.json'

Vue.use(Vuex)

const mapVariant = function(variant) {
  let result = { ...variant }
  result['ref_alt'] = `${result.ref}/${result.alt}`
  return result
}

export const state = {
  sampleId: '15G002035-GM12878K_20pc_10kb_200',
  settings: _.mergeWith(defaultSettings, mitoSettings, settingsSampleMerger),
  loading: false,
  snackbar: DEFAULT_SNACKBAR_OPTS,
  variants: variants.map(mapVariant),
  maternalVariants: {},
  filteredVariants: variants.map(mapVariant),
  deletions: deletions,
  maxReadDepth: 99999,
  syncFeature: false,
}

export const mutations = {}

export const actions = {
  setSyncFeature() {},
  fetchData() {},
  saveAppSettings() {},
  saveSearch() {},
  deleteSearch() {},
  saveCuration() {},
  saveSettings() {},
  closeSnackbar() {},
  filterImportantVariants() {},
}

export const getters = {
  getIgvHost: () => {
    return DEFAULT_IGV_HOST
  },

  getGeneCardsUrlPrefix: () => {
    return DEFAULT_GENECARDS_URL_PREFIX
  },

  getHaplogroups: () => {
    return DEFAULT_HAPLOGROUPS
  },

  getFirstHaplogroup: () => {
    return DEFAULT_FIRST_HAPLOGROUP
  },

  getFirstFullHaplogroup: () => {
    return DEFAULT_FIRST_HAPLOGROUP
  },

  getHmtVarUrlPrefix: () => {
    return DEFAULT_HMT_VAR_URL_PREFIX
  },

  getSampleSettings: () => {
    return defaultSettings.sample
  },

  getSettingsCouchDbUrl: state => {
    return getters.getSampleSettings(state).couchDbUrl
  },

  getSettingsBamDir: state => {
    return getters.getSampleSettings(state).bamDir
  },

  getSettingsBamFilename: () => {
    return getters.getSampleSettings(state).bamFilename
  },

  getSettingsBamFile: state => {
    return `${getters.getSettingsBamDir(state)}${getters.getSettingsBamFilename(
      state
    )}`
  },

  getVariantById: state => id => {
    return state.variants.find(v => v.id === id)
  },

  getVariantTags: () => {
    return [
      { name: 'Review', custom: false },
      { name: 'Excluded', custom: false },
      { name: 'FalsePositive', custom: false },
      { name: 'Likely', custom: false },
      { name: 'Match', custom: false },
      { name: 'Mismatch', custom: false },
    ]
  },

  getImportantVariantTags: realGetters.getImportantVariantTags,

  getCurationByVariantId: realGetters.getCurationByVariantId,
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  strict: process.env.NODE_ENV !== 'production',
})
