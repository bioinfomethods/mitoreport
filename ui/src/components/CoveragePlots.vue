<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title>Coverage Plot</v-card-title>
          <v-card-text>
            <svg id="coveragePlot" style="width: 100%; height: 240px"></svg>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title>Split Read Plot</v-card-title>
          <v-card-text>
            <svg id="splitReadPlot" style="width: 100%; height: 240px"></svg>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import nv from 'nvd3'
import d3 from 'd3'
import '@/assets/css/nv.d3.min.css'
import { mapGetters, mapState } from 'vuex'

export default {
  name: 'CoveragePlots',
  props: {},

  mounted: function() {
    if (this.sample && this.deletions[this.sample]) {
      this.plotCoverage()
      this.plotSplitReads()
    }
  },

  computed: {
    ...mapGetters(['sample']),
    ...mapState(['deletions']),
  },

  methods: {
    plotCoverage() {
      const covData = [
        {
          key: this.sample,
          values: this.deletions[this.sample].coverage,
        },
      ]
      const coverageChart = nv.models
        .stackedAreaChart()
        .margin({ right: 100 })
        .x(function(d) {
          return d.pos
        })
        .y(function(d) {
          return d.cov
        })
      d3.select('#coveragePlot')
        .datum(covData)
        .call(coverageChart)
    },

    plotSplitReads() {
      const splitReadData = [
        {
          key: this.sample,
          values: this.deletions[this.sample].splitReads,
        },
      ]

      const splitReadChart = nv.models
        .multiBarChart()
        .margin({ right: 100 })
        .x(function(d) {
          return d.pos
        })
        .y(function(d) {
          return d.count
        })

      d3.select('#splitReadPlot')
        .datum(splitReadData)
        .call(splitReadChart)
    },
  },

  watch: {
    sample: function() {
      this.plotCoverage()
      this.plotSplitReads()
    },
  },
}
</script>

<style scoped></style>
