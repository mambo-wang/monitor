<template>
  <div class="network-config" v-loading="networkLoading"
       :element-loading-text="loadingText">
    <div class="network-title-wrapper">
      <span class="network-title">{{ $t("message.initConfig.networkInit") }}</span>
      <el-button @click="backNetwork" type="primary" style="z-index: 2001;" v-if="backButtonVisible">
        {{ $t("message.initConfig.networkConfig") }}
      </el-button>
    </div>
    <div class="config-title-wrapper">
      <span class="config-title">{{ $t("message.initConfig.networkConfig") }}</span>
    </div>
    <div class="network-tab-wrapper">
      <el-radio-group v-model="networkType" size="medium" @change="changeNetworkType">
        <el-radio-button :label="1">{{ $t("message.initConfig.singleNetwork") }}
        </el-radio-button>
        <el-radio-button :label="2">{{ $t("message.initConfig.multipleNetwork") }}
        </el-radio-button>
      </el-radio-group>
    </div>
    <p class="network-config-tip" v-show="networkType === 1">{{ $t("message.initConfig.singleNetworkTip") }}</p>
    <p class="network-config-tip" v-show="networkType === 2">{{ $t("message.initConfig.multipleNetworkTip") }}</p>
    <div class="network-config-form-wrapper">
      <!--单网络模式-->
      <el-form
          :model="singleFormData"
          :rules="singleRule"
          class="network-config-form"
          label-width="150px"
          ref="singleFormRef"
          v-show="networkType === 1">
        <el-form-item :label="$t('message.initConfig.selectIntranetCard')" prop="inner.name">
          <el-select class="search-selector" v-model="singleFormData.inner.name" @change="changeSingleCard"
                     style="width: 100%">
            <el-option v-for="(item,index) in networkCardList" :key="index" :label="item.name" :value="item.name">
              <div style="display: flex;justify-content: space-between;align-items: center;">
                <span>{{ item.name }}</span>
                <el-tag type="success">{{ cardTypeMap[item.wifi] }}</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <!--        <el-form-item :label="$t('message.initConfig.ipAllocate')" prop="inner.allocation">-->
        <!--          <el-radio-group v-model="singleFormData.inner.allocation" class="allocate-radio-group"-->
        <!--                          @change="setSingleFormButtonStatus">-->
        <!--            <el-radio size="large" v-for="item in allocationList" :key="item.label" :label="item.label">-->
        <!--              {{ item.text }}-->
        <!--            </el-radio>-->
        <!--          </el-radio-group>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="">-->
        <!--          <div class="ip-allocate-tip">{{ $t('message.initConfig.ipAllocateTip') }}</div>-->
        <!--        </el-form-item>-->
        <el-form-item :label="$t('message.initConfig.ipAddress')" prop="inner.ip">
          <el-input v-model="singleFormData.inner.ip" @input="setSingleFormButtonStatus"
                    :disabled="singleFormData.inner.allocation==='DHCP'"/>
        </el-form-item>
        <el-form-item :label="$t('message.initConfig.mask')" prop="inner.mask">
          <el-input v-model="singleFormData.inner.mask" @input="setSingleFormButtonStatus"
                    :disabled="singleFormData.inner.allocation==='DHCP'"/>
        </el-form-item>
        <el-form-item :label="$t('message.initConfig.gateway')" prop="inner.gateway">
          <el-input v-model="singleFormData.inner.gateway" @input="setSingleFormButtonStatus"
                    :disabled="singleFormData.inner.allocation==='DHCP'"/>
        </el-form-item>
      </el-form>
      <div class="node-type-wrapper" v-show="networkType === 1">
        <span class="type-title">{{ $t("message.initConfig.nodeType") }}</span>
      </div>
      <el-form :model="singleFormData"
               :rules="singleRule"
               class="network-config-form"
               label-width="150px"
               v-show="networkType === 1">
        <el-form-item :label="$t('message.initConfig.currentNodeType')" prop="master">
          <el-radio-group v-model="singleFormData.master" class="node-type-radio-group"
                          @change="setSingleFormButtonStatus">
            <el-radio :label="1">{{ $t("message.initConfig.masterNode") }}
            </el-radio>
            <el-radio :label="0">{{ $t("message.initConfig.backupNode") }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>


      <!--多网络模式-->
      <el-form
          :model="multipleFormData"
          :rules="multiRule"
          class="network-config-form multiple-form"
          label-width="150px"
          ref="multipleFormRef"
          v-show="networkType === 2">
        <el-row>
          <el-col :span="12">
            <el-form-item :label="$t('message.initConfig.selectIntranetCard')" prop="inner.name">
              <el-select class="search-selector" v-model="multipleFormData.inner.name"
                         @change="changeMultiInnerCard"
                         style="width: 100%">
                <el-option v-for="(item,index) in networkCardList" :key="index" :label="item.name" :value="item.name">
                  <div style="display: flex;justify-content: space-between;align-items: center;">
                    <span>{{ item.name }}</span>
                    <el-tag type="success">{{ cardTypeMap[item.wifi] }}</el-tag>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
            <!--            <el-form-item :label="$t('message.initConfig.ipAllocate')" prop="inner.allocation">-->
            <!--              <el-radio-group v-model="multipleFormData.inner.allocation" class="allocate-radio-group"-->
            <!--                              @change="setMultipleFormButtonStatus">-->
            <!--                <el-radio size="large" v-for="item in allocationList" :key="item.label" :label="item.label">-->
            <!--                  {{ item.text }}-->
            <!--                </el-radio>-->
            <!--              </el-radio-group>-->
            <!--            </el-form-item>-->
            <!--            <el-form-item label="">-->
            <!--              <div class="ip-allocate-tip multiple-allocate-tip">{{ $t('message.initConfig.ipAllocateTip') }}</div>-->
            <!--            </el-form-item>-->
            <el-form-item :label="$t('message.initConfig.ipAddress')" prop="inner.ip">
              <el-input v-model="multipleFormData.inner.ip" @input="setMultipleFormButtonStatus"
                        :disabled="multipleFormData.inner.allocation==='DHCP'"/>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.mask')" prop="inner.mask">
              <el-input v-model="multipleFormData.inner.mask" @input="setMultipleFormButtonStatus"
                        :disabled="multipleFormData.inner.allocation==='DHCP'"/>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.gateway')" prop="inner.gateway">
              <el-input v-model="multipleFormData.inner.gateway" @input="setMultipleFormButtonStatus"
                        :disabled="multipleFormData.inner.allocation==='DHCP'" clearable/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="$t('message.initConfig.selectExtranetCard')" prop="outer.name">
              <el-select class="search-selector" v-model="multipleFormData.outer.name"
                         @change="changeMultiOuterCard"
                         style="width: 100%">
                <el-option v-for="(item,index) in networkCardList" :key="index" :label="item.name" :value="item.name">
                  <div style="display: flex;justify-content: space-between;align-items: center;">
                    <span>{{ item.name }}</span>
                    <el-tag type="success">{{ cardTypeMap[item.wifi] }}</el-tag>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.selectWifi')" prop="outer.wifi" v-if="outerCardType===1">
              <el-select class="search-selector" v-model="multipleFormData.outer.wifi"
                         @change="setMultipleFormButtonStatus" style="width: 100%">
                <el-option v-for="(item,index) in wifiList" :key="index" :label="item" :value="item"/>
              </el-select>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.wifiPassword')" prop="outer.wifiPwd" v-if="outerCardType===1">
              <el-input v-model="multipleFormData.outer.wifiPwd" @input="setMultipleFormButtonStatus"
                        :type="wifiPasswordType">
                <template #append>
                  <i class="sfont password-icon" @click="changePasswordType"
                     :class="wifiPasswordType ? 'system-yanjing-guan' : 'system-yanjing'"/>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.ipAllocate')" prop="outer.allocation" v-if="!onlyOneCard">
              <el-radio-group v-model="multipleFormData.outer.allocation" class="allocate-radio-group"
                              @change="setMultipleFormButtonStatus" :disabled="outerCardType===1">
                <el-radio size="large" v-for="item in allocationList" :key="item.label" :label="item.label">
                  {{ item.text }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.ipAddress')" prop="outer.ip">
              <el-input v-model="multipleFormData.outer.ip" @input="setMultipleFormButtonStatus"
                        :disabled="multipleFormData.outer.allocation==='DHCP'"/>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.mask')" prop="outer.mask">
              <el-input v-model="multipleFormData.outer.mask" @input="setMultipleFormButtonStatus"
                        :disabled="multipleFormData.outer.allocation==='DHCP'"/>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.gateway')" prop="outer.gateway">
              <el-input v-model="multipleFormData.outer.gateway" @input="setMultipleFormButtonStatus"
                        :disabled="multipleFormData.outer.allocation==='DHCP'" clearable/>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.masterDns')" prop="outer.dns1">
              <el-input v-model="multipleFormData.outer.dns1" @input="setMultipleFormButtonStatus"/>
            </el-form-item>
            <el-form-item :label="$t('message.initConfig.backupDns')" prop="outer.dns2">
              <el-input v-model="multipleFormData.outer.dns2" @input="setMultipleFormButtonStatus"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="node-type-wrapper" v-show="networkType === 2">
        <span class="type-title">{{ $t("message.initConfig.nodeType") }}</span>
      </div>
      <el-form :model="multipleFormData"
               :rules="multiRule"
               class="network-config-form"
               label-width="150px"
               v-show="networkType === 2">
        <el-form-item :label="$t('message.initConfig.currentNodeType')" prop="master">
          <el-radio-group v-model="multipleFormData.master" class="node-type-radio-group"
                          @change="setMultipleFormButtonStatus">
            <el-radio :label="1">{{ $t("message.initConfig.masterNode") }}
            </el-radio>
            <el-radio :label="0">{{ $t("message.initConfig.backupNode") }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    <div>
      <el-button
          type="primary"
          :disabled="networkDisabled"
          @click="confirm"
          class="save-network-config-button"
          color="#0546CE"
      >{{ $t("message.initConfig.saveConfig") }}
      </el-button>
      <el-button
          @click="skipNetworkConfig"
          class="save-network-config-button skip-network-button"
      >{{ $t("message.initConfig.skipAuth") }}
      </el-button>
    </div>
    <new-ip-tip :url="newUrl" v-model="newIpVisible" v-if="newIpVisible"/>
  </div>
</template>

<script lang="ts" setup>
import {computed, onBeforeUnmount, onMounted, reactive, ref, watch} from "vue";
import {useI18n} from 'vue-i18n'
import util from "@/utils/system/common-util";
import {ElMessage} from "element-plus";
import initService from "@/api/init/index"
import _ from "lodash";
import newIpTip from './new-ip-tip.vue';

onMounted(() => {
  networkLoading.value = true;
  loadingText.value = '';
  Promise.all([getInitStep(), getNetworkConfig(), getNetworkCard(), getWifi()]).then((res: any) => {
    networkLoading.value = false;
    const [step, config, card, wifi] = res;
    networkCardList.value = card.data && card.data.length ? card.data : [];
    wifiList.value = wifi.data && wifi.data.length ? wifi.data : [];
    handleNetworkConfig(step, config);
  }).catch(() => {
    networkLoading.value = false;
  })
})
onBeforeUnmount(() => {
  clearTimer();
})
const emit = defineEmits(["confirmNetworkConfig", "defaultConfig"]);
const {t} = useI18n();
const networkType = ref<number>(1);
const wifiPasswordType = ref("password");
const networkDisabled = ref<boolean>(true);
const networkLoading = ref<boolean>(false);
const singleFormRef = ref();
const multipleFormRef = ref();
const networkCardList = ref([]);
const timer = ref();
const newUrl = ref();
const newIpVisible = ref<boolean>(false);
const loadingText = ref('');
const wifiList = ref([]);
const allocationList = [
  {
    label: 'STATIC',
    text: t("message.initConfig.staticAllocation"),
  }, {
    label: 'DHCP',
    text: 'DHCP',
  },
];
const cardTypeMap = {
  0: t("message.initConfig.wiredCard"),
  1: t("message.initConfig.wirelessCard"),
};
const singleFormData = reactive({
  master: 1,
  inner: {
    name: null,
    ip: null,
    mask: null,
    gateway: null,
    // allocation: 'STATIC',
  },
});
const multipleFormData = reactive({
  outer: {
    name: null,
    ip: null,
    mask: null,
    gateway: null,
    allocation: 'STATIC',
    wifi: null,
    wifiPwd: null,
    dns1: null,
    dns2: null
  },
  master: 1,
  inner: {
    name: null,
    ip: null,
    mask: null,
    gateway: null,
    // allocation: 'STATIC',
  },
});
const outerCardType = computed(() => (_.find(networkCardList.value, item => multipleFormData.outer.name === item.name) || {}).wifi);
const onlyOneCard = computed(() => (networkCardList.value || []).length === 1);
const backButtonVisible = computed(() => (singleFormData.master === 0 || multipleFormData.master === 0) && networkLoading.value)
const singleFormStatus = computed(() => singleFormData.inner.ip && singleFormData.inner.mask && singleFormData.inner.gateway && [0, 1].includes(singleFormData.master));
const multipleFormStatus = computed(() => {
  let wifiResult = true;
  let allocationResult = true;
  wifiResult = outerCardType.value === 1 ? multipleFormData.outer.wifi : true;
  if (multipleFormData.outer.allocation === 'DHCP') {
    allocationResult = multipleFormData.outer.name
  } else {
    allocationResult = multipleFormData.outer.ip && multipleFormData.outer.name && multipleFormData.outer.mask;
  }
  return multipleFormData.inner.ip && multipleFormData.inner.name && multipleFormData.inner.mask && wifiResult && allocationResult && [0, 1].includes(multipleFormData.master);
})
const checkMultipleAllocation = (rule: any, value: string, callback: any) => {
  if (networkType.value === 1) {
    callback();
  }
  const innerName = multipleFormData.inner.name;
  const innerAllocation = multipleFormData.inner.allocation;
  const outerName = multipleFormData.outer.name;
  const outerAllocation = multipleFormData.outer.allocation;
  if (!(innerName && outerName)) {
    callback();
  }
  if (innerAllocation !== outerAllocation) {
    callback(t("message.initConfig.allocationCardTip"));
  } else {
    callback();
  }
}
const checkMask = (rule: any, value: any, callback: any) => {
  if (!value || util.maskList.indexOf(value) !== -1) {
    return callback();
  }
  return callback(new Error(t("message.initConfig.maskError")));
};

const checkMultiGateway = (rule: any, value: any, callback: any) => {
  if (value === null || value === undefined || value === '') {
    return callback();
  }
  if (!util.ipReg.test(value)) {
    return callback(t("message.tenant.ipTip"));
  }
  // if (multipleFormData.inner.gateway && multipleFormData.outer.gateway) {
  //   return callback(t("message.initConfig.multiGatewayTip"));
  // }
  return callback();
};

const singleRule = reactive({
  'inner.ip': [
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
  'inner.mask': [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    {validator: checkMask, trigger: "change"},
  ],
  'inner.name': [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  'inner.allocation': [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    },
    // {validator: checkMultipleAllocation, trigger: 'change'}
  ],
  master: [
    {
      required: true,
      message: t("message.tenant.required"),
      trigger: "change",
    }
  ],
  'inner.gateway': [
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
});
const multiRule = reactive(JSON.parse(JSON.stringify(singleRule)));
multiRule['inner.ip'] = [
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
]
multiRule['inner.mask'] = [
  {
    required: true,
    message: t("message.tenant.required"),
    trigger: "change",
  },
  {validator: checkMask, trigger: "change"},
]
multiRule['inner.gateway'] = [{validator: checkMultiGateway, trigger: "change"}];
multiRule['outer.ip'] = multiRule['inner.ip'];
multiRule['outer.name'] = multiRule['inner.name'];
multiRule['outer.allocation'] = multiRule['inner.allocation'];
multiRule['outer.mask'] = multiRule['inner.mask'];
multiRule['outer.gateway'] = multiRule['inner.gateway'];
multiRule['outer.dns1'] = multiRule['outer.dns2'] = [{
  pattern: util.ipReg,
  message: t("message.initConfig.dnsTip"),
  trigger: "change",
}];

watch(() => multipleFormData.outer.name, (newVal) => {
  if (outerCardType.value === 1) {
    multiRule['outer.wifi'] = multiRule['inner.name'];
    // multiRule['outer.wifiPwd'] = multiRule['inner.name'];
    multiRule['outer.wifiPwd'] = [];
    multipleFormData.outer.allocation = 'DHCP';
  } else if (outerCardType.value === 0) {
    multiRule['outer.wifiPwd'] = [];
    multiRule['outer.wifi'] = [];
  }
  setMultipleFormButtonStatus();
})
watch(() => multipleFormData.outer.allocation, (newVal) => {
  if (newVal === 'DHCP') {
    multiRule['outer.ip'] = [{
      pattern: util.ipReg,
      message: t("message.tenant.ipTip"),
      trigger: "change",
    }];
    multiRule['outer.mask'] = [{validator: checkMask, trigger: "change"}];
  } else {
    multiRule['outer.ip'] = multiRule['inner.ip'];
    multiRule['outer.mask'] = multiRule['inner.mask'];
  }
  setMultipleFormButtonStatus();
})

const confirm = () => {
  networkLoading.value = true;
  loadingText.value = t('message.initConfig.saveConfigLoadingText');
  const payload = {
    inner: singleFormData.inner,
    master: singleFormData.master,
  };
  if (networkType.value === 2) {
    payload.inner = multipleFormData.inner;
    payload.outer = JSON.parse(JSON.stringify(multipleFormData.outer));
    payload.master = multipleFormData.master;
    payload.outer.wifiPwd = util.encryptBySm4(multipleFormData.outer.wifiPwd);
    if (outerCardType.value === 0) {
      delete payload.outer.wifiPwd;
      delete payload.outer.wifi;
    }
    if (onlyOneCard.value) {
      payload.outer.allocation = 'STATIC';
    }
  }
  initService.saveNetworkConfig(payload).then(res => {
    messageTip(res.successMessage, "success");
    if (window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1' && window.location.hostname !== payload.inner.ip) {
      goNewIp(payload.inner.ip);
    } else {
      networkLoading.value = false;
      if (payload.master === 0) {
        changeLoadingText(t('message.initConfig.awaitMasterDeploy'));
        awaitMasterConfig();
      } else {
        emit("confirmNetworkConfig", 1);
        emit("defaultConfig", JSON.parse(JSON.stringify(payload.inner)));
      }
    }
  }).catch(() => {
    networkLoading.value = false;
  })
}

const goNewIp = (ip: string) => {
  newUrl.value = `${window.location.protocol}//${ip}`;
  newIpVisible.value = true;
}

const awaitMasterConfig = () => {
  // networkLoading.value = true;
  timer.value = setInterval(() => {
    getInitStep().then((res: any) => {
      if (res.data && [2, 3].includes(res.data.step)) {
        networkLoading.value = false;
        clearTimer();
        emit("confirmNetworkConfig", res.data.step);
      }
    })
  }, 1000)
}
const getInitStep = () => initService.getInitStatus()
const getNetworkConfig = () => initService.getNetworkConfig()
const getNetworkCard = () => initService.getNetworkCard()
const getWifi = () => initService.getWifi()
const handleNetworkConfig = (step: any, config: any) => {
  // step.data.step = 0
  if (config.data) {
    singleFormData.inner = config.data.inner || singleFormData.inner;
    singleFormData.master = config.data.master;
    setSingleFormButtonStatus();
    multipleFormData.inner = JSON.parse(JSON.stringify(config.data.inner || multipleFormData.inner));
    multipleFormData.outer = config.data.outer || multipleFormData.outer;
    multipleFormData.master = config.data.master;
    multipleFormData.outer.wifiPwd = util.decryptBySm4(multipleFormData.outer.wifiPwd);
  }
  if (step.data && config.data && config.data.master === 0 && step.data.step === 1) {
    changeLoadingText(t('message.initConfig.awaitMasterDeploy'));
    awaitMasterConfig();
  } else {
    emit("confirmNetworkConfig", step.data.step);
  }
}

const backNetwork = () => {
  networkLoading.value = false;
  clearTimer();
}

const clearTimer = () => {
  if (timer.value) {
    window.clearInterval(timer.value);
  }
}

const changeLoadingText = (text: string) => {
  new Promise((resolve) => {
    networkLoading.value = false;
    resolve();
  }).then(() => {
    loadingText.value = text;
    networkLoading.value = true;
  })
}

const changeSingleCard = (value: string) => {
  const network = _.find(networkCardList.value, list => list.name === value) || {};
  singleFormData.inner.ip = network.ip;
  singleFormData.inner.mask = network.mask;
  singleFormData.inner.gateway = network.gateway;
  setSingleFormButtonStatus();
}

const changeMultiInnerCard = (value: string) => {
  const network = _.find(networkCardList.value, list => list.name === value) || {};
  multipleFormData.inner.ip = network.ip;
  multipleFormData.inner.mask = network.mask;
  multipleFormData.inner.gateway = network.gateway;
  setMultipleFormButtonStatus();
}

const changeMultiOuterCard = (value: string) => {
  const network = _.find(networkCardList.value, list => list.name === value) || {};
  multipleFormData.outer.ip = network.ip;
  multipleFormData.outer.mask = network.mask;
  multipleFormData.outer.gateway = network.gateway;
  setMultipleFormButtonStatus();
}

const changeNetworkType = () => {
  if (networkType.value === 1) {
    setSingleFormButtonStatus();
  } else {
    setMultipleFormButtonStatus();
  }
}
const setSingleFormButtonStatus = () => {
  if (singleFormRef.value && singleFormStatus.value) {
    singleFormRef.value.validate((valid: any) => {
      networkDisabled.value = !valid;
    });
  } else {
    networkDisabled.value = true;
  }
};
const setMultipleFormButtonStatus = () => {
  if (multipleFormRef.value && multipleFormStatus.value) {
    multipleFormRef.value.validate((valid: any) => {
      networkDisabled.value = !valid;
    });
  } else {
    networkDisabled.value = true;
  }
};
const changePasswordType = () => {
  wifiPasswordType.value === "" ? (wifiPasswordType.value = "password") : (wifiPasswordType.value = "");
};
const skipNetworkConfig = () => {
  networkLoading.value = true;
  initService.editStep({step: 1}).then(() => {
    networkLoading.value = false;
    emit("confirmNetworkConfig", 1);
  }).catch(() => {
    networkLoading.value = true;
  })
}

const messageTip = (message: string, type: any) => {
  ElMessage({
    message,
    type,
    grouping: true
  });
};

</script>

<style lang="scss" scoped>
.network-config {
  padding: 24px;

  .network-title-wrapper {
    display: flex;
    justify-content: space-between;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }

  .config-title-wrapper, .node-type-wrapper {
    display: flex;
    font-weight: 700;
    font-size: 18px;
    margin-top: 18px;
    margin-left: 160px;
    color: black;
  }

  .node-type-wrapper {
    width: 100%;
  }

  .network-tab-wrapper {
    margin-top: 12px;

    :deep(.el-radio-button--medium .el-radio-button__inner) {
      min-width: 275px;
    }
  }

  .network-config-tip {
    width: 100%;
    padding: 12px 0;
    font-size: 14px;
    color: #7F7F7F;
  }

  .network-config-form-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 12px;
    flex-wrap: wrap;

    .network-config-form {
      min-width: 550px;
      width: 550px;

      .allocate-radio-group, .node-type-radio-group {
        width: 100%;
        display: flex;
        margin-top: -5px;
      }

      .node-type-radio-group {
        margin-top: 0;
      }

      .ip-allocate-tip {
        text-align: justify;
        //word-break: break-all;
        margin-top: -18px;
        color: #E24949;
      }

      .multiple-allocate-tip {
        margin-top: 0;
      }
    }

    .multiple-form {
      min-width: 800px;
      width: 800px;
    }
  }

  .save-network-config-button {
    height: 35px;
    padding: 0 80px;
    margin-left: 150px;
    margin-top: 24px;
  }

  .skip-network-button {
    margin-left: 20px;
  }

}
</style>
