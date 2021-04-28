<template>
  <v-simple-table dense class="variant-info">
    <template v-slot:default>
      <tbody>
        <tr>
          <td>HGVS.g:</td>
          <td>
            {{
              variant.HGVS ||
                variant.id.replace(/chrM-(\d+)-(\w)-(\w+)/, 'm.$1$2>$3')
            }}
          </td>
        </tr>
        <tr>
          <td>HGVS.p:</td>
          <td>{{ variant.hgvsp }}</td>
        </tr>
        <tr>
          <td>HGVS.c:</td>
          <td>{{ variant.hgvsc }}</td>
        </tr>
        <tr>
          <td>Disease:</td>
          <td>{{ variant.Disease }}</td>
        </tr>
        <tr>
          <td>MitoMap Status:</td>
          <td>{{ variant.Status_MitoMap }}</td>
        </tr>
        <tr>
          <td>Haplotype Defining:</td>
          <td>
            {{
              variant.gnomAD
                ? variant.gnomAD.hap_defining_variant
                  ? 'Haplotype Defining'
                  : 'Not Haplotype Defining'
                : 'No gnomAD data'
            }}
          </td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'VariantInfo',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },

  computed: {
    ...mapGetters(['getVariantById']),
    variant() {
      return this.getVariantById(this.variantId)
    },
  },
}
</script>

<style lang="css" scoped>
.variant-info tr > td:first-child {
  font-weight: bold;
  width: 150px;
}
</style>
