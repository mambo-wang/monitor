import type { Route } from '../index.type'
import Layout from '@/layout/index.vue'
import { createNameComponent } from '../createNode'

const route: Route[] = [
  {
    path: '/tool-share',
    component: Layout,
    redirect: '/tool-share/index',
    meta: { title: 'message.menu.toolShare.name', icon: 'el-icon-share' },
    hideMenu: false,
    children: [
      {
        path: 'index',
        component: createNameComponent(() => import('@/views/main/tool-share/index.vue')),
        meta: { title: 'message.menu.toolShare.name', icon: 'el-icon-share', hideClose: true }
      }
    ]
  }
]

export default route
