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
  const localStorageSettings =
    JSON.parse(localStorage.getItem('mitoSettings')) || {}

  const samplesMerged = _.mergeWith(
    {},
    defaultSettings,
    fileSettings,
    localStorageSettings,
    settingsAllSamplesMerger
  )

  samplesMerged.samples = samplesMerged.samples.map(sam => {
    const defaultSample = defaultSettings?.samples?.find(s => s.id === sam.id)
    const fileSample = fileSettings?.samples?.find(s => s.id === sam.id)
    const localSample = localStorageSettings?.samples?.find(
      s => s.id === sam.id
    )

    const mergedSample = _.mergeWith(
      {},
      defaultSample,
      fileSample,
      localSample,
      settingsSampleMerger
    )

    return mergedSample
  })

  return {
    status: 200,
    statusText: 'OK',
    data: samplesMerged,
  }
}

export async function saveSettingsToLocal(settings) {
  const settingsToSave = _.cloneDeep(settings)
  const nonBlankCurationPredicate = curation =>
    !_.isEmpty(curation.selectedTagNames) || !_.isEmpty(curation.variantNote)
  settingsToSave.samples.forEach(s => {
    s.curations = s.curations?.filter(nonBlankCurationPredicate) || []
  })

  localStorage.setItem('mitoSettings', JSON.stringify(settingsToSave))

  return {
    status: 200,
    statusText: 'OK',
  }
}
