import VariantTable from '@/components/VariantTable.vue'
import router from '@/router/index.js'
import { DEFAULT_VARIANT_SEARCH } from '@/shared/constants'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import Vuex from 'vuex'
import store, { getters, mutations, state } from './TestStore'

const app = document.createElement('div')
app.setAttribute('data-app', true)
document.body.append(app)

describe('VariantTable.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    vuetify = new Vuetify()
    underTest = mount(VariantTable, {
      LOCAL_VUE,
      store,
      router,
      vuetify,
    })
  })

  it('renders VariantTable', () => {
    const expHeaderTexts = [
      'HGVS.g',
      'Gene',
      'Consequence',
      'Heteroplasmy',
      'Genbank',
      'gnomAD Het',
      'gnomAD Hom',
      'Heteroplasmy Distribution',
      'Age Distribution (Homoplasmic)',
      'Curation',
    ]
    const wrapperText = underTest.text()
    expHeaderTexts.every(header => {
      expect(wrapperText).toContain(header)
    })
  })

  describe('onSavedSearchChange()', () => {
    it('change to custom search sets filterConfig correctly', () => {
      underTest.vm.onSavedSearchChange({
        name: 'Custom Filter 1',
        description: 'Custom 1 description',
        custom: true,
      })

      expect(underTest.vm.$data.filterConfig).toEqual({
        posRange: [0, 16569],
        allele: '',
        selectedMitoTIP: [],
        selectedTypes: ['SNP', 'DEL'],
        selectedGenes: [],
        selectedMasks: ['MT-HV1', 'MT-HV2', 'MT-HV3', 'MT-OHR', 'MT-CR'],
        selectedConsequences: [],
        vafRange: [0.03, 0.1],
        depthRange: [500, 12000],
        disease: '',
        diseaseShowBlank: false,
        curationSearch: '',
        importantCuration: false,
        gnomADHap: [],
        gbFreqTickIndex: 3,
        gnomADHetFreqTickIndex: 6,
        gnomADHomFreqTickIndex: 6,
        mitoMap: '',
        mitoMapShowBlank: false,
        selectedCuratedRefName: 'All',
        hgvsp: '',
        hgvspShowBlank: false,
        hgvsc: '',
        hgvscShowBlank: false,
        hgvs: '',
        hgvsShowBlank: false,
      })
      expect(underTest.vm.$data.vafIndexRange).toEqual([3, 5])
      expect(underTest.vm.$data.searchForm).toEqual({
        valid: true,
        name: 'Custom Filter 1',
        description: 'Custom 1 description',
      })
    })
  })

  describe('onSaveSearch()', () => {
    it('dispatches new search config to store for saving', () => {
      const mockSaveSearch = jest.fn()
      const mockActions = {
        saveSearch: mockSaveSearch,
      }

      const LOCAL_VUE = createLocalVue()
      LOCAL_VUE.use(VueRouter)
      LOCAL_VUE.use(Vuex)

      const store = new Vuex.Store({
        state,
        getters,
        mutations,
        actions: mockActions,
        strict: process.env.NODE_ENV !== 'production',
      })

      let underTest = mount(VariantTable, {
        LOCAL_VUE,
        store,
        router,
        vuetify,
      })

      underTest.setData({
        searchForm: {
          valid: true,
          name: 'Preset Filter 1',
          description: 'Preset Filter 1 desc',
        },
        filterConfig: {
          posRange: [1000, 15000],
          allele: 'A/C',
          selectedMitoTIP: [],
          selectedTypes: ['SNP', 'INS', 'DEL'],
          selectedGenes: [],
          selectedMasks: ['MT-HV1', 'MT-HV2', 'MT-HV3', 'MT-OHR', 'MT-CR'],
          selectedConsequences: [],
          vafRange: [0.00001, 0.1],
          depthRange: [0, 10000],
          disease: '',
          diseaseShowBlank: false,
          curationSearch: '',
          importantCuration: false,
          gnomADHap: [],
          gbFreqTickIndex: 6,
          gnomADHetFreqTickIndex: 8,
          gnomADHomFreqTickIndex: 8,
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
        vafIndexRange: [1, 3],
        selectedSavedSearch: DEFAULT_VARIANT_SEARCH,
      })

      underTest.vm.onSaveSearch()

      expect(mockSaveSearch).toHaveBeenCalledTimes(1)

      expect(mockSaveSearch).toHaveBeenNthCalledWith(1, expect.anything(), {
        name: 'Preset Filter 1',
        description: 'Preset Filter 1 desc',
        custom: true,
        filterConfig: {
          posRange: [1000, 15000],
          allele: 'A/C',
          selectedMitoTIP: [],
          selectedTypes: ['SNP', 'INS', 'DEL'],
          selectedGenes: [],
          selectedMasks: ['MT-HV1', 'MT-HV2', 'MT-HV3', 'MT-OHR', 'MT-CR'],
          selectedConsequences: [],
          vafRange: [0.01, 0.03],
          depthRange: [0, 10000],
          disease: '',
          diseaseShowBlank: false,
          curationSearch: '',
          importantCuration: false,
          gnomADHap: [],
          gbFreqTickIndex: 6,
          gnomADHetFreqTickIndex: 8,
          gnomADHomFreqTickIndex: 8,
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
      })
    })
  })
})
