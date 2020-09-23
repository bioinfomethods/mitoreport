import App from '@/App.vue'
import router from '@/router'
import Deletions from '@/views/Deletions.vue'
import Variants from '@/views/Variants.vue'
import { createLocalVue, mount } from '@vue/test-utils'
import Vue from 'vue'
import VueRouter from 'vue-router'
import Vuetify from 'vuetify'
import store from './TestStore'

const app = document.createElement('div')
app.setAttribute('data-app', true)
document.body.append(app)

describe('App.vue', () => {
  let underTest
  let vuetify

  beforeEach(() => {
    Vue.use(Vuetify)
    const LOCAL_VUE = createLocalVue()
    LOCAL_VUE.use(VueRouter)

    vuetify = new Vuetify()
    underTest = mount(App, {
      LOCAL_VUE,
      store,
      router,
      vuetify,
    })
  })

  it('renders App and defaults to Variants page', async () => {
    expect(router.currentRoute.fullPath).toBe('/variants')
    const variantsView = underTest.findComponent(Variants)
    expect(variantsView.exists()).toBe(true)
  })

  describe('navigate to /deletions', () => {
    it('renders Deletions page', async () => {
      router.push('/deletions')
      await underTest.vm.$nextTick()

      expect(router.currentRoute.fullPath).toBe('/deletions')
      const deletionsView = underTest.findComponent(Deletions)
      expect(deletionsView.exists()).toBe(true)
    })
  })
})
