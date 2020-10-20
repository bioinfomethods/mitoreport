<template>
  <v-card rounded="0">
    <v-card-text v-show="variantId">
      <v-row align="start" justify="start" no-gutters>
        <v-col md="3">
          <VariantInfo :variantId="variantId"></VariantInfo>
        </v-col>
        <v-divider vertical class="pr-4 mr-4"></v-divider>
        <v-col md="3">
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
    VariantCuration,
    VariantInfo,
  },

  computed: {
    ...mapGetters(['getVariantTags', 'getCurationByVariantId']),
  },
}
</script>

<style lang="css" scoped></style>
