import GeneCardsLink from '@/components/GeneCardsLink.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('GeneCardsLink.vue', () => {
  let vuetify
  Vue.use(Vuetify)
  vuetify = new Vuetify()

  Vue.use(Vuex)
  const LOCAL_VUE = createLocalVue()
  LOCAL_VUE.use(Vuex)

  it('renders correct GeneCards URL', () => {
    const getters = {
      geneCardsUrlPrefix: () => {
        return 'http://genecards.host:12345/carddisp.pl'
      },
    }

    const store = new Vuex.Store({
      getters,
      strict: process.env.NODE_ENV !== 'production',
    })

    let underTest = mount(GeneCardsLink, {
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
