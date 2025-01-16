<template>
  <div class="layout-container">
    <div class="agent-management" v-loading="loading">
      <div class="agent-title-wrapper"><span class="agent-title">{{ $t("message.agent.title") }}</span></div>
      <div class="agent-tab-wrapper">
        <el-radio-group v-model="selectedTab" size="large" @change="changeNode">
          <el-radio-button v-for="(list,index) in agentList.list" :label="list.order" :key="list.order + list.name">
            {{ list.name }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="agent-detail">
        <div class="agent-node-info"><span>{{ $t("message.agent.nodeInfo") }}</span></div>
        <el-row class="agent-info-wrapper">
          <el-col :span="8" class="virtual-node-ip"><span class="node-ip">{{ $t("message.agent.nodeIp") }}: {{
              nodeIp
            }}</span></el-col>
          <el-col :span="8" class="virtual-node-ip" v-if="virtualIp"><span
              class="virtual-ip">{{ $t("message.agent.vip") }}: {{
              virtualIp
            }}</span></el-col>
        </el-row>
        <div class="agent-detail-title-wrapper">
          <span>{{ $t("message.agent.nodeComponent") }}</span>
          <el-icon @click="getDeployStatus" :title="$t('message.agent.refresh')"
                   style="font-size: 18px; cursor: pointer">
            <RefreshLeft/>
          </el-icon>
        </div>
        <el-table :data="nodeComponent.list"
                  border
                  stripe
                  class="agent-detail-table"
                  :header-cell-style="{backgroundColor: '#ececec', height: '50px'}"
        >
          <el-table-column
              :label="$t('message.agent.componentName')"
              prop="name"
              show-overflow-tooltip
          ></el-table-column>
          <el-table-column
              :label="$t('message.agent.status')"
              prop="detail"
              show-overflow-tooltip
          >
          </el-table-column>
          <el-table-column
              :label="$t('message.agent.operation')"
              show-overflow-tooltip
          >
            <template #default="scope">
              <el-button type="text" v-if="statusMap[scope.row.status] === 2"
                         @click="manageComponent(scope.row.name, 'startup')">{{
                  $t("message.agent.startup")
                }}
              </el-button>
              <el-button type="text" v-if="statusMap[scope.row.status] === 1"
                         @click="manageComponent(scope.row.name, 'restart')">
                {{ $t("message.agent.restart") }}
              </el-button>
              <el-button type="text" v-if="statusMap[scope.row.status] === 1"
                         @click="manageComponent(scope.row.name, 'shutdown')">{{
                  $t("message.agent.shutdown")
                }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {useI18n} from 'vue-i18n';
import deployService from "@/api/deploy/index";
import _ from "lodash";
import {RefreshLeft} from "@element-plus/icons";

const {t} = useI18n();
const selectedTab = ref(0);
const loading = ref<boolean>(false);
const nodeIp = ref<string>("");
const virtualIp = ref<string>("");
const statusMap = {
  startup: 1,
  shutdown: 2,
  unknown: 3
};
onMounted(() => {
  getDeployStatus();
})
const getDeployStatus = () => {
  loading.value = true;
  deployService.getDeployStatus().then((res) => {
    if (res.data && res.data.length) {
      agentList.list = res.data.map((list: any, index: number) => {
        return {
          name: list.isMaster ? t('message.agent.masterNode') : t('message.agent.backupNode', {id: index + 1}),
          order: index,
          ...list
        }
      });
      nodeComponent.list = agentList.list[0].components || [];
      nodeIp.value = agentList.list[0].ip;
      virtualIp.value = agentList.list[0].vip;
      selectedTab.value = agentList.list[0].order || 0;
    }
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  })
}
const agentList = reactive<any>({
  list: []
});
const nodeComponent = reactive({
  list: []
});
const changeNode = (val: any) => {
  const index = _.findIndex(agentList.list, (list: any) => list.order === val);
  if (index !== -1) {
    nodeComponent.list = agentList.list[index].components || [];
    selectedTab.value = val;
    nodeIp.value = agentList.list[index].ip;
    virtualIp.value = agentList.list[index].vip;
  } else {
    nodeComponent.list = [];
  }
}
const manageComponent = (name: string, operate: string) => {
  loading.value = true;
  const payload = {
    name,
    ip: nodeIp.value,
    operate
  }
  deployService.manageComponent(payload).then(() => {
    getDeployStatus();
  }).catch(() => {
    loading.value = false;
  })
}
</script>

<style lang="scss" scoped>
.agent-management {
  padding: 24px 24px 0 24px;

  .agent-title-wrapper {
    display: flex;
    font-weight: 700;
    font-size: 24px;
    margin-top: 12px;
    margin-left: 24px;
    color: black;
  }

  .agent-tab-wrapper {
    display: flex;
    margin-top: 24px;
    margin-left: 24px;

    :deep(.el-radio-button--large .el-radio-button__inner) {
      min-width: 160px;
    }
  }

  .agent-detail {
    padding: 20px 24px;

    .virtual-node-ip {
      display: flex;
      font-size: 14px;

      .node-ip, .virtual-ip {
        padding: 24px;
      }
    }

    .agent-detail-title-wrapper, .agent-node-info {
      display: flex;
      margin-top: 12px;
      color: black;
      font-weight: 700;
      font-size: 14px;
    }
    .agent-detail-title-wrapper {
      justify-content: space-between;
      align-items: center;
    }

    .agent-detail-table {
      margin-top: 12px;
    }
  }
}
</style>