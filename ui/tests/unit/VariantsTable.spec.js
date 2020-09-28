import VariantTable from '@/components/VariantTable.vue'
import router from '@/router/index.js'
import { DEFAULT_VARIANT_SEARCH } from '@/shared/constants'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import Vuex from 'vuex'
import defaultSettings from '../fixtures/defaultSettings.json'
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
      'Position',
      'Allele',
      'Type',
      'Gene',
      'Consequence',
      'VAF',
      'Depth',
      'Disease',
      'MitoMap',
      'Curated Refs',
      'HGVS.p',
      'HGVS.c',
      'HGVS',
    ]
    const wrapperText = underTest.text()
    expHeaderTexts.every(header => {
      expect(wrapperText).toContain(header)
    })
  })

  describe('onSavedSearchChange()', () => {
    it('change to preset search sets filterConfig correctly', () => {
      underTest.vm.onSavedSearchChange(
        defaultSettings.samples[0].variantSearches.find(
          vs => vs.name === 'Preset Filter 1'
        )
      )

      expect(underTest.vm.$data.filterConfig).toEqual({
        posRange: [200, 16300],
        allele: 'A/C',
        selectedTypes: ['SNP', 'INS', 'DEL'],
        selectedGenes: [],
        selectedConsequences: [],
        vafRange: [0.00001, 0.1],
        depthRange: [0, 10000],
        disease: '',
        mitoMap: '',
        curatedRefs: '',
        hgvsp: '',
        hgvsc: '',
        hgvs: '',
      })
      expect(underTest.vm.$data.vafIndexRange).toEqual([1, 10])
      expect(underTest.vm.$data.searchForm).toEqual({
        valid: true,
        name: '',
        description: '',
      })
    })

    it('change to custom search sets filterConfig correctly', () => {
      underTest.vm.onSavedSearchChange({
        name: 'Custom Filter 1',
        description: 'Custom 1 description',
        custom: true,
      })

      expect(underTest.vm.$data.filterConfig).toEqual({
        posRange: [0, 16500],
        allele: '',
        selectedTypes: ['SNP', 'DEL'],
        selectedGenes: [],
        selectedConsequences: [],
        vafRange: [0.00001, 0.05],
        depthRange: [500, 12000],
        disease: '',
        mitoMap: '',
        curatedRefs: '',
        hgvsp: '',
        hgvsc: '',
        hgvs: '',
      })
      expect(underTest.vm.$data.vafIndexRange).toEqual([1, 9])
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
          selectedTypes: ['SNP', 'INS', 'DEL'],
          selectedGenes: [],
          selectedConsequences: [],
          vafRange: [0.00001, 0.1],
          depthRange: [0, 10000],
          disease: '',
          mitoMap: '',
          curatedRefs: '',
          hgvsp: '',
          hgvsc: '',
          hgvs: '',
        },
        vafIndexRange: [3, 7],
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
          selectedTypes: ['SNP', 'INS', 'DEL'],
          selectedGenes: [],
          selectedConsequences: [],
          vafRange: [0.0001, 0.03],
          depthRange: [0, 10000],
          disease: '',
          mitoMap: '',
          curatedRefs: '',
          hgvsp: '',
          hgvsc: '',
          hgvs: '',
        },
      })
    })
  })
})
