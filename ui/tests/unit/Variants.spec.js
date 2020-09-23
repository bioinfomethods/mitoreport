import VariantTable from '@/components/VariantTable.vue'
import router from '@/router/index.js'
import Variants from '@/views/Variants.vue'
import { createLocalVue, shallowMount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'

describe('Variants.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    underTest = shallowMount(Variants, {
      LOCAL_VUE,
      router,
      vuetify,
    })
  })

  it('renders VariantTable', () => {
    const variantTable = underTest.findComponent(VariantTable)

    expect(router.currentRoute.fullPath).toBe('/variants')
    expect(variantTable.exists()).toBe(true)
  })
})
