<template>
  <span v-if="item.mitoTipScore">
    <v-tooltip top>
      <template v-slot:activator="{ on, attrs }">
        <span v-bind="attrs" v-on="on">
          <span v-if="item.mitoTipScorePercentile"
            >{{ item.mitoTipScorePercentile }}%</span
          >

          <v-icon
            v-if="item.mitoTipQuartile === 'Q4'"
            style="color: #008001; margin-right: -8px;"
            >mdi-arrow-down-thick</v-icon
          >
          <v-icon v-if="item.mitoTipQuartile === 'Q4'" style="color: #008001;"
            >mdi-arrow-down-thick</v-icon
          >
          <v-icon v-if="item.mitoTipQuartile === 'Q3'" style="color: #008001;"
            >mdi-arrow-down-thick</v-icon
          >
          <v-icon v-if="item.mitoTipQuartile === 'Q2'" style="color: #fca500;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon
            v-if="item.diseaseConfirmedPathogenic"
            style="color: #fa0001; margin-right: -8px;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon
            v-if="item.mitoTipQuartile === 'Q1'"
            style="color: #fa0001; margin-right: -8px;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon v-if="item.mitoTipQuartile === 'Q1'" style="color: #fa0001;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon
            v-if="freqAlert(item)"
            style="color: #008001; margin-left: -8px;"
            >mdi-asterisk</v-icon
          >
        </span>
      </template>
      <ul>
        <li>
          <v-icon style="color: #fa0001; margin-right: -8px;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon style="color: #fa0001; margin-right: -8px;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon style="color: #fa0001;">mdi-arrow-up-thick</v-icon>
          Confirmed pathogenic
        </li>
        <li>
          <v-icon style="color: #fa0001; margin-right: -8px;"
            >mdi-arrow-up-thick</v-icon
          >
          <v-icon style="color: #fa0001;">mdi-arrow-up-thick</v-icon>
          Likely pathogenic
        </li>
        <li>
          <v-icon style="color: #fca500;">mdi-arrow-up-thick</v-icon>
          Possibly pathogenic
        </li>
        <li>
          <v-icon style="color: #008001;">mdi-arrow-down-thick</v-icon>
          Possibly benign
        </li>
        <li>
          <v-icon style="color: #008001; margin-right: -8px;"
            >mdi-arrow-down-thick</v-icon
          >
          <v-icon style="color: #008001;">mdi-arrow-down-thick</v-icon>
          Likely benign
        </li>
        <li>
          <v-icon style="color: #008001;">mdi-asterisk</v-icon>Frequency alert
        </li>
      </ul>
    </v-tooltip>
  </span>
</template>

<script>
import * as _ from 'lodash'

export default {
  props: {
    item: {
      type: Object,
      required: true,
    },
  },
  methods: {
    // A "Frequency Alert" asterisk is added to the MitoTIP ranking if a variant is found (1) at >1% in at least one of the macro-lineages L, M, or N, or (2) at >10% in at least one of the major (single letter) haplogroups, with minimum # sequences = 10. Note that the frequency alert is for information purposes only. It does not confirm a benign status, especially if a variant is found outside of the high-frequency haplogroup.
    // https://mitomap.org/foswiki/bin/view/MITOMAP/MitoTipInfo#A_42New_Additional_Features_42
    freqAlert(item) {
      if (this.mitoTipCount && this.mitoTipCount >= 10) {
        if (
          _.includes('LMN', this.getFirstHaplogroup) &&
          item.mitoTipFreq >= 1
        ) {
          return true
        } else if (
          this.getFirstHaplogroup.length === 1 &&
          item.mitoTipFreq >= 10
        ) {
          return true
        }
      }
      return false
    },
  },
}
</script>
