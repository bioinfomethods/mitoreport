<template>
  <div>
    <VariantTable
      :variantId="variantId"
      :tag-repo="this.tagRepo"
      :tag-store="this.tagStore"
    />
  </div>
</template>

<script>
import VariantTable from '@/components/VariantTable.vue'
import { mapActions, mapState } from 'vuex'

import { TagRepository } from 'tagmesh'
import Vue from 'vue'

export default {
  name: 'Variants',

  async mounted() {
    this.setSyncFeature(this.syncFeature)
    const tagRepo = await TagRepository.create(this.sampleId, this.tagStore, {
      serverURL: 'http://localhost:5288/db',
    })
    tagRepo.connect()
    Vue.set(this, 'tagRepo', tagRepo)
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

  data: () => {
    return {
      tagRepo: {},
      tagStore: {},
    }
  },

  computed: {
    ...mapState(['sampleId']),
  },

  methods: {
    ...mapActions(['setSyncFeature']),
  },
}
</script>
