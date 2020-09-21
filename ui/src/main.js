import vuetify from '@/plugins/vuetify'
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

new Vue({
  store,
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app')
