import VariantTable from '@/components/VariantTable.vue'
import router from '@/router/index.js'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import store from './TestStore'

const app = document.createElement('div')
app.setAttribute('data-app', true)
document.body.append(app)

describe('VariantTable.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    vuetify = new Vuetify()
    underTest = mount(VariantTable, {
      LOCAL_VUE,
      store,
      router,
      vuetify,
    })
  })

  it('renders VariantTable', () => {
    const expHeaderTexts = [
      'Position',
      'Allele',
      'Type',
      'Gene',
      'Consequence',
      'VAF',
      'Depth',
      'Disease',
      'MitoMap',
      'Curated Refs',
      'HGVS.p',
      'HGVS.c',
      'HGVS',
    ]
    const wrapperText = underTest.text()
    expHeaderTexts.every(header => {
      expect(wrapperText).toContain(header)
    })
  })
})
