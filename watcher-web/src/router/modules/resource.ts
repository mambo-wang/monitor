import type {Route} from "../index.type";
import Layout from "@/layout/index.vue";
import {createNameComponent} from "../createNode";

const route: Route[] = [
    {
        path: "/resource",
        component: Layout,
        meta: {title: "", icon: "sfont  system-menu"},
        children: [
            {
                path: "index",
                name: "resource-index",
                component: createNameComponent(
                    () => import("@/views/main/resource/index.vue")
                ),
                meta: {
                    title: "message.menu.resource.name",
                    icon: "sfont  system-menu",
                    hideClose: true,
                },
            },
            {
                path: "add",
                name: "resource-add",
                component: createNameComponent(
                    () => import("@/views/main/resource/resource-add.vue")
                ),
                meta: {
                    title: "message.resource.addResource",
                    icon: "sfont  system-menu",
                    hideClose: true,
                },
            },
        ],
    },
];

export default route;
