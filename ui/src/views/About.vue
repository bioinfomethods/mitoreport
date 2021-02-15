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
              within the mitochondria DNA using gnomAD and other resources, and
              allows manual curation and annotation of these variants.
            </p>

            <span>gnomAD</span>
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
            <span>MITOMAP</span>
            <p>
              <a href="https://mitomap.org/MITOMAP" target="_blank">MITOMAP</a>
              reports published data on human mitochondrial DNA variation.
              MITOMAP is a Service provided by the
              <a href="https://cmem.research.chop.edu/" target="_blank"
                >Center for Mitochondrial & Epigenomic Medicine</a
              >
              at the
              <a href="https://www.chop.edu/" target="_blank"
                >Children's Hospital of Philadelphia</a
              >.
            </p>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col>
        <v-card>
          <v-card-title>Data</v-card-title>
          <v-card-text>
            <table id="metadata">
              <tr v-for="(item, key) in displayedData" :key="key">
                <td>{{ key }}</td>
                <td>{{ item }}</td>
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
import * as _ from 'lodash'

export default {
  name: 'About',
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
      const data = {
        "Sample ID": this.getSampleSettings.id,
        "Report Generated": this.reportDate,
        "Report Last Modified": this.reportDate,
        "Report Author": "",
        "gnomAD Version": "v3.1",
        "gnomAD Timestamp": new Date(this.metadata.created).toLocaleString(),
        "gnomAD Path": this.getSampleMetadata.absolutePath,
        "Mitoreport Version": this.getSampleMetadata.mitoreportVersion,
      }

// Should this stuff always be fresh? Perhaps don't pull it from localstorage mitosettings?
      if (this.getSampleMetadata.gitBranch) {
        _.merge(data, {
          "Mitoreport Git Branch": this.getSampleMetadata.gitBranch,
          "Mitoreport Git Hash": this.getSampleMetadata.gitHash,
          "Mitoreport Git Date": this.getSampleMetadata.gitDate,
        })
      }
      return data
    },
  },
}
</script>
