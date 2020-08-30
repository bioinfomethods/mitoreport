<template>
  <v-app>
    <v-main>
     <v-tabs v-model="tab" background-color="primary" dark>
           <v-tab>Variants</v-tab>
           <v-tab>Deletions</v-tab>
           <v-spacer/>    
           <div class='pt-3 pr-2'>Mito Report for {{sample}}</div>

      </v-tabs>

      <v-tabs-items v-model='tab'>
        <v-tab-item>
          <VariantTable/>
        </v-tab-item>

        <v-tab-item>
          <CoveragePlots/>
        </v-tab-item>          

      </v-tabs-items>

    </v-main>
  </v-app>
</template>

<script>
import VariantTable from './components/VariantTable';
import CoveragePlots from './components/CoveragePlots';
export default {
  name: 'App',

  components: {
    VariantTable,
    CoveragePlots
  },

  computed: {
    sample() {
        return Object.keys(this.deletions)[0]
    }
  },

  data: () => {return{
    deletions: window.deletions,
    tab: null,
  }},
};
</script>
