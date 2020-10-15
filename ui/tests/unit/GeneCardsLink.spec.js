import GeneCardsLink from '@/components/GeneCardsLink.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('GeneCardsLink.vue', () => {
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
      getGeneCardsUrlPrefix: () => {
        return 'http://genecards.host:12345/carddisp.pl'
      },
    }

    store = new Vuex.Store({
      getters,
      strict: process.env.NODE_ENV !== 'production',
    })
  })

  it('renders correct GeneCards URL', () => {
    underTest = mount(GeneCardsLink, {
      LOCAL_VUE,
      store,
      vuetify,
      propsData: {
        gene: 'MT-ND1',
      },
    })

    expect(decodeURIComponent(underTest.vm.geneCardsUrl)).toEqual(
      'http://genecards.host:12345/carddisp.pl?gene=MT-ND1#diseases'
    )

    const geneCardsLink = underTest.find('#geneCardsUrlLink')
    expect(decodeURIComponent(geneCardsLink.attributes('href'))).toEqual(
      'http://genecards.host:12345/carddisp.pl?gene=MT-ND1#diseases'
    )
  })
})
