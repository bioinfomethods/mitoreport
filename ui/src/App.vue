<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span class="white--text text-h6">Mito Report for {{ sampleId }}</span>
      </v-toolbar-title>
      <v-progress-circular class="ml-4" v-if="loading" indeterminate />
      <v-spacer></v-spacer>
      <v-switch
        v-if="getSyncFeatureEnabled"
        id="toggleSync"
        v-model="localSyncEnabled"
        dark
        class="pt-4 pr-4"
        ><template v-slot:label
          ><span class="white--text"
            >{{ localSyncEnabled ? 'Disable' : 'Enable' }} Sync</span
          ></template
        ></v-switch
      >
      <AppSettings></AppSettings>
      <v-avatar class="mx-2" color="white" size="30">
        {{ auth.initials }}
      </v-avatar>
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
import AppSettings from '@/components/AppSettings'
import Cookies from 'js-cookie'
import { mapGetters, mapState } from 'vuex'

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
    }
  },

  computed: {
    localSyncEnabled: {
      get() {
        return this.syncEnabled
      },
      set(value) {
        console.debug('localSyncEnabled', value)
        Cookies.set('syncEnabled', value)
        this.$store.dispatch('toggleSync', value)

        if (window.location.protocol !== 'file:') {
          if (value && !this.$keycloak?.authenticated) {
            this.$keycloak.login()
          }
          if (!value && this.$keycloak?.authenticated) {
            this.$keycloak.logout()
          }
        }
      },
    },
    ...mapState([
      'auth',
      'syncEnabled',
      'loading',
      'snackbar',
      'initialFetchDataLoaded',
      'sampleId',
    ]),
    ...mapGetters(['getSyncFeatureEnabled']),
  },

  methods: {
    closeSnackbar: function() {
      this.$store.dispatch('closeSnackbar')
    },
  },
}
</script>
