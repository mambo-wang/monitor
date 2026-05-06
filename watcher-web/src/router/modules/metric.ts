import type {Route} from "../index.type";
import Layout from "@/layout/index.vue";
import {createNameComponent} from "../createNode";

const route: Route[] = [
    {
        path: "/metric",
        component: Layout,
        meta: {title: "", icon: "sfont  system-menu"},
        children: [
            {
                path: "detail",
                component: createNameComponent(
                    () => import("@/views/main/metric/metric-detail.vue")
                ),
                meta: {
                    title: "message.metric.metricDetail",
                    icon: "sfont  system-menu",
                    hideClose: true,
                },
            },
        ],
    },
];

export default route;
