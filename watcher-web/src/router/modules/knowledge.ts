import type { Route } from "../index.type";
import Layout from "@/layout/index.vue";
import { createNameComponent } from "../createNode";

const route: Route[] = [
  {
    path: "/knowledge",
    component: Layout,
    meta: { title: "", icon: "sfont system-document" },
    children: [
      {
        path: "index",
        component: createNameComponent(
          () => import("@/views/main/knowledge/KnowledgeIndex.vue")
        ),
        meta: {
          title: "知识库",
          icon: "sfont system-document",
          hideClose: true,
        },
      },
    ],
  },
];

export default route;
