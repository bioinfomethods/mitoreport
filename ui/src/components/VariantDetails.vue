<template>
  <v-card rounded="0">
    <v-card-text class="ma-0 pa-2">
      <v-row align="start" justify="start" no-gutters>
        <v-col cols="3">
          <VariantInfo :variantId="variantId"></VariantInfo>
        </v-col>
        <v-divider vertical inset class="pr-4 mr-4"></v-divider>
        <v-col cols="2">
          <VariantCuration
            :variantId="variantId"
            :curation="getCurationByVariantId(variantId)"
          ></VariantCuration>
        </v-col>
        <v-divider vertical inset class="pr-4 mr-4"></v-divider>
        <v-col cols="6">
          <VariantCharts :variantId="variantId"></VariantCharts>
        </v-col>
      </v-row>
    </v-card-text>
  </v-card>
</template>

<script>
import { mapGetters } from 'vuex'
import VariantCharts from '@/components/VariantCharts.vue'
import VariantCuration from '@/components/VariantCuration.vue'
import VariantInfo from '@/components/VariantInfo.vue'

export default {
  name: 'VariantDetails',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  components: {
    VariantCharts,
    VariantCuration,
    VariantInfo,
  },

  computed: {
    ...mapGetters(['getVariantTags', 'getCurationByVariantId']),
  },
}
</script>

<style lang="css" scoped></style>
