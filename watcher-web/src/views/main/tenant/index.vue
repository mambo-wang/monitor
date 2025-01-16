<template>
  <div class="layout-container">
    <div class="tenant-auth" v-loading="loading">
      <div class="tenant-title-wrapper"><span class="tenant-title">{{ $t("message.tenant.title") }}</span></div>
      <div class="org-info-wrapper">
        <div class="org-title-wrapper">
          <span>{{ $t("message.tenant.orgTitle") }}</span>
        </div>
        <div class="org-company-info">
          <div class="company-info">
            {{ $t("message.tenant.companyName") }}:&nbsp;&nbsp;&nbsp;&nbsp;{{ orgInfo.comName }}
          </div>
          <div class="org-info">{{ $t("message.tenant.orgName") }}:&nbsp;&nbsp;&nbsp;&nbsp;{{ orgInfo.orgName }}</div>
        </div>
      </div>
      <div class="auth-title-wrapper">
        <div class="title-wrapper">
          <span>{{ $t("message.tenant.authTitle") }}</span>
        </div>
      </div>
      <div class="tenant-auth-form-wrapper">
        <el-form
            :model="formData.list"
            :rules="validateRule"
            class="tenant-auth-form"
            label-width="150px"
            ref="formRef"
        >
          <el-form-item :label="$t('message.tenant.platformAddress')" prop="ip">
            <el-input v-model="formData.list.ip" @input="setButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.tenant.tenantLoginName')" prop="username">
            <el-input v-model="formData.list.username" @input="setButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.tenant.password')" prop="password">
            <el-input v-model="formData.list.password" @input="setButtonStatus" :type="passwordType">
              <template #append>
                <i class="sfont password-icon"
                   :class="passwordType ? 'system-yanjing-guan' : 'system-yanjing'"
                   @click="changePasswordType"
                ></i>
              </template>
            </el-input>
          </el-form-item>
           <!--隐私声明 -->
           <el-form-item prop="privacyPolicy">
            <el-checkbox-group v-model="agreeStatus">
              <el-checkbox :label="1">
                <a @click.prevent="showPrivacyPolicy" style="cursor:pointer"> 
                  {{ $t('message.privacyPolicy.privacyPolicyTip') }}
                  {{ $t('message.privacyPolicy.leftBookQuotes') }}
                  <a style="cursor:pointer;color:#0546CE">
                    {{ $t('message.privacyPolicy.privacyPolicyTitle')}}
                  </a>
                    {{ $t('message.privacyPolicy.rightBookQuotes') }}
                </a>
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>
      <div>
        <el-button
            type="primary"
            @click="openRecertify"
            :disabled="disabled || agreeStatus[0]!==1"
            color="#0546CE"
            class="recertify-button"
        >{{ $t("message.tenant.recertify") }}
        </el-button>
      </div>
    </div>
  </div>
  <confirm-recertify-tip v-model="recertifyVisible"
                         v-if="recertifyVisible"
                         @confirmRecertify="confirmRecertify"
                         @closeRecertify="closeRecertify"
  ></confirm-recertify-tip>
  <privacy-policy
        v-model="privacyPolicyVisible"
        v-if="privacyPolicyVisible"
        @confirmPrivacyPolicy="confirmPrivacyPolicy"
        @closePrivacyPolicy="closePrivacyPolicy"        
      ></privacy-policy>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from "vue";
import {useI18n} from 'vue-i18n';
import util from "@/utils/system/common-util";
import {ElMessage} from "element-plus";
import confirmRecertifyTip from "./component/recertify-confirm.vue"
import deployService from "@/api/deploy/index";
import _ from "lodash"
import privacyPolicy from '../init-config/components/privacy-polity.vue'

const {t} = useI18n();
const formRef = ref();
const recertifyVisible = ref<boolean>(false);
const passwordType = ref("password");
const disabled = ref<boolean>(true);
const loading = ref<boolean>(false);
const tenantId = ref<string>("");
const formData = reactive({
  list: {
    ip: "",
    username: "",
    password: ""
  }
});
const orgInfo = reactive({
  comName: null,
  orgName: null
})
const agreeStatus = ref([]);
const privacyPolicyVisible = ref<boolean>(false);

onMounted(() => {
  getTenantConfig();
  getWebsocketState();
})
const formStatus = computed(() => _.every(formData.list, item => !!item));
const checkIp = (rule: any, value: string, callback: any) => {
  if (!value) return;
  if (util.ipReg.test(value) || util.domainReg.test(value)) {
    callback();
  } else {
    callback(t("message.tenant.domainOrIpTip"));
  }
}
const validateRule = reactive({
  ip: [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    // {
    //   validator: checkIp,
    //   trigger: "change",
    // },
  ],
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
const openRecertify = () => {
  recertifyVisible.value = true;
}
const confirmRecertify = () => {
  confirm();
  closeRecertify();
}
const closeRecertify = () => {
  recertifyVisible.value = false;
}
const confirm = () => {
  const payload = JSON.parse(JSON.stringify(formData.list));
  payload.password = util.encryptBySm4(payload.password);
  if (tenantId.value) {
    payload.id = tenantId.value;
  }
  tenantAuth(payload);
}
const changePasswordType = () => {
  passwordType.value === "" ? (passwordType.value = "password") : (passwordType.value = "");
};
const setButtonStatus = () => {
  if (formRef.value && formStatus.value) {
    formRef.value.validate((valid: any) => {
      disabled.value = !valid;
    });
  } else {
    disabled.value = true;
  }
};
const tenantAuth = (payload: any) => {
  loading.value = true;
  ElMessage.closeAll();
  deployService.tenantAuth(payload).then((res: any) => {
    messageTip(res.successMessage, "success");
    getTenantConfig();
    getWebsocketState();
  }).catch(() => {
    loading.value = false;
  })
}
const getTenantConfig = () => {
  loading.value = true;
  deployService.getTenantConfig().then((res: any) => {
    loading.value = false;
    if (res.data) {
      formData.list.ip = res.data.ip;
      formData.list.username = res.data.username;
      formData.list.password = util.decryptBySm4(res.data.password);
      tenantId.value = res.data.id;
      setButtonStatus();
      orgInfo.comName = res.data.comName;
      orgInfo.orgName = res.data.orgName;
    }
  }).catch(() => {
    loading.value = false;
  })
}
const getWebsocketState = () => {
  deployService.getWebsocketState().then((res: any) => {
    if (res.data && res.data.state !== -1 && res.data.state !== 1) {
      messageTip(res.data.message, "error", 0);
    }
  })
}
const messageTip = (message: string, type: any, duration = 3000) => {
  ElMessage({
    message,
    type,
    duration,
    showClose: true,
    grouping: true
  });
};
const closePrivacyPolicy = () => {
  privacyPolicyVisible.value = false
}
const confirmPrivacyPolicy = (status: boolean) => {
  console.log(444,status)
  agreeStatus.value = status;
  closePrivacyPolicy()
}
const showPrivacyPolicy = () => {
  console.log(11111)
  privacyPolicyVisible.value = true
}


</script>

<style lang="scss" scoped>
.tenant-auth {
  padding: 24px 24px 0 24px;

  .tenant-title-wrapper {
    display: flex;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }

  .org-info-wrapper, .auth-title-wrapper {
    padding: 24px;
    display: flex;

    .org-title-wrapper, .title-wrapper {
      font-weight: 700;
      font-size: 18px;
      color: black;
      padding: 24px 50px;
    }
  }

  .org-info-wrapper {
    flex-wrap: wrap;
    padding-bottom: 0;

    .org-title-wrapper, .org-company-info {
      width: 100%;
      text-align: left;
    }

    .org-company-info {
      padding: 0 80px;
      color: black;

      .company-info {
        padding-bottom: 18px;
      }
    }
  }

  .auth-title-wrapper {
    padding-bottom: 0;
  }

  .tenant-auth-form-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 6px;

    .tenant-auth-form {
      min-width: 550px;
      width: 550px;
    }
  }

  .recertify-button {
    height: 35px;
    padding: 0 80px;
    margin-left: 150px;
    margin-top: 24px;
  }
}
</style>
