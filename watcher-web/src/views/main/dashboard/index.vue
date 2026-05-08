<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="header-left">
        <el-select v-model="selectedPlatform" @change="onPlatformChange" class="platform-selector">
          <el-option
            v-for="item in platformList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </div>
      <div class="header-right">
        <time-range-selector @change="onTimeRangeChange" />
      </div>
    </div>
    <div class="dashboard-content">
      <div class="dashboard-sidebar">
        <topology-tree
          :platform="selectedPlatform"
          :time-range="timeRange"
          @select="onNodeSelect"
        />
      </div>
      <div class="dashboard-main">
        <div class="panel-container" v-loading="loading">
          <template v-if="selectedNode">
            <cluster-panel
              v-if="selectedNode.type === 'cluster'"
              :resource-id="selectedNode.id"
              :platform="selectedPlatform"
              :time-range="timeRange"
            />
            <host-panel
              v-else-if="selectedNode.type === 'host'"
              :resource-id="selectedNode.id"
              :platform="selectedPlatform"
              :time-range="timeRange"
            />
            <vm-panel
              v-else-if="selectedNode.type === 'vm'"
              :resource-id="selectedNode.id"
              :platform="selectedPlatform"
              :time-range="timeRange"
            />
            <desktop-pool-panel
              v-else-if="selectedNode.type === 'desktop_pool'"
              :resource-id="selectedNode.id"
              :platform="selectedPlatform"
              :time-range="timeRange"
            />
            <div v-else class="empty-state">
              <el-empty :description="emptyDescription" />
            </div>
          </template>
          <div v-else class="empty-state">
            <el-empty :description="emptyDescription" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import topologyTree from "./components/topology-tree.vue";
import timeRangeSelector from "./components/time-range-selector.vue";
import clusterPanel from "./panels/cluster-panel.vue";
import hostPanel from "./panels/host-panel.vue";
import vmPanel from "./panels/vm-panel.vue";
import desktopPoolPanel from "./panels/desktop-pool-panel.vue";
import type { ResourceTreeNode } from "@/api/dashboard";

const { t } = useI18n();

const emptyDescription = computed(() => t("message.dashboard.selectNode"));

const loading = ref(false);
const selectedPlatform = ref("all");
const selectedNode = ref<ResourceTreeNode | null>(null);
const timeRange = reactive({
  startTime: "",
  endTime: "",
  refreshInterval: 0
});

const platformList = [
  { value: "all", label: t("message.dashboard.allPlatforms") },
  { value: "cas", label: "CAS" },
  { value: "workspace", label: "Workspace" },
  { value: "uis", label: "UIS" },
  { value: "onestor", label: "ONEStor" }
];

const onPlatformChange = () => {
  selectedNode.value = null;
};

const onTimeRangeChange = (range: typeof timeRange) => {
  timeRange.startTime = range.startTime;
  timeRange.endTime = range.endTime;
  timeRange.refreshInterval = range.refreshInterval;
};

const onNodeSelect = (node: ResourceTreeNode) => {
  selectedNode.value = node;
};

onMounted(() => {
  const now = new Date();
  const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
  timeRange.startTime = oneHourAgo.toISOString();
  timeRange.endTime = now.toISOString();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--system-container-background);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;

  .header-left {
    display: flex;
    align-items: center;

    .platform-selector {
      width: 150px;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
  }
}

.dashboard-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.dashboard-sidebar {
  width: 280px;
  min-width: 280px;
  background-color: #fff;
  border-right: 1px solid #ebeef5;
  overflow-y: auto;
}

.dashboard-main {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.panel-container {
  min-height: 100%;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}
</style>
