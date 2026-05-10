import {createNameComponent} from "@/router/createNode";

const menuList = [
  {
    path: "/dashboard",
    meta: { title: "", icon: "sfont system-dashboard" },
    hideMenu: false,
    children: [
      {
        path: "index",
        meta: {
          title: "message.menu.dashboard.name",
          icon: "sfont system-dashboard",
          hideClose: true,
        },
      },
    ],
  },
  {
    path: "/agent",
    redirect: "/agent/index",
    meta: { title: "", icon: "sfont system-component" },
    hideMenu: false,
    children: [
      {
        path: "index",
        meta: {
          title: "message.menu.agent.name",
          icon: "sfont system-component",
          hideClose: true,
        },
      },
    ],
  },
  {
    path: "/tenant",
    meta: { title: "", icon: "sfont system-xitongzhuangtai" },
    hideMenu: false,
    children: [
      {
        path: "index",
        meta: {
          title: "message.menu.tenant.name",
          icon: "sfont system-xitongzhuangtai",
          hideClose: true,
        },
      },
    ],
  },
  {
    path: "/resource",
    meta: { title: "", icon: "sfont system-menu" },
    hideMenu: false,
    children: [
      {
        path: "index",
        meta: {
          title: "message.menu.resource.name",
          icon: "sfont system-menu",
          hideClose: true,
        },
      },
    ],
  },
  {
    path: "/net",
    meta: { title: "message.menu.net.name", icon: "sfont system-page" },
    hideMenu: false,
    children: [
      {
        path: "config",
        meta: {title: "message.menu.net.name"},
        redirect: '/net/config/list',
        children: [
          {
            path: 'list',
            component: createNameComponent(() => import('@/views/main/net/network-config/network-config-list.vue')),
            meta: {title: 'message.initConfig.networkConfig'}
          },
        ]
      },
      {
        path: "router",
        meta: {title: "message.menu.net.name"},
        redirect: '/net/router/list',
        children: [
          {
            path: 'list',
            component: createNameComponent(() => import('@/views/main/net/router-management/router-management-list.vue')),
            meta: {title: 'message.routerManagement.routerManagement'}
          },
        ]
      },
    ],
  },
  {
    path: "/main/user",
    redirect: "/main/user/register-list",
    meta: { title: "message.menu.user.name", icon: "sfont system-user" },
    hideMenu: false,
    children: [
      {
        path: "register-list",
        component: createNameComponent(() => import('@/views/main/user/register-list.vue')),
        meta: {
          title: "message.menu.user.register-list",
          icon: "sfont system-user",
          hideClose: true,
        },
      },
      {
        path: "user-list",
        component: createNameComponent(() => import('@/views/main/user/user-list.vue')),
        meta: {
          title: "message.menu.user.user-list",
          icon: "sfont system-user",
          hideClose: true,
        },
      },
    ],
  }
];

export default menuList;
