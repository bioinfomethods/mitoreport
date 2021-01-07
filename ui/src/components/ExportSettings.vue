<template>
  <v-menu
    v-model="exportMenu"
    :close-on-content-click="false"
    :nudge-width="500"
  >
    <template v-slot:activator="{ on: menu, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn
            id="btnExportMenu"
            icon
            large
            v-on="{ ...tooltip, ...menu }"
            v-bind="attrs"
          >
            <v-icon class="white--text" large>mdi-download</v-icon>
          </v-btn>
        </template>
        <span>Export</span>
      </v-tooltip>
    </template>
    <v-card>
      <v-card-title>Export</v-card-title>
      <v-divider></v-divider>
      <v-list three-line>
        <v-list-item-group>
          <v-list-item @click.prevent="downloadSettings">
            <v-list-item-content>
              <v-list-item-title>Export All</v-list-item-title>
              <v-list-item-subtitle
                >Export all settings and curations for all Mito Report samples
                found in your browser's storage</v-list-item-subtitle
              >
            </v-list-item-content>
          </v-list-item>
          <v-list-item @click.prevent="downloadSettingsSample">
            <v-list-item-content>
              <v-list-item-title>Export Single</v-list-item-title>
              <v-list-item-subtitle
                >Export only for current report sample
                <span class="font-weight-bold">{{ sampleId }}</span>
              </v-list-item-subtitle>
              <v-list-item-subtitle
                >This can be used to share current report settings and curations
                with other Mito Report users
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </v-list-item-group>
      </v-list>
    </v-card>
  </v-menu>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'ExportSettings',

  data: () => {
    return {
      exportMenu: false,
    }
  },

  computed: {
    ...mapState(['sampleId']),
  },

  methods: {
    downloadSettings: function() {
      this.exportMenu = false
      this.$store.dispatch('downloadSettings')
    },
    downloadSettingsSample: function() {
      this.exportMenu = false
      this.$store.dispatch('downloadSettingsSample')
    },
  },
}
</script>

<style lang="scss" scoped></style>
