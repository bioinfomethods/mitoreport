import CoveragePlots from '@/components/CoveragePlots.vue'
import router from '@/router/index.js'
import Deletions from '@/views/Deletions.vue'
import { createLocalVue, shallowMount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('Deletions.vue', () => {
  let underTest

  beforeEach(() => {
    const LOCAL_VUE = createLocalVue()
    Vue.use(Vuetify)
    Vue.use(Vuex)
    Vue.use(VueRouter)

    underTest = shallowMount(Deletions, {
      LOCAL_VUE,
      router,
    })
  })

  it('renders VariantTable', () => {
    router.push('/deletions')
    const coveragePlots = underTest.findComponent(CoveragePlots)

    expect(router.currentRoute.fullPath).toBe('/deletions')
    expect(coveragePlots.exists()).toBe(true)
  })
})
