import Vue from 'vue'
import Vuex from 'vuex'
import deletions from '../fixtures/deletions.json'
import variants from '../fixtures/variants.json'

Vue.use(Vuex)

const store = new Vuex.Store({
  state: {
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
  },
})

export default store
