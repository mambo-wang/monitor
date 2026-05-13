/** 大屏概览路由 */
import Layout from "@/layout/index.vue";

const largeDisplay: any[] = [
  {
    path: "/large-display",
    component: Layout,
    redirect: "/large-display/index",
    meta: {
      title: "大屏概览",
      icon: "el-icon-s-data",
      roles: ["admin", "user"],
      cache: true,
    },
    children: [
      {
        path: "index",
        name: "LargeDisplay",
        component: () =>
          import(
            "@/views/large-display/LargeDisplayView.vue"
          ),
        meta: {
          title: "大屏概览",
          roles: ["admin", "user"],
          cache: true,
        },
      },
    ],
  },
];

export default largeDisplay;