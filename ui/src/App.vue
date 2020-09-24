<template>
  <v-app mitoreport>
    <v-app-bar app color="primary">
      <v-toolbar-title>
        <span>Mito Report for {{ sample }}</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn @click.prevent="downloadSettings" icon large>
        <v-icon large>mdi-download</v-icon>
      </v-btn>
      <v-btn icon large>
        <v-icon large>mdi-cog</v-icon>
      </v-btn>
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
    }
  },

  computed: {
    ...mapState(['loading', 'snackbar']),
    ...mapGetters(['sample']),
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
  },

  beforeDestroy() {
    clearInterval(this.saveInterval)
  },
}
</script>
