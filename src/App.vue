<template>
  <v-app>
    <v-app-bar
      app
      color="primary"
      dark
    >
    Mito Report for {{sample}}

    </v-app-bar>

    <v-main>
      <VariantTable/>
    </v-main>
  </v-app>
</template>

<script>
import VariantTable from './components/VariantTable';

export default {
  name: 'App',

  components: {
    VariantTable,
  },

  computed: {
    sample() {
        return Object.keys(this.deletions)[0]
    }
  },

  data: () => {return{
    deletions: window.deletions
  }},
};
</script>
