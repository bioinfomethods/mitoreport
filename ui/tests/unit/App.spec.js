import App from '@/App.vue'
import router from '@/router'
import Deletions from '@/views/Deletions.vue'
import Variants from '@/views/Variants.vue'
import VariantCuration from '@/components/VariantCuration.vue'
import VariantDetails from '@/components/VariantDetails.vue'
import VariantInfo from '@/components/VariantInfo.vue'
import VariantTable from '@/components/VariantTable.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import Vuex from 'vuex'
import store, { getters, mutations, state } from './TestStore'

const app = document.createElement('div')
app.setAttribute('data-app', true)
document.body.append(app)

describe('App.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    vuetify = new Vuetify()
    underTest = mount(App, {
      LOCAL_VUE,
      store,
      router,
      vuetify,
    })
  })

  it('renders App and defaults to Variants page with VariantTable rendered', async () => {
    expect(router.currentRoute.fullPath).toBe('/variants')
    const variantsView = underTest.findComponent(Variants)
    expect(variantsView.exists()).toBe(true)
    const variantTable = variantsView.findComponent(VariantTable)
    expect(variantTable.exists()).toBe(true)
  })

  it('renders VariantDetails when navigate to a variant row', async () => {
    router.push({
      name: 'variantDetails',
      params: { variantId: 'chrM-2465-T-A' },
    })
    await underTest.vm.$nextTick()

    const variantDetails = underTest.findComponent(VariantDetails)
    expect(variantDetails.exists()).toBe(true)

    const variantInfo = variantDetails.findComponent(VariantInfo)
    expect(variantInfo.exists()).toBe(true)

    const variantCuration = variantDetails.findComponent(VariantCuration)
    expect(variantCuration.exists()).toBe(true)
  })

  describe('navigate to /deletions', () => {
    it('renders Deletions page', async () => {
      router.push('/deletions')
      await underTest.vm.$nextTick()

      expect(router.currentRoute.fullPath).toBe('/deletions')
      const deletionsView = underTest.findComponent(Deletions)
      expect(deletionsView.exists()).toBe(true)
    })
  })

  describe('save settings', () => {
    it('dispatches action to save', async () => {
      const mockSaveAppSettings = jest.fn()
      const mockActions = {
        saveAppSettings: mockSaveAppSettings,
        fetchData: jest.fn(),
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

      let underTest = mount(App, {
        LOCAL_VUE,
        store,
        router,
        vuetify,
      })

      const btnSettingsMenu = underTest.find('#btnSettingsMenu')
      await btnSettingsMenu.trigger('click')
      const btnSubmitSaveSettings = underTest.find('#btnSubmitSaveSettings')
      const inputNewBamDir = underTest.find('#inputNewBamDir')
      await inputNewBamDir.setValue('/tmp/newBamDir/')
      await btnSubmitSaveSettings.trigger('click')

      expect(mockSaveAppSettings).toHaveBeenCalledTimes(1)

      expect(mockSaveAppSettings).toHaveBeenNthCalledWith(
        1,
        expect.anything(),
        {
          newBamDir: '/tmp/newBamDir/',
          userTags: [],
        }
      )
    })
  })
})
