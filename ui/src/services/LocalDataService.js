import * as _ from 'lodash'
import PouchDB from 'pouchdb'
import getStore from '@/store'
import {
  USER_SETTINGS_APPEND_PROP_NAMES,
  USER_SETTINGS_PROP_NAMES,
} from '@/shared/constants'
export const LOCAL_DB = new PouchDB('mitoreport')

export async function getVariants() {
  return {
    status: 200,
    statusText: 'OK',
    data: window.variants,
  }
}

export async function getMaternalVariants() {
  return {
    status: 200,
    statusText: 'OK',
    data: window.maternalVariants,
  }
}

export async function getDeletions() {
  return {
    status: 200,
    statusText: 'OK',
    data: window.deletions,
  }
}

export function settingsSampleMerger(objValue, srcValue, key) {
  if (USER_SETTINGS_APPEND_PROP_NAMES.includes(key)) {
    return _.unionBy((srcValue || []).concat(objValue || []), 'name')
  } else if (USER_SETTINGS_PROP_NAMES.includes(key)) {
    return objValue
  } else {
    return undefined
  }
}

export async function loadSettings() {
  const defaultSettings = _.cloneDeep(window.defaultSettings)
  const sampleId = defaultSettings?.sample?.id
  let localDbSettings = {}
  try {
    localDbSettings = await loadLocalDbSettings(sampleId)
  } catch (err) {
    if (err.status !== 404) {
      throw new Error(
        `Unexpected error=${err} trying to load settings for sample=${sampleId}`
      )
    }
  }

  const mergedSettings = _.mergeWith(
    localDbSettings,
    defaultSettings,
    settingsSampleMerger
  )

  return {
    status: 200,
    statusText: 'OK',
    data: mergedSettings,
  }
}

export async function loadLocalDbSettings(sampleId) {
  try {
    return LOCAL_DB.get(sampleId)
  } catch (err) {
    if (err.status !== 404) {
      throw new Error(
        `Unexpected error=${err} trying to load settings for sample=${sampleId}`
      )
    }
  }
}

export async function saveSettingsToLocal(settings) {
  const sampleId = settings.sample.id
  try {
    const existing = await LOCAL_DB.get(sampleId)
    const toSave = {
      ...settings,
      _id: existing._id,
      _rev: existing._rev,
    }
    return LOCAL_DB.put(toSave)
  } catch (err) {
    if (err.status === 404) {
      const toSave = {
        ...settings,
        _id: settings.sample.id,
      }
      return LOCAL_DB.put(toSave)
    } else {
      throw new Error(
        `Unexpected error=${err} trying to save settings for sample=${sampleId}`
      )
    }
  }
}

export let SYNC_HANDLER = null

export async function syncWithRemote(authState) {
  console.debug('Start syncing with remote CouchDB', authState)
  const couchDbUrl = getStore.getters.getSettingsCouchDbUrl
  const idToken = authState?.idToken?.idToken
  let remoteDB = new PouchDB(couchDbUrl, {
    headers: { Authorization: `Bearer ${idToken}` },
  })

  SYNC_HANDLER = LOCAL_DB.sync(remoteDB, {
    live: true,
    retry: true,
  })
    .on('change', function(change) {
      const sampleId = change.change.docs[0].sample.id
      getStore.dispatch('loadLocalSettings', sampleId)
    })
    .on('error', function(err) {
      console.error(
        `Unexpected error occured while trying to sync: ${JSON.stringify(err)}`
      )
      SYNC_HANDLER = null
    })
}

export function cancelSyncWithRemote() {
  console.debug('Stop syncing with remote CouchDB')
  SYNC_HANDLER?.cancel()
  SYNC_HANDLER = null
}
