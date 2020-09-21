import VariantTable from '@/components/VariantTable.vue'
import { createLocalVue, shallowMount } from '@vue/test-utils'
import { cloneDeep } from 'lodash'
import Vue from 'vue'
import Vuetify from 'vuetify'
import Vuex from 'vuex'

Vue.use(Vuetify)
Vue.use(Vuex)
const localVue = createLocalVue()

const testStore = {
  state: {
    count: 0,
  },
}
const store = new Vuex.Store(cloneDeep(testStore))

describe('VariantTable.vue', () => {
  it('renders VariantTable', () => {
    const wrapper = shallowMount(VariantTable, { store, localVue })
    expect(wrapper.exists()).toBe(true)
  })
})
