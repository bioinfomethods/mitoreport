<template>
  <v-simple-table dense class="variant-info">
    <template v-slot:default>
      <tbody>
        <tr>
          <td>HGVS.g:</td>
          <td>
            {{ hgvsg }}
            <button
              class="copyHGVS"
              @click="copyToClipboard(hgvsg)"
              title="Copy HGVS.g to clipboard"
            >
              <v-icon>mdi-clipboard-outline</v-icon>
            </button>
          </td>
        </tr>
        <tr>
          <td>HGVS.p:</td>
          <td>
            {{ variant.hgvsp }}
            <button
              v-if="variant.hgvsp"
              class="copyHGVS"
              @click="copyToClipboard(variant.hgvsp)"
              title="Copy HGVS.p to clipboard"
            >
              <v-icon>mdi-clipboard-outline</v-icon>
            </button>
          </td>
        </tr>
        <tr>
          <td>HGVS.c:</td>
          <td>
            {{ variant.hgvsc }}
            <button
              v-if="variant.hgvsc"
              class="copyHGVS"
              @click="copyToClipboard(variant.hgvsc)"
              title="Copy HGVS.c to clipboard"
            >
              <v-icon>mdi-clipboard-outline</v-icon>
            </button>
          </td>
        </tr>
        <tr>
          <td>Consequence:</td>
          <td>
            <span v-if="variant.consequence">
              {{ variant.consequence.displayTerm }}
            </span>
            <span v-else>No VEP consequence</span>
          </td>
        </tr>
        <tr>
          <td>MitoMap Status:</td>
          <td>{{ variant.diseaseStatus }}</td>
        </tr>
        <tr>
          <td>MitoMap Refs:</td>
          <td>
            <CuratedRefLink
              v-if="variant.curatedRef"
              :href="variant.curatedRef.url"
              :count="variant.curatedRef.count"
            ></CuratedRefLink>
          </td>
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
<style lang="scss">
div.variant-info div.v-data-table__wrapper table tr td {
  padding: 0 2px 0 0;
  &:first-child {
    width: 50px;
  }
}
.copyHGVS {
  float: right;
  &:hover i {
    color: black;
    background: lightgrey;
    border-radius: 5px;
  }
}
</style>
<script>
import { mapGetters } from 'vuex'
import CuratedRefLink from '@/components/CuratedRefLink'

export default {
  name: 'VariantInfo',

  props: {
    variantId: {
      type: String,
      required: false,
    },
  },
  components: {
    CuratedRefLink,
  },
  computed: {
    ...mapGetters(['getVariantById']),
    variant() {
      return this.getVariantById(this.variantId)
    },
    hgvsg() {
      return (
        this.variant.HGVS ||
        this.variant.id.replace(/chrM-(\d+)-(\w)-(\w+)/, 'm.$1$2>$3')
      )
    },
  },
  methods: {
    copyToClipboard(text) {
      navigator.clipboard.writeText(text)
    },
  },
}
</script>

<style lang="css" scoped>
.variant-info tr > td:first-child {
  font-weight: bold;
  width: 150px;
  padding-left: 5px;
}
</style>
