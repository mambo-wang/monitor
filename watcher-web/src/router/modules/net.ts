import type {Route} from "../index.type";
import Layout from "@/layout/index.vue";
import {createNameComponent} from "../createNode";

const route: Route[] = [
    {
        path: "/net",
        component: Layout,
        meta: {title: "message.menu.net.name", icon: "sfont system-home"},
        children: [
            {
                path: "config",
                component: createNameComponent(() => import("@/views/main/net/network-config/index.vue")),
                meta: {title: "message.menu.net.name"},
                redirect: '/net/config/list',
                children: [
                    {
                        path: 'list',
                        component: createNameComponent(() => import('@/views/main/net/network-config/network-config-list.vue')),
                        meta: {title: 'message.menu.net.name'}
                    },
                    {
                        path: 'edit',
                        component: createNameComponent(() => import('@/views/main/net/network-config/components/net-config.vue')),
                        meta: { title: 'message.initConfig.editNetwork', hideSelf: true},
                        hideMenu:true
                    },

                ]
            },
            {
                path: "router",
                component: createNameComponent(() => import("@/views/main/net/router-management/index.vue")),
                meta: {title: "message.menu.net.name"},
                redirect: '/net/router/list',
                children: [
                    {
                        path: 'list',
                        component: createNameComponent(() => import('@/views/main/net/router-management/router-management-list.vue')),
                        meta: {title: 'message.menu.net.name'}
                    },
                ]
            },
           
        ],
    },
];

export default route;
