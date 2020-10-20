<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span class="white--text text-h6">Mito Report for {{ getSample }}</span>
      </v-toolbar-title>
      <v-progress-circular class="ml-4" v-if="loading" indeterminate />
      <v-spacer></v-spacer>
      <v-btn @click.prevent="downloadSettings" icon large>
        <v-icon class="white--text" large>mdi-download</v-icon>
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
import AppSettings from '@/components/AppSettings'

export default {
  name: 'App',

  components: {
    AppSettings,
  },

  async created() {
    console.info(`MitoReport version=${process.env.VUE_APP_VERSION}`)
    await this.$store.dispatch('fetchData')
  },

  data: () => {
    return {
      rules: {
        required: value => !!value || 'Required.',
      },
    }
  },

  computed: {
    ...mapState(['loading', 'snackbar', 'initialFetchDataLoaded']),
    ...mapGetters(['getSample']),
  },

  methods: {
    downloadSettings: function() {
      this.$store.dispatch('downloadSettings')
    },

    closeSnackbar: function() {
      this.$store.dispatch('closeSnackbar')
    },
  },
}
</script>
