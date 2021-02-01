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

            <span>Gnomad and other citations:</span>
            <!-- <svg id="coveragePlot" style="width: 100%; height: 240px"></svg> -->
          </v-card-text>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-card-title>Data</v-card-title>
          <v-card-text>
            <ul style="clip: rect(0px, 1000px, 0px, 0px);">
              <li>Sample ID: {{ sampleSettings.id }}</li>
              <li>Absolute Path: {{ metadata.absolutePath }}</li>
            </ul>
            <!-- <svg id="splitReadPlot" style="width: 100%; height: 240px"></svg> -->
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
#test {
  background: red;
}
</style>

<script lang="typescript">
import { mapGetters } from 'vuex'
// import * as _ from 'lodash'

export default {
  name: 'AboutPage',

  computed: {
    ...mapGetters(['getSampleMetaData', 'getSampleSettings']),

    metadata() {
      return this.getSampleMetaData
    },

    sampleSettings() {
      return this.getSampleSettings
    }

  },
}
</script>
