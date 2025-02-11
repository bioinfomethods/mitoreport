import vuetify from '@/plugins/vuetify'
import Vue from 'vue'
import App from './App.vue'
import router from './router/router.js'
import store from './store'

new Vue({
  store: store.store,
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app')
