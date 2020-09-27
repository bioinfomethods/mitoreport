import { DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import Vue from 'vue'
import Vuex from 'vuex'
import defaultSettings from '../fixtures/defaultSettings.json'
import deletions from '../fixtures/deletions.json'
import variants from '../fixtures/variants.json'

Vue.use(Vuex)

export const state = {
  settings: defaultSettings,
  loading: false,
  snackbar: DEFAULT_SNACKBAR_OPTS,
  variants: variants,
  deletions: deletions,
}

export const mutations = {}

export const actions = {
  fetchData() {},
}

export const getters = {
  sample: () => {
    return 'TestSample'
  },

  igvHost: () => {
    return 'http://localhost:60151'
  },

  sampleSettings: state => {
    return defaultSettings.samples.find(
      sample => sample.id === getters.sample(state)
    )
  },

  settingsBamDir: () => {
    return defaultSettings.sampleBamDir
  },

  settingsBamFilename: () => {
    return defaultSettings.bamFilename
  },

  settingsBamFile: () => {
    return `${defaultSettings.sampleBamDir}${defaultSettings.sampleBamFilename}`
  },
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  strict: process.env.NODE_ENV !== 'production',
})
