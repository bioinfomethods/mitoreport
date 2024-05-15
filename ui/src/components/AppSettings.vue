<template>
  <v-menu
    v-model="settingsMenu"
    :close-on-content-click="false"
    :nudge-width="500"
  >
    <template v-slot:activator="{ on: menu, attrs }">
      <v-tooltip bottom>
        <template v-slot:activator="{ on: tooltip }">
          <v-btn
            id="btnSettingsMenu"
            icon
            large
            v-on="{ ...tooltip, ...menu }"
            v-bind="attrs"
          >
            <v-icon class="white--text" large>mdi-cog</v-icon>
          </v-btn>
        </template>
        <span>Application Settings</span>
      </v-tooltip>
    </template>
    <v-card>
      <v-form
        id="save-settings-form"
        v-model="settingsForm.valid"
        @submit.prevent="saveAppSettings"
      >
        <v-card-title>Settings</v-card-title>
        <div v-if="syncFeature">
          <v-divider></v-divider>
          <v-card-subtitle>Couch DB</v-card-subtitle>
          <v-card-text>
            <v-text-field
              id="inputCouchDbUrl"
              name="inputCouchDbUrl"
              v-model="settingsForm.newCouchDbUrl"
              label="Couch DB URL"
              hint="URL to MCRI Mitoreport Couch DB"
              persistent-hint
              maxlength="1000"
            >
            </v-text-field>
            <v-text-field
              id="inputCouchDbUsername"
              name="inputCouchDbUsername"
              v-model="settingsForm.newCouchDbUsername"
              label="Couch DB username"
              hint="Couch DB username, should be same value as ENV var COUCHDB_USER on server"
              persistent-hint
              maxlength="50"
              width="150px"
              max-width="150px"
            >
            </v-text-field>
            <v-text-field
              id="inputCouchDbPassword"
              name="inputCouchDbPassword"
              @input="onPasswordChange"
              label="Couch DB password"
              hint="Couch DB password, should be same value as ENV var COUCHDB_PASSWORD on server"
              persistent-hint
              maxlength="50"
              width="150px"
              max-width="150px"
            >
            </v-text-field>
          </v-card-text>
        </div>
        <v-divider></v-divider>
        <v-card-subtitle>IGV</v-card-subtitle>
        <v-card-text>
          <v-text-field
            id="inputNewBamDir"
            name="inputNewBamDir"
            v-model="settingsForm.newBamDir"
            :rules="[validationRules.required]"
            label="BAM File Directory"
            hint="replaced by message slot"
            persistent-hint
            maxlength="1000"
          >
            <template v-slot:message>
              <span
                >Directory to BAM File
                <span class="font-weight-bold">{{
                  getSettingsBamFilename
                }}</span>
              </span>
            </template>
          </v-text-field>
        </v-card-text>
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
import { mapGetters, mapState } from 'vuex'
import { DEBOUNCE_DELAY } from '@/shared/constants'
import * as _ from 'lodash'

export default {
  name: 'AppSettings',

  mounted() {
    this.settingsForm.newBamDir = this.getSettingsBamDir
    this.settingsForm.userTags = _.cloneDeep(
      this.getVariantTags.filter(t => t.custom)
    )
  },

  data: () => {
    return {
      settingsMenu: false,
      deleteTagMenus: {},
      settingsForm: {
        valid: true,
        dirty: false,
        newCouchDbUrl: '',
        newCouchDbUsername: '',
        newCouchDbPassword: '',
        newBamDir: '',
        newTagName: '',
        newTagImportant: false,
        userTags: [],
      },
    }
  },

  computed: {
    ...mapState(['syncFeature']),
    ...mapGetters([
      'getSettingsCouchDbUrl',
      'getSettingsCouchDbUsername',
      'getSettingsBamDir',
      'getSettingsBamFilename',
      'getVariantTags',
    ]),

    bamDirInputHint() {
      return `Directory to BAM File ${this.getSettingsBamFilename}`
    },

    defaultTags() {
      return this.getVariantTags?.filter(t => !t.custom) || []
    },

    customTags() {
      return this.getVariantTags?.filter(t => t.custom) || []
    },

    settingsSubmitDisabled() {
      if (!this.settingsForm.valid) {
        return true
      }
      if (!this.settingsForm.dirty) {
        return true
      }

      return false
    },

    addNewTagDisabled() {
      return this.settingsForm.newTagName === ''
    },

    validationRules() {
      return {
        required: value => !!value || 'Required.',
        newTagNameUnique: this.newTagNameUnique,
      }
    },
  },

  methods: {
    saveSettings: function() {
      this.$store.dispatch('saveSettings')
    },

    // onBamDirChange: function(newBamDir) {
    //   this.settingsForm.newBamDir = newBamDir
    // },

    saveAppSettings: function() {
      this.$store.dispatch('saveAppSettings', {
        newCouchDbUrl: this.settingsForm.newCouchDbUrl,
        newCouchDbUsername: this.settingsForm.newCouchDbUsername,
        newBamDir: this.settingsForm.newBamDir,
        userTags: this.settingsForm.userTags,
      })
      this.settingsMenu = false
      this.settingsForm.newCouchDbUrl = this.getSettingsCouchDbUrl
      this.settingsForm.newBamDir = this.getSettingsBamDir
      this.settingsForm.userTags = this.getVariantTags.filter(t => t.custom)
    },

    addNewTag: function() {
      this.settingsForm.userTags.push({
        name: this.settingsForm.newTagName,
        important: this.settingsForm.newTagImportant,
        custom: true,
      })
      this.resetNewTagForm()
    },

    deleteTag: function(toDelete) {
      this.settingsForm.userTags = this.settingsForm.userTags.filter(
        t => t.name !== toDelete.name
      )
      delete this.deleteTagMenus[toDelete.name]
    },

    resetNewTagForm: function() {
      this.settingsForm.newTagName = ''
      this.settingsForm.newTagImportant = false
    },

    newTagNameUnique: function(value) {
      ''.toUpperCase
      return (
        !this.getVariantTags.some(
          t => t.name?.toUpperCase() === value?.toUpperCase()
        ) &&
        !this.settingsForm.userTags.some(
          t => t.name?.toUpperCase() === value?.toUpperCase()
        )
      )
    },
    onPasswordChange: _.debounce(function(newCouchDbPassword) {
      this.$store.dispatch('storeCouchDbPassword', newCouchDbPassword)
    }, DEBOUNCE_DELAY.MEDIUM),
  },

  watch: {
    getSettingsCouchDbUrl: function(value) {
      this.settingsForm.newCouchDbUrl = value
    },
    getSettingsCouchDbUsername: function(value) {
      this.settingsForm.newCouchDbUsername = value
    },
    getSettingsBamDir: function(value) {
      this.settingsForm.newBamDir = value
    },
    getVariantTags: function(value) {
      this.settingsForm.userTags = _.cloneDeep(value.filter(t => t.custom))
    },
    settingsForm: {
      deep: true,
      handler: function() {
        const couchDbUrlMatchInitial =
          this.settingsForm.newCouchDbUrl === this.getSettingsCouchDbUrl
        const couchDbUsernameMatchInitial =
          this.settingsForm.newCouchDbUsername ===
          this.getSettingsCouchDbUsername
        const bamMatchInitial =
          this.settingsForm.newBamDir === this.getSettingsBamDir
        const userTagsMatchInitial = _.isEqual(
          this.settingsForm.userTags,
          this.customTags
        )
        const matchInitial =
          couchDbUrlMatchInitial &&
          couchDbUsernameMatchInitial &&
          bamMatchInitial &&
          userTagsMatchInitial

        if (matchInitial) {
          this.settingsForm.dirty = false
        } else {
          this.settingsForm.dirty = true
        }
      },
    },
  },
}
</script>

<style lang="scss" scoped></style>
