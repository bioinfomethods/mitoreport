<template>
  <VariantTable :variantId="variantId" />
</template>

<script>
import VariantTable from '@/components/VariantTable.vue'
import { mapActions } from 'vuex'

export default {
  name: 'Variants',

  created() {},

  components: {
    VariantTable,
  },

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  methods: {},
}
</script>
