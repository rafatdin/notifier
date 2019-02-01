import Vue from 'vue'
import Router from 'vue-router'
import Hello from '@/components/Hello'
import Service from '@/components/Service'
import User from '@/components/User'
import Log from '@/components/Log'
import PartnerSystem from '@/components/PartnerSystem'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Logs',
      component: Log
    },
    {
      path: '/hello',
      name: 'Hello',
      component: Hello
    },
    {
      path: '/callservice',
      name: 'Service',
      component: Service
    },
    {
      path: '/user',
      name: 'User',
      component: User
    },
    {
      path: '/logs',
      name: 'Logs',
      component: Log
    },
    {
      path: '/partners',
      name: 'Partners',
      component: PartnerSystem
    }
  ]
})
