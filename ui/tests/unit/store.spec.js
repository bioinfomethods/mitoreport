import * as dataService from '@/services/LocalDataService.js'
import { actions, mutations } from '@/store'
import flushPromises from 'flush-promises'
import * as _ from 'lodash'

const VARIANTS = [
  {
    chr: 'chrM',
    pos: 152,
    ref: 'T',
    alt: 'C',
    type: 'SNP',
    consequence: {
      id: 'missense_variant',
      rank: 10,
    },
  },
  {
    chr: 'chrM',
    pos: 263,
    ref: 'A',
    alt: 'G',
    type: 'SNP',
    consequence: {
      id: 'inframe_deletion',
      rank: 9,
    },
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
})

describe('root store actions', () => {
  beforeEach(() => {
    dataService.getDeletions = jest.fn(() => {
      return Promise.resolve({ data: DELETIONS })
    })
    dataService.getVariants = jest.fn(() => {
      return Promise.resolve({ data: VARIANTS })
    })
  })

  it('fetchData', async () => {
    const commit = jest.fn()

    await actions.fetchData({ commit })
    await flushPromises()

    expect(commit).toHaveBeenCalledTimes(4)
    expect(commit).toHaveBeenNthCalledWith(1, 'SET_LOADING')
    expect(commit).toHaveBeenNthCalledWith(2, 'SET_VARIANTS', VARIANTS)
    expect(commit).toHaveBeenNthCalledWith(3, 'SET_DELETIONS', DELETIONS)
    expect(commit).toHaveBeenNthCalledWith(4, 'UNSET_LOADING')
  })
})

afterAll(() => {
  return jest.resetAllMocks()
})
