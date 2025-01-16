<template>
  <el-dialog width="500px" :close-on-click-modal="false" class="new-ip-container" :show-close="false">
    <template #title>
      <div class="new-ip-title">{{ $t("message.initConfig.newIpTip") }}</div>
    </template>
    <p style="text-align: justify;padding: 0 10px;font-size: 16px;">{{ $t("message.initConfig.newIpContent") }}</p>
    <template #footer>
      <div>
        <el-button
            type="primary"
            @click="confirm"
            style="width: 80px; height: 35px"
        >{{ $t("message.tenant.confirm") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>

const props = defineProps({
  url: {
    type: String,
    required: true,
    default: ''
  }
});
console.log(props.url);

const confirm = () => {
  window.open(props.url);
};
</script>

<style lang="scss" scoped>
.new-ip-title {
  font-size: 16px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #d8dce5;
}
</style>
