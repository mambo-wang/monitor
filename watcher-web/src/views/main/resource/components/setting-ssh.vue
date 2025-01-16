<template>
  <el-dialog width="500px" :close-on-click-modal="false" class="setting-role-container">
    <template #title>
      <div class="setting-role-title">{{ $t("message.resource.settingParams") }}</div>
    </template>
    <el-form :model="formData" ref="settingSshFormRef" style="padding: 20px 20px 0 20px;" v-loading="loading">
      <el-form-item prop="ssh" :label="$t('message.resource.hasSsh')">
        <el-switch v-model="formData.ssh" style="width: 100%"/>
      </el-form-item>
      <el-form-item prop="time" :label="$t('message.resource.effectiveTime')" v-if="formData.ssh">
        <el-select v-model="timeValue" style="width: 100%" @change="changeTime">
          <el-option v-for="item in timeList" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button
            type="primary"
            @click="confirm"
            :disabled="disabled"
            style="width: 80px; height: 35px"
        >{{ $t("message.tenant.confirm") }}
        </el-button>
        <el-button
            @click="cancel"
            style="width: 80px; height: 35px; margin: 0 20px"
        >{{ $t("message.tenant.cancel") }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {useI18n} from "vue-i18n";
import resourceService from "@/api/resource/index";
import {ElMessage} from "element-plus";

const props = defineProps({
  resource: {
    type: Object,
    required: true,
    default: {}
  }
});
onMounted(() => {
  const remoteType = props.resource.detail.remoteType === -1 ? 1 : props.resource.detail.remoteType;
  formData.ssh = !!props.resource.detail.remote;
  formData.time = remoteType;
  timeValue.value = remoteType;
})
const {t} = useI18n();
const loading = ref<boolean>(false);
const settingSshFormRef = ref();
const emit = defineEmits(["closeSettingSsh", "confirmSettingSsh"]);
const formData = reactive({
  ssh: true,
  time: 1,
});
const timeList = [
  {
    value: 1,
    label: t("message.resource.currentDay")
  }, {
    value: 2,
    label: t("message.resource.threeDays")
  }, {
    value: 3,
    label: t("message.resource.sevenDays")
  },
  // {
  //   value: 4,
  //   label: t("message.resource.thirtyDays")
  // },
];
const timeValue = ref<number>(1);
const changeTime = (value: number) => {
  formData.time = value;
}
const confirm = () => {
  const payload = JSON.parse(JSON.stringify(props.resource.detail));
  payload.remote = formData.ssh ? 1 : 0;
  payload.remoteType = formData.time;
  resourceService.settingSsh(payload).then((res: { [key: string]: any }) => {
    loading.value = false;
    messageTip(res.successMessage, "success");
    emit("confirmSettingSsh");
  }).catch(() => {
    loading.value = false;
  })
};
const cancel = () => {
  emit("closeSettingSsh");
};
const messageTip = (message: string, type: any, duration = 3000) => {
  ElMessage({
    message,
    type,
    duration,
    showClose: true,
    grouping: true
  });
};
</script>

<style lang="scss" scoped>
.setting-role-title {
  font-size: 16px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #d8dce5;
}
</style>
