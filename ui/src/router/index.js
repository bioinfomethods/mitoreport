import Deletions from '@/views/Deletions.vue'
import Variants from '@/views/Variants.vue'
import NotFound from '@/views/NotFound.vue'
import QC from '@/views/QC.vue'
import About from '@/views/About.vue'
import Vue from 'vue'
import VueRouter from 'vue-router'
import { OktaAuth } from '@okta/okta-auth-js'
import OktaVue from '@okta/okta-vue'
import LoginComponent from '@/components/Login'
import sampleConfig from '@/config'

const oktaAuth = new OktaAuth(sampleConfig.oidc)

Vue.use(VueRouter)
Vue.use(OktaVue, {
  oktaAuth,
  onAuthRequired: () => {
    router.push('/login')
  },
})

const routes = [
  {
    path: '/',
    redirect: { name: 'variants' },
  },
  {
    path: '/variants',
    name: 'variants',
    component: Variants,
    children: [
      {
        path: ':variantId',
        name: 'variantDetails',
        props: route => ({ variantId: route.params.variantId }),
      },
    ],
    props: route => ({
      variantId: route.params.variantId,
    }),
  },
  {
    path: '/login',
    component: LoginComponent,
  },
  {
    path: '/login/callback',
    component: LoginComponent,
  },
  {
    path: '/deletions',
    name: 'deletions',
    component: Deletions,
  },
  {
    path: '/qc',
    name: 'QC',
    component: QC,
  },
  {
    path: '/about',
    name: 'about',
    component: About,
  },
  {
    path: '*',
    name: '404',
    component: NotFound,
  },
]

const router = new VueRouter({
  base: process.env.BASE_URL,
  routes,
})

export default router
