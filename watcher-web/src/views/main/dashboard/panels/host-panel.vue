<template>
  <div class="host-panel">
    <div class="panel-header">
      <h3>{{ panelTitle }}</h3>
    </div>

    <div class="stat-cards">
      <stat-card
        :title="statsLabels.cpuAllocate"
        :value="stats.cpuAllocate"
        unit="%"
        color="#409eff"
        :precision="1"
      />
      <stat-card
        :title="statsLabels.memAllocate"
        :value="stats.memAllocate"
        unit="%"
        color="#67c23a"
        :precision="1"
      />
      <stat-card
        :title="statsLabels.diskUsage"
        :value="stats.diskUsage"
        unit="%"
        color="#e6a23c"
        :precision="1"
      />
      <stat-card
        :title="statsLabels.healthStatus"
        :value="stats.healthStatus"
        :color="stats.healthStatus === 'Healthy' ? '#67c23a' : '#f56c6c'"
      />
    </div>

    <div class="charts-grid">
      <metric-chart
        :title="chartLabels.cpuUsage"
        :resource-id="resourceId"
        metric-type="host_cpu_usage"
        :time-range="timeRange"
        height="250px"
      />
      <metric-chart
        :title="chartLabels.memUsage"
        :resource-id="resourceId"
        metric-type="mem_usage"
        :time-range="timeRange"
        height="250px"
      />
      <metric-chart
        :title="chartLabels.diskIOPS"
        :resource-id="resourceId"
        metric-type="disk_iops"
        :time-range="timeRange"
        height="250px"
      />
      <metric-chart
        :title="chartLabels.netThroughput"
        :resource-id="resourceId"
        metric-type="net_throughput"
        :time-range="timeRange"
        height="250px"
      />
    </div>

    <div class="gauges-row">
      <metric-gauge
        :title="gaugeLabels.diskUsage"
        :value="stats.diskUsage"
        unit="%"
        :max="100"
        :thresholds="diskThresholds"
      />
      <metric-gauge
        :title="gaugeLabels.partitionUsage"
        :value="stats.partitionUsage"
        unit="%"
        :max="100"
        :thresholds="partitionThresholds"
      />
    </div>

    <div class="vm-table">
      <h4>{{ tableTitle }}</h4>
      <el-table :data="vmList" border stripe max-height="300">
        <el-table-column prop="name" :label="vmColumns.name" />
        <el-table-column prop="status" :label="vmColumns.status">
          <template #default="{ row }">
            <el-tag :type="row.status === 'Running' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cpuUsage" :label="vmColumns.cpuUsage">
          <template #default="{ row }">
            {{ row.cpuUsage?.toFixed(1) }}%
          </template>
        </el-table-column>
        <el-table-column prop="memUsage" :label="vmColumns.memUsage">
          <template #default="{ row }">
            {{ row.memUsage?.toFixed(1) }}%
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { useI18n } from "vue-i18n";
import statCard from "../components/stat-card.vue";
import metricChart from "../components/metric-chart.vue";
import metricGauge from "../components/metric-gauge.vue";
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

const panelTitle = computed(() => t("message.dashboard.hostPanel"));
const tableTitle = computed(() => t("message.dashboard.vmList"));

const statsLabels = computed(() => ({
  cpuAllocate: t("message.dashboard.cpuAllocate"),
  memAllocate: t("message.dashboard.memAllocate"),
  diskUsage: t("message.dashboard.diskUsage"),
  healthStatus: t("message.dashboard.healthStatus")
}));

const chartLabels = computed(() => ({
  cpuUsage: t("message.dashboard.cpuUsage"),
  memUsage: t("message.dashboard.memUsage"),
  diskIOPS: t("message.dashboard.diskIOPS"),
  netThroughput: t("message.dashboard.netThroughput")
}));

const gaugeLabels = computed(() => ({
  diskUsage: t("message.dashboard.diskUsage"),
  partitionUsage: t("message.dashboard.partitionUsage")
}));

const vmColumns = computed(() => ({
  name: t("message.dashboard.vmName"),
  status: t("message.dashboard.vmStatus"),
  cpuUsage: t("message.dashboard.cpuUsage"),
  memUsage: t("message.dashboard.memUsage")
}));

const stats = reactive({
  cpuAllocate: 0,
  memAllocate: 0,
  diskUsage: 0,
  partitionUsage: 0,
  healthStatus: "Healthy"
});

const vmList = ref<any[]>([]);

const diskThresholds = [
  { value: 60, color: "#67c23a" },
  { value: 80, color: "#e6a23c" },
  { value: 100, color: "#f56c6c" }
];

const partitionThresholds = [
  { value: 70, color: "#67c23a" },
  { value: 85, color: "#e6a23c" },
  { value: 100, color: "#f56c6c" }
];

const fetchHostStats = async () => {
  try {
    const res: any = await dashboardApi.getMetricData({
      resourceId: props.resourceId,
      metricType: "host_basic"
    });
    if (res.data) {
      const data = res.data;
      stats.cpuAllocate = data.cpuAllocateRate || data.host_cpu_allocate_rate || 0;
      stats.memAllocate = data.memAllocateRate || data.host_mem_allocate_rate || 0;
      stats.diskUsage = data.diskUsage || data.host_disk_usage || 0;
      stats.partitionUsage = data.partitionUsage || data.host_partition_usage || 0;
      stats.healthStatus = data.healthStatus || data.host_health_status || "Healthy";
    }
  } catch (error) {
    console.error("Failed to fetch host stats:", error);
  }
};

const fetchVMList = async () => {
  try {
    const res: any = await dashboardApi.getResourceTree();
    if (res.data) {
      const findVMs = (items: any[]): any[] => {
        let vms: any[] = [];
        for (const item of items) {
          if (item.type === "vm" || item.resourceType === "vm") {
            vms.push({
              name: item.resourceName || item.label,
              status: item.status === "healthy" ? "Running" : "Stopped",
              cpuUsage: item.cpuUsage || 0,
              memUsage: item.memUsage || 0
            });
          }
          if (item.children?.length) {
            vms = vms.concat(findVMs(item.children));
          }
        }
        return vms;
      };
      vmList.value = findVMs(res.data).slice(0, 10);
    }
  } catch (error) {
    console.error("Failed to fetch VM list:", error);
  }
};

watch(
  () => props.resourceId,
  () => {
    fetchHostStats();
    fetchVMList();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.host-panel {
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
  margin-bottom: 24px;
}

.gauges-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.vm-table {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 16px 0;
  }
}

@media (max-width: 1200px) {
  .stat-cards,
  .charts-grid,
  .gauges-row {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
