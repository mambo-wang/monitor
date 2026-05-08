<template>
  <div class="metric-chart">
    <div class="chart-header">
      <span class="chart-title">{{ title }}</span>
      <el-button-group v-if="showLegend" size="small">
        <el-button
          v-for="item in legendItems"
          :key="item.key"
          :type="activeLegend === item.key ? 'primary' : ''"
          @click="activeLegend = item.key"
        >
          {{ item.label }}
        </el-button>
      </el-button-group>
    </div>
    <div class="chart-body" v-loading="loading">
      <chart ref="chartRef" :option="chartOption" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from "vue";
import chart from "@/components/charts/index.vue";
import dashboardApi from "@/api/dashboard";

interface DataPoint {
  timestamp: number;
  value: number;
}

interface LegendItem {
  key: string;
  label: string;
  color: string;
}

interface Props {
  title: string;
  resourceId: string;
  metricType: string;
  metricTypes?: Array<{ type: string; name: string; color?: string }>;
  timeRange?: {
    startTime: string;
    endTime: string;
  };
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  title: "",
  resourceId: "",
  metricType: "",
  metricTypes: () => [],
  timeRange: () => ({
    startTime: "",
    endTime: ""
  }),
  height: "300px"
});

const chartRef = ref();
const loading = ref(false);
const chartData = ref<Record<string, DataPoint[]>>({});
const activeLegend = ref("");

const showLegend = computed(() => props.metricTypes.length > 1);

const legendItems = computed<LegendItem[]>(() =>
  props.metricTypes.map((item, index) => ({
    key: item.type,
    label: item.name,
    color: item.color || getDefaultColor(index)
  }))
);

const getDefaultColor = (index: number) => {
  const colors = ["#409eff", "#67c23a", "#e6a23c", "#f56c6c", "#909399"];
  return colors[index % colors.length];
};

const chartOption = computed(() => {
  const series = Object.entries(chartData.value).map(([key, data]) => {
    const legend = legendItems.value.find(l => l.key === key);
    return {
      name: legend?.label || key,
      type: "line",
      smooth: true,
      symbol: "circle",
      symbolSize: 4,
      lineStyle: {
        width: 2,
        color: legend?.color || getDefaultColor(0)
      },
      itemStyle: {
        color: legend?.color || getDefaultColor(0)
      },
      areaStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: (legend?.color || "#409eff") + "40" },
            { offset: 1, color: (legend?.color || "#409eff") + "05" }
          ]
        }
      },
      data: data.map(d => [d.timestamp, d.value])
    };
  });

  return {
    tooltip: {
      trigger: "axis",
      formatter: (params: any) => {
        if (!params.length) return "";
        const time = new Date(params[0].data[0]).toLocaleString();
        let result = `${time}<br/>`;
        params.forEach((p: any) => {
          result += `${p.marker} ${p.seriesName}: ${p.data[1].toFixed(2)}<br/>`;
        });
        return result;
      }
    },
    legend: {
      show: !showLegend.value,
      data: legendItems.value.map(l => l.label)
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true
    },
    xAxis: {
      type: "time",
      boundaryGap: false,
      axisLabel: {
        formatter: (value: number) => new Date(value).toLocaleTimeString()
      }
    },
    yAxis: {
      type: "value"
    },
    series
  };
});

const fetchMetricData = async () => {
  if (!props.resourceId || !props.metricType) return;

  loading.value = true;
  try {
    const types = props.metricType.split(",");
    const promises = types.map(type =>
      dashboardApi.getMetricData({
        resourceId: props.resourceId,
        metricType: type,
        startTime: props.timeRange.startTime,
        endTime: props.timeRange.endTime
      })
    );

    const results = await Promise.all(promises);
    const newData: Record<string, DataPoint[]> = {};

    results.forEach((res: any, index) => {
      const type = types[index];
      if (res.data) {
        newData[type] = res.data;
      }
    });

    chartData.value = newData;
    if (types.length > 0) {
      activeLegend.value = types[0];
    }
  } catch (error) {
    console.error("Failed to fetch metric data:", error);
  } finally {
    loading.value = false;
  }
};

watch(
  () => [props.resourceId, props.metricType, props.timeRange],
  () => {
    fetchMetricData();
  },
  { deep: true }
);

onMounted(() => {
  fetchMetricData();
});
</script>

<style lang="scss" scoped>
.metric-chart {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-body {
  height: v-bind(height);
}
</style>
