import * as _ from 'lodash'
import PouchDB from 'pouchdb'

export const LOCAL_DB = new PouchDB('mitoreport')

export async function getVariants() {
  return {
    status: 200,
    statusText: 'OK',
    data: window.variants,
  }
}

export async function getDeletions() {
  return {
    status: 200,
    statusText: 'OK',
    data: window.deletions,
  }
}

export function settingsAllSamplesMerger(objValue, srcValue, key) {
  if (key === 'samples') {
    return _.unionBy((srcValue || []).concat(objValue || []), 'id')
  } else {
    return undefined
  }
}

export function settingsSampleMerger(objValue, srcValue, key) {
  if (key === 'variantSearches' || key === 'variantTags') {
    return _.unionBy((srcValue || []).concat(objValue || []), 'name')
  } else if (key === 'curations') {
    return srcValue
  } else {
    return undefined
  }
}

export async function loadSettings() {
  const defaultSettings = _.cloneDeep(window.defaultSettings)
  const fileSettings = _.cloneDeep(window.settings)
  const sampleId = defaultSettings?.sample?.id
  let localDbSettings = {}
  try {
    localDbSettings = await LOCAL_DB.get(sampleId)
  } catch (err) {
    if (err.status !== 404) {
      throw new Error(
        `Unexpected error=${err} trying to load settings for sample=${sampleId}`
      )
    }
  }

  const samplesMerged = _.mergeWith(
    {},
    defaultSettings,
    fileSettings,
    localDbSettings,
    settingsSampleMerger
  )

  return {
    status: 200,
    statusText: 'OK',
    data: samplesMerged,
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
