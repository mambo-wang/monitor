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
    path: "/large-display",
    redirect: "/large-display/index",
    meta: { title: "", icon: "sfont system-dashboard" },
    hideMenu: false,
    children: [
      {
        path: "index",
        component: createNameComponent(() => import('@/views/large-display/LargeDisplayView.vue')),
        meta: {
          title: "大屏概览",
          icon: "sfont system-dashboard",
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
    path: "/knowledge",
    redirect: "/knowledge/index",
    meta: { title: "message.menu.knowledge.name", icon: "el-icon-document" },
    hideMenu: false,
    children: [
      {
        path: "index",
        component: createNameComponent(() => import('@/views/main/knowledge/index.vue')),
        meta: {
          title: "message.menu.knowledge.name",
          icon: "el-icon-document",
          hideClose: true,
        },
      },
    ],
  },
  {
    path: "/tool-share",
    redirect: "/tool-share/index",
    meta: { title: "message.menu.toolShare.name", icon: "el-icon-share" },
    hideMenu: false,
    children: [
      {
        path: "index",
        component: createNameComponent(() => import('@/views/main/tool-share/index.vue')),
        meta: {
          title: "message.menu.toolShare.name",
          icon: "el-icon-share",
          hideClose: true,
        },
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