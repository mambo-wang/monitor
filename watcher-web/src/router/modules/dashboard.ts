import type { Route } from "../index.type";
import Layout from "@/layout/index.vue";
import { createNameComponent } from "../createNode";

const route: Route[] = [
  {
    path: "/dashboard",
    component: Layout,
    meta: { title: "", icon: "sfont system-dashboard" },
    children: [
      {
        path: "index",
        component: createNameComponent(
          () => import("@/views/main/dashboard/index.vue")
        ),
        meta: {
          title: "message.menu.dashboard.name",
          icon: "sfont system-dashboard",
          hideClose: true,
        },
      },
    ],
  },
];

export default route;
