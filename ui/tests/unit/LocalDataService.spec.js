import { loadSettings } from '@/services/LocalDataService'
import flushPromises from 'flush-promises'
import DEFAULT_SETTINGS from '../fixtures/defaultSettings.json'
import EXP_SETTINGS from '../fixtures/LocalDataService.spec.expected.json'
import LOCAL_SETTINGS from '../fixtures/localStorage.json'
import SETTINGS from '../fixtures/mitoSettings.json'

describe('LocalDataService', () => {
  it('loadSettings()', async () => {
    window.defaultSettings = DEFAULT_SETTINGS
    window.settings = SETTINGS
    localStorage.setItem('mitoSettings', JSON.stringify(LOCAL_SETTINGS))

    const actualResult = await loadSettings()
    await flushPromises()
    expect(actualResult.data).toStrictEqual(EXP_SETTINGS)
  })
})

afterAll(() => {
  return jest.resetAllMocks()
})
