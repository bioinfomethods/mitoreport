<template>
  <v-card rounded="0">
    <v-card-text>
      <v-row align="start" justify="start" no-gutters>
        <v-col md="4">
          <VariantCuration
            :variantId="variantId"
            :curation="getCurationByVariantId(variantId)"
          ></VariantCuration>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import VariantCuration from '@/components/VariantCuration.vue'

export default {
  name: 'VariantDetails',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  components: {
    VariantCuration,
  },

  computed: {
    ...mapGetters(['getVariantTags', 'getCurationByVariantId']),
  },
}
</script>

<style lang="css" scoped></style>
