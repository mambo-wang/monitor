<template>
  <div class="layout-container">
    <div class="resource-list-wrapper" v-loading="loading">
      <div class="resource-title">
        <span class="tenant-title">{{
          $t('message.routerManagement.routerList')
        }}</span>
      </div>
      <!-- 头部按钮 -->
      <div class="button-wrapper">
        <div>
          <el-button type="primary" @click="openAuthRole()">{{
            $t('message.routerManagement.add')
          }}</el-button>
          <el-button
            type="primary"
            plain
            :disabled="!multipleSelection.length"
            @click="deleteSelected"
            >{{ $t('message.routerManagement.delete') }}</el-button
          >
        </div>
        <div class="action-wrapper">
          <el-select
            class="search-selector"
            v-model="searchValue"
            @change="getRouterListData"
          >
            <el-option
              v-for="item in searchList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-input
            class="search-input"
            v-model="searchText"
            @input="filterData"
            :suffix-icon="Search"
            clearable
          />
          <el-button type="primary" @click="getRouterListData">{{
            $t('message.agent.refresh')
          }}</el-button>
        </div>
      </div>
      <!-- list -->
      <div class="resource-list">
        <em-table
          :data="routerList.list"
          border
          stripe
          row-key="id"
          class="agent-detail-table"
          :showPage="false"
          :header-cell-style="{ backgroundColor: '#ececec', height: '50px' }"
          @selection-change="changeSelected"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column
            :label="$t('message.routerManagement.aimAddress')"
            prop="targetIp"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column
            :label="$t('message.routerManagement.subnetMask')"
            prop="targetMask"
            show-overflow-tooltip
          >
          </el-table-column>
          <el-table-column
            :label="$t('message.routerManagement.nextAddress')"
            prop="via"
            show-overflow-tooltip
          ></el-table-column>
          <!-- <el-table-column
            :label="$t('message.routerManagement.outLetInterCard')"
            prop="dev"
            show-overflow-tooltip
          >
            <template #default="scope">
              {{ getDev(scope.row.dev || '') }}
            </template>
          </el-table-column> -->
          <el-table-column
            :label="$t('message.routerManagement.applyCollection')"
            prop="watchers"
            show-overflow-tooltip
          >
          </el-table-column>
          <el-table-column
            :label="$t('message.routerManagement.description')"
            prop="desc"
            show-overflow-tooltip
          >
          </el-table-column>
          <el-table-column
            :label="$t('message.agent.operation')"
            show-overflow-tooltip
          >
            <template #default="scope">
              <el-button type="text" @click="editRouter(scope.row)">
                {{ $t('message.routerManagement.edit') }}
              </el-button>
              <el-button type="text" @click="testRouter(scope.row)">
                {{ $t('message.routerManagement.test') }}
              </el-button>
              <el-button type="text" @click="deleteRouter(scope.row)">
                {{ $t('message.routerManagement.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </em-table>
      </div>
    </div>
  </div>
  <router-add
    v-model="authRoleVisible"
    v-if="authRoleVisible"
    @confirmAuthRole="confirmAuthRole"
    @closeAuthRole="closeAuthRole"
    :rowData="rowData"
    :fromWhere="fromWhere"
  />
  <router-test
    v-model="testVisible"
    v-if="testVisible"
    @confirmTest="confirmTest"
    @closeTest="closeTest"
    :rowData="rowData"
  />
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { ElMessageBox } from 'element-plus'
import _ from 'lodash'
import emTable from '@/components/table/index.vue'
import { Search } from '@element-plus/icons-vue'
import initService from '@/api/init/index'
import routerAdd from './components/router-add.vue'
import routerTest from './components/router-test.vue'

onMounted(() => {
  getRouterListData()
})

const { t } = useI18n()
const loading = ref<boolean>(false)
// const pageParams = reactive({
//   page: { index: 1, size: 10, total: 0 },
// })
const searchList = [
  {
    value: 1,
    label: t('message.routerManagement.aimAddress'),
  },
  // {
  //   value: 2,
  //   label: t('message.resource.address'),
  // },
]
const searchValue = ref<number>(1)
const searchText = ref<string>('')
let fromWhere = ref<string>('')
const authRoleVisible = ref<boolean>(false)
const testVisible = ref<boolean>(false)
const rowData: { [key: string]: any } = reactive({
  detail: null,
})
let routerList: { [key: string]: any } = reactive({
  list: [],
})
const multipleSelection = ref<any>([])
let selectedIds = ref<any>([])

const filterData = () => {
  // this.selectedRowKeys = [];
  console.log(searchText.value)
  if (searchText.value) {
    routerList.list = _.filter(routerList.list, (item) =>
      (item.targetIp || '').includes(searchText.value)
    )
  } else {
    getRouterListData()
  }
}

const getRouterListData = () => {
  loading.value = true
  // const params: { [key: string]: any } = {
  //   page: pageParams.page.index - 1,
  //   size: pageParams.page.size,
  // }
  const params: { [key: string]: any } = {}
  if (searchValue.value === 1) {
    params.targetIp = searchText.value
  }
  // else {
  //   params.ipAddress = searchText.value
  // }
  initService
    .getRouterList(params)
    .then((res: any) => {
      loading.value = false
      console.log(res)
      if (res.data && res.data.length) {
        routerList.list = res.data
        // pageParams.page.total = res.data.length
      } else {
        // routerList.list = []
        routerList.list = []
        // pageParams.page.total = 0
      }
    })
    .catch(() => {
      loading.value = false
    })
}
// const changePage = () => {
//   console.log(pageParams.page)
//   console.log('changePage')
// }
const changeSelected = (val: any) => {
  multipleSelection.value = val
  const selectedArr = val.map((item: any) => {
    let arr: any[] = []
    arr.push(item.id)
    return arr
  })
  selectedIds = selectedArr.flat()
}
const openAuthRole = () => {
  authRoleVisible.value = true
  fromWhere.value = 'add'
}
const getDev = (dev: string) => {
  switch (dev) {
    case 'inner':
      return t('message.routerManagement.innerCard')
    case 'outer':
      return t('message.routerManagement.outerCard')
    default:
      return ''
  }
}
const editRouter = (row: object) => {
  rowData.detail = row
  authRoleVisible.value = true
  fromWhere.value = 'edit'
}
const deleteRouter = (row: object) => {
  ElMessageBox.confirm(t('message.routerManagement.deleteTip')).then((res) => {
    loading.value = true
    initService
      .deleteRouter([row.id])
      .then((res: any) => {
        console.log(res)
        loading.value = false
        messageTip(t('message.routerManagement.deleteSuccess'), 'success')
        getRouterListData()
      })
      .catch((res: any) => {
        loading.value = false
      })
  })
}
const deleteSelected = () => {
  console.log(selectedIds)
  ElMessageBox.confirm(t('message.routerManagement.deleteSelected')).then(
    (res) => {
      loading.value = true
      initService
        .deleteRouter(selectedIds)
        .then((res: any) => {
          console.log(res)
          loading.value = false
          messageTip(t('message.routerManagement.deleteSuccess'), 'success')
          getRouterListData()
        })
        .catch((res: any) => {
          loading.value = false
        })
    }
  )
}
const testRouter = (row: object) => {
  console.log(row)
  rowData.detail = row
  testVisible.value = true
}
const confirmAuthRole = () => {
  closeAuthRole()
  getRouterListData()
}
const closeAuthRole = () => {
  authRoleVisible.value = false
}

const confirmTest = () => {
  closeTest()
  getRouterListData()
}
const closeTest = () => {
  testVisible.value = false
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
.resource-list-wrapper {
  padding: 24px 24px 0 24px;

  .resource-title {
    display: flex;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }
  .button-wrapper {
    display: flex;
    justify-content: space-between;
    padding-left: 25px;
    margin-top: 10px;
  }

  .action-wrapper {
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .search-selector,
    .search-input {
      width: 155px;
      margin-right: 12px;
    }

    .search-input {
      width: 200px;
    }
  }
}
:deep(.el-message-box__header) {
  padding-bottom: 24px !important;
}
</style>
