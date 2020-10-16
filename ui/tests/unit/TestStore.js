import { customSettingsMerger } from '@/services/LocalDataService'
import { DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import * as _ from 'lodash'
import Vue from 'vue'
import Vuex from 'vuex'
import {
  DEFAULT_GENECARDS_URL_PREFIX,
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
  settings: _.mergeWith(defaultSettings, mitoSettings, customSettingsMerger),
  loading: false,
  snackbar: DEFAULT_SNACKBAR_OPTS,
  variants: variants.map(mapVariant),
  deletions: deletions,
  maxReadDepth: 99999,
}

export const mutations = {}

export const actions = {
  fetchData() {},
  saveBamDir() {},
  saveSearch() {},
  deleteSearch() {},
  saveCuration() {},
  saveSettings() {},
  downloadSettings() {},
  closeSnackbar() {},
}

export const getters = {
  getSample: () => {
    return '15G002035-GM12878K_20pc_10kb_200'
  },

  getIgvHost: () => {
    return DEFAULT_IGV_HOST
  },

  getGeneCardsUrlPrefix: () => {
    return DEFAULT_GENECARDS_URL_PREFIX
  },

  getHmtVarUrlPrefix: () => {
    return DEFAULT_HMT_VAR_URL_PREFIX
  },

  getSampleSettings: state => {
    return defaultSettings.samples.find(
      sample => sample.id === getters.getSample(state)
    )
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

  getCurationByVariantId: realGetters.getCurationByVariantId,
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  strict: process.env.NODE_ENV !== 'production',
})
