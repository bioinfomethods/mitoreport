import { getDeletions, getVariants } from '@/services/LocalDataService.js'
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export const state = {
  loading: false,
  variants: [],
  deletions: {},
}

export const getters = {
  sample: state => {
    if (!state.deletions) {
      return 'No Sample'
    }

    const samples = Object.keys(state.deletions)
    if (samples.length > 0) {
      return samples[0]
    } else {
      return 'No Sample'
    }
  },
}

export const mutations = {
  SET_LOADING(state) {
    state.loading = true
  },
  UNSET_LOADING(state) {
    state.loading = false
  },
  SET_VARIANTS(state, variants) {
    state.variants = variants
  },
  SET_DELETIONS(state, deletions) {
    state.deletions = deletions
  },
}

export const actions = {
  fetchData({ commit }) {
    commit('SET_LOADING')
    Promise.all([getVariants(), getDeletions()])
      .then(responses => {
        let varResp, delResp
        ;[varResp, delResp] = responses
        commit('SET_VARIANTS', varResp.data)
        commit('SET_DELETIONS', delResp.data)
      })
      .catch(error => {
        console.error(`There was a problem fetching data ${error.message}`)
      })
      .finally(() => {
        commit('UNSET_LOADING')
      })
  },
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  strict: process.env.NODE_ENV !== 'production',
})
