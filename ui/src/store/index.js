import {
  getDeletions,
  getVariants,
  saveSettingsToLocal,
} from '@/services/LocalDataService.js'
import { CONSEQUENCE_NAMES, DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import { saveAs } from 'file-saver'
import * as _ from 'lodash'
import Vue from 'vue'
import Vuex from 'vuex'
import { loadSettings } from '../services/LocalDataService'

Vue.use(Vuex)

export const state = {
  settings: {},
  loading: false,
  snackbar: { ...DEFAULT_SNACKBAR_OPTS },
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

  igvHost: state => {
    return state.settings.igvHost
  },

  sampleSettings: state => {
    const result = (state.settings.samples || []).find(
      sample => sample.id === getters.sample(state)
    )
    return result || {}
  },

  settingsBamDir: state => {
    return getters.sampleSettings(state).bamDir
  },

  settingsBamFilename: state => {
    return getters.sampleSettings(state).bamFilename
  },

  settingsBamFile: state => {
    const sampleBamDir = getters.settingsBamDir(state)
    const sampleBamFilename = getters.settingsBamFilename(state)
    if (!sampleBamDir || !sampleBamFilename) {
      return null
    }

    return `${sampleBamDir}${sampleBamFilename}`
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
      let result = {}

      for (const [key, value] of Object.entries(variant)) {
        if (key === 'consequence') {
          const consequenceName =
            CONSEQUENCE_NAMES.find(item => item.id === value.id)?.name ||
            value.id
          result[key] = { ...value, name: consequenceName }
        } else {
          result[key] = value
        }
      }

      result['ref_alt'] = `${result.ref}/${result.alt}`

      return result
    })
  },

  SET_DELETIONS(state, deletions) {
    state.deletions = deletions
  },

  SET_BAM_DIR(state, newBamDir) {
    getters.sampleSettings(state).bamDir = newBamDir
  },

  ACTIVATE_SNACKBAR(state, options) {
    state.snackbar = _.merge(DEFAULT_SNACKBAR_OPTS, { active: true }, options)
  },

  DEACTIVATE_SNACKBAR(state) {
    state.snackbar = _.merge(DEFAULT_SNACKBAR_OPTS, { active: false })
  },
}

export const actions = {
  fetchData({ commit }) {
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
        // commit('ACTIVATE_SNACKBAR', {
        //   color: 'red',
        //   message: `There was a problem fetching data:`,
        // })
      })
  },

  saveBamDir({ commit }, newBamDir) {
    commit('SET_BAM_DIR', newBamDir)
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
