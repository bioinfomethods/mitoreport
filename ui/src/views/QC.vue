<template>
  <v-container>
    <v-row>
      <v-col md="3">
        <v-card v-if="hasCoverageStats">
          <v-card-title>Coverage Stats</v-card-title>
          <v-card-text>
            <v-simple-table dense class="qc-info">
              <template v-slot:default>
                <tbody>
                  <tr>
                    <td>Mean depth coverage</td>
                    <td>
                      {{ sampleMean | precisionTo(5) }}
                    </td>
                  </tr>
                  <tr>
                    <td>Median depth coverage</td>
                    <td>{{ sampleMedian | precisionTo(5) }}</td>
                  </tr>
                </tbody>
              </template>
            </v-simple-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped></style>

<script lang="typescript">
import { mapGetters, mapState } from 'vuex'
import * as vueFilters from '@/shared/vueFilters'
// import * as _ from 'lodash'

export default {
  name: 'QC',

  computed: {
    ...mapState(['sampleId']),

    ...mapGetters([
      'getSampleQc',
    ]),

    hasCoverageStats() {
        return this.coverageStats
    },

    coverageStats() {
      return this.getSampleQc.coverageStats
    },

    sampleMean() {
        return this.coverageStats.means[this.sampleId]
    },

    sampleMedian() {
        return this.coverageStats.medians[this.sampleId]
    }
  },

  filters: {
    precisionTo: vueFilters.precisionTo,
  },
}
</script>
