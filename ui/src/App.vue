<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span class="white--text text-h6">Mito Report for {{ state.sampleId }}</span>
      </v-toolbar-title>
      <v-progress-circular class="ml-4" v-if="loading" indeterminate />
      <v-spacer></v-spacer>
      <v-switch
        v-if="syncFeature"
        id="toggleSync"
        @change="onToggleSyncChange"
        v-model="syncEnabled"
        dark
        class="pt-4 pr-4"
        ><template v-slot:label
          ><span class="white--text"
            >{{ syncEnabled ? 'Disable' : 'Enable' }} Sync</span
          ></template
        ></v-switch
      >
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
import { state } from './store'
import Keycloak from 'keycloak-js'
        
let keycloak = null

window['check-sso'] = function() {
    console.log('check-sso invoked')
}

export default {
  name: 'App',

  components: {
    AppSettings,
  },

  async created() {
    console.info(`MitoReport version=${process.env.VUE_APP_VERSION}`)
    console.log('State is: ', state)
    await this.$store.dispatch('fetchData')
    await this.$store.dispatch('saveSettings')
    
    await this.authenticate()
    
    let number_of_scripts_loaded = await loadScripts()
    
    console.log("Successfully loaded " + number_of_scripts_loaded + " scripts")

    await this.$store.dispatch('fetchData')

    this.$store.dispatch('createTagRepository')
    
    document.title = this.sampleId || 'MitoReport'
  },

  data: () => {
    return {
      rules: {
        required: value => !!value || 'Required.',
      },
      syncEnabled: false,
      
      state : state
    }
  },

  computed: {
    ...mapState([
      'loading',
      'snackbar',
      'initialFetchDataLoaded',
      'sampleId',
      'syncFeature',
      'settingsAuthUrl'
    ]),
  },

  methods: {
    
    getBasePath() {
        const { protocol, host, pathname } = window.location;

        // Remove the last segment from pathname
        const basePath = pathname.endsWith('/') 
            ? pathname.slice(0, pathname.lastIndexOf('/'))  // Remove trailing /
            : pathname.slice(0, pathname.lastIndexOf('/') + 1); // Keep the last /

        return `${protocol}//${host}${basePath}`;
    },
    
    
    setAuthCookie(token, days) {
        const date = new Date();
        date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
        
        const secure = (window.location.protocol == 'https' ? "Secure;" : "")
        document.cookie = `mitoreport_auth_token=${token}; Path=/; ${secure} Expires=${date.toUTCString()}; SameSite=Strict`;
    },
    
    async authenticate() {
        try {
            
            const authUrl = this.state.settings?.sample?.authUrl
            console.info(`Creating new keycloak instance at ${authUrl}`)
            if(!authUrl) {
                console.info("No auth URL configured, not attempting OIDC authentication")
                return
            }
            
            keycloak = new Keycloak({
              url: authUrl,
              realm: 'giphub',
              clientId: 'mitoreport',
            })

            window.addEventListener("message", (event) => {
                if(event.data?.source && event.data.source.includes('vue'))
                    return
                console.log("Message received from iframe:", event.data);
            });
            
            console.log("Checking authentication to set cookie")

            window.Keycloak = Keycloak;
            
            console.log("My base path is: ", this.getBasePath())

            const authenticated =
                await keycloak.init({
                    onLoad: 'check-sso',
                    checkLoginIframe: false ,
                    //silentCheckSsoRedirectUri: 'http://localhost:8081/checksso.html'
                    silentCheckSsoRedirectUri: this.getBasePath() + '/checksso.html',
                });

            window.keycloak = keycloak

            console.log(`User is ${authenticated ? 'authenticated' : 'not authenticated'}`, authenticated);
            
            if(!authenticated) {
                console.log('User is not authenticated: attempting authentication')
                // keycloak.login({redirectUri: 'http://localhost:8081/#/authenticated'})

                keycloak.login({redirectUri: window.location.href})
            }
            else {
                console.log(`=================================================\nUser Authenticated as ${keycloak.idTokenParsed.email}\n=================================================`)

                // Set the token as a cookie so that it can be verified by the server to protect
                // the assets served back from here (eg: nginx)
                console.log('Setting cookie with keycloak token')

                this.setAuthCookie(keycloak.token)
                
                console.log("Reloading scripts after auth success")

            }

        } catch (error) {
            console.error('Failed to initialize adapter:', error);
        }
    },
    
    
    onToggleSyncChange: function(value) {
      if (value) {
        syncWithRemote()
      } else {
        cancelSyncWithRemote()
      }
    },

    closeSnackbar: function() {
      this.$store.dispatch('closeSnackbar')
    },
  },
}
</script>
