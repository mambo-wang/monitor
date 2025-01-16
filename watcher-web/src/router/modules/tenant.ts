import type { Route } from "../index.type";
import Layout from "@/layout/index.vue";
import { createNameComponent } from "../createNode";
const route: Route[] = [
  {
    path: "/tenant",
    component: Layout,
    meta: { title: "", icon: "sfont system-xitongzhuangtai" },
    children: [
      {
        path: "index",
        component: createNameComponent(
          () => import("@/views/main/tenant/index.vue")
        ),
        meta: {
          title: "message.menu.tenant.name",
          icon: "sfont system-xitongzhuangtai",
          hideClose: true,
        },
      },
    ],
  },
];

export default route;