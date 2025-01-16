<template>
  <el-dialog
    width="500px"
    :close-on-click-modal="false"
    class="auth-role-container"
  >
    <template #title>
      <div class="auth-role-title">
        {{ $t('message.routerManagement.test') }}
      </div>
    </template>
    <el-form
      :model="formData"
      style="padding: 20px 20px 0 20px"
      v-loading="loading"
      label-width="150px"
      :rules="validateRule"
    >
      <el-form-item
        prop="aimIpAddress"
        :label="$t('message.routerManagement.aimIpAddress')"
      >
        <el-input v-model="formData.aimIpAddress"></el-input>
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
          >{{ $t('message.tenant.confirm') }}
        </el-button>
        <el-button
          @click="cancel"
          style="width: 80px; height: 35px; margin: 0 20px"
          >{{ $t('message.tenant.cancel') }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, toRefs } from 'vue'
import { useI18n } from 'vue-i18n'
import _ from 'lodash'
import util from '@/utils/system/common-util'
import initService from '@/api/init/index'
import { ElMessage } from 'element-plus'

const props = defineProps({
  rowData: {
    type: Object,
    required: true,
    default: {},
  },
})

const { t } = useI18n()
const testFormRef = ref()
const loading = ref<boolean>(false)
const disabled = computed(() => {
  return !formData.aimIpAddress
})

const emit = defineEmits(['closeTest', 'confirmTest'])
let formData = reactive({
  aimIpAddress: '',
})
const validateRule = reactive({
  aimIpAddress: [
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
})
const confirm = () => {
  loading.value = true
  const data = {
    target: formData.aimIpAddress,
    watchers: props.rowData.detail.watchers,
  }
  initService
    .testRouterData(data)
    .then((res) => {
      console.log(res)
      loading.value = false
      messageTip(t('message.routerManagement.testSuccess'), 'success')
      emit('confirmTest', data)
    })
    .catch((res) => {
      loading.value = false
    })
}
const cancel = () => {
  emit('closeTest')
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
