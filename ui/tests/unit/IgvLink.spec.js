import IgvLink from '@/components/IgvLink.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('IgvLink.vue', () => {
  let vuetify
  Vue.use(Vuetify)
  vuetify = new Vuetify()

  Vue.use(Vuex)
  const LOCAL_VUE = createLocalVue()
  LOCAL_VUE.use(Vuex)

  it('renders correct IGV URL', () => {
    const getters = {
      igvHost: () => {
        return 'http://localhost:60151'
      },
      settingsBamFile: () => {
        return '/bampath/bamFile.bam'
      },
    }

    const store = new Vuex.Store({
      getters,
      strict: process.env.NODE_ENV !== 'production',
    })

    let underTest = mount(IgvLink, {
      LOCAL_VUE,
      store,
      vuetify,
      propsData: {
        position: 8888,
      },
    })

    expect(decodeURIComponent(underTest.vm.igvUrl)).toEqual(
      'http://localhost:60151/load?file=file:///bampath/bamFile.bam&locus=chrM:8888'
    )

    const igvLink = underTest.find('#igvUrlLink')
    expect(decodeURIComponent(igvLink.attributes('href'))).toEqual(
      'http://localhost:60151/load?file=file:///bampath/bamFile.bam&locus=chrM:8888'
    )
  })
})
