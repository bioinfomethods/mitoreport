<template>
  <VariantTable :variantId="variantId" />
</template>

<script>
import VariantTable from '@/components/VariantTable.vue'

export default {
  name: 'Variants',

  components: {
    VariantTable,
  },

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },
}
</script>
