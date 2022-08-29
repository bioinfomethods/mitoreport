<template>
  <VariantTable :variantId="variantId" />
</template>

<script>
import VariantTable from "@/components/VariantTable.vue";

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
