import vuetify from '@/plugins/vuetify'
import keycloak from '@/shared/keycloak'
import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

Vue.config.productionTip = false

keycloak.init({ onLoad: 'login-required' }).then(() => {
  new Vue({
    store,
    router,
    vuetify,
    render: h => h(App),
  }).$mount('#app')

  Vue.prototype.$keycloak = keycloak
})

keycloak.onReady = () => {
  store.dispatch('keycloakOnReady', keycloak)
}

keycloak.onAuthSuccess = () => {
  store.dispatch('keycloakOnAuthSuccess', keycloak)
}

keycloak.onAuthError = () => {
  store.dispatch('keycloakOnAuthError', keycloak)
}

keycloak.onAuthRefreshSuccess = () => {
  store.dispatch('keycloakOnAuthRefreshSuccess', keycloak)
}

keycloak.onAuthRefreshError = () => {
  store.dispatch('keycloakOnAuthRefreshError', keycloak)
}

keycloak.onAuthLogout = () => {
  store.dispatch('keycloakOnAuthLogout', keycloak)
}

keycloak.onTokenExpired = () => {
  store.dispatch('keycloakOnTokenExpired', keycloak)
}

keycloak.onActionUpdate = status => {
  store.dispatch('keycloakOnActionUpdate', keycloak, status)
}
