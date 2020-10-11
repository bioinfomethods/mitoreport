import CuratedRefLink from '@/components/CuratedRefLink.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('CuratedRefLink.vue', () => {
  let underTest
  let vuetify
  const LOCAL_VUE = createLocalVue()

  beforeEach(() => {
    Vue.use(Vuetify)
    Vue.use(Vuex)
    LOCAL_VUE.use(Vuex)
    vuetify = new Vuetify()
  })

  it('renders correct Curated Reference URL', () => {
    underTest = mount(CuratedRefLink, {
      LOCAL_VUE,
      vuetify,
      propsData: {
        count: 5,
        href:
          'https://mitomap.org/cgi-bin/print_ref_list?refs=10433&title=Coding+Polymorphism+A-G+at+776',
      },
    })

    expect(decodeURIComponent(underTest.vm.curatedRefUrl)).toEqual(
      'https://mitomap.org/cgi-bin/print_ref_list?refs=10433&title=Coding Polymorphism A-G at 776'
    )

    const curatedRefLink = underTest.find('#curatedRefLink')
    expect(curatedRefLink.text()).toEqual('5')
    expect(decodeURIComponent(curatedRefLink.attributes('href'))).toEqual(
      'https://mitomap.org/cgi-bin/print_ref_list?refs=10433&title=Coding Polymorphism A-G at 776'
    )
    const curatedRefSpan = underTest.find('#curatedRefSpan')
    expect(curatedRefSpan.exists()).toBe(false)
  })

  it('Given count is zero, renders correct null Curated Reference URL', () => {
    underTest = mount(CuratedRefLink, {
      LOCAL_VUE,
      vuetify,
      propsData: {
        count: 0,
      },
    })

    expect(underTest.vm.curatedRefUrl).toBeNull()

    const curatedRefLink = underTest.find('#curatedRefLink')
    expect(curatedRefLink.exists()).toBe(false)
    const curatedRefSpan = underTest.find('#curatedRefSpan')
    expect(curatedRefSpan.exists()).toBe(true)
    expect(curatedRefSpan.text()).toEqual('0')
  })
})
