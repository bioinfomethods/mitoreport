import * as _ from 'lodash'

export function getVariants() {
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

export async function loadSettings() {
  const defaultSettings = window.defaultSettings
  const fileSettings = window.settings
  const localStorageSettings = JSON.parse(localStorage.getItem('mitoSettings'))

  const result = _.merge(defaultSettings, fileSettings, localStorageSettings)

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
