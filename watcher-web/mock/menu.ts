import { MockMethod } from 'vite-plugin-mock'

/** 给接口使用 */
const menu = [
  {
    path: '/agent',
    meta: { title: 'message.menu.agent.name', icon: 'sfont system-home' },
    children: [
      {
        path: 'agent',
        meta: { title: 'message.menu.agent.index', icon: 'sfont system-home', hideClose: true }
      }
    ]
  }
]

export default [
  /** 需要展示的菜单模拟接口 */
  {
    url: `/mock/menu/list`,
    method: 'post',
    response: ({ body }) => {
      return {
        code: 200,
        data: menu
      }
    }
  }
]