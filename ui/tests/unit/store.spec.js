import * as dataService from '@/services/LocalDataService.js'
import { actions, mutations, getters } from '@/store'
import flushPromises from 'flush-promises'
import * as _ from 'lodash'
import DEFAULT_SETTINGS from '../fixtures/defaultSettings.json'
import SETTINGS from '../fixtures/mitoSettings.json'

const VARIANTS = [
  {
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
  describe('sample', () => {
    it.each`
      deletions                                                                                           | expResult
      ${null}                                                                                             | ${'No Sample'}
      ${undefined}                                                                                        | ${'No Sample'}
      ${{}}                                                                                               | ${'No Sample'}
      ${[]}                                                                                               | ${'No Sample'}
      ${{ TestSample: { coverage: [], splitReads: [] } }}                                                 | ${'TestSample'}
      ${{ TestSample1: { coverage: [], splitReads: [] }, TestSample2: { coverage: [], splitReads: [] } }} | ${'TestSample1'}
    `(
      'Given deletions is $deletions, expect sample is $expResult',
      ({ deletions, expResult }) => {
        const state = {
          deletions: deletions,
        }
        expect(getters.getSample(state)).toEqual(expResult)
      }
    )
  })

  describe('sampleSettings', () => {
    it.each`
      settings                                                      | expResult
      ${null}                                                       | ${{}}
      ${undefined}                                                  | ${{}}
      ${{ samples: [] }}                                            | ${{}}
      ${{ samples: [{ id: 'TestSample' }] }}                        | ${{ id: 'TestSample' }}
      ${{ samples: [{ id: 'TestSample' }, { id: 'TestSample2' }] }} | ${{ id: 'TestSample' }}
    `(
      'Given settings is $settings, expect sampleSettings is $expResult',
      ({ settings, expResult }) => {
        const state = {
          settings: settings,
        }
        const mockSample = (getters.getSample = jest.fn())
        mockSample.mockReturnValue('TestSample')
        expect(getters.getSampleSettings(state)).toEqual(expResult)
      }
    )
  })

  describe('settingsBamFile', () => {
    it.each`
      sampleSettings                                  | expResult
      ${null}                                         | ${null}
      ${undefined}                                    | ${null}
      ${{ bamDir: '', bamFilename: 'test.bam' }}      | ${null}
      ${{ bamDir: '/tmp/', bamFilename: '' }}         | ${null}
      ${{ bamDir: '/tmp/', bamFilename: 'test.bam' }} | ${'/tmp/test.bam'}
    `(
      'Given sampleSettings is $sampleSettings, expect settingsBamFile is $expResult',
      ({ sampleSettings, expResult }) => {
        const mockSampleSettings = (getters.getSampleSettings = jest.fn())
        mockSampleSettings.mockReturnValue(sampleSettings)
        expect(getters.getSettingsBamFile({})).toEqual(expResult)
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
      variants: [],
      deletions: {},
    }
    let expVariants = _.cloneDeep(VARIANTS)
    expVariants[0].ref_alt = `${expVariants[0].ref}/${expVariants[0].alt}`
    expVariants[1].ref_alt = `${expVariants[1].ref}/${expVariants[1].alt}`
    expVariants[0].consequence.name = 'missense_variant'
    expVariants[1].consequence.name = 'inframe_deletion'

    mutations.SET_VARIANTS(state, VARIANTS)

    expect(state).toEqual({
      loading: false,
      variants: expVariants,
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
        vafRange: [0.00001, 0.05],
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
  })

  it('fetchData', async () => {
    const commit = jest.fn()

    const state = { variants: [] }
    await actions.fetchData({ state, commit })
    await flushPromises()

    expect(commit).toHaveBeenCalledTimes(5)

    expect(commit).toHaveBeenNthCalledWith(1, 'SET_LOADING')
    expect(commit).toHaveBeenNthCalledWith(2, 'SET_SETTINGS', expSettings)
    expect(commit).toHaveBeenNthCalledWith(3, 'SET_VARIANTS', VARIANTS)
    expect(commit).toHaveBeenNthCalledWith(4, 'SET_DELETIONS', DELETIONS)
    expect(commit).toHaveBeenNthCalledWith(5, 'UNSET_LOADING')
  })
})

afterAll(() => {
  return jest.resetAllMocks()
})
