import CoveragePlots from '@/components/CoveragePlots.vue'
import router from '@/router/index.js'
import Deletions from '@/views/Deletions.vue'
import { createLocalVue, shallowMount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'

describe('Deletions.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    underTest = shallowMount(Deletions, {
      LOCAL_VUE,
      router,
      vuetify,
    })
  })

  it('renders CoveragePlots', () => {
    router.push('/deletions')
    const coveragePlots = underTest.findComponent(CoveragePlots)

    expect(router.currentRoute.fullPath).toBe('/deletions')
    expect(coveragePlots.exists()).toBe(true)
  })
})
