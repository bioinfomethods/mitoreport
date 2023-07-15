<template>
  <VariantTable :variantId="variantId" />
</template>

<script>
import VariantTable from '@/components/VariantTable.vue'
import { mapActions } from 'vuex'

export default {
  name: 'Variants',

  created() {
    this.setSyncFeature(this.syncFeature)
  },

  components: {
    VariantTable,
  },

  props: {
    variantId: {
      type: String,
      required: false,
    },
    syncFeature: {
      type: Boolean,
      required: false,
    },
  },

  methods: {
    ...mapActions(['setSyncFeature']),
  },
}
</script>
