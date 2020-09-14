import App from './App.vue'
import router from './router'
import store from './store'
import Vue from 'vue'
import vuetify from '@/plugins/vuetify'

new Vue({
  store,
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app')
