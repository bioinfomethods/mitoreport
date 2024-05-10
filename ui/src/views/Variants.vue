<template>
  <div>
    <VariantTable :variantId="variantId" />
  </div>
</template>

<script>
import VariantTable from '@/components/VariantTable.vue'
import { mapActions, mapState } from 'vuex'

import 'tagmesh-vue2/dist/style.css'

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

  computed: {
    ...mapState(['tags', 'sampleId']),
  },

  methods: {
    ...mapActions(['setSyncFeature']),
  },
}
</script>
