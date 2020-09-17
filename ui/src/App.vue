<template>
  <v-app mitoreport>
    <v-main>
      <v-app-bar dense color="primary">
        <v-toolbar-title>Mito Report for {{ sample }}</v-toolbar-title>
      </v-app-bar>
      <v-container fluid>
        <v-row align="center" justify="center">
          <v-col class="text-center pt-0">
            <v-tabs>
              <v-tab to="/variants">Variants</v-tab>
              <v-tab to="/deletions">Deletions</v-tab>
              <v-spacer />
            </v-tabs>
          </v-col>
        </v-row>

        <router-view> </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'App',

  created() {
    this.$store.dispatch('fetchData')
  },

  computed: {
    ...mapGetters(['sample']),
  },
}
</script>
