import {
  getDeletions,
  getVariants,
  saveSettingsToLocal,
} from '@/services/LocalDataService.js'
import { DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import { saveAs } from 'file-saver'
import * as _ from 'lodash'
import { v4 as uuidv4 } from 'uuid'
import Vue from 'vue'
import Vuex from 'vuex'
import { loadSettings } from '../services/LocalDataService'
import {
  DEFAULT_GENECARDS_URL_PREFIX,
  DEFAULT_HMT_VAR_URL_PREFIX,
  DEFAULT_IGV_HOST,
} from '../shared/constants'

Vue.use(Vuex)

export const state = {
  settings: {},
  loading: false,
  snackbar: { ...DEFAULT_SNACKBAR_OPTS },
  variants: [],
  maxReadDepth: 0,
  deletions: {},
}

export const getters = {
  getSample: state => {
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

  getIgvHost: state => {
    return state.settings.igvHost || DEFAULT_IGV_HOST
  },

  getGeneCardsUrlPrefix: state => {
    return state.settings.geneCardsUrlPrefix || DEFAULT_GENECARDS_URL_PREFIX
  },

  getHmtVarUrlPrefix: state => {
    return state.settings.hmtVarUrlPrefix || DEFAULT_HMT_VAR_URL_PREFIX
  },

  getSampleSettings: state => {
    const result = (state.settings?.samples || []).find(
      sample => sample.id === getters.getSample(state)
    )
    return result || {}
  },

  getSettingsBamDir: state => {
    return getters.getSampleSettings(state)?.bamDir
  },

  getSettingsBamFilename: state => {
    return getters.getSampleSettings(state)?.bamFilename
  },

  getSettingsBamFile: state => {
    const sampleBamDir = getters.getSettingsBamDir(state)
    const sampleBamFilename = getters.getSettingsBamFilename(state)
    if (!sampleBamDir || !sampleBamFilename) {
      return null
    }

    return `${sampleBamDir}${sampleBamFilename}`
  },

  getVariantById: state => variantId => {
    return state.variants.find(v => v.id === variantId)
  },

  getVariantTags: state => {
    return getters.getSampleSettings(state)?.variantTags || []
  },

  getCurationByVariantId: state => variantId => {
    const sampleCurations = getters.getSampleSettings(state)?.curations || []

    return sampleCurations.find(c => c.variantId === variantId) || {}
  },
}

export const mutations = {
  SET_SETTINGS(state, settings) {
    state.settings = settings
  },

  SET_LOADING(state) {
    state.loading = true
  },

  UNSET_LOADING(state) {
    state.loading = false
  },

  SET_VARIANTS(state, variants) {
    state.variants = variants.map(variant => {
      let result = { ...variant }

      result['ref_alt'] = `${result.ref}/${result.alt}`

      return result
    })
    const uniqReadDepths = _.union(state.variants.map(v => _.toNumber(v.DP)))
    state.maxReadDepth = _.max(uniqReadDepths)
  },

  SET_DELETIONS(state, deletions) {
    state.deletions = deletions
  },

  SET_BAM_DIR(state, newBamDir) {
    getters.getSampleSettings(state).bamDir = newBamDir
  },

  ACTIVATE_SNACKBAR(state, options) {
    state.snackbar = _.merge(DEFAULT_SNACKBAR_OPTS, { active: true }, options)
  },

  DEACTIVATE_SNACKBAR(state) {
    state.snackbar = _.merge(DEFAULT_SNACKBAR_OPTS, { active: false })
  },

  SET_SAVED_SEARCH(state, searchConfig) {
    if (!searchConfig || !searchConfig.name || !searchConfig.filterConfig) {
      return
    }
    const allCustomSearches = getters
      .getSampleSettings(state)
      .variantSearches.filter(vs => {
        return vs.custom
      })
    const existingSearch = allCustomSearches.find(
      vs => vs.name === searchConfig.name
    )
    if (!existingSearch) {
      getters.getSampleSettings(state).variantSearches.push(searchConfig)
    } else {
      existingSearch.description = searchConfig.description
      existingSearch.filterConfig = Object.assign({}, searchConfig.filterConfig)
    }
  },

  DELETE_SAVED_SEARCH(state, searchToDelete) {
    getters.getSampleSettings(
      state
    ).variantSearches = getters
      .getSampleSettings(state)
      .variantSearches.filter(vs => {
        return vs.name !== searchToDelete.name
      })
  },

  SET_CURATION(state, curationToSave) {
    let existingCurations = getters.getSampleSettings(state)?.curations || []
    let existing = existingCurations.find(
      c => c.variantId === curationToSave.variantId
    )

    if (!existing) {
      existing = { id: uuidv4() }
      existingCurations.push(existing)
    }

    existing.variantId = curationToSave.variantId
    existing.selectedTagNames = curationToSave.selectedTags.map(ct => ct.name)
    existing.variantNote = curationToSave.variantNote
  },
}

export const actions = {
  async fetchData({ commit }) {
    commit('SET_LOADING')

    Promise.all([loadSettings(), getVariants(), getDeletions()])
      .then(responses => {
        let settingsResp, varResp, delResp
        ;[settingsResp, varResp, delResp] = responses
        commit('SET_SETTINGS', settingsResp.data)
        commit('SET_VARIANTS', varResp.data)
        commit('SET_DELETIONS', delResp.data)
      })
      .catch(error => {
        commit('ACTIVATE_SNACKBAR', {
          color: 'red',
          message: `There was a problem fetching data: ${error.message}`,
        })
      })
      .finally(() => {
        commit('UNSET_LOADING')
      })
  },

  saveBamDir({ commit }, newBamDir) {
    commit('SET_BAM_DIR', newBamDir)
  },

  saveSearch({ commit, dispatch }, searchConfig) {
    commit('SET_SAVED_SEARCH', searchConfig)
    dispatch('saveSettings')
  },

  deleteSearch({ commit }, searchToDelete) {
    if (searchToDelete.custom) {
      commit('DELETE_SAVED_SEARCH', searchToDelete)
    }
  },

  saveCuration({ commit, dispatch }, curationToSave) {
    commit('SET_CURATION', curationToSave)
    dispatch('saveSettings')
  },

  saveSettings({ commit, state }) {
    saveSettingsToLocal(state.settings).catch(error => {
      commit('ACTIVATE_SNACKBAR', {
        color: 'red',
        message: `There was a problem saving settings: ${error.message}`,
      })
    })
  },

  downloadSettings({ state }) {
    var blob = new Blob([JSON.stringify(state.settings, null, 2)], {
      type: 'text/json;charset=utf-8',
    })
    saveAs(blob, 'mitoSettings.json')
  },

  closeSnackbar({ commit }) {
    commit('DEACTIVATE_SNACKBAR')
  },
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions,
  strict: process.env.NODE_ENV !== 'production',
})
