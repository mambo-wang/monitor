<template>
  <div class="metric-detail-wrapper">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack">{{ $t('message.common.back') }}</el-button>
        <span class="page-title">{{ $t('message.metric.metricDetail') }} - {{ resourceName }}</span>
      </div>
      <div class="header-right">
        <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
          <el-radio-button label="1">{{ $t('message.metric.1hour') }}</el-radio-button>
          <el-radio-button label="6">{{ $t('message.metric.6hours') }}</el-radio-button>
          <el-radio-button label="24">{{ $t('message.metric.24hours') }}</el-radio-button>
          <el-radio-button label="168">{{ $t('message.metric.7days') }}</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div class="content-wrapper" v-loading="loading">
      <!-- 资源信息卡片 -->
      <el-card class="info-card">
        <template #header>
          <span>{{ $t('message.metric.resourceInfo') }}</span>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item :label="$t('message.resource.ipAddress')">
            {{ resourceInfo.ipAddress }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.resource.platform')">
            {{ resourceInfo.platform }}
          </el-descriptions-item>
          <el-descriptions-item :label="$t('message.metric.lastReportTime')">
            {{ resourceInfo.lastReportTime }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 指标类型选择 -->
      <el-card class="metric-type-card">
        <template #header>
          <span>{{ $t('message.metric.metricTypes') }}</span>
        </template>
        <el-select v-model="selectedMetricType" @change="handleMetricTypeChange" class="metric-select">
          <el-option
            v-for="item in metricTypes"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-card>

      <!-- 监控图表 -->
      <el-card class="chart-card">
        <template #header>
          <span>{{ selectedMetricType }} - {{ $t('message.metric.trendChart') }}</span>
        </template>
        <div ref="chartRef" class="chart-container"></div>
        <el-empty v-if="chartData.length === 0" :description="$t('message.metric.noData')" />
      </el-card>

      <!-- 指标数据表格 -->
      <el-card class="table-card">
        <template #header>
          <span>{{ $t('message.metric.dataTable') }}</span>
        </template>
        <el-table :data="tableData" border stripe>
          <el-table-column prop="metricType" :label="$t('message.metric.metricType')" width="150" />
          <el-table-column prop="metricName" :label="$t('message.metric.metricName')" width="200" />
          <el-table-column prop="metricValue" :label="$t('message.metric.metricValue')" width="150" />
          <el-table-column prop="metricUnit" :label="$t('message.metric.unit')" width="100" />
          <el-table-column prop="reportTime" :label="$t('message.metric.reportTime')" />
        </el-table>
        <el-pagination
          v-if="tableData.length > 0"
          class="pagination"
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="totalCount"
          layout="total, prev, pager, next"
        />
      </el-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
import * as echarts from "echarts";
import request from "@/utils/system/request";

const route = useRoute();
const router = useRouter();
const { t } = useI18n();

const resourceId = ref(route.query.id as string);
const resourceName = ref(route.query.name as string || "Unknown");
const loading = ref(false);
const timeRange = ref("24");
const selectedMetricType = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

const chartRef = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;

const resourceInfo = reactive({
  ipAddress: "",
  platform: "",
  lastReportTime: "",
  totalMetrics: 0,
});

const metricTypes = ref([
  { value: "cpu_usage", label: "CPU利用率" },
  { value: "mem_usage", label: "内存利用率" },
  { value: "disk_usage", label: "磁盘利用率" },
  { value: "net_throughput", label: "网络吞吐量" },
  { value: "disk_iops", label: "磁盘IOPS" },
  { value: "disk_latency", label: "磁盘延迟" },
]);

const chartData = ref<any[]>([]);
const tableData = ref<any[]>([]);

const goBack = () => {
  router.push({ name: "resource-index" });
};

const handleTimeRangeChange = () => {
  loadMetricData();
};

const handleMetricTypeChange = () => {
  loadMetricData();
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  loadTableData();
};

const loadResourceInfo = async () => {
  try {
    const res = await request({
      url: `/metric/summary/${resourceId.value}`,
      method: "get",
    });
    if (res.successMessage) {
      Object.assign(resourceInfo, res.data || {});
    }
  } catch (error) {
    console.error("Failed to load resource info:", error);
  }
};

const loadMetricData = async () => {
  if (!selectedMetricType.value) return;
  
  loading.value = true;
  try {
    const res = await request({
      url: `/metric/trend/${resourceId.value}/${selectedMetricType.value}`,
      method: "get",
      params: { hours: timeRange.value },
    });
    
    if (res.successMessage) {
      chartData.value = res.data || [];
      updateChart();
      loadTableData();
    }
  } catch (error) {
    console.error("Failed to load metric data:", error);
  } finally {
    loading.value = false;
  }
};

const loadTableData = async () => {
  try {
    const res = await request({
      url: "/metric/list",
      method: "get",
      params: {
        resourceId: resourceId.value,
        metricType: selectedMetricType.value,
        page: currentPage.value - 1,
        size: pageSize.value,
      },
    });
    
    if (res.successMessage) {
      tableData.value = res.data || [];
      totalCount.value = res.totalLength || 0;
    }
  } catch (error) {
    console.error("Failed to load table data:", error);
  }
};

const updateChart = () => {
  if (!chartRef.value) return;
  
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value);
  }
  
  const times = chartData.value.map((item: any) => 
    new Date(item.reportTime).toLocaleString()
  );
  const values = chartData.value.map((item: any) => parseFloat(item.metricValue) || 0);
  
  const option = {
    tooltip: {
      trigger: "axis",
      formatter: "{b}: {c}",
    },
    xAxis: {
      type: "category",
      data: times,
      axisLabel: {
        rotate: 45,
        interval: Math.floor(times.length / 10),
      },
    },
    yAxis: {
      type: "value",
      name: selectedMetricType.value,
    },
    series: [
      {
        name: selectedMetricType.value,
        type: "line",
        data: values,
        smooth: true,
        areaStyle: {
          opacity: 0.3,
        },
      },
    ],
    grid: {
      left: "3%",
      right: "4%",
      bottom: "10%",
      containLabel: true,
    },
  };
  
  chartInstance.setOption(option);
};

const handleResize = () => {
  chartInstance?.resize();
};

onMounted(() => {
  loadResourceInfo();
  if (metricTypes.value.length > 0) {
    selectedMetricType.value = metricTypes.value[0].value;
    loadMetricData();
  }
  
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  chartInstance?.dispose();
});
</script>

<style lang="scss" scoped>
.metric-detail-wrapper {
  padding: 20px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .page-title {
        font-size: 18px;
        font-weight: 600;
      }
    }
  }

  .content-wrapper {
    .info-card,
    .metric-type-card,
    .chart-card,
    .table-card {
      margin-bottom: 20px;
    }

    .metric-select {
      width: 300px;
    }

    .chart-container {
      width: 100%;
      height: 400px;
    }

    .pagination {
      margin-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
