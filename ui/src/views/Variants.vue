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
import { TagRepository } from 'tagmesh'
import Vue from 'vue'
import { mapGetters, mapState } from 'vuex'

export default {
  name: 'Variants',

  async mounted() {
    const tagRepo = await TagRepository.create(this.sampleId, this.tagStore, {
      serverURL: this.getSettingsCouchDbUrl,
    })
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
  },

  data: () => {
    return {
      tagRepo: {},
      tagStore: {},
    }
  },

  computed: {
    ...mapState(['sampleId', 'syncEnabled', 'couchDbPassword']),
    ...mapGetters([
      'getSyncFeatureEnabled',
      'getSettingsCouchDbUrl',
      'getSettingsCouchDbUsername',
    ]),
  },

  watch: {
    syncEnabled: async function(value) {
      if (this.getSyncFeatureEnabled) {
        if (value) {
          try {
            await this.tagRepo.connect(
              this.getSettingsCouchDbUrl,
              this.getSettingsCouchDbUsername,
              this.couchDbPassword
            )
          } catch (error) {
            this.$store.dispatch('openSnackbar', {
              message: `Failed to connect to CouchDB: ${error?.message}`,
              color: 'error',
            })
            this.$store.dispatch('toggleSync', false)
          }
        } else {
          await this.tagRepo.disconnect()
        }
      }
    },
  },
}
</script>
