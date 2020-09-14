import { shallowMount } from '@vue/test-utils'
import Home from '@/views/Home.vue'
import VariantTable from '@/components/VariantTable.vue'

describe('Home.vue', () => {
  it('renders VariantTable', () => {
    const wrapper = shallowMount(Home)
    const variantTable = wrapper.findComponent(VariantTable)
    expect(variantTable.exists()).toBe(true)
  })
})
