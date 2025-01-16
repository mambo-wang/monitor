<template>
  <header>
    <div class="left-box">
      <img
        v-show="levelList.length !== 1"
        class="return-style"
        src="@/assets/images/返回.png"
        alt=""
        @click="goBack"
        style="cursor: pointer"
      /><span
        v-show="levelList.length !== 1"
        @click="goBack"
        style="cursor: pointer"
      >
        {{ $t("message.common.return") }}</span
      >
      <el-divider v-show="levelList.length !== 1" direction="vertical" />
      <Breadcrumb />
    </div>
    <div class="right-box"></div>
  </header>
</template>

<script lang="ts" setup>
import {
  defineComponent,
  computed,
  reactive,
  ref,
  onMounted,
  Ref,
  watch,
} from "vue";
import { useStore } from "vuex";
import { useRouter, useRoute, RouteLocationMatched } from "vue-router";
import Breadcrumb from "./Breadcrumb.vue";

const store = useStore();
const router = useRouter();
const route = useRoute();

const levelList: Ref<RouteLocationMatched[]> = ref([]);
const getBreadcrumb = (): void => {
  let matched = route.matched.filter((item) => item.meta && item.meta.title);
  const first = matched[0];
  levelList.value = matched.filter(
    (item) => item.meta && item.meta.title && item.meta.breadcrumb !== false
  );
};
watch(
  () => route.path,
  () => getBreadcrumb()
);
getBreadcrumb();

const isCollapse = computed(() => store.state.app.isCollapse);
// isCollapse change to hide/show the sidebar
const opendStateChange = () => {
  store.commit("app/isCollapseChange", !isCollapse.value);
};

const goBack = () => {
  window.history.back();
};
</script>

<style lang="scss" scoped>
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
  background-color: #ffffff;
  padding-right: 22px;
}
.left-box {
  height: 100%;
  display: flex;
  align-items: center;
  font-size: 14px;
  // color: var(--system-header-background);
  font-weight: 700;
  .return-style {
    padding: 0 10px 0 40px;
  }
  .menu-icon {
    width: 60px;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 25px;
    font-weight: 100;
    cursor: pointer;
    margin-right: 10px;
    &:hover {
      background-color: #ffffff;
    }
    i {
      color: #141e2c;
    }
  }
}
// .right-box {
//   display: flex;
//   justify-content: center;
//   align-items: center;
//   .function-list{
//     display: flex;
//     .function-list-item {
//       width: 32px;
//       height: 32px;
//       display: flex;
//       justify-content: center;
//       align-items: center;
//       :deep(i) {
//         color: var(--system-header-text-color);
//       }
//     }
//   }
//   .user-info {
//     margin-left: 20px;
//     .el-dropdown-link {
//       color: var(--system-header-breadcrumb-text-color);
//     }
//   }
// }
// .head-fold {
//   font-size: 20px;
// }
</style>
