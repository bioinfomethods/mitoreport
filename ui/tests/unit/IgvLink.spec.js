import IgvLink from '@/components/IgvLink.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('IgvLink.vue', () => {
  let underTest
  let vuetify
  let getters
  let store
  const LOCAL_VUE = createLocalVue()

  beforeEach(() => {
    Vue.use(Vuetify)
    Vue.use(Vuex)
    LOCAL_VUE.use(Vuex)
    vuetify = new Vuetify()

    getters = {
      getIgvHost: () => {
        return 'http://localhost:60151'
      },
      getSettingsBamFile: () => {
        return '/bampath/bamFile.bam'
      },
    }

    store = new Vuex.Store({
      getters,
      strict: process.env.NODE_ENV !== 'production',
    })
  })

  it('renders correct IGV URL', () => {
    underTest = mount(IgvLink, {
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
