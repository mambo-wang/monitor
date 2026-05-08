<template>
  <div class="vm-panel">
    <div class="panel-header">
      <h3>{{ panelTitle }}</h3>
    </div>

    <div class="stat-cards">
      <stat-card
        :title="statsLabels.cpuUsage"
        :value="stats.cpuUsage"
        unit="%"
        color="#409eff"
        :precision="1"
      />
      <stat-card
        :title="statsLabels.connectionCount"
        :value="stats.connectionCount"
        color="#67c23a"
      />
      <stat-card
        :title="statsLabels.memoryUsage"
        :value="stats.memoryUsage"
        unit="%"
        color="#e6a23c"
        :precision="1"
      />
      <stat-card
        :title="statsLabels.diskUsage"
        :value="stats.diskUsage"
        unit="%"
        color="#f56c6c"
        :precision="1"
      />
    </div>

    <div class="charts-grid">
      <metric-chart
        :title="chartLabels.cpuDetail"
        :resource-id="resourceId"
        metric-type="cpu_usage_detail"
        :time-range="timeRange"
        height="250px"
      />
      <metric-chart
        :title="chartLabels.diskThroughput"
        :resource-id="resourceId"
        metric-type="disk_throughput"
        :time-range="timeRange"
        height="250px"
      />
      <metric-chart
        :title="chartLabels.diskRate"
        :resource-id="resourceId"
        metric-type="disk_rate"
        :time-range="timeRange"
        height="250px"
      />
      <metric-chart
        :title="chartLabels.networkTraffic"
        :resource-id="resourceId"
        metric-type="net_traffic"
        :time-range="timeRange"
        height="250px"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, computed, watch } from "vue";
import { useI18n } from "vue-i18n";
import statCard from "../components/stat-card.vue";
import metricChart from "../components/metric-chart.vue";
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

const panelTitle = computed(() => t("message.dashboard.vmPanel"));

const statsLabels = computed(() => ({
  cpuUsage: t("message.dashboard.cpuUsage"),
  connectionCount: t("message.dashboard.connectionCount"),
  memoryUsage: t("message.dashboard.memoryUsage"),
  diskUsage: t("message.dashboard.diskUsage")
}));

const chartLabels = computed(() => ({
  cpuDetail: t("message.dashboard.cpuDetail"),
  diskThroughput: t("message.dashboard.diskThroughput"),
  diskRate: t("message.dashboard.diskRate"),
  networkTraffic: t("message.dashboard.networkTraffic")
}));

const stats = reactive({
  cpuUsage: 0,
  connectionCount: 0,
  memoryUsage: 0,
  diskUsage: 0
});

const fetchVMStats = async () => {
  try {
    const res: any = await dashboardApi.getMetricData({
      resourceId: props.resourceId,
      metricType: "vm_basic"
    });
    if (res.data) {
      const data = res.data;
      stats.cpuUsage = data.cpuUsage || 0;
      stats.connectionCount = data.connectionCount || data.vm_user_number || 0;
      stats.memoryUsage = data.memoryUsage || data.vm_mem_usage || 0;
      stats.diskUsage = data.diskUsage || data.vm_disk_usage || 0;
    }
  } catch (error) {
    console.error("Failed to fetch VM stats:", error);
  }
};

watch(
  () => props.resourceId,
  () => {
    fetchVMStats();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.vm-panel {
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

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (max-width: 1200px) {
  .stat-cards,
  .charts-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
