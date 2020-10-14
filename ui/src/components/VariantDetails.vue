<template>
  <div>
    <span>Variant Details for {{ variantId }}</span>
  </div>
</template>

<script>
export default {
  name: 'VariantDetails',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  mounted() {
    console.log(
      `VariantDetails route=${JSON.stringify(
        this.$route.path
      )},params=${JSON.stringify(this.$route.params)},variantId=${
        this.variantId
      }`
    )
  },
}
</script>
