import CoveragePlots from '@/components/CoveragePlots.vue'
import router from '@/router/index.js'
import Deletions from '@/views/Deletions.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import store from './TestStore'

describe('Deletions.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    underTest = mount(Deletions, {
      LOCAL_VUE,
      store,
      router,
      vuetify,
    })
  })

  it('renders CoveragePlots', async () => {
    router.push('/deletions')
    await underTest.vm.$nextTick()
    const coveragePlots = underTest.findComponent(CoveragePlots)

    expect(router.currentRoute.fullPath).toBe('/deletions')
    expect(coveragePlots.exists()).toBe(true)

    const coveragePlot = coveragePlots.find('#coveragePlot')
    expect(coveragePlot.exists()).toBe(true)
    expect(coveragePlot.element.tagName).toBe('svg')

    const splitReadPlot = coveragePlots.find('#splitReadPlot')
    expect(splitReadPlot.exists()).toBe(true)
    expect(splitReadPlot.element.tagName).toBe('svg')
  })
})
