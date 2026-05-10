import {createApp} from "vue";
import ElementPlus from "element-plus";
import {baidu} from "./utils/system/statistics";

import "element-plus/theme-chalk/display.css"; // 引入基于断点的隐藏类
import "element-plus/dist/index.css";
import "normalize.css"; // css初始化
import "./assets/style/common.scss"; // 公共css
import "./theme/modules/chinese/index.scss";
import App from "./App.vue";
import store from "./store";
import router from "./router";
import {getAuthRoutes} from "./router/permission";
import i18n from "./locale";
import uploader from "vue-simple-uploader";
import "vue-simple-uploader/dist/style.css";
import debounce from "@/directive/debounce";
import mitt from "mitt";

if (import.meta.env.MODE !== "development") {
    // 非开发环境调用百度统计
    baidu();
}
const app = createApp(App);
app.config.globalProperties.$eventBus = mitt();
app.use(ElementPlus, {size: store.state.app.elementSize});
app.use(store);
app.use(router);

/** 权限路由处理主方法（store 初始化后再调用） */
getAuthRoutes();
app.use(i18n);
app.use(uploader);
app.directive("debounce", debounce);

// app.config.performance = true
app.mount("#app");
