<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title>About</v-card-title>
          <v-card-text>
            <span>Mitoreport</span>
            <p>
              Mitoreport is a tool developed by the Bioinformatics Methods team
              at the MCRI. This tool generates an automated report of variants
              within the mitochondria DNA using Gnomad and other resources, and
              allows manual curation and annotation of these variants.
            </p>

            <span>Gnomad</span>
            <p>
              The
              <a href="https://gnomad.broadinstitute.org/about" target="_blank"
                >Genome Aggregation Database</a
              >
              (gnomAD) is a resource developed by an international coalition of
              investigators, with the goal of aggregating and harmonizing both
              exome and genome sequencing data from a wide variety of
              large-scale sequencing projects, and making summary data available
              for the wider scientific community.
            </p>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-card-title>Data</v-card-title>
          <v-card-text>
            <table id="metadata">
              <AboutPage
                inline-template
                v-for="(item, key) in displayedData"
                :prop="item"
                :key="key"
              >
                <tr>
                  <td>{{ key }}</td>
                  <td>{{ item }}</td>
                </tr>
              </AboutPage>
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
    ...mapGetters([
      'getSampleMetadata',
      'getSampleSettings',
    ]),

    metadata() {
      return this.getSampleMetadata
    },

    reportDate() {
      return new Date(this.metadata.accessed).toLocaleString();
    },

    sampleSettings() {
      return this.getSampleSettings
    },

    displayedData() {
      return {
        "Sample ID": this.getSampleSettings.id,
        "Report Generated": this.reportDate,
        "Report Last Modified": this.reportDate,
        "Report Author": "",
        "Gnomad Version": "v3.1",
        "Gnomad Timestamp": new Date(this.metadata.created).toLocaleString(),
        "Gnomad Path": this.getSampleMetadata.absolutePath,
        "Mitoreport Version": this.getSampleMetadata.mitoreportVersion,
        "Mitoreport Git Branch": this.getSampleMetadata.gitBranch,
        "Mitoreport Git Hash": this.getSampleMetadata.gitHash,
        "Mitoreport Git Date": this.getSampleMetadata.gitDate,
      }
    },

  },
}
</script>
