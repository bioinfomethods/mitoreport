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

export const COLORS = {
  PRIMARY: '#3974CC',
  PRIMARY_LIGHT: '#97C4FA',
}

export const MIN_POS = 200

export const MAX_POS = 16569

export const DEFAULT_IGV_HOST = 'http://localhost:60151'

export const DEFAULT_HAS_MATERNAL_VARIANTS = false

export const DEFAULT_GENECARDS_URL_PREFIX =
  'https://www.genecards.org/cgi-bin/carddisp.pl'

export const DEFAULT_HAPLOGROUPS = []
export const DEFAULT_FIRST_HAPLOGROUP = null

export const DEFAULT_HMT_VAR_URL_PREFIX = 'https://www.hmtvar.uniba.it/results'

export const DEFAULT_VARIANT_SEARCH = {
  name: 'Default',
  description: 'Default recommended filter configs',
  custom: false,
  filterConfig: {
    posRange: [500, 16569],
    allele: '',
    selectedMitoTIP: [],
    selectedTypes: [],
    selectedGenes: [],
    selectedMasks: ['MT-HV1', 'MT-HV2', 'MT-HV3', 'MT-OHR', 'MT-CR'],
    selectedConsequences: [],
    gnomADHap: [],
    vafRange: [0.02, 1],
    gbFreqTickIndex: 3,
    gnomADHetFreqTickIndex: 6,
    gnomADHomFreqTickIndex: 6,
    disease: '',
    diseaseShowBlank: false,
    curationSearch: '',
    importantCuration: false,
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

export const VARIANT_MASKS = [
  {
    name: 'MT-HV1',
    start: 16024,
    end: 16383,
  },
  {
    name: 'MT-HV2',
    start: 57,
    end: 372,
  },
  {
    name: 'MT-HV3',
    start: 438,
    end: 574,
  },
  {
    name: 'MT-OHR',
    start: 110,
    end: 441,
  },
  {
    name: 'MT-CR',
    start: 16024,
    end: 576,
  },
]

export const USER_SETTINGS_APPEND_PROP_NAMES = [
  'variantSearches',
  'variantTags',
]

export const USER_SETTINGS_PROP_NAMES = [
  'curations',
  'couchDbUrl',
  'bamDir',
  'bamFilename',
]
