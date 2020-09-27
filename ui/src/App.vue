<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span>Mito Report for {{ sample }}</span>
      </v-toolbar-title>
      <v-progress-circular class="ml-4" v-if="loading" indeterminate />
      <v-spacer></v-spacer>
      <v-btn @click.prevent="downloadSettings" icon large>
        <v-icon large>mdi-download</v-icon>
      </v-btn>
      <v-menu
        v-model="settingsMenu"
        :close-on-content-click="false"
        :nudge-width="500"
        offset-x
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn icon large v-bind="attrs" v-on="on">
            <v-icon large>mdi-cog</v-icon>
          </v-btn>
        </template>

        <v-card>
          <v-form id="save-settings-form" @submit.prevent="onSaveSettings">
            <v-card-title>Settings</v-card-title>
            <v-divider></v-divider>
            <v-text-field
              name="inputBamDir"
              :value="settingsBamDir"
              :hint="bamDirInputHint"
              @input="onBamDirChange"
              label="BAM File Directory"
              persistent-hint
              class="px-4 py-8"
              maxlength="1000"
            ></v-text-field>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn text @click="settingsMenu = false">Cancel</v-btn>
              <v-btn
                type="submit"
                form="save-settings-form"
                color="primary"
                text
                :disabled="settingsSubmitDisabled"
                >Save</v-btn
              >
            </v-card-actions>
          </v-form>
        </v-card>
      </v-menu>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <v-row>
          <v-col class="pt-0">
            <v-tabs>
              <v-tab to="/variants">Variants</v-tab>
              <v-tab to="/deletions">Deletions</v-tab>
              <v-spacer />
            </v-tabs>
          </v-col>
        </v-row>

        <router-view></router-view>
      </v-container>
    </v-main>

    <v-snackbar
      :value="snackbar.active"
      :color="snackbar.color"
      :timeout="snackbar.timeout"
      bottom
    >
      {{ snackbar.message }}
      <template v-slot:action="{ attrs }">
        <v-btn dark text v-bind="attrs" @click="closeSnackbar">
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import { SAVE_INTERVAL_MS } from '@/shared/constants'

export default {
  name: 'App',

  created() {
    this.$store.dispatch('fetchData')
    this.saveInterval = setInterval(this.saveSettings, SAVE_INTERVAL_MS)
  },

  data: () => {
    return {
      saveInterval: null,
      settingsMenu: true,
      newBamDir: 'not set',
    }
  },

  computed: {
    ...mapState(['loading', 'snackbar']),
    ...mapGetters(['sample', 'settingsBamDir', 'settingsBamFilename']),
    bamDirInputHint() {
      return `Directory to BAM File ${this.settingsBamFilename}`
    },
    settingsSubmitDisabled() {
      if (!this.settingsBamDir || this.newBamDir === 'not set') {
        return true
      }

      return this.newBamDir === this.settingsBamDir
    },
  },

  methods: {
    saveSettings: function() {
      this.$store.dispatch('saveSettings')
    },

    downloadSettings: function() {
      this.$store.dispatch('downloadSettings')
    },

    closeSnackbar: function() {
      this.$store.dispatch('closeSnackbar')
    },

    onBamDirChange: function(newBamDir) {
      this.newBamDir = newBamDir
    },

    onSaveSettings: function() {
      // Keep form submission simple for now as there is only one input.
      this.$store.dispatch('saveBamDir', this.newBamDir)
      this.settingsMenu = false
    },
  },

  beforeDestroy() {
    clearInterval(this.saveInterval)
  },
}
</script>
