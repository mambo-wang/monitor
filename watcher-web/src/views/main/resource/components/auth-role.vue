<template>
  <el-dialog width="500px" :close-on-click-modal="false" class="auth-role-container">
    <template #title>
      <div class="auth-role-title">{{ $t("message.resource.permissionAuth") }}</div>
    </template>
    <p style="text-align: justify;padding: 0 10px;">{{ $t("message.resource.permissionAuthTip") }}</p>
    <el-form :model="formData.list" :rules="validateRule" ref="authRoleFormRef" style="padding: 20px 20px 0 20px;"
             v-loading="loading">
      <el-form-item prop="username" :label="$t('message.resource.adminAccount')">
        <el-input v-model="formData.list.username" @input="setButtonStatus"/>
      </el-form-item>
      <el-form-item prop="password" :label="$t('message.resource.adminPassword')">
        <el-input v-model="formData.list.password" @input="setButtonStatus" :type="passwordType">
          <template #append>
            <i class="sfont password-icon" @click="changePasswordType"
               :class="passwordType ? 'system-yanjing-guan' : 'system-yanjing'"/>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button
            type="primary"
            @click="confirm"
            :disabled="disabled"
            :loading="loading"
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
import {computed, reactive, ref, toRefs} from "vue";
import {useI18n} from "vue-i18n";
import _ from "lodash";
import util from "@/utils/system/common-util";
import resourceService from "@/api/resource/index";
import {ElMessage} from "element-plus";

const props = defineProps({
  resource: {
    type: Object,
    required: true,
    default: {}
  }
});

const {t} = useI18n();
const authRoleFormRef = ref();
const loading = ref<boolean>(false);
const disabled = ref<boolean>(true);
const emit = defineEmits(["closeAuthRole", "confirmAuthRole"]);
const passwordType = ref("password");
const formData = reactive({
  list: {
    username: null,
    password: null,
  },
});
const validateRule = reactive({
  username: [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  password: [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ]
});
const formStatus = computed(() => _.every(formData.list, item => item !== null && item !== undefined));
const changePasswordType = () => {
  passwordType.value === "" ? (passwordType.value = "password") : (passwordType.value = "");
};
const setButtonStatus = () => {
  if (authRoleFormRef.value && formStatus.value) {
    authRoleFormRef.value.validate((valid: any) => {
      disabled.value = !valid;
    });
  } else {
    disabled.value = true;
  }
};
const confirm = () => {
  loading.value = true;
  const payload = Object.assign({}, JSON.parse(JSON.stringify(props.resource.detail)), JSON.parse(JSON.stringify(formData.list)));
  payload.password = util.encryptBySm4(payload.password);
  resourceService.authRole(payload).then(res => {
    loading.value = false;
    emit("confirmAuthRole", payload);
  }).catch(res => {
    loading.value = false;
  })
};
const cancel = () => {
  emit("closeAuthRole");
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
.auth-role-title {
  font-size: 16px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #d8dce5;
}
</style>
