<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title>About</v-card-title>
          <v-card-text>
            <h3>Mitoreport</h3>
            <p>
              Mitoreport is a tool developed by the Bioinformatics Methods team
              at the MCRI. This tool generates an automated report of variants
              within the mitochondria DNA using gnomAD and other resources, and
              allows manual curation and annotation of these variants.
            </p>

            <h3>gnomAD</h3>
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
            <h3>MITOMAP</h3>
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
            <h3>MCRI / VCGS</h3>
            <span>
              MitoReport has benefited from expertise, advice and input provided
              by many contributors at the Murdoch Children's Research Institute
              (MCRI) and the Victorian Clinical Genetics Services (VCGS). In
              particular we acknowledge the VCGS Clinical Genomics and Targeted
              Genomics Diagnostics (TGD) groups for their insight into the
              process for clinical interpretation and processes for validation
              of mitochondrial testing. We further acknowledge the VCGS Clinical
              Bioinformatics Unit and Miriam Fanjul Fernandez for the initial
              inspiration and groundwork that provided the basis for MitoReport.
              Finally, we acknowledge the Brain and Mitochondrial Research Group
              at MCRI who provided us with significant insight and advice into
              complex issues in mitochondrial variant interpretation.
            </span>
            <h3>AGHA</h3>
            <span>
              We thank the Australian Genomics Health Alliance for allowing
              access to data that to test and validate the operation of
              MitoReport, as well as the patients and families who allowed their
              genomic data to be used for research.
            </span>
            <h3>How to cite Mitoreport</h3>
            <span>
              We are actively working on a publication about MitoReport. Until
              we have made a preprint available, please cite the MitoReport
              Github page at:
              <pre>https://github.com/bioinfomethods/mitoreport</pre>
            </span>
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
        <v-card v-if="haplogrepClass">
          <v-card-title>Haplogrep Classification</v-card-title>
          <v-card-text>
            <span>
              <b>Has Multiple Base Haplogroups:</b>
              {{ haplogrepClass.hasMultipleBaseHaplogroups }}
            </span>
            <table id="haplogrepClassification">
              <tr>
                <td>Rank</td>
                <td>Base Haplogroup</td>
                <td>Haplogroup</td>
                <td>Quality</td>
              </tr>
              <tr
                v-for="(item, key) in haplogrepClass.haplogrepResults"
                :key="key"
              >
                <td>{{ item.rank }}</td>
                <td>{{ item.baseHaplogroup }}</td>
                <td>{{ item.haplogroup }}</td>
                <td>{{ item.quality }}</td>
              </tr>
            </table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
table#metadata,
table#haplogrepClassification {
  width: 100%;
  td {
    padding: 2px;
    border-bottom: solid 1px grey;
    border-right: solid 1px grey;
    &:last-child {
      border-right: none;
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

    haplogrepClass() {
      return this.getSampleSettings.haplogrepClassification
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
      if (this.getSampleMetadata.buildCommit) {
        _.merge(data, {
          "Mitoreport Git Hash": this.getSampleMetadata.buildCommit.slice(1),
          "Mitoreport Git Date": new Date(this.getSampleMetadata.buildTimestamp).toLocaleString('en-AU'),
        })
      }
      return data
    },
  },
}
</script>
