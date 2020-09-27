import flushPromises from 'flush-promises'
import * as _ from 'lodash'
import { loadSettings } from '@/services/LocalDataService'
import DEFAULT_SETTINGS from '../fixtures/defaultSettings.json'
import SETTINGS from '../fixtures/mitoSettings.json'
import LOCAL_SETTINGS from '../fixtures/localStorage.json'
import EXP_SETTINGS from '../fixtures/LocalDataService.spec.expected.json'

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
