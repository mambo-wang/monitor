<template>
  <!-- <a href="https://github.com/cmdparkour/vue-admin-box" target="_blank" :title="$t('message.system.github')"><i class="sfont system-github" @click="toggle"></i></a> -->
  <img class="userhead" src="@/assets/images/头像.png" alt="" />
</template>

<script lang="ts">
import { defineComponent } from "vue";
export default defineComponent({});
</script>

<style lang="scss" scoped>
a {
  &:focus {
    outline: none;
  }
}
i {
  cursor: pointer;
  font-size: 18px;
  &:focus {
    outline: none;
  }
}
.userhead {
  left: 0px;
  top: 0px;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}
</style>
