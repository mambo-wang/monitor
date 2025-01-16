<template>
  <el-dialog width="500px" :close-on-click-modal="false">
    <template #title>
      <div class="modify-password-title">{{ $t("message.system.changePassword") }}</div>
    </template>
    <el-form :model="formData" :rules="validateRule" ref="modifyPasswordFormRef" style="margin-top:20px;"
             label-width="100px" v-loading="loading">
      <el-form-item prop="oldPassword" :label="$t('message.system.oldPassword')">
        <el-input v-model="formData.oldPassword" @input="setButtonStatus" :type="oldPasswordType">
          <template #append>
            <i class="sfont password-icon"
               :class="oldPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
               @click="changePasswordType('old')"
            ></i>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="newPassword" :label="$t('message.system.newPassword')">
        <el-input v-model="formData.newPassword" @input="setButtonStatus" :type="newPasswordType">
          <template #append>
            <i class="sfont password-icon"
               :class="newPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
               @click="changePasswordType('new')"
            ></i>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="renewPassword" :label="$t('message.system.renewPassword')">
        <el-input v-model="formData.renewPassword" @input="setButtonStatus" :type="renewPasswordType">
          <template #append>
            <i class="sfont password-icon"
               :class="renewPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
               @click="changePasswordType('renew')"
            ></i>
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
import {computed, onMounted, reactive, ref} from "vue";
import {useI18n} from "vue-i18n";
import util from "@/utils/system/common-util";
import {ElMessage} from "element-plus";
import {modifyPassword, getPasswordComplexity} from "@/api/login/login";
import _ from "lodash";

onMounted(() => {
  getPasswordComplexity().then((res: any) => {
    passwordComplexity.value = res.data || 0;
  })
})

const props = defineProps({
  password: {
    type: String,
    required: true,
    default: ""
  }
})
const {t} = useI18n();
const modifyPasswordFormRef = ref();
const oldPasswordType = ref("password");
const newPasswordType = ref("password");
const renewPasswordType = ref("password");
const disabled = ref<boolean>(true);
const loading = ref<boolean>(false);
const passwordComplexity = ref<number>(0);
const emit = defineEmits(["closeModifyPassword", "confirmModifyPassword"]);
const formData = reactive({
  oldPassword: "",
  newPassword: "",
  renewPassword: ""
});
const formStatus = computed(() => _.every(formData, item => !!item));
const checkOldPassword = (rule: any, value: string, callback: any) => {
  if (!value) return;
  if (props.password !== value) {
    callback(t("message.system.errorPassword"));
  } else {
    callback();
  }
}
const checkNewPassword = (rule: any, value: string, callback: any) => {
  if (!value) return;
  if (props.password === value) {
    callback(t("message.system.notSamePassword"));
  } else if (formData.newPassword !== formData.renewPassword) {
    callback(t("message.system.twicePasswordNotMatch"));
  } else {
    callback();
  }
}
const checkPasswordComplexity = (rule: any, value: string, callback: any) => {
  const regNum = '[0-9]+';
  const regLowercase = '[a-z]+';
  const regUppercase = '[A-Z]+';
  const regEx = "[`~!@#$%^&*()=|{}':;',\\\\ \\[\\].\"<>/?\\_\\-\\+]+";
  const noNumber = value.match(new RegExp(regNum)) === null;
  const noLowercase = value.match(new RegExp(regLowercase)) === null;
  const noUppercase = value.match(new RegExp(regUppercase)) === null;
  const noSpecialCharts = value.match(new RegExp(regEx)) === null;
  switch (passwordComplexity.value) {
    case 0:
      callback();
      break;
    case 1:
      if (noNumber || (noLowercase && noUppercase)) {
        callback(t("message.tenant.passwordComplexity1Tip"));
      } else {
        callback();
      }
      break;
    case 2:
      if (noSpecialCharts) {
        callback(t("message.tenant.passwordComplexity2Tip"));
      } else {
        callback();
      }
      break;
    case 3:
      if (noNumber || (noLowercase && noUppercase) || noSpecialCharts) {
        callback(t("message.tenant.passwordComplexity3Tip"));
      } else {
        callback();
      }
      break;
    case 4:
      if (noNumber || noLowercase || noUppercase || noSpecialCharts) {
        callback(t("message.tenant.passwordComplexity4Tip"));
      } else {
        callback();
      }
      break;
  }
}
const validateRule = reactive({
  oldPassword: [{
    required: true,
    message: t("message.tenant.required"),
    trigger: "change",
  }, {
    validator: checkOldPassword,
    trigger: "change",
  }],
  newPassword: [{
    required: true,
    message: t("message.tenant.required"),
    trigger: "change",
  }, {
    min: 8,
    max: 32,
    message: t("message.tenant.passwordLength"),
    trigger: "change",
  }, {
    validator: checkNewPassword,
    trigger: "change",
  }, {
    validator: checkPasswordComplexity,
    trigger: "change",
  }],
  renewPassword: [{
    required: true,
    message: t("message.tenant.required"),
    trigger: "change",
  }, {
    min: 8,
    max: 32,
    message: t("message.tenant.passwordLength"),
    trigger: "change",
  }, {
    validator: checkNewPassword,
    trigger: "change",
  }, {
    validator: checkPasswordComplexity,
    trigger: "change",
  }]
})
const setButtonStatus = () => {
  if (modifyPasswordFormRef.value && formStatus.value) {
    modifyPasswordFormRef.value.validate((valid: any) => {
      disabled.value = !valid;
    });
  } else {
    disabled.value = true;
  }
};
const changePasswordType = (type: string) => {
  switch (type) {
    case "old":
      oldPasswordType.value === "" ? (oldPasswordType.value = "password") : (oldPasswordType.value = "");
      break;
    case "new":
      newPasswordType.value === "" ? (newPasswordType.value = "password") : (newPasswordType.value = "");
      break;
    case "renew":
      renewPasswordType.value === "" ? (renewPasswordType.value = "password") : (renewPasswordType.value = "");
      break;
  }

};
const confirm = () => {
  loading.value = true;
  disabled.value = true;
  const payload = JSON.parse(JSON.stringify(formData));
  payload.oldPassword = util.encryptBySm4(payload.oldPassword);
  payload.newPassword = util.encryptBySm4(payload.newPassword);
  payload.renewPassword = util.encryptBySm4(payload.renewPassword);
  modifyPassword(payload).then(() => {
    loading.value = false;
    setButtonStatus();
    emit("confirmModifyPassword");
  }).catch((res) => {
    loading.value = false;
    setButtonStatus();
  })
};
const cancel = () => {
  emit("closeModifyPassword");
};
const messageTip = (message: string, type: any) => {
  ElMessage({
    message,
    type,
  });
};
</script>

<style lang="scss" scoped>
.modify-password-title {
  font-size: 16px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #d8dce5;
}
</style>
