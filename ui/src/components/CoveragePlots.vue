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

export default {
  name: 'CoveragePlots',
  props: {},
  mounted: function() {
    console.log('mounted')

    let covData = [
      {
        key: this.sample,
        values: this.deletions[this.sample].coverage,
      },
    ]

    var coverageChart = nv.models
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

    let splitReadData = [
      {
        key: this.sample,
        values: this.deletions[this.sample].splitReads,
      },
    ]

    var splitReadChart = nv.models
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

  computed: {
    sample() {
      return Object.keys(this.deletions)[0]
    },
  },

  data: () => {
    return {
      deletions: window.deletions,
      testdata: [],
    }
  },
}
</script>

<style scoped></style>
