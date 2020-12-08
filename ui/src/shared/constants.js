export const DEFAULT_SNACKBAR_OPTS = {
  active: false,
  color: 'green',
  message: null,
  timeout: 3000,
}

export const DEBOUNCE_DELAY = {
  SHORT: 300,
  MEDIUM: 500,
  LONG: 1000,
}

export const ANIMATION_DURATION = {
  SHORT: 200,
  MEDIUM: 400,
  LONG: 800,
}

export const MIN_POS = 200

export const MAX_POS = 16300

export const SAVE_INTERVAL_MS = 5000

export const DEFAULT_IGV_HOST = 'http://localhost:60151'

export const DEFAULT_GENECARDS_URL_PREFIX =
  'https://www.genecards.org/cgi-bin/carddisp.pl'

export const DEFAULT_HMT_VAR_URL_PREFIX = 'https://www.hmtvar.uniba.it/results'

export const DEFAULT_VARIANT_SEARCH = {
  name: 'All',
  description: 'No filters applied',
  custom: false,
  filterConfig: {
    posRange: [0, 16300],
    allele: '',
    selectedTypes: [],
    selectedGenes: [],
    selectedConsequence: {},
    vafRange: [0, 1],
    depthRange: [0, 999999],
    disease: '',
    diseaseShowBlank: false,
    curationSearch: '',
    importantCuration: false,
    gbFreqMax: 100.0,
    mitoMap: '',
    mitoMapShowBlank: false,
    selectedCuratedRefName: 'All',
    hgvsp: '',
    hgvspShowBlank: false,
    hgvsc: '',
    hgvscShowBlank: false,
    hgvs: '',
    hgvsShowBlank: false,
  },
}
