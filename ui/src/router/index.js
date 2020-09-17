import Vue from 'vue'
import VueRouter from 'vue-router'
import Variants from '@/views/Variants.vue'
import Deletions from '@/views/Deletions.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: { name: 'variants' },
  },
  {
    path: '/variants',
    name: 'variants',
    component: Variants,
  },
  {
    path: '/deletions',
    name: 'deletions',
    component: Deletions,
  },
]

const router = new VueRouter({
  base: process.env.BASE_URL,
  routes,
})

export default router
