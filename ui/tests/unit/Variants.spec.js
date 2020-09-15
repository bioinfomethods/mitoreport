import { shallowMount } from '@vue/test-utils'
import VariantTable from '@/components/VariantTable.vue'
import Variants from '@/views/Variants.vue'

describe('Variants.vue', () => {
  it('renders VariantTable', () => {
    const wrapper = shallowMount(Variants)
    const variantTable = wrapper.findComponent(VariantTable)
    expect(variantTable.exists()).toBe(true)
  })
})
