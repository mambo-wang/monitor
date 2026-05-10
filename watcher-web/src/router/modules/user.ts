import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'

const route: Route[] = [
  {
    path: '/main/user',
    component: Layout,
    redirect: '/main/user/register-list',
    meta: { title: 'message.menu.user.name' },
    children: [
      {
        path: 'register-list',
        component: createNameComponent(() => import('@/views/main/user/register-list.vue')),
        meta: { title: 'message.menu.user.register-list', icon: 'user' }
      },
      {
        path: 'register-detail',
        component: createNameComponent(() => import('@/views/main/user/register-detail.vue')),
        meta: { title: 'message.menu.user.register-detail', hideMenu: true }
      },
      {
        path: 'user-list',
        component: createNameComponent(() => import('@/views/main/user/user-list.vue')),
        meta: { title: 'message.menu.user.user-list', icon: 'user' }
      },
    ]
  }
]

export default route
