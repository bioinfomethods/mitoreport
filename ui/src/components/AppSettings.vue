<template>
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
              <span class="font-weight-bold">{{ getSettingsBamFilename }}</span>
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
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'AppSettings',

  mounted() {
    this.settingsForm.newBamDir = this.getSettingsBamDir
  },

  data: () => {
    return {
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
    ...mapGetters(['getSample', 'getSettingsBamDir', 'getSettingsBamFilename']),
    bamDirInputHint() {
      return `Directory to BAM File ${this.getSettingsBamFilename}`
    },
    settingsSubmitDisabled() {
      if (!this.getSettingsBamDir || !this.settingsForm.valid) {
        return true
      }

      return this.settingsForm.newBamDir === this.getSettingsBamDir
    },
  },

  methods: {
    saveSettings: function() {
      this.$store.dispatch('saveSettings')
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

  watch: {
    getSettingsBamDir: function(value) {
      this.settingsForm.newBamDir = value
    },
  },
}
</script>

<style lang="scss" scoped></style>
