import { loadSettings, LOCAL_DB } from '@/services/LocalDataService'
import flushPromises from 'flush-promises'
import DEFAULT_SETTINGS from '../fixtures/defaultSettings.json'
import LOCAL_SETTINGS from '../fixtures/localStorage.json'
import FILE_SETTINGS from '../fixtures/mitoSettings.json'
import EXP_MERGED_DEFAULT_AND_FILE_SETTINGS from '../fixtures/LocalDataService.spec.mergeDefaultAndFile.expected.json'
import EXP_MERGED_ALL_SETTINGS from '../fixtures/LocalDataService.spec.mergeAllSettings.expected.json'

describe('LocalDataService', () => {
  it('loadSettings() handles correctly when only default settings exists', async () => {
    window.defaultSettings = DEFAULT_SETTINGS

    const actualResult = await loadSettings()
    await flushPromises()
    expect(actualResult.data).toStrictEqual(DEFAULT_SETTINGS)
  })

  it('loadSettings() handles correctly when merging default settings and file settings', async () => {
    window.defaultSettings = DEFAULT_SETTINGS
    window.settings = FILE_SETTINGS

    const actualResult = await loadSettings()
    await flushPromises()

    expect(actualResult.data).toStrictEqual(
      EXP_MERGED_DEFAULT_AND_FILE_SETTINGS
    )
  })

  it('loadSettings() handles correctly when local settings exists', async () => {
    window.defaultSettings = DEFAULT_SETTINGS
    window.settings = FILE_SETTINGS
    let localSettings = LOCAL_SETTINGS
    localSettings._id = '15G002035-GM12878K_20pc_10kb_200'
    LOCAL_DB.put(localSettings)
    // localStorage.setItem('mitoSettings', JSON.stringify(LOCAL_SETTINGS))

    const actualResult = await loadSettings()
    await flushPromises()

    const { _id, _rev, ...actual } = actualResult.data
    expect(actual).toStrictEqual(EXP_MERGED_ALL_SETTINGS)
  })
})

afterAll(() => {
  return jest.resetAllMocks()
})
