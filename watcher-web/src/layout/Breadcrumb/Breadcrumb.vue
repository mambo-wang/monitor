<template>
  <el-breadcrumb class="app-breadcrumb hidden-sm-and-down" separator="/">
    <transition-group appear name="breadcrumb" v-if="levelList.length === 1">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <h3
          style="margin-left: 20px"
          v-if="item.redirect === 'noRedirect' || index == levelList.length - 1"
          class="no-redirect"
        >
          {{ $t(item.meta.title) }}
        </h3>
      </el-breadcrumb-item>
    </transition-group>

    <transition-group appear name="breadcrumb" v-else>
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index == levelList.length - 1"
          class="no-redirect"
          >{{ $t(item.meta.title) }}</span
        >
        <a v-else>
          {{ $t(item.meta.title) }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script lang="ts">
import { ref, defineComponent, watch, Ref } from "vue";
import { useRoute, useRouter, RouteLocationMatched } from "vue-router";

export default defineComponent({
  name: "BreadCrumb",
  setup() {
    const levelList: Ref<RouteLocationMatched[]> = ref([]);

    const route = useRoute();
    const router = useRouter();

    const getBreadcrumb = (): void => {
      let matched = route.matched.filter(
        (item) => item.meta && item.meta.title
      );
      const first = matched[0];

      levelList.value = matched.filter(
        (item) => item.meta && item.meta.title && item.meta.breadcrumb !== false
      );
    };

    getBreadcrumb();
    watch(
      () => route.path,
      () => getBreadcrumb()
    );

    const handleLink = (item: RouteLocationMatched): any => {
      const { redirect, path } = item;
      if (redirect) {
        router.push(redirect.toString());
        return;
      }
      router.push(path);
    };
    return { levelList, handleLink, route };
  },
});
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  .no-redirect {
    // color: var(--system-header-background);
    cursor: text;
  }
  a {
    // color: var(--system-header-background);
  }
}

:deep(.el-breadcrumb__inner a:hover, .el-breadcrumb__inner.is-link:hover) {
  cursor: auto;
}
</style>
