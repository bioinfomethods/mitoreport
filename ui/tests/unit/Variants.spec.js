import VariantTable from '@/components/VariantTable.vue'
import router from '@/router/index.js'
import Variants from '@/views/Variants.vue'
import { createLocalVue, shallowMount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

describe('Variants.vue', () => {
  let underTest

  beforeEach(() => {
    const LOCAL_VUE = createLocalVue()
    Vue.use(Vuetify)
    Vue.use(Vuex)
    Vue.use(VueRouter)

    underTest = shallowMount(Variants, {
      LOCAL_VUE,
      router,
    })
  })

  it('renders VariantTable', () => {
    const variantTable = underTest.findComponent(VariantTable)

    expect(router.currentRoute.fullPath).toBe('/variants')
    expect(variantTable.exists()).toBe(true)
  })
})
