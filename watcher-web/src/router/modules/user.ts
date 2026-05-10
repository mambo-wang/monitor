import {RouteRecordRaw} from "vue-router";

const user: Array<RouteRecordRaw> = [
  {
    path: "/user",
    redirect: "/user/list",
    meta: {
      title: "message.menu.user.name",
      icon: "system-user",
    },
    children: [
      {
        path: "/user/list",
        name: "UserList",
        component: () => import("@/views/main/user/user-list.vue"),
        meta: {
          title: "message.menu.user.userList",
        },
      },
      {
        path: "/approval/list",
        name: "ApprovalList",
        component: () => import("@/views/main/user/approval-list.vue"),
        meta: {
          title: "message.menu.user.approval",
        },
      },
    ],
  },
];

export default user;
