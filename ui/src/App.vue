<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span class="white--text text-h6">Mito Report for {{ sample }}</span>
      </v-toolbar-title>
      <v-progress-circular class="ml-4" v-if="loading" indeterminate />
      <v-spacer></v-spacer>
      <v-btn @click.prevent="downloadSettings" icon large>
        <v-icon class="white--text" large>mdi-download</v-icon>
      </v-btn>
      <v-menu
        v-model="settingsMenu"
        :close-on-content-click="false"
        :nudge-width="500"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn id="btnSettingsMenu" icon large v-on="on" v-bind="attrs">
            <v-icon class="white--text" large>mdi-cog</v-icon>
          </v-btn>
        </template>
        <v-card>
          <v-form
            id="save-settings-form"
            v-model="settingsForm.valid"
            @submit.prevent="onSaveSettings"
          >
            <v-card-title>Settings</v-card-title>
            <v-divider></v-divider>
            <v-text-field
              id="inputNewBamDir"
              name="inputNewBamDir"
              v-model="settingsForm.newBamDir"
              :rules="[rules.required]"
              label="BAM File Directory"
              hint="replaced by message slot"
              persistent-hint
              class="px-4 py-8"
              maxlength="1000"
            >
              <template v-slot:message>
                <span
                  >Directory to BAM File
                  <span class="font-weight-bold">{{
                    settingsBamFilename
                  }}</span>
                </span>
              </template>
            </v-text-field>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn text @click="settingsMenu = false">Cancel</v-btn>
              <v-btn
                id="btnSubmitSaveSettings"
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

<style>
@import url('./assets/css/mitoreport.css');
</style>

<script>
import { mapGetters, mapState } from 'vuex'
import { SAVE_INTERVAL_MS } from '@/shared/constants'

export default {
  name: 'App',

  async created() {
    console.info(`MitoReport version=${process.env.VUE_APP_VERSION}`)
    await this.$store.dispatch('fetchData')
    this.saveInterval = setInterval(this.saveSettings, SAVE_INTERVAL_MS)
    this.settingsForm.newBamDir = this.settingsBamDir
  },

  data: () => {
    return {
      saveInterval: null,
      settingsMenu: false,
      settingsForm: {
        valid: true,
        newBamDir: '',
      },
      rules: {
        required: value => !!value || 'Required.',
      },
    }
  },

  computed: {
    ...mapState(['loading', 'snackbar']),
    ...mapGetters(['sample', 'settingsBamDir', 'settingsBamFilename']),
    bamDirInputHint() {
      return `Directory to BAM File ${this.settingsBamFilename}`
    },
    settingsSubmitDisabled() {
      if (!this.settingsBamDir || !this.settingsForm.valid) {
        return true
      }

      return this.settingsForm.newBamDir === this.settingsBamDir
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
      this.settingsForm.newBamDir = newBamDir
    },

    onSaveSettings: function() {
      // Keep form submission simple for now as there is only one input.
      this.$store.dispatch('saveBamDir', this.settingsForm.newBamDir)
      this.settingsMenu = false
    },
  },

  beforeDestroy() {
    clearInterval(this.saveInterval)
  },
}
</script>
