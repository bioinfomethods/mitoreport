<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title>About</v-card-title>
          <v-card-text>
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur. Excepteur sint occaecat cupidatat non proident, sunt in
            culpa qui officia deserunt mollit anim id est laborum.

            <br />
            <br />
            <span>Gnomad and other citations:</span>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-card-title>Data</v-card-title>
          <v-card-text>
            <table id="metadata">
              <tr>
                <td>Sample ID</td>
                <td>{{ sampleSettings.id }}</td>
              </tr>
              <tr>
                <td>Time generated</td>
                <td>{{ date }}</td>
              </tr>
              <tr>
                <td>Path</td>
                <td>{{ metadata.absolutePath }}</td>
              </tr>
            </table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
table#metadata {
  td {
    padding: 2px;
    border-bottom: solid 1px grey;
    &:first-child {
      border-right: solid 1px grey;
    }
  }
}
</style>

<script lang="typescript">
import { mapGetters } from 'vuex'
// import * as _ from 'lodash'

export default {
  name: 'AboutPage',

  computed: {
    ...mapGetters(['getSampleMetadata', 'getSampleSettings']),

    metadata() {
      return this.getSampleMetadata
    },

    date() {
      return new Date(this.metadata.accessed).toLocaleString();
    },

    sampleSettings() {
      return this.getSampleSettings
    }

  },
}
</script>
