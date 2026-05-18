<template>
  <div class="layout-container">
    <div class="resource-list-wrapper" v-loading="loading">
      <div class="resource-title"><span class="tenant-title">{{ $t("message.resource.resourceList") }}</span></div>
      <div class="action-wrapper">
        <el-button type="primary" @click="goToAddResource">
          {{ $t("message.resource.addResource") }}
        </el-button>
        <el-select v-model="platformValue" class="search-selector" @change="getResourceList">
          <el-option v-for="item in platformList" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-select class="search-selector" v-model="searchValue" @change="getResourceList">
          <el-option v-for="item in searchList" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
        <el-input class="search-input" v-model="searchText" @input="getResourceList" :suffix-icon="Search" clearable
                  :placeholder="searchValue===1?$t('message.resource.namePlaceholder'):$t('message.resource.addressPlaceholder')"/>
        <el-button type="primary" @click="getResourceList">{{ $t('message.agent.refresh') }}</el-button>
      </div>
      <div class="resource-list">
        <em-table :data="resource.list"
                  v-model:page="pageParams.page"
                  @getTableData="getResourceList"
                  border
                  stripe
                  class="agent-detail-table"
                  :header-cell-style="{backgroundColor: '#ececec', height: '50px'}">
          <el-table-column
              :label="$t('message.resource.name')"
              prop="resourceName"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              :label="$t('message.resource.platform')"
              prop="platform"
              show-overflow-tooltip
          >
            <template #default="scope">
              {{ getPlatform(scope.row.platform || "") }}
            </template>
          </el-table-column>
          <el-table-column
              :label="$t('message.resource.address')"
              prop="ipAddress"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              :label="$t('message.resource.sshPermission')"
              prop="remote"
              show-overflow-tooltip
          >
            <template #default="scope">
              {{ sshPermissionMap[scope.row.remote] }}
            </template>
          </el-table-column>
          <el-table-column
              :label="$t('message.resource.sshCloseTime')"
              prop="endTimeStr"
              show-overflow-tooltip
          >
          </el-table-column>
          <el-table-column
              :label="$t('message.resource.resourceStatus')"
              prop="usable"
              show-overflow-tooltip
          >
            <template #default="scope">
              <span v-if="scope.row.usable===1">{{ $t("message.resource.normal") }}</span>
              <span v-else style="color: red;">{{ $t("message.resource.abnormal") }}</span>
            </template>
          </el-table-column>
          <el-table-column
              :label="$t('message.agent.operation')"
              show-overflow-tooltip
          >
            <template #default="scope">
              <el-button type="text" @click="goToEditResource(scope.row)">
                {{ $t("message.resource.edit") }}
              </el-button>
              <el-button type="text" @click="goToMetricDetail(scope.row)">
                {{ $t("message.resource.viewMetric") }}
              </el-button>
              <el-button type="text" @click="openAuthRole(scope.row)" :disabled="!scope.row.usable">
                {{ $t("message.resource.settingParams") }}
              </el-button>
            </template>
          </el-table-column>
        </em-table>
      </div>
    </div>
  </div>
  <auth-role v-model="authRoleVisible" v-if="authRoleVisible" @confirmAuthRole="confirmAuthRole"
             @closeAuthRole="closeAuthRole" :resource="selectedResource"/>
  <setting-ssh v-model="settingSshVisible" v-if="settingSshVisible" @confirmSettingSsh="confirmSettingSsh"
               @closeSettingSsh="closeSettingSsh" :resource="selectedResource"/>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from "vue";
import {useI18n} from 'vue-i18n';
import {useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import _ from "lodash";
import emTable from "@/components/table/index.vue";
import {Search} from '@element-plus/icons-vue';
import resourceService from "@/api/resource/index";
import authRole from "./components/auth-role.vue";
import settingSsh from "./components/setting-ssh.vue";

onMounted(() => {
  getResourceList();
})

const router = useRouter();
const {t} = useI18n();

const goToAddResource = () => {
  router.push("/resource/add");
};

const goToEditResource = (row: any) => {
  router.push({
    path: "/resource/edit",
    query: { id: row.id }
  });
};

const goToMetricDetail = (row: any) => {
  router.push({
    path: "/metric/detail",
    query: { id: row.id, name: row.resourceName }
  });
};
const loading = ref<boolean>(false);
const resource = reactive({
  list: []
});
const pageParams = reactive({
  page: {index: 1, size: 10, total: 0},
});
const platformList = [
  {
    value: 0,
    label: t("message.resource.allPlatform")
  }, {
    value: 1,
    label: 'Workspace'
  }, {
    value: 2,
    label: 'UIS'
  }, {
    value: 3,
    label: 'CAS'
  },
];
const platformValue = ref<number>(0);
const searchList = [
  {
    value: 1,
    label: t("message.resource.name")
  }, {
    value: 2,
    label: t("message.resource.address")
  },
];
const searchValue = ref<number>(1);
const searchText = ref<string>('');
const authRoleVisible = ref<boolean>(false);
const settingSshVisible = ref<boolean>(false);
const sshPermissionMap = {
  0: t("message.resource.disable"),
  1: t("message.resource.enable"),
};
const selectedResource: { [key: string]: any } = reactive({
  detail: {}
})

const getResourceList = () => {
  loading.value = true;
  const params: { [key: string]: any } = {
    page: pageParams.page.index - 1,
    size: pageParams.page.size,
  };
  if (platformValue.value !== 0) {
    params.platform = (platformList.find(item => item.value === platformValue.value) as { [key: string]: any }).label.toLocaleLowerCase();
  }
  if (searchValue.value === 1) {
    params.resourceName = searchText.value;
  } else {
    params.ipAddress = searchText.value;
  }
  resourceService.getResourceList(params).then(res => {
    loading.value = false;
    if (res.data && res.data.length) {
      resource.list = res.data;
      pageParams.page.total = res.totalLength;
    } else {
      resource.list = [];
      pageParams.page.total = 0;
    }
  }).catch(() => {
    loading.value = false;
  })
}
const getPlatform = (platform: string) => {
  switch (platform) {
    case 'workspace':
      return "Workspace";
    default:
      return platform.toUpperCase();
  }
}
const openAuthRole = (resource: object) => {
  selectedResource.detail = resource;
  authRoleVisible.value = true;
}
const confirmAuthRole = (payload: object) => {
  selectedResource.detail = payload;
  closeAuthRole();
  openSettingSsh();
}
const closeAuthRole = () => {
  authRoleVisible.value = false;
}

const openSettingSsh = () => {
  settingSshVisible.value = true;
}
const confirmSettingSsh = () => {
  closeSettingSsh();
  getResourceList();
}
const closeSettingSsh = () => {
  settingSshVisible.value = false;
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

  .action-wrapper {
    padding: 0 24px;
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .search-selector, .search-input {
      width: 155px;
      margin-right: 12px;
    }

    .search-input {
      width: 200px;
    }
  }
}
</style>
