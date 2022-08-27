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
        <div v-if="authState.isAuthenticated">
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
        <v-divider></v-divider>
        <v-subheader>Variant Tags</v-subheader>
        <v-card-text>
          <v-row no-gutters v-for="tag in defaultTags" :key="tag.name">
            <v-text-field
              :value="tag.name"
              disabled
              type="text"
              dense
            ></v-text-field>
            <div class="pt-1 px-4">
              <v-icon v-if="tag.important" color="red">mdi-tag-multiple</v-icon>
              <v-icon v-else>mdi-tag-multiple-outline</v-icon>
            </div>
            <v-spacer></v-spacer>
            <v-btn icon disabled> </v-btn>
          </v-row>

          <v-row
            no-gutters
            v-for="(tag, index) in settingsForm.userTags"
            :key="index"
          >
            <v-text-field
              v-model="tag.name"
              type="text"
              disabled
              dense
            ></v-text-field>
            <v-checkbox
              v-model="tag.important"
              on-icon="mdi-tag-multiple"
              label="Important?"
              off-icon="mdi-tag-multiple-outline"
              color="red"
              class="mt-1 pl-4 pt-0"
            ></v-checkbox>
            <v-spacer></v-spacer>

            <v-menu
              v-model="deleteTagMenus[tag.name]"
              :close-on-content-click="true"
              :nudge-width="200"
              offset-x
            >
              <template v-slot:activator="{ on, attrs }">
                <v-btn icon v-bind="attrs" v-on="on">
                  <v-icon>mdi-dots-vertical</v-icon>
                </v-btn>
              </template>
              <v-list>
                <v-list-item @click="deleteTag(tag)" link dense>
                  <v-list-item-icon>
                    <v-icon>mdi-delete</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title>Delete</v-list-item-title>
                    <v-list-item-subtitle
                      >Save will also remove this tag from
                      variants</v-list-item-subtitle
                    >
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-menu>
          </v-row>

          <v-row no-gutters>
            <v-text-field
              v-model="settingsForm.newTagName"
              :rules="[validationRules.newTagNameUnique]"
              label="New tag name"
              hint="New tag name must be unique"
              persistent-hint
              type="text"
              dense
            ></v-text-field>
            <v-checkbox
              v-model="settingsForm.newTagImportant"
              on-icon="mdi-tag-multiple"
              off-icon="mdi-tag-multiple-outline"
              label="Important?"
              color="red"
              class="mt-1 pl-4 pt-0"
            ></v-checkbox>
            <v-spacer></v-spacer>
            <v-btn icon :disabled="addNewTagDisabled" @click="addNewTag">
              <v-icon>mdi-plus</v-icon>
            </v-btn>
          </v-row>
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
import * as _ from 'lodash'

export default {
  name: 'AppSettings',

  mounted() {
    this.settingsForm.newBamDir = this.getSettingsBamDir
    this.settingsForm.newCouchDbUrl = this.getSettingsCouchDbUrl
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
        newBamDir: '',
        newTagName: '',
        newTagImportant: false,
        userTags: [],
      },
    }
  },

  computed: {
    ...mapState(['authState']),
    ...mapGetters([
      'getSettingsCouchDbUrl',
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

    saveAppSettings: function() {
      this.$store.dispatch('saveAppSettings', {
        newCouchDbUrl: this.settingsForm.newCouchDbUrl,
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
  },

  watch: {
    getSettingsCouchDbUrl: function(value) {
      this.settingsForm.newCouchDbUrl = value
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
        const bamMatchInitial =
          this.settingsForm.newBamDir === this.getSettingsBamDir
        const userTagsMatchInitial = _.isEqual(
          this.settingsForm.userTags,
          this.customTags
        )
        const matchInitial =
          couchDbUrlMatchInitial && bamMatchInitial && userTagsMatchInitial

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
