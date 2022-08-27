<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span class="white--text text-h6">Mito Report for {{ sampleId }}</span>
      </v-toolbar-title>
      <v-progress-circular class="ml-4" v-if="loading" indeterminate />
      <v-spacer></v-spacer>
      <v-switch
        v-if="authState.isAuthenticated"
        id="toggleSync"
        @change="onToggleSyncChange"
        v-model="syncEnabled"
        dark
        class="pt-4 pr-4"
        ><template v-slot:label
          ><span class="white--text"
            >Sync {{ syncEnabled ? 'Enabled' : 'Disabled' }}</span
          ></template
        ></v-switch
      >
      <v-btn
        v-if="!authState.isAuthenticated"
        id="signIn"
        to="/login"
        text
        class="white--text mx-1"
      >
        Sign In
      </v-btn>
      <v-btn
        v-if="authState.isAuthenticated"
        id="signOut"
        @click="signOut"
        text
        class="white--text mx-1"
      >
        Sign Out
      </v-btn>
      <AppSettings></AppSettings>
    </v-app-bar>

    <v-main>
      <v-container fluid>
        <v-row>
          <v-col class="pt-0">
            <v-tabs>
              <v-tab to="/variants">Variants</v-tab>
              <v-tab to="/deletions">Deletions</v-tab>
              <v-tab to="/qc">QC</v-tab>
              <v-tab to="/about">About</v-tab>
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
        <v-btn dark text v-bind="attrs" @click="closeSnackbar"> Close </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<style>
@import url('./assets/css/mitoreport.scss');
</style>

<script>
import { mapState } from 'vuex'
import AppSettings from '@/components/AppSettings'
import {
  syncWithRemote,
  cancelSyncWithRemote,
} from '@/services/LocalDataService'

export default {
  name: 'App',

  components: {
    AppSettings,
  },

  async created() {
    console.info(`MitoReport version=${process.env.VUE_APP_VERSION}`)
    await this.$store.dispatch('fetchData')
    await this.$store.dispatch('saveSettings')
    document.title = this.sampleId || 'MitoReport'
  },

  data: () => {
    return {
      rules: {
        required: value => !!value || 'Required.',
      },
      syncEnabled: false,
    }
  },

  computed: {
    ...mapState(['loading', 'snackbar', 'initialFetchDataLoaded', 'sampleId']),
  },

  methods: {
    onToggleSyncChange: function(value) {
      if (value) {
        syncWithRemote(this.authState)
      } else {
        cancelSyncWithRemote()
      }
    },

    closeSnackbar: function() {
      this.$store.dispatch('closeSnackbar')
    },

    signOut: function() {
      cancelSyncWithRemote()
      this.$auth.signOut()
    },
  },
}
</script>
