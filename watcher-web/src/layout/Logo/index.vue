<template>
  <div class="logo-container">
    <!-- <img class="logo" src="@/assets/images/logo.png" alt=""> -->
    <h1 v-if="!isCollapse" class="agent-title">{{ $t(systemTitle) }}</h1>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";
import { useStore } from "vuex";
import { systemTitle } from "@/config";
export default defineComponent({
  setup() {
    const store = useStore();
    const isCollapse = computed(() => store.state.app.isCollapse);
    return {
      isCollapse,
      systemTitle,
    };
  },
});
</script>

<style lang="scss" scoped>
.logo-container {
  height: 64px;
  padding: 3px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  // background-color: var(--system-logo-background);
  .logo {
    left: 0px;
    top: 0px;
    width: 32px;
    height: 32px;
    margin-right: 10px;
  }

  h1 {
    font: Roboto;
    font-style: normal;
    font-size: 24px;
    text-align: left;
    line-height: 26px;
    white-space: nowrap;
    color: #0546ce;
  }
}
</style>
