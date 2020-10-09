import HmtVarLink from '@/components/HmtVarLink.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('HmtVarLink.vue', () => {
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
      hmtVarUrlPrefix: () => {
        return 'https://www.hmtvar.uniba.it/results'
      },
    }

    store = new Vuex.Store({
      getters,
      strict: process.env.NODE_ENV !== 'production',
    })
  })

  it('renders correct HmtVar URL', () => {
    underTest = mount(HmtVarLink, {
      LOCAL_VUE,
      store,
      vuetify,
      propsData: {
        position: 310,
        refAllele: 'T',
        altAllele: 'C',
      },
    })

    expect(decodeURIComponent(underTest.vm.hmtVarSearchResultsUrl)).toEqual(
      'https://www.hmtvar.uniba.it/results?mutation=T310'
    )

    const hmtVarLink = underTest.find('#hmtVarSearchResultsUrlLink')
    expect(decodeURIComponent(hmtVarLink.attributes('href'))).toEqual(
      'https://www.hmtvar.uniba.it/results?mutation=T310'
    )
  })

  it('renders correct HmtVar URL with include alt allele search option', () => {
    underTest = mount(HmtVarLink, {
      LOCAL_VUE,
      store,
      vuetify,
      propsData: {
        position: 310,
        refAllele: 'T',
        altAllele: 'C',
        includeAltInSearch: true,
      },
    })

    expect(decodeURIComponent(underTest.vm.hmtVarSearchResultsUrl)).toEqual(
      'https://www.hmtvar.uniba.it/results?mutation=T310C'
    )

    const hmtVarLink = underTest.find('#hmtVarSearchResultsUrlLink')
    expect(decodeURIComponent(hmtVarLink.attributes('href'))).toEqual(
      'https://www.hmtvar.uniba.it/results?mutation=T310C'
    )
  })
})
