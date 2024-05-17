import {
  getDeletions,
  getMaternalVariants,
  getVariants,
  saveSettingsToLocal,
} from '@/services/LocalDataService.js'
import { DEFAULT_SNACKBAR_OPTS } from '@/shared/constants'
import Cookies from 'js-cookie'
import * as _ from 'lodash'
import { v4 as uuidv4 } from 'uuid'
import Vue from 'vue'
import Vuex from 'vuex'
import { loadLocalDbSettings, loadSettings } from '../services/LocalDataService'
import {
  DEFAULT_GENECARDS_URL_PREFIX,
  DEFAULT_HMT_VAR_URL_PREFIX,
  DEFAULT_IGV_HOST,
} from '../shared/constants'

Vue.use(Vuex)

const boolFromCookie = name => {
  return Cookies.get(name) === 'true' ? true : false
}

export const state = {
  auth: {
    sub: 'oauth.sub',
    authenticated: false,
    preferred_username: 'anonymous',
    email: 'anonymous@mcri.edu.au',
    given_name: 'Anonymous',
    family_name: 'User',
    initials: 'AA',
    groups: [],
    ad_groups: [],
  },
  sampleId: '',
  settings: {},
  couchDbPassword: '',
  loading: false,
  snackbar: { ...DEFAULT_SNACKBAR_OPTS },
  variants: {},
  maternalVariants: {},
  filteredVariants: {},
  maxReadDepth: 0,
  deletions: {},
  syncEnabled: boolFromCookie('syncEnabled'),
}

export const getters = {
  getIgvHost: state => {
    return state.settings.igvHost || DEFAULT_IGV_HOST
  },

  getGeneCardsUrlPrefix: state => {
    return state.settings.geneCardsUrlPrefix || DEFAULT_GENECARDS_URL_PREFIX
  },

  getHaplogroups: (state, getters) => {
    let haplogrepClass = getters.getSampleSettings.haplogrepClassification
    if (haplogrepClass && !_.isEmpty(haplogrepClass.haplogrepResults)) {
      return _.uniq(
        Object.keys(haplogrepClass.haplogrepResults).map(
          d => haplogrepClass.haplogrepResults[d].baseHaplogroup
        )
      )
    } else {
      // No haplogroup info?
      return ['No Group data provided']
    }
  },

  getFullHaplogroups: (state, getters) => {
    let haplogrepClass = getters.getSampleSettings.haplogrepClassification
    if (haplogrepClass && !_.isEmpty(haplogrepClass.haplogrepResults)) {
      return _.uniq(
        Object.keys(haplogrepClass.haplogrepResults).map(
          d => haplogrepClass.haplogrepResults[d].haplogroup
        )
      )
    } else {
      // No haplogroup info?
      return ['No Group data provided']
    }
  },

  getFirstFullHaplogroup: (state, getters) => {
    return getters.getFullHaplogroups[0]
  },

  getFirstHaplogroup: (state, getters) => {
    return getters.getHaplogroups[0]
  },

  getHmtVarUrlPrefix: state => {
    return state.settings.hmtVarUrlPrefix || DEFAULT_HMT_VAR_URL_PREFIX
  },

  getSampleSettings: state => {
    if (!state.sampleId) {
      return {}
    }
    const result = state.settings?.sample || {}
    return result
  },

  getSampleQc: (state, getters) => {
    return getters.getSampleSettings?.qc || {}
  },

  hasMaternalVariants: (state, getters) => {
    return getters.getSampleSettings?.maternalVcfFilename !== null
  },

  getSampleMetadata: (state, getters) => {
    return getters.getSampleSettings?.metadata || {}
  },

  getSyncFeatureEnabled: (state, getters) => {
    return !_.isEmpty(getters.getSettingsCouchDbUrl)
  },

  getSettingsCouchDbUrl: (state, getters) => {
    return getters.getSampleSettings?.couchDbUrl
  },

  getSettingsCouchDbUsername: (state, getters) => {
    return getters.getSampleSettings?.couchDbUsername
  },

  getSettingsBamDir: (state, getters) => {
    return getters.getSampleSettings?.bamDir
  },

  getSettingsBamFilename: (state, getters) => {
    return getters.getSampleSettings?.bamFilename
  },

  getSettingsBamFile: (state, getters) => {
    const sampleBamDir = getters.getSettingsBamDir
    const sampleBamFilename = getters.getSettingsBamFilename
    if (!sampleBamDir || !sampleBamFilename) {
      return null
    }

    return `${sampleBamDir}${sampleBamFilename}`
  },

  getVariantById: state => variantId => {
    return state.variants[variantId] || {}
  },

  getVariantTags: (state, getters) => {
    return getters?.getSampleSettings?.variantTags || []
  },

  getImportantVariantTags: (state, getters) => {
    return getters.getVariantTags.filter(t => t.important)
  },

  getCurationByVariantId: (state, getters) => variantId => {
    const sampleCurations = getters.getSampleSettings?.curations || {}

    return sampleCurations[variantId] || {}
  },
}

export const mutations = {
  SET_AUTH(state, auth) {
    state.auth = auth
  },

  SET_SETTINGS(state, settings) {
    state.settings = settings
  },

  SET_SYNC_VALUE(state, value) {
    state.syncEnabled = value
  },

  SET_LOADING(state) {
    state.loading = true
  },

  UNSET_LOADING(state) {
    state.loading = false
  },

  SET_SAMPLE_ID(state, deletions) {
    let sampleId = 'No Sample'

    const samples = Object.keys(deletions)
    if (samples.length > 0) {
      sampleId = samples[0]
    }

    state.sampleId = sampleId
  },

  SET_VARIANTS(state, variants) {
    const variantsObj = {}
    variants.forEach(v => {
      const matchingVariant = state.maternalVariants[v.id]
      const withMum = matchingVariant
        ? {
            ...v,
            maternal: {
              heteroplasmy: matchingVariant.genotypes[0].AF,
              readDepth: matchingVariant.DP,
            },
          }
        : { ...v, maternal: null }
      variantsObj[v.id] = withMum
    })
    state.variants = variantsObj
    state.filteredVariants = variantsObj

    const uniqReadDepths = _.union(variants.map(v => _.toNumber(v.DP)))
    state.maxReadDepth = _.max(uniqReadDepths)
  },

  SET_MATERNAL_VARIANTS(state, variants) {
    const variantsObj = {}
    variants.forEach(v => {
      variantsObj[v.id] = v
    })
    state.maternalVariants = variantsObj
  },

  SET_FILTERED_VARIANTS(state, filteredVariants) {
    state.filteredVariants = filteredVariants
  },

  SET_DELETIONS(state, deletions) {
    state.deletions = deletions
  },

  SET_COUCH_DB_URL(state, newCouchDbUrl) {
    getters.getSampleSettings(state).couchDbUrl = newCouchDbUrl
  },

  SET_COUCH_DB_USERNAME(state, newCouchDbUsername) {
    getters.getSampleSettings(state).couchDbUsername = newCouchDbUsername
  },

  SET_COUCH_DB_PASSWORD(state, newCouchDbPassword) {
    state.couchDbPassword = newCouchDbPassword
  },

  SET_BAM_DIR(state, newBamDir) {
    getters.getSampleSettings(state).bamDir = newBamDir
  },

  SET_USER_TAGS(state, { userTags, currentTags }) {
    const defaultTags = currentTags.filter(t => !t.custom)
    const newVariantTags = defaultTags.concat(userTags)
    getters.getSampleSettings(state).variantTags = newVariantTags
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
    let existingCurations = getters.getSampleSettings(state).curations
    let existing = existingCurations[curationToSave.variantId]

    if (!existing) {
      existing = { id: uuidv4() }
    }

    existing.variantId = curationToSave.variantId
    existing.selectedTagNames =
      curationToSave.selectedTags?.map(ct => ct.name) ||
      existing.selectedTagNames
    existing.variantNote =
      curationToSave.variantNote || existing.variantNote || ''

    Vue.set(existingCurations, curationToSave.variantId, existing)
  },
}

export const actions = {
  async keycloakOnReady({ commit }, keycloak) {
    const result = {
      authenticated: keycloak.authenticated,
      sub: keycloak.sub(),
      preferred_username: keycloak.preferred_username(),
      email: keycloak.email(),
      given_name: keycloak.given_name(),
      family_name: keycloak.family_name(),
      initials: keycloak.initials(),
      groups: keycloak.groups(),
      ad_groups: keycloak.ad_groups(),
    }
    commit('SET_AUTH', result)
  },

  async keycloakOnAuthSuccess(_, keycloak) {
    console.debug('store.onAuthRefreshSuccess', keycloak)
  },

  async keycloakOnAuthError(_, keycloak) {
    console.debug('store.onAuthError', keycloak)
  },

  async keycloakOnAuthRefreshSuccess(_, keycloak) {
    console.debug('store.onAuthRefreshSuccess', keycloak)
  },

  async keycloakOnAuthRefreshError(_, keycloak) {
    console.debug('store.onAuthRefreshError', keycloak)
  },

  async keycloakOnAuthLogout(_, keycloak) {
    console.debug('store.onAuthLogout', keycloak)
  },

  async keycloakOnTokenExpired(_, keycloak) {
    console.debug('store.onTokenExpired', keycloak)
  },

  async keycloakOnActionUpdate(_, keycloak, status) {
    console.debug('store.onActionUpdate', keycloak, status)
  },

  async fetchData({ commit }) {
    commit('SET_LOADING')

    try {
      const settResp = await loadSettings()
      commit('SET_SETTINGS', settResp.data)

      const maternalVarResp = await getMaternalVariants()
      commit('SET_MATERNAL_VARIANTS', maternalVarResp.data)

      const varResp = await getVariants()
      const delResp = await getDeletions()
      commit('SET_VARIANTS', varResp.data)
      commit('SET_DELETIONS', delResp.data)
      commit('SET_SAMPLE_ID', delResp.data)
    } catch (error) {
      console.error(error)

      commit('ACTIVATE_SNACKBAR', {
        color: 'red',
        message: `There was a problem fetching data: ${error.message}`,
      })
    } finally {
      commit('UNSET_LOADING')
    }

    console.debug('Finished action fetchData')
  },

  setSettings({ commit }, settings) {
    commit('SET_SETTINGS', settings)
  },

  async loadLocalSettings({ commit }, sampleId) {
    const settings = await loadLocalDbSettings(sampleId)
    commit('SET_SETTINGS', settings)
  },

  saveAppSettings(
    { dispatch, commit, getters },
    { newCouchDbUrl, newCouchDbUsername, newBamDir, userTags }
  ) {
    const currentTags = getters.getVariantTags
    commit('SET_COUCH_DB_URL', newCouchDbUrl)
    commit('SET_COUCH_DB_USERNAME', newCouchDbUsername)
    commit('SET_BAM_DIR', newBamDir)
    commit('SET_USER_TAGS', { userTags, currentTags })
    dispatch('removeVariantTags', userTags)
    dispatch('saveSettings')
  },

  storeCouchDbPassword({ commit }, newCouchDbPassword) {
    commit('SET_COUCH_DB_PASSWORD', newCouchDbPassword)
  },

  removeVariantTags({ state, commit }, userTags) {
    const defaultTagNames = getters.getVariantTags(state).map(t => t.name)
    const newUserTags = userTags.map(t => t.name)
    const newTagNames = defaultTagNames.concat(newUserTags)
    _.cloneDeep(state.curations || []).forEach(curation => {
      const hasTagNamesToRemove = curation.selectedTagNames.some(
        stn => !newTagNames.includes(stn)
      )
      curation.selectedTagNames = curation.selectedTagNames.filter(stn =>
        newTagNames.includes(stn)
      )
      if (hasTagNamesToRemove) {
        commit('SET_CURATION', curation)
      }
    })
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

  toggleSync({ commit }, value) {
    commit('SET_SYNC_VALUE', value)
  },

  openSnackbar({ commit }, options) {
    commit('ACTIVATE_SNACKBAR', options)
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
