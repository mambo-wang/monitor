import type {Route} from "../index.type";
import Layout from "@/layout/index.vue";
import {createNameComponent} from "../createNode";

const route: Route[] = [
    {
        path: "/agent",
        component: Layout,
        meta: {title: "", icon: "sfont system-home"},
        children: [
            {
                path: "index",
                component: createNameComponent(
                    () => import("@/views/main/agent/index.vue")
                ),
                meta: {
                    title: "message.menu.agent.name",
                    icon: "sfont system-home",
                    hideClose: true,
                },
            },
        ],
    },
];

export default route;
