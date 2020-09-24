import Vue from 'vue'
import Vuex from 'vuex'
import defaultSettings from '../fixtures/defaultSettings.json'
import deletions from '../fixtures/deletions.json'
import variants from '../fixtures/variants.json'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
    defaultSettings: defaultSettings,
    loading: false,
    variants: variants,
    deletions: deletions,
  },

  mutations: {},

  actions: {
    fetchData() {},
  },

  getters: {
    sample: () => {
      return 'TestSample'
    },
    bamFile: () => {
      return `${defaultSettings.sampleBamDir}${defaultSettings.sampleBamFilename}`
    },
  },
})

export default store
