import type {Route} from "../index.type";
import Layout from "@/layout/index.vue";
import {createNameComponent} from "../createNode";

const route: Route[] = [
    {
        path: "/",
        component: Layout,
        redirect: "/init",
        children: [
            {
                path: "init",
                component: createNameComponent(() => import("@/views/main/init-config/index.vue")),
                meta: {
                    title: "message.menu.initConfig.name",
                    hideClose: true,
                },
            },
        ],
    },
];

export default route;
