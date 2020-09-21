import * as dataService from '@/services/LocalDataService.js'
import { actions, mutations } from '@/store'
import flushPromises from 'flush-promises'

const expVaraints = [
  {
    chr: 'chrM',
    pos: 152,
    ref: 'T',
    alt: 'C',
    type: 'SNP',
  },
  {
    chr: 'chrM',
    pos: 263,
    ref: 'A',
    alt: 'G',
    type: 'SNP',
  },
]
const expDeletions = {
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
})

describe('root store actions', () => {
  beforeEach(() => {
    dataService.getDeletions = jest.fn(() => {
      return Promise.resolve({ data: expDeletions })
    })
    dataService.getVariants = jest.fn(() => {
      return Promise.resolve({ data: expVaraints })
    })
  })

  it('fetchData', async () => {
    const commit = jest.fn()

    await actions.fetchData({ commit })
    await flushPromises()

    expect(commit).toHaveBeenCalledTimes(4)
    expect(commit).toHaveBeenNthCalledWith(1, 'SET_LOADING')
    expect(commit).toHaveBeenNthCalledWith(2, 'SET_VARIANTS', expVaraints)
    expect(commit).toHaveBeenNthCalledWith(3, 'SET_DELETIONS', expDeletions)
    expect(commit).toHaveBeenNthCalledWith(4, 'UNSET_LOADING')
  })
})

afterAll(() => {
  return jest.resetAllMocks()
})
