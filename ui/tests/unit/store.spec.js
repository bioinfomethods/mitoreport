/* eslint-disable no-import-assign */

import * as dataService from '@/services/LocalDataService.js'
import { actions, mutations, getters } from '@/store'
import flushPromises from 'flush-promises'
import * as _ from 'lodash'
import DEFAULT_SETTINGS from '../fixtures/defaultSettings.json'
import SETTINGS from '../fixtures/mitoSettings.json'

const VARIANTS = [
  {
    id: 'chrM-152-T-C',
    chr: 'chrM',
    pos: 152,
    ref: 'T',
    alt: 'C',
    type: 'SNP',
    consequence: {
      id: 'missense_variant',
      name: 'missense_variant',
      rank: 10,
    },
    DP: 4858,
  },
  {
    id: 'chrM-263-A-G',
    chr: 'chrM',
    pos: 263,
    ref: 'A',
    alt: 'G',
    type: 'SNP',
    consequence: {
      id: 'inframe_deletion',
      name: 'inframe_deletion',
      rank: 9,
    },
    DP: 3858,
  },
]

const DELETIONS = {
  TestSample: {
    coverage: [
      {
        pos: 209.0,
        cov: 0.869,
      },
      {
        pos: 219.0,
        cov: 0.863,
      },
    ],
    splitReads: [
      {
        pos: 250.0,
        count: 0.0,
      },
      {
        pos: 350.0,
        count: 0.0,
      },
    ],
  },
}

const expSettings = _.merge(DEFAULT_SETTINGS, SETTINGS)

describe('root store getters', () => {
  describe('sampleSettings', () => {
    it.each`
      settings                            | expResult
      ${null}                             | ${{}}
      ${undefined}                        | ${{}}
      ${{ sample: { id: 'TestSample' } }} | ${{ id: 'TestSample' }}
    `(
      'Given settings is $settings, expect sampleSettings is $expResult',
      ({ settings, expResult }) => {
        const state = {
          sampleId: 'TestSample',
          settings: settings,
        }
        expect(getters.getSampleSettings(state)).toEqual(expResult)
      }
    )
  })
})

describe('root store mutations', () => {
  it('SET_LOADING', () => {
    const state = {
      loading: false,
    }
    mutations.SET_LOADING(state, {})
    expect(state).toEqual({
      loading: true,
    })
  })

  it('SET_VARIANTS', () => {
    const state = {
      loading: false,
      variants: {},
      maternalVariants: {},
      deletions: {},
    }
    let expVariants = {
      [VARIANTS[0]['id']]: { ...VARIANTS[0], maternal: null },
      [VARIANTS[1]['id']]: { ...VARIANTS[1], maternal: null },
    }

    mutations.SET_VARIANTS(state, VARIANTS)

    expect(state).toEqual({
      loading: false,
      variants: expVariants,
      maternalVariants: {},
      filteredVariants: expVariants,
      maxReadDepth: 4858,
      deletions: {},
    })
  })

  it('SET_DELETIONS', () => {
    const state = {
      loading: false,
      variants: [],
      deletions: {},
    }

    mutations.SET_DELETIONS(state, DELETIONS)

    expect(state).toEqual({
      loading: false,
      variants: [],
      deletions: DELETIONS,
    })
  })

  it('SET_SAVED_SEARCH skips invalid search config', () => {
    const searchTest1 = {
      name: 'Test1',
      description: 'Test1 Desc',
      custom: true,
      filterConfig: {
        posRange: [200, 16300],
        allele: 'A',
        selectedTypes: ['SNP', 'DEL'],
      },
    }

    const currentSearches = [searchTest1]
    const mockSampleSettings = (getters.getSampleSettings = jest.fn())
    mockSampleSettings.mockReturnValue({
      id: 'TestSample',
      variantSearches: currentSearches,
    })

    mutations.SET_SAVED_SEARCH({}, {})

    expect(currentSearches).toEqual(currentSearches)
  })

  it('SET_SAVED_SEARCH creates new search when given new name', () => {
    const searchTest1 = {
      name: 'Test1',
      description: 'Test1 Desc',
      custom: true,
      filterConfig: {
        posRange: [200, 16300],
        allele: 'A',
        selectedTypes: ['SNP', 'DEL'],
      },
    }

    const currentSearches = []
    const mockSampleSettings = (getters.getSampleSettings = jest.fn())
    mockSampleSettings.mockReturnValue({
      id: 'TestSample',
      variantSearches: currentSearches,
    })

    mutations.SET_SAVED_SEARCH({}, searchTest1)

    expect(currentSearches).toEqual([searchTest1])
  })

  it('SET_SAVED_SEARCH replaces existing search when given existing name', () => {
    const searchTest1 = {
      name: 'Test1',
      description: 'Test1 Desc',
      custom: true,
      filterConfig: {
        posRange: [200, 16300],
        allele: 'A',
        selectedTypes: ['SNP', 'DEL'],
      },
    }

    const searchTest1b = {
      name: 'Test1',
      description: 'Test1b Desc',
      custom: true,
      filterConfig: {
        posRange: [2000, 16300],
        allele: 'T',
        selectedTypes: ['SNP', 'INS'],
        vafRange: [0.03, 0.1],
      },
    }

    const currentSearches = [searchTest1]
    const mockSampleSettings = (getters.getSampleSettings = jest.fn())
    mockSampleSettings.mockReturnValue({
      id: 'TestSample',
      variantSearches: currentSearches,
    })

    mutations.SET_SAVED_SEARCH({}, searchTest1b)

    expect(currentSearches).toEqual([searchTest1b])
  })
})

describe('root store actions', () => {
  beforeEach(() => {
    dataService.loadSettings = jest.fn(() => {
      return Promise.resolve({ data: DEFAULT_SETTINGS })
    })
    dataService.getDeletions = jest.fn(() => {
      return Promise.resolve({ data: DELETIONS })
    })
    dataService.getVariants = jest.fn(() => {
      return Promise.resolve({ data: VARIANTS })
    })
    dataService.getMaternalVariants = jest.fn(() => {
      return Promise.resolve({ data: [] })
    })
  })

  it('fetchData', async () => {
    const commit = jest.fn()
    const dispatch = jest.fn()

    const state = { variants: {}, maternalVariants: {} }
    await actions.fetchData({ state, commit, dispatch })
    await flushPromises()

    expect(commit).toHaveBeenCalledTimes(7)
    expect(commit).toHaveBeenNthCalledWith(1, 'SET_LOADING')
    expect(commit).toHaveBeenNthCalledWith(2, 'SET_SETTINGS', expSettings)
    expect(commit).toHaveBeenNthCalledWith(3, 'SET_MATERNAL_VARIANTS', [])
    expect(commit).toHaveBeenNthCalledWith(4, 'SET_VARIANTS', VARIANTS)
    expect(commit).toHaveBeenNthCalledWith(5, 'SET_DELETIONS', DELETIONS)
    expect(commit).toHaveBeenNthCalledWith(6, 'SET_SAMPLE_ID', DELETIONS)
    expect(commit).toHaveBeenNthCalledWith(7, 'UNSET_LOADING')
  })
})

afterAll(() => {
  return jest.resetAllMocks()
})
