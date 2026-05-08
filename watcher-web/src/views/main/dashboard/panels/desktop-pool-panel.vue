<template>
  <div class="desktop-pool-panel">
    <div class="panel-header">
      <h3>{{ panelTitle }}</h3>
    </div>

    <div class="stat-cards">
      <stat-card
        :title="statsLabels.totalDesktops"
        :value="stats.totalDesktops"
        color="#409eff"
      />
      <stat-card
        :title="statsLabels.activeUsers"
        :value="stats.activeUsers"
        color="#67c23a"
      />
      <stat-card
        :title="statsLabels.availableDesktops"
        :value="stats.availableDesktops"
        color="#e6a23c"
      />
      <stat-card
        :title="statsLabels.allocatedDesktops"
        :value="stats.allocatedDesktops"
        color="#f56c6c"
      />
    </div>

    <div class="table-section">
      <h4>{{ tableTitle }}</h4>
      <el-table :data="desktopList" border stripe max-height="300">
        <el-table-column prop="name" :label="columns.name" />
        <el-table-column prop="status" :label="columns.status">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="user" :label="columns.user" />
        <el-table-column prop="ip" :label="columns.ip" />
      </el-table>
    </div>

    <div class="terminal-section" v-if="terminalList.length">
      <h4>{{ terminalTitle }}</h4>
      <el-table :data="terminalList" border stripe max-height="300">
        <el-table-column prop="name" :label="terminalColumns.name" />
        <el-table-column prop="type" :label="terminalColumns.type" />
        <el-table-column prop="ip" :label="terminalColumns.ip" />
        <el-table-column prop="status" :label="terminalColumns.status">
          <template #default="{ row }">
            <el-tag :type="row.status === 'Online' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, watch } from "vue";
import { useI18n } from "vue-i18n";
import statCard from "../components/stat-card.vue";
import dashboardApi from "@/api/dashboard";

interface Props {
  resourceId: string;
  platform?: string;
  timeRange?: {
    startTime: string;
    endTime: string;
    refreshInterval: number;
  };
}

const props = withDefaults(defineProps<Props>(), {
  platform: "all",
  timeRange: () => ({
    startTime: "",
    endTime: "",
    refreshInterval: 0
  })
});

const { t } = useI18n();

const panelTitle = computed(() => t("message.dashboard.desktopPoolPanel"));
const tableTitle = computed(() => t("message.dashboard.desktopList"));
const terminalTitle = computed(() => t("message.dashboard.terminalInfo"));

const statsLabels = computed(() => ({
  totalDesktops: t("message.dashboard.totalDesktops"),
  activeUsers: t("message.dashboard.activeUsers"),
  availableDesktops: t("message.dashboard.availableDesktops"),
  allocatedDesktops: t("message.dashboard.allocatedDesktops")
}));

const columns = computed(() => ({
  name: t("message.dashboard.desktopName"),
  status: t("message.dashboard.desktopStatus"),
  user: t("message.dashboard.user"),
  ip: t("message.dashboard.ip")
}));

const terminalColumns = computed(() => ({
  name: t("message.dashboard.terminalName"),
  type: t("message.dashboard.terminalType"),
  ip: t("message.dashboard.ip"),
  status: t("message.dashboard.terminalStatus")
}));

const stats = reactive({
  totalDesktops: 0,
  activeUsers: 0,
  availableDesktops: 0,
  allocatedDesktops: 0
});

const desktopList = ref<any[]>([]);
const terminalList = ref<any[]>([]);

const getStatusType = (status: string) => {
  switch (status) {
    case "Running":
      return "success";
    case "Stopped":
      return "info";
    case "Error":
      return "danger";
    default:
      return "";
  }
};

const fetchDesktopPoolStats = async () => {
  try {
    const res: any = await dashboardApi.getMetricData({
      resourceId: props.resourceId,
      metricType: "desktop_pool_basic"
    });
    if (res.data) {
      const data = res.data;
      stats.totalDesktops = data.totalDesktops || data.desktop_pool_number || 0;
      stats.activeUsers = data.activeUsers || data.resource_user_number || 0;
      stats.availableDesktops = data.availableDesktops || 0;
      stats.allocatedDesktops = data.allocatedDesktops || 0;
    }
  } catch (error) {
    console.error("Failed to fetch desktop pool stats:", error);
  }
};

const fetchDesktopList = async () => {
  try {
    const res: any = await dashboardApi.getResourceTree();
    if (res.data) {
      const findDesktops = (items: any[]): any[] => {
        let desktops: any[] = [];
        for (const item of items) {
          if (item.type === "vm" || item.resourceType === "vm") {
            desktops.push({
              name: item.resourceName || item.label,
              status: item.status === "healthy" ? "Running" : "Stopped",
              user: item.user || "-",
              ip: item.ip || item.ipAddress || "-"
            });
          }
          if (item.children?.length) {
            desktops = desktops.concat(findDesktops(item.children));
          }
        }
        return desktops;
      };
      desktopList.value = findDesktops(res.data);
    }
  } catch (error) {
    console.error("Failed to fetch desktop list:", error);
  }
};

watch(
  () => props.resourceId,
  () => {
    fetchDesktopPoolStats();
    fetchDesktopList();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.desktop-pool-panel {
  .panel-header {
    margin-bottom: 16px;

    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin: 0;
    }
  }
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.table-section,
.terminal-section {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 24px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 16px 0;
  }
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
