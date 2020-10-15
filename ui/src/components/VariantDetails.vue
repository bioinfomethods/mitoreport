<template>
  <v-card rounded="0">
    <v-card-text>
      <span>Variant Details for {{ variantId }}</span>
      <!-- <v-row align="start" justify="start">
        <v-col md="auto">
          <VariantCuration
            :curation="getCurationByVariantId(variantId)"
          ></VariantCuration>
        </v-col>
      </v-row> -->
    </v-card-text>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
// import VariantCuration from '@/components/VariantCuration.vue'

export default {
  name: 'VariantDetails',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  // components: {
  //   VariantCuration,
  // },

  computed: {
    ...mapGetters(['getVariantTags', 'getCurationByVariantId']),
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
