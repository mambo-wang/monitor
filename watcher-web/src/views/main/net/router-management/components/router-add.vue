<template>
  <el-dialog
    width="500px"
    :close-on-click-modal="false"
    class="auth-role-container"
  >
    <template #title>
      <div class="auth-role-title">
        {{
          props.fromWhere === 'add'
            ? $t('message.routerManagement.addRouter')
            : $t('message.routerManagement.editRouter')
        }}
      </div>
    </template>
    <el-form
      :model="formData.list"
      :rules="validateRule"
      ref="confirmActionFormRef"
      style="padding: 20px 20px 0 20px"
      v-loading="loading"
      label-width="100px"
    >
      <!-- <el-form-item
        prop="dev"
        :label="$t('message.routerManagement.outLetInterCard')"
      >
        <el-select
          v-model="formData.list.dev"
          @change="setButtonStatus"
          style="width: 320px"
        >
          <el-option
            v-for="item in outLetInterCardOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item> -->
      <el-form-item
        prop="targetIp"
        :label="$t('message.routerManagement.aimAddress')"
      >
        <el-input v-model="formData.list.targetIp" @input="setButtonStatus">
        </el-input>
      </el-form-item>
      <el-form-item
        prop="targetMask"
        :label="$t('message.routerManagement.subnetMask')"
      >
        <el-input v-model="formData.list.targetMask" @input="setButtonStatus">
        </el-input>
      </el-form-item>
      <el-form-item
        prop="via"
        :label="$t('message.routerManagement.nextAddress')"
      >
        <el-input v-model="formData.list.via" @input="setButtonStatus">
        </el-input>
      </el-form-item>
      <el-form-item
        prop="watchers"
        :label="$t('message.routerManagement.applyCollection')"
      >
        <el-select
          v-model="formData.list.watchers"
          multiple
          style="width: 320px"
          @change="changeApplyCollection"
        >
          <el-option
            v-for="item in applyCollectionOptions.list"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        prop="desc"
        :label="$t('message.routerManagement.description')"
      >
        <el-input
          v-model="formData.list.desc"
          maxlength="256"
          show-word-limit
          type="textarea"
          @input="setButtonStatus"
        >
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div>
        <el-button
          type="primary"
          @click="test"
          :disabled="disabled"
          :loading="loading"
          style="width: 80px; height: 35px"
          >{{ $t('message.routerManagement.test') }}
        </el-button>
        <el-button
          type="primary"
          @click="confirm"
          :disabled="disabled"
          :loading="loading"
          style="width: 80px; height: 35px"
          >{{ $t('message.tenant.confirm') }}
        </el-button>
        <el-button
          @click="cancel"
          style="width: 80px; height: 35px; margin: 0 20px"
          >{{ $t('message.tenant.cancel') }}
        </el-button>
      </div>
    </template>
    <!-- <router-test
      v-model="testVisible"
      v-if="testVisible"
      @confirmTest="confirmTest"
      @closeTest="closeTest"
      :rowData="rowData"
    /> -->
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, toRefs } from 'vue'
import { useI18n } from 'vue-i18n'
import _ from 'lodash'
import util from '@/utils/system/common-util'
import initService from '@/api/init/index'
import { ElMessage } from 'element-plus'
// import routerTest from './router-test.vue'

const props = defineProps({
  rowData: {
    type: Object,
    required: true,
    default: {},
  },
  fromWhere: {
    type: String,
  },
})

const { t } = useI18n()
const authRoleFormRef = ref()
const loading = ref<boolean>(false)
const buttonLoading = ref<boolean>(false)
const testVisible = ref<boolean>(false)
const disabled = ref<boolean>(true)
const confirmActionFormRef = ref()
const emit = defineEmits(['closeAuthRole', 'confirmAuthRole'])
let applyCollectionOptions = reactive({ list: [] })
let formData = reactive({
  list: {
    // dev: null,
    targetIp: null,
    targetMask: null,
    via: null,
    watchers: [],
    desc: '',
  },
})
onMounted(() => {
  getCollectionIp()
  initFormData()
})
const initFormData = () => {
  if (props.fromWhere === 'add') {
    formData.list = {
      // dev: null,
      targetIp: null,
      targetMask: null,
      via: null,
      watchers: [],
      desc: '',
    }
  } else {
    formData.list = JSON.parse(JSON.stringify(props.rowData.detail))
  }
}
const checkMask = (rule: any, value: any, callback: any) => {
  if (!value || util.maskList.indexOf(value) !== -1) {
    return callback()
  }
  return callback(new Error(t('message.initConfig.maskError')))
}
const outLetInterCardOptions = reactive([
  {
    label: t('message.routerManagement.innerCard'),
    value: 'inner',
  },
  {
    label: t('message.routerManagement.outerCard'),
    value: 'outer',
  },
])
const rowData: { [key: string]: any } = reactive({
  detail: null,
})
const validateRule = reactive({
  dev: [
    {
      required: true,
      message: t('message.tenant.required'),
      trigger: 'change',
    },
  ],
  targetIp: [
    {
      required: true,
      message: t('message.tenant.required'),
      trigger: 'change',
    },
    {
      pattern:
        /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/,
      message: t('message.routerManagement.invalidIPAddress'),
      trigger: 'change',
    },
  ],
  targetMask: [
    {
      required: true,
      message: t('message.tenant.required'),
      trigger: 'change',
    },
    { validator: checkMask, trigger: 'change' },
  ],
  via: [
    {
      required: true,
      message: t('message.tenant.required'),
      trigger: 'change',
    },
    {
      pattern:
        /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/,
      message: t('message.routerManagement.invalidIPAddress'),
      trigger: 'change',
    },
  ],
  watchers: [
    {
      required: true,
      message: t('message.tenant.required'),
      trigger: 'change',
    },
  ],
})
const formStatus = computed(() =>
  _.every(
    formData.list,
    (item) => item !== null && item !== undefined && item !== []
  )
)
const getCollectionIp = () => {
  loading.value = true
  initService
    .getCollectionIp()
    .then((res) => {
      if (res.data && res.data.length) {
        // console.log(res)
        applyCollectionOptions.list = res.data.map(
          (list: any, index: number) => {
            return {
              value: list.ip,
              label: list.ip,
            }
          }
        )
        if (props.fromWhere === 'add') {
          formData.list.watchers = res.data.map((item: any) => item.ip)
        }
      }
      loading.value = false
    })
    .catch(() => {
      loading.value = false
    })
}
const changeApplyCollection = (val: any) => {
  if (formData.list.watchers === []) {
    formData.list.watchers === null
  }
  setButtonStatus()
}
const setButtonStatus = () => {
  if (confirmActionFormRef.value) {
    confirmActionFormRef.value.validate((valid: any) => {
      console.log(valid)
      disabled.value = !valid
    })
  }
}
const test = () => {
  loading.value = true
  rowData.detail = JSON.parse(JSON.stringify(formData.list))
  // testVisible.value = true
  const payload: Object = JSON.parse(JSON.stringify(formData.list))
    if (props.fromWhere === 'add') {
      initService
      .addTest(payload)
      .then((res: any) => {
        loading.value = false
        messageTip(t('message.routerManagement.testSuccess'), 'success')
      })
      .catch((res: any) => {
        loading.value = false
      })
    }else{
      initService
      .editTest(payload)
      .then((res: any) => {
        loading.value = false
        messageTip(t('message.routerManagement.testSuccess'), 'success')
      })
      .catch((res: any) => {
        loading.value = false
      })
    }
   
}
const confirmTest = () => {
  closeTest()
}
const closeTest = () => {
  testVisible.value = false
}
const confirm = () => {
  loading.value = true
  if (props.fromWhere === 'add') {
    const payload: Object = JSON.parse(JSON.stringify(formData.list))
    initService
      .addRouter(payload)
      .then((res: any) => {
        loading.value = false
        messageTip(t('message.routerManagement.addSuccess'), 'success')
        emit('confirmAuthRole')
      })
      .catch((res: any) => {
        loading.value = false
      })
  } else {
    const payload: Object = JSON.parse(JSON.stringify(formData.list))
    console.log(payload)
    initService
      .editRouter(payload)
      .then((res: any) => {
        loading.value = false
        emit('confirmAuthRole')
      })
      .catch((res: any) => {
        loading.value = false
      })
  }
}
const cancel = () => {
  emit('closeAuthRole')
}
const messageTip = (message: string, type: any, duration = 3000) => {
  ElMessage({
    message,
    type,
    duration,
    showClose: true,
    grouping: true,
  })
}
</script>

<style lang="scss" scoped>
.auth-role-title {
  font-size: 16px;
  padding: 10px;
  display: flex;
  border-bottom: 1px solid #d8dce5;
}
</style>
