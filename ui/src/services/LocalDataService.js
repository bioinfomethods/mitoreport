import * as _ from 'lodash'

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

export function concatSearches(objValue, srcValue, key) {
  if (key === 'variantSearches') {
    return _.unionBy((objValue || []).concat(srcValue || []), 'name')
  } else {
    return undefined
  }
}

export async function loadSettings() {
  const defaultSettings = _.cloneDeep(window.defaultSettings)
  const fileSettings = _.cloneDeep(window.settings)
  const localStorageSettings = JSON.parse(localStorage.getItem('mitoSettings'))

  const result = _.mergeWith(
    defaultSettings,
    fileSettings,
    localStorageSettings,
    concatSearches
  )

  return {
    status: 200,
    statusText: 'OK',
    data: result,
  }
}

export async function saveSettingsToLocal(settings) {
  localStorage.setItem('mitoSettings', JSON.stringify(settings))

  return {
    status: 200,
    statusText: 'OK',
  }
}
