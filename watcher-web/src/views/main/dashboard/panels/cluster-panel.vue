<template>
  <div class="cluster-panel">
    <div class="panel-header">
      <h3>{{ panelTitle }}</h3>
    </div>

    <div class="stat-cards">
      <stat-card
        :title="statsLabels.totalHosts"
        :value="stats.totalHosts"
        color="#409eff"
      />
      <stat-card
        :title="statsLabels.totalVMs"
        :value="stats.totalVMs"
        color="#67c23a"
      />
      <stat-card
        :title="statsLabels.overallCPU"
        :value="stats.overallCPU"
        unit="%"
        color="#e6a23c"
        :precision="1"
      />
      <stat-card
        :title="statsLabels.overallMem"
        :value="stats.overallMem"
        unit="%"
        color="#f56c6c"
        :precision="1"
      />
    </div>

    <div class="charts-grid">
      <metric-chart
        :title="chartLabels.hostTopCPU"
        :resource-id="resourceId"
        metric-type="cluster_host_top5_cpu_usage"
        :time-range="timeRange"
        height="280px"
      />
      <metric-chart
        :title="chartLabels.hostTopMem"
        :resource-id="resourceId"
        metric-type="cluster_host_top5_mem_usage"
        :time-range="timeRange"
        height="280px"
      />
      <metric-chart
        :title="chartLabels.virtHostCPU"
        :resource-id="resourceId"
        metric-type="cluster_virt_host_cpu_TopN"
        :time-range="timeRange"
        height="280px"
      />
      <metric-chart
        :title="chartLabels.virtHostMem"
        :resource-id="resourceId"
        metric-type="cluster_virt_host_mem_TopN"
        :time-range="timeRange"
        height="280px"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
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

const panelTitle = computed(() => t("message.dashboard.clusterPanel"));

const statsLabels = computed(() => ({
  totalHosts: t("message.dashboard.totalHosts"),
  totalVMs: t("message.dashboard.totalVMs"),
  overallCPU: t("message.dashboard.overallCPU"),
  overallMem: t("message.dashboard.overallMem")
}));

const chartLabels = computed(() => ({
  hostTopCPU: t("message.dashboard.hostTopCPU"),
  hostTopMem: t("message.dashboard.hostTopMem"),
  virtHostCPU: t("message.dashboard.virtHostCPU"),
  virtHostMem: t("message.dashboard.virtHostMem")
}));

const stats = reactive({
  totalHosts: 0,
  totalVMs: 0,
  overallCPU: 0,
  overallMem: 0
});

const fetchClusterStats = async () => {
  try {
    const res: any = await dashboardApi.getMetricData({
      resourceId: props.resourceId,
      metricType: "cluster_basic"
    });
    if (res.data) {
      const data = res.data;
      stats.totalHosts = data.totalHosts || data.cluster_host_number || 0;
      stats.totalVMs = data.totalVMs || data.cluster_vm_number || 0;
      stats.overallCPU = data.overallCPU || data.cluster_cpu_allocate_rate || 0;
      stats.overallMem = data.overallMem || data.cluster_mem_allocate_rate || 0;
    }
  } catch (error) {
    console.error("Failed to fetch cluster stats:", error);
  }
};

watch(
  () => props.resourceId,
  () => {
    fetchClusterStats();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.cluster-panel {
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
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
