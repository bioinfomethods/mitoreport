<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png" />
    <VariantTable />
  </div>
</template>

<script>
// @ is an alias to /src
import VariantTable from '@/components/VariantTable.vue'

export default {
  name: 'Home',
  components: {
    VariantTable,
  },
}
</script>
