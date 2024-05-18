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
    this.sync(this.syncEnabled)
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

  methods: {
    sync: async function(value) {
      if (this.getSyncFeatureEnabled) {
        if (value) {
          try {
            if (this.$keycloak?.authenticated) {
              const idToken = this.$keycloak.idToken
              await this.tagRepo.connect({
                couchBaseURL: this.getSettingsCouchDbUrl,
                headers: {
                  'Content-Type': 'application/json',
                  Authorization: `Bearer ${idToken}`,
                },
              })
            } else {
              await this.tagRepo.connect({
                couchBaseURL: this.getSettingsCouchDbUrl,
                username: this.getSettingsCouchDbUsername,
                password: this.couchDbPassword,
              })
            }
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

  watch: {
    syncEnabled: async function(value) {
      this.sync(value)
    },
  },
}
</script>
