<template>
  <div class="layout-container">
    <div class="network-list-wrapper" v-loading="loading">
      <div class="network-title"><span class="tenant-title">{{ $t("message.initConfig.networkConfig") }}</span></div>
      <div class="action-wrapper">
        <!--        <el-select v-model="platformValue" class="search-selector" @change="getNetworkList">-->
        <!--          <el-option v-for="item in platformList" :key="item.value" :label="item.label" :value="item.value"/>-->
        <!--        </el-select>-->
        <!--        <el-select class="search-selector" v-model="searchValue" @change="getNetworkList">-->
        <!--          <el-option v-for="item in searchList" :key="item.value" :label="item.label" :value="item.value"/>-->
        <!--        </el-select>-->
        <!--        <el-input class="search-input" v-model="searchText" @input="getNetworkList" :suffix-icon="Search" clearable-->
        <!--                  :placeholder="searchValue===1?$t('message.resource.namePlaceholder'):$t('message.resource.addressPlaceholder')"/>-->
        <el-button type="primary" @click="getNetworkList">{{ $t('message.agent.refresh') }}</el-button>
      </div>
      <div class="network-list">
        <el-table :data="network"
                  border
                  stripe
                  :header-cell-style="{backgroundColor: '#ececec', height: '50px', color: '#262626',fontSize: '14px'}">
          <el-table-column
              :label="$t('message.initConfig.nodeName')"
              prop="nodeName"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              :label="$t('message.initConfig.intranetCard')"
              prop="innerName"
              show-overflow-tooltip>
          </el-table-column>
          <el-table-column
              :label="$t('message.initConfig.intranetIp')"
              prop="innerIp"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              :label="$t('message.initConfig.extranetCard')"
              prop="outerName"
              show-overflow-tooltip>
          </el-table-column>
          <el-table-column
              :label="$t('message.initConfig.extranetIp')"
              prop="outerIp"
              show-overflow-tooltip>
          </el-table-column>
          <el-table-column
              :label="$t('message.initConfig.extranetAllocate')"
              prop="outerAllocation"
              show-overflow-tooltip>
            <template #default="scope">
              {{ getAllocation(scope.row.outerAllocation) }}
            </template>
          </el-table-column>
          <el-table-column
              :label="$t('message.agent.operation')"
              show-overflow-tooltip>
            <template #default="scope">
              <el-button type="text" @click="editNetwork(scope)">
                {{ $t("message.initConfig.config") }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, onMounted, reactive, ref} from "vue";
import {useI18n} from 'vue-i18n';
import {ElMessage} from "element-plus";
import _ from "lodash";
import initService from "@/api/init/index"
import {useRouter} from "vue-router";

onMounted(() => {
  getNetworkList();
})
const router = useRouter();
const {t} = useI18n();
const loading = ref<boolean>(false);
const network = ref<any>([]);
const searchValue = ref<number>(1);
const searchText = ref<string>('');

const getNetworkList = () => {
  loading.value = true;
  initService.getNetworkList().then(res => {
    loading.value = false;
    if (res.data && res.data.length) {
      network.value = res.data;
    } else {
      network.value = [];
    }
  }).catch(() => {
    loading.value = false;
  })
}
const editNetwork = (scope: object) => {
  router.push({
    path: `/net/config/edit`,
    query: {
      nodeName: scope.row.nodeName
    },
  });
}

const getAllocation = (type: any) => {
  if (type) {
    return type === 'DHCP' ? 'DHCP' : t('message.initConfig.staticAllocation');
  } else {
    return '-';
  }
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
.network-list-wrapper {
  padding: 24px 24px 0 24px;

  .network-title {
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

  .network-list {
    padding: 20px 24px;
  }
}
</style>
