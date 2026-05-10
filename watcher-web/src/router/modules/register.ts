import {RouteRecordRaw} from "vue-router";

const register: Array<RouteRecordRaw> = [
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/system/register.vue"),
    meta: {
      title: "注册",
    },
  },
];

export default register;
