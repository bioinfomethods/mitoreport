import { concatSearches } from '@/services/LocalDataService'
import { DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import * as _ from 'lodash'
import Vue from 'vue'
import Vuex from 'vuex'
import {
  DEFAULT_GENECARDS_URL_PREFIX,
  DEFAULT_HMT_VAR_URL_PREFIX,
  DEFAULT_IGV_HOST,
} from '../../src/shared/constants'
import defaultSettings from '../fixtures/defaultSettings.json'
import deletions from '../fixtures/deletions.json'
import mitoSettings from '../fixtures/mitoSettings.json'
import variants from '../fixtures/variants.json'
Vue.use(Vuex)

export const state = {
  settings: _.mergeWith(defaultSettings, mitoSettings, concatSearches),
  loading: false,
  snackbar: DEFAULT_SNACKBAR_OPTS,
  variants: variants,
  deletions: deletions,
}

export const mutations = {}

export const actions = {
  fetchData() {},
  saveBamDir() {},
  saveSearch() {},
  deleteSearch() {},
  saveSettings() {},
  downloadSettings() {},
  closeSnackbar() {},
}

export const getters = {
  sample: () => {
    return 'TestSample'
  },

  igvHost: () => {
    return DEFAULT_IGV_HOST
  },

  geneCardsUrlPrefix: () => {
    return DEFAULT_GENECARDS_URL_PREFIX
  },

  hmtVarUrlPrefix: () => {
    return DEFAULT_HMT_VAR_URL_PREFIX
  },

  sampleSettings: state => {
    return defaultSettings.samples.find(
      sample => sample.id === getters.sample(state)
    )
  },

  settingsBamDir: state => {
    return getters.sampleSettings(state).bamDir
  },

  settingsBamFilename: () => {
    return getters.sampleSettings(state).bamFilename
  },

  settingsBamFile: state => {
    return `${getters.settingsBamDir(state)}${getters.settingsBamFilename(
      state
    )}`
  },
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  strict: process.env.NODE_ENV !== 'production',
})
