<template>
  <div class="layout-container" v-loading.fullscreen="initLoading">
    <keep-alive>
      <network-config v-if="stepValue===0" @confirmNetworkConfig="confirmNetworkConfig" @defaultConfig="defaultConfig"/>
    </keep-alive>
    <div class="init-config" v-if="stepValue!==0" v-loading="collectNodeLoading"
         :element-loading-text="$t('message.initConfig.deploying')">
      <div class="init-title-wrapper">
        <span class="init-title">{{ $t("message.initConfig.initConfig") }}</span>
        <el-button @click="goNetwork" type="primary" v-if="stepValue===1">
          {{ $t("message.initConfig.networkConfig") }}
        </el-button>
      </div>
      <div class="step-wrapper">
        <el-steps :active="stepValue" align-center>
          <el-step :title="$t('message.initConfig.collectNodeInfo')"/>
          <el-step :title="$t('message.initConfig.reportTenantAuth')"/>
        </el-steps>
      </div>
      <div class="init-tab-wrapper" v-show="stepValue === 1">
        <el-radio-group v-model="nodeType" size="medium" @change="changeNodeType">
          <el-radio-button :label="1">{{ $t("message.initConfig.singleAcquisitionNode") }}
          </el-radio-button>
          <el-radio-button :label="2">{{ $t("message.initConfig.multipleAcquisitionNode") }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="init-config-form-wrapper">
        <!--单采集节点-->
        <el-form
            :model="collectNodeFormData.list"
            :rules="validateRule"
            class="init-config-form"
            label-width="150px"
            ref="singleFormRef"
            v-if="stepValue === 1 && nodeType === 1"
        >
          <el-form-item :label="$t('message.initConfig.masterNodeIpAddress')" prop="master.ip">
            <el-input v-model="collectNodeFormData.list.master.ip" @input="setSingleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.username')" prop="master.username">
            <el-input v-model="collectNodeFormData.list.master.username" @input="setSingleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.password')" prop="master.password">
            <el-input v-model="collectNodeFormData.list.master.password" @input="setSingleFormButtonStatus"
                      :type="singleMasterPasswordType">
              <template #append>
                <i class="sfont password-icon"
                   :class="singleMasterPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
                   @click="changePasswordType(1)"
                ></i>
              </template>
            </el-input>
          </el-form-item>
        </el-form>

        <!--多采集节点-->
        <el-form
            :model="collectNodeFormData.list"
            :rules="validateRule"
            class="init-config-form"
            label-width="150px"
            ref="multipleFormRef"
            v-if="stepValue === 1 && nodeType === 2"
        >
          <el-form-item :label="$t('message.initConfig.vip')" prop="vip">
            <el-input v-model="collectNodeFormData.list.vip" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.mask')" prop="mask">
            <el-input v-model="collectNodeFormData.list.mask" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.masterNodeIpAddress')" prop="master.ip">
            <el-input v-model="collectNodeFormData.list.master.ip" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.username')" prop="master.username">
            <el-input v-model="collectNodeFormData.list.master.username" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.password')" prop="master.password">
            <el-input v-model="collectNodeFormData.list.master.password" @input="setMultipleFormButtonStatus"
                      :type="multipleMasterPasswordType">
              <template #append>
                <i class="sfont password-icon"
                   :class="multipleMasterPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
                   @click="changePasswordType(2)"
                ></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.backupNode1IpAddress')" prop="backupOne.ip">
            <el-input v-model="collectNodeFormData.list.backupOne.ip" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.username')" prop="backupOne.username">
            <el-input v-model="collectNodeFormData.list.backupOne.username" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.password')" prop="backupOne.password">
            <el-input v-model="collectNodeFormData.list.backupOne.password" @input="setMultipleFormButtonStatus"
                      :type="multipleBackupOnePasswordType">
              <template #append>
                <i class="sfont password-icon"
                   :class="multipleBackupOnePasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
                   @click="changePasswordType(3)"
                ></i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.backupNode2IpAddress')" prop="backupTwo.ip">
            <el-input v-model="collectNodeFormData.list.backupTwo.ip" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.username')" prop="backupTwo.username">
            <el-input v-model="collectNodeFormData.list.backupTwo.username" @input="setMultipleFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.initConfig.password')" prop="backupTwo.password">
            <el-input v-model="collectNodeFormData.list.backupTwo.password" @input="setMultipleFormButtonStatus"
                      :type="multipleBackupTwoPasswordType">
              <template #append>
                <i class="sfont password-icon"
                   :class="multipleBackupTwoPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
                   @click="changePasswordType(4)"
                ></i>
              </template>
            </el-input>
          </el-form-item>
        </el-form>

        <!-- 上报租户认证-->
        <el-form
            :model="tenantAuthFormData.list"
            :rules="validateRule"
            class="tenant-auth-form"
            label-width="150px"
            ref="tenantFormRef"
            v-loading="tenantLoading"
            v-if="stepValue === 2"
        >
          <el-form-item :label="$t('message.tenant.platformAddress')" prop="ip">
            <el-input v-model="tenantAuthFormData.list.ip" @input="setTenantFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.tenant.tenantLoginName')" prop="username">
            <el-input v-model="tenantAuthFormData.list.username" @input="setTenantFormButtonStatus"/>
          </el-form-item>
          <el-form-item :label="$t('message.tenant.password')" prop="password">
            <el-input v-model="tenantAuthFormData.list.password" @input="setTenantFormButtonStatus"
                      :type="tenantPasswordType">
              <template #append>
                <i class="sfont password-icon"
                   :class="tenantPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"
                   @click="changePasswordType(5)"
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
            @click="confirmCollectNode"
            :disabled="collectNodeDisabled"
            color="#0546CE"
            class="start-deploy-button"
            v-if="stepValue === 1"
        >{{ $t("message.initConfig.startDeployment") }}
        </el-button>
        <el-button
            type="primary"
            @click="confirmTenantAuth"
            :disabled="tenantAuthDisabled || agreeStatus[0]!==1"
            color="#0546CE"
            class="tenant-auth-button"
            v-if="stepValue === 2"
        >{{ $t("message.initConfig.tenantAuth") }}
        </el-button>
        <el-button
            @click="skipAuth"
            class="tenant-auth-button skip-auth-button"
            v-if="stepValue === 2"
        >{{ $t("message.initConfig.skipAuth") }}
        </el-button>
      </div>
      <privacy-policy
        v-model="privacyPolicyVisible"
        v-if="privacyPolicyVisible"
        @confirmPrivacyPolicy="confirmPrivacyPolicy"
        @closePrivacyPolicy="closePrivacyPolicy"        
      ></privacy-policy>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, getCurrentInstance, onMounted, reactive, ref} from "vue";
import {useI18n} from 'vue-i18n'
import util from "@/utils/system/common-util";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import deployService from "@/api/deploy/index";
import initService from "@/api/init/index"
import _ from "lodash";
import networkConfig from "./components/network-config.vue"
import privacyPolicy from './components/privacy-polity.vue'

const {t} = useI18n();
const router = useRouter();
const nodeType = ref<number>(1);
const stepValue = ref<number>(0);
const singleMasterPasswordType = ref("password");
const multipleMasterPasswordType = ref("password");
const multipleBackupOnePasswordType = ref("password");
const multipleBackupTwoPasswordType = ref("password");
const tenantPasswordType = ref("password");
const collectNodeDisabled = ref<boolean>(true);
const tenantAuthDisabled = ref<boolean>(true);
const initLoading = ref<boolean>(false);
const collectNodeLoading = ref<boolean>(false);
const tenantLoading = ref<boolean>(false);
const singleFormRef = ref();
const multipleFormRef = ref();
const tenantFormRef = ref();
const initStatus = ref<boolean>(false);
const instance = getCurrentInstance();
const eventBus = instance.appContext.config.globalProperties.$eventBus;
const agreeStatus = ref([]);
const privacyPolicyVisible = ref<boolean>(false);

onMounted(() => {
  emitInitConfig(true);
  // getInitStatus();
})
const collectNodeFormData = reactive({
  list: {
    vip: "",
    mask: "",
    master: {
      ip: "",
      username: "",
      password: "",
      isMaster: true
    },
    backupOne: {
      ip: "",
      username: "",
      password: "",
      isMaster: false
    },
    backupTwo: {
      ip: "",
      username: "",
      password: "",
      isMaster: false
    }
  }
});
const tenantAuthFormData = reactive({
  list: {
    ip: "",
    username: "",
    password: ""
  }
});
const singleFormStatus = computed(() => _.every(collectNodeFormData.list.master, item => !!item));

const multipleFormStatus = computed(() => {
  return _.every(collectNodeFormData.list, (item: any) => {
    if (item instanceof Object) {
      const {isMaster, ...temp} = item;
      return _.every(temp, (value) => !!value);
    } else {
      return !!item;
    }
  })
})
const tenantFormStatus = computed(() => _.every(tenantAuthFormData.list, item => !!item));

const checkIp = (rule: any, value: string, callback: any) => {
  if (!value) return;
  if (util.ipReg.test(value) || util.domainReg.test(value)) {
    callback();
  } else {
    callback(t("message.tenant.domainOrIpTip"));
  }
}
const checkMask = (rule: any, value: any, callback: any) => {
  if (!value || util.maskList.indexOf(value) !== -1) {
    return callback();
  }
  return callback(new Error(t("message.initConfig.maskError")));
};

const validateRule = reactive({
  vip: [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    {
      pattern: util.ipReg,
      message: t("message.tenant.ipTip"),
      trigger: "change",
    }
  ],
  mask: [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    {validator: checkMask, trigger: "change"},
  ],
  "master.ip": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    {
      pattern: util.ipReg,
      message: t("message.tenant.ipTip"),
      trigger: "change",
    },
  ],
  "master.username": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  "master.password": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  "backupOne.ip": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    {
      pattern: util.ipReg,
      message: t("message.tenant.ipTip"),
      trigger: "change",
    },
  ],
  "backupOne.username": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  "backupOne.password": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  "backupTwo.ip": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    {
      pattern: util.ipReg,
      message: t("message.tenant.ipTip"),
      trigger: "change",
    },
  ],
  "backupTwo.username": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  "backupTwo.password": [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
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
  ],
});
const confirmCollectNode = () => {
  const formData = JSON.parse(JSON.stringify(collectNodeFormData.list));
  const {master} = formData;
  master.password = util.encryptBySm4(master.password);
  if (nodeType.value === 1) {
    deploySingleNode(master);
  } else {
    const {backupOne, backupTwo, vip, mask} = formData;
    const payload: any = {
      nodes: [],
      vip,
      mask
    };
    backupOne.password = util.encryptBySm4(backupOne.password);
    backupTwo.password = util.encryptBySm4(backupTwo.password);
    payload.nodes.push(master);
    payload.nodes.push(backupOne);
    payload.nodes.push(backupTwo);
    deployBatchNode(payload);
  }
}
const confirmTenantAuth = () => {
  const payload = JSON.parse(JSON.stringify(tenantAuthFormData.list));
  payload.password = util.encryptBySm4(payload.password);
  tenantAuth(payload);
};
const deploySingleNode = (payload: any) => {
  collectNodeLoading.value = true;
  deployService.deploySingleNode(payload).then((res: any) => {
    if (res.state === 0) {
      stepValue.value = 2;
    }
    collectNodeLoading.value = false;
  }).catch(() => {
    collectNodeLoading.value = false;
  })
}
const deployBatchNode = (payload: any) => {
  collectNodeLoading.value = true;
  deployService.deployBatchNode(payload).then((res: any) => {
    if (res.state === 0) {
      stepValue.value = 2;
    }
    collectNodeLoading.value = false;
  }).catch(() => {
    collectNodeLoading.value = false;
  })
}
const getInitStatus = () => {
  emitInitConfig(true);
  initLoading.value = true;
  initService.getInitStatus().then((res: any) => {
    initLoading.value = false;
    // res.data.step =2
    if (res.data && [0, 1, 2].includes(res.data.step)) {
      stepValue.value = res.data.step;
    } else if (res.data && res.data.step === 3) {
      emitInitConfig(false);
      goAgent();
    }
  })
}
const tenantAuth = (payload: any) => {
  tenantAuthDisabled.value = true;
  initLoading.value = true;
  deployService.tenantAuth(payload).then((res: any) => {
    setTenantFormButtonStatus();
    emitInitConfig(false);
    goAgent();
  }).catch(() => {
    setTenantFormButtonStatus();
    initLoading.value = false;
  })
}
const skipAuth = () => {
  tenantAuthDisabled.value = true;
  initLoading.value = true;
  initService.editStep({step: 3}).then((res: any) => {
    initLoading.value = false;
    setTenantFormButtonStatus();
    emitInitConfig(false);
    goAgent();
  }).catch(() => {
    setTenantFormButtonStatus();
    initLoading.value = false;
  })
}
const emitInitConfig = (status: boolean) => {
  initStatus.value = status;
  eventBus.emit("initConfig", initStatus.value);
}
const changePasswordType = (type: number) => {
  switch (type) {
    case 1:
      singleMasterPasswordType.value === "" ? (singleMasterPasswordType.value = "password") : (singleMasterPasswordType.value = "");
      break;
    case 2:
      multipleMasterPasswordType.value === "" ? (multipleMasterPasswordType.value = "password") : (multipleMasterPasswordType.value = "");
      break;
    case 3:
      multipleBackupOnePasswordType.value === "" ? (multipleBackupOnePasswordType.value = "password") : (multipleBackupOnePasswordType.value = "");
      break;
    case 4:
      multipleBackupTwoPasswordType.value === "" ? (multipleBackupTwoPasswordType.value = "password") : (multipleBackupTwoPasswordType.value = "");
      break;
    case 5:
      tenantPasswordType.value === "" ? (tenantPasswordType.value = "password") : (tenantPasswordType.value = "");
      break;
  }
}
const setSingleFormButtonStatus = () => {
  if (singleFormRef.value && singleFormStatus.value) {
    singleFormRef.value.validate((valid: any) => {
      collectNodeDisabled.value = !valid;
    });
  } else {
    collectNodeDisabled.value = true;
  }
};
const setMultipleFormButtonStatus = () => {
  if (multipleFormRef.value && multipleFormStatus.value) {
    multipleFormRef.value.validate((valid: any) => {
      collectNodeDisabled.value = !valid;
    });
  } else {
    collectNodeDisabled.value = true;
  }
};
const setTenantFormButtonStatus = () => {
  if (tenantFormRef.value && tenantFormStatus.value) {
    tenantFormRef.value.validate((valid: any) => {
      tenantAuthDisabled.value = !valid;
    });
  } else {
    tenantAuthDisabled.value = true;
  }
};
const messageTip = (message: string, type: any) => {
  ElMessage({
    message,
    type,
    grouping: true
  });
};

const changeNodeType = (val: number) => {
  if (nodeType.value === 1) {
    setSingleFormButtonStatus();
  } else {
    setMultipleFormButtonStatus();
  }
}
const goAgent = () => {
  router.push({
    path: `/agent/index`
  });
};

const confirmNetworkConfig = (value: number) => {
  stepValue.value = value;
  if (value === 3) {
    emitInitConfig(false);
    goAgent();
  }
}

const defaultConfig = (config: object) => {
  collectNodeFormData.list.master.ip = config.ip;
}

const goNetwork = () => {
  stepValue.value = 0;
}
const closePrivacyPolicy = () => {
  privacyPolicyVisible.value = false
}
const confirmPrivacyPolicy = (status: boolean) => {
  console.log(444,status)
  agreeStatus.value = status;
  closePrivacyPolicy()
}
const showPrivacyPolicy = () => {
  privacyPolicyVisible.value = true
}

</script>

<style lang="scss" scoped>
.init-config {
  padding: 24px 24px 0 24px;

  .init-title-wrapper {
    display: flex;
    justify-content: space-between;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }

  .step-wrapper {
    margin-top: 24px;

    :deep(.el-steps--horizontal .is-finish .el-step__line) {
      height: 4px;
      background-color: #0546CE;
    }

    :deep(.el-steps--horizontal .is-finish .is-text) {
      background-color: #0546CE;
    }

    :deep(.el-steps--horizontal .is-finish .el-step__icon-inner) {
      color: #FFFFFF;
    }

    :deep(.el-steps--horizontal .el-step__main .el-step__title) {
      font-weight: 700;
      font-size: 16px;
    }
  }

  .init-tab-wrapper {
    margin-top: 32px;

    :deep(.el-radio-button--medium .el-radio-button__inner) {
      min-width: 275px;
    }
  }

  .init-config-form-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 24px;

    .init-config-form, .tenant-auth-form {
      min-width: 550px;
      width: 550px;
    }

    .tenant-auth-form {
      margin-top: 68px;
    }
  }

  .start-deploy-button, .tenant-auth-button {
    height: 35px;
    padding: 0 80px;
    margin-left: 150px;
    margin-top: 24px;
  }
  .skip-auth-button{
    margin-left: 20px;
  }

}
</style>

function closePrivacyPolicy() {
  throw new Error("Function not implemented.");
}
