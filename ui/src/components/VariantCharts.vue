<template>
  <v-container>
    <v-row align="start" justify="start" no-gutters>
      <v-col cols="6" v-if="this.heteroDistDataExists">
        <span>{{ heteroDistData.title }}</span>
        <svg
          :id="`heteroplasmy-dist-${variantId}`"
          :key="`heteroplasmy-dist-${variantId}`"
        ></svg>
      </v-col>
      <v-col cols="6" v-if="this.ageDistDataExists">
        <span>{{ ageHomoDistData.title }}</span>
        <svg
          :id="`age-homo-dist-${variantId}`"
          :key="`age-homo-dist-${variantId}`"
        ></svg>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import nv from 'nvd3'
import d3 from 'd3'
import '@/assets/css/nv.d3.min.css'
import { mapGetters } from 'vuex'
import { ANIMATION_DURATION } from '@/shared/constants'
import * as _ from 'lodash'

export default {
  name: 'AgeDistributionChart',

  props: {
    variantId: {
      type: String,
      required: false,
    },
    dataList: {
      type: Array,
      required: false,
    },
  },

  mounted: function() {
    console.log(`AgeChart mounted variantId=${this.variantId}`)
    this.drawChart()
  },

  computed: {
    ...mapGetters(['getVariantById']),

    heteroDistDataExists() {
      const variant = this.getVariantById(this.variantId)

      return _.some(variant?.gnomAD?.hl_hist || [], Boolean)
    },

    heteroDistData() {
      const variant = this.getVariantById(this.variantId)
      const heterDistValues = variant?.gnomAD?.hl_hist || []
      const labels = [
        '0.0-0.1',
        '0.1-0.2',
        '0.2-0.3',
        '0.3-0.4',
        '0.4-0.5',
        '0.5-0.6',
        '0.6-0.7',
        '0.7-0.8',
        '0.8-0.9',
        '0.9-1.0',
      ]
      const labelledValues = heterDistValues.map((v, idx) => {
        return {
          label: labels[idx],
          height: v,
        }
      })

      return {
        title: 'Heteroplasmy Distribution',
        values: labelledValues,
      }
    },

    ageDistDataExists() {
      const variant = this.getVariantById(this.variantId)
      const smaller = variant?.gnomAD?.age_hist_hom?.smaller || 0
      const larger = variant?.gnomAD?.age_hist_hom?.larger || 0
      const values = [smaller].concat(
        variant?.gnomAD?.age_hist_hom?.bins || [],
        [larger]
      )

      return _.some(values || [], Boolean)
    },

    ageHomoDistData() {
      const variant = this.getVariantById(this.variantId)
      const smaller = variant?.gnomAD?.age_hist_hom?.smaller || 0
      const larger = variant?.gnomAD?.age_hist_hom?.larger || 0
      const values = [smaller].concat(
        variant?.gnomAD?.age_hist_hom?.bins || [],
        [larger]
      )

      const labels = [
        '< 30',
        '30-35',
        '35-40',
        '40-45',
        '45-50',
        '50-55',
        '55-60',
        '60-65',
        '65-70',
        '70-75',
        '75-80',
        '> 80',
      ]
      const labelledValues = values.map((v, idx) => {
        return {
          label: labels[idx],
          height: v,
        }
      })

      return {
        title: 'Age Distribution (Homoplasmic)',
        values: labelledValues,
      }
    },

    // dataList() {
    //   const variant = this.getVariantById(this.variantId)
    //   const heterDistValues = variant?.gnomAD?.hl_hist || []
    //   const ageHomoDistValues = variant?.gnomAD?.age_hist_hom || []

    //   return []
    // },
  },

  methods: {
    drawChart() {
      console.debug(`drawing charts`)

      const heteroplasmyDistBar = nv.models
        .discreteBarChart()
        .x(function(d) {
          return d.label
        })
        .y(function(d) {
          return d.height
        })
        .color(['#3974CC'])
        .duration(ANIMATION_DURATION.MEDIUM)
        .width(450)
        .height(100)
        .valueFormat(d3.format(',.0f'))
        .showXAxis(true)
        .showYAxis(false)
        .showValues(true)
        .margin({ top: 15, right: 0, bottom: 0, left: 0 })
        .staggerLabels(true)

      const ageHomoDistBar = nv.models
        .discreteBarChart()
        .x(function(d) {
          return d.label
        })
        .y(function(d) {
          return d.height
        })
        .color(['#3974CC'])
        .duration(ANIMATION_DURATION.MEDIUM)
        .width(450)
        .height(100)
        .valueFormat(d3.format(',.0f'))
        .showXAxis(true)
        .showYAxis(false)
        .showValues(true)
        .margin({ top: 15, right: 0, bottom: 0, left: 0 })
        .staggerLabels(true)

      d3.select(`#heteroplasmy-dist-${this.variantId}`)
        .datum([this.heteroDistData])
        .call(heteroplasmyDistBar)

      d3.select(`#age-homo-dist-${this.variantId}`)
        .datum([this.ageHomoDistData])
        .call(ageHomoDistBar)
    },
  },
}
</script>

<style lang="css" scoped></style>
