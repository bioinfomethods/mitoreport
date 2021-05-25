<template>
  <v-container class="px-0">
    <v-row align="start" justify="start" no-gutters>
      <v-col v-for="c in chartDataList" :key="c.id">
        <div class="variantChart" v-if="c.dataExists">
          <span>{{ c.title }}</span>
          <svg viewBox="0 0 450 150" :id="`${c.id}`"></svg>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import nv from 'nvd3'
import d3 from 'd3'
import '@/assets/css/nv.d3.min.css'
import { mapGetters } from 'vuex'
import { ANIMATION_DURATION, COLORS } from '@/shared/constants'
import * as _ from 'lodash'

export default {
  name: 'AgeDistributionChart',

  props: {
    variantId: {
      type: String,
      required: true,
    },
  },

  mounted: function() {
    console.debug(`VariantCharts mounted for variantId=${this.variantId}`)
    this.drawCharts()
  },

  computed: {
    ...mapGetters(['getVariantById']),

    chartDataList() {
      const variant = this.getVariantById(this.variantId)

      return [
        this.createHeteroplasmyDistChartData(variant),
        this.createAgeHomoDistData(variant),
      ]
    },
  },

  methods: {
    drawCharts() {
      this.chartDataList.forEach(chartData => {
        const charter = nv.models
          .discreteBarChart()
          .x(function(d) {
            return d.label
          })
          .y(function(d) {
            return d.height
          })
          .color([COLORS.PRIMARY])
          .duration(ANIMATION_DURATION.MEDIUM)
          .width(350)
          .height(100)
          .valueFormat(d3.format(',.0f'))
          .showXAxis(true)
          .showYAxis(false)
          .showValues(true)
          .margin({ top: 15, right: 0, bottom: 0, left: 0 })
          .staggerLabels(true)

        d3.select(`#${chartData.id}`)
          .datum([chartData])
          .call(charter)
      })
    },

    createHeteroplasmyDistChartData(variant) {
      const dataValues = variant?.gnomAD?.hl_hist || []
      const dataExists = _.some(dataValues, Boolean)
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
      const labelledValues = dataValues.map((v, idx) => {
        return {
          label: labels[idx],
          height: v,
        }
      })

      return {
        id: `${variant.id}-heteroplasmy-dist-chart`,
        title: 'Heteroplasmy Distribution',
        dataExists: dataExists,
        values: labelledValues,
      }
    },

    createAgeHomoDistData(variant) {
      const smaller = variant?.gnomAD?.age_hist_hom?.smaller || 0
      const larger = variant?.gnomAD?.age_hist_hom?.larger || 0
      const dataValues = [smaller].concat(
        variant?.gnomAD?.age_hist_hom?.bins || [],
        [larger]
      )
      const dataExists = _.some(dataValues, Boolean)
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
      const labelledValues = dataValues.map((v, idx) => {
        return {
          label: labels[idx],
          height: v,
        }
      })

      return {
        id: `${variant.id}-age-homo-dist-chart`,
        title: 'Age Distribution (Homoplasmic)',
        dataExists: dataExists,
        values: labelledValues,
      }
    },
  },
}
</script>

<style lang="css" scoped>
div.variantChart {
  border: solid 1px lightgrey;
  text-align: center;
}
div.variantChart span,
div.variantChart svg {
  width: 100%;
  padding: 1px;
}
</style>
