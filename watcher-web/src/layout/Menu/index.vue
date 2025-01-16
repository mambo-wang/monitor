<template>
  <el-scrollbar  v-loading="initStatus" element-loading-spinner=".">
    <el-menu
      class="layout-menu system-scrollbar"
      background-color="var(--system-menu-background)"
      text-color="var(--system-menu-text-color)"
      active-text-color="var(--system-primary-color)"
      :default-active="activeMenu"
      :class="isCollapse ? 'collapse' : ''"
      :collapse="isCollapse"
      :collapse-transition="false"
      :unique-opened="expandOneMenu"
    >
      <menu-item v-for="(menu, key) in menuList" :key="key" :menu="menu"/>
    </el-menu>
  </el-scrollbar>
</template>

<script lang="ts">
import {defineComponent, computed, onMounted, getCurrentInstance, ref} from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";
import MenuItem from "./MenuItem.vue";
import menuList from "./menu";
export default defineComponent({
  components: {
    MenuItem,
  },
  setup() {
    const instance = getCurrentInstance();
    const eventBus = instance.appContext.config.globalProperties.$eventBus;
    const initStatus = ref<boolean>(false);
    const store = useStore();
    const isCollapse = computed(() => store.state.app.isCollapse);
    const expandOneMenu = computed(() => store.state.app.expandOneMenu);
    let allRoutes = useRouter().options.routes;
    const route = useRoute();
    const activeMenu = computed(() => {
      const { meta, path } = route;
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    });
    onMounted(() => {
      eventBus.on("initConfig", (status: boolean) => {
        initStatus.value = status;
      })
    });
    return {
      isCollapse,
      expandOneMenu,
      allRoutes,
      activeMenu,
      menuList,
      initStatus
    };
  },
});
</script>

<style lang="scss" scoped>
.el-scrollbar {
  background-color: var(--system-menu-background);
}
.layout-menu {
  width: 100%;
  border: none;
  &.collapse {
    margin-left: 0px;
  }
  :deep() {
    .el-menu-item,
    .el-sub-menu {
      background-color: var(--system-menu-background) !important;
    }
    .el-menu-item i,
    .el-menu-item-group__title,
    .el-sub-menu__title i {
      color: var(--system-menu-text-color);
    }
    .el-menu-item,
    .el-sub-menu__title {
      &.is-active {
        background-color: rgba(43, 133, 251, 0.15) !important;
        color: #262626 !important;
        i {
          color: #262626 !important;
        }
        &:hover {
          background-color: rgba(43, 133, 251, 0.15) !important;
          color: #262626 !important;
        }
      }
      &:hover {
        background-color: rgba(43, 133, 251, 0.15) !important;
      }
    }
    .el-sub-menu {
      &.is-active {
        > .el-sub-menu__title,
        > .el-sub-menu__title i {
          color: #262626 !important;
        }
      }
      .el-menu-item {
        background-color: var(--system-menu-children-background) !important;
        &.is-active {
          background-color: rgba(43, 133, 251, 0.15) !important;
          color: #262626 !important;
          &:hover {
            background-color: rgba(43, 133, 251, 0.15) !important;
            color: #262626 !important;
          }
        }
        &:hover {
          background-color: rgba(43, 133, 251, 0.15) !important;
        }
      }
      .el-sub-menu {
        .el-sub-menu__title {
          background-color: var(--system-menu-children-background) !important;
          &:hover {
            background-color: var(--system-menu-hover-background) !important;
          }
        }
      }
    }
  }
}
</style>
