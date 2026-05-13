<template>
  <div class="metric-line-chart">
    <div class="chart-header" v-if="title">
      <h4>{{ title }}</h4>
      <div class="chart-stats">
        <span class="stat">
          <span class="label">当前</span>
          <span class="value">{{ currentValue }}</span>
        </span>
        <span class="stat">
          <span class="label">平均</span>
          <span class="value">{{ averageValue }}</span>
        </span>
        <span class="stat">
          <span class="label">最大</span>
          <span class="value">{{ maxValue }}</span>
        </span>
      </div>
    </div>
    <div ref="chartRef" class="chart-container"></div>
    <div v-if="!metric" class="chart-empty">
      <i class="el-icon-data-line"></i>
      <p>暂无数据</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';
import type { MetricTrendDTO, TimeRange } from '@/views/large-display/types';
import { useChartData } from '@/views/large-display/composables/useChartData';

interface Props {
  metric: MetricTrendDTO | null;
  title?: string;
  height?: string;
  timeRange?: TimeRange;
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  height: '250px',
  timeRange: '1h',
});

const emit = defineEmits<{
  (e: 'refresh'): void;
}>();

const chartRef = ref<HTMLElement>();
let chartInstance: echarts.ECharts | null = null;

const { 
  cpuChartOptions,
  memoryChartOptions,
  formatTime,
  getMetricColor,
  getGradientColors,
} = useChartData(
  computed(() => props.metric?.metricType === 'CPU' ? props.metric : null),
  computed(() => props.metric?.metricType === 'MEMORY' ? props.metric : null)
);

const currentValue = computed(() => {
  if (!props.metric || !props.metric.values || props.metric.values.length === 0) {
    return '--';
  }
  const latest = props.metric.values[props.metric.values.length - 1];
  return `${latest.toFixed(1)}${props.metric.unit}`;
});

const averageValue = computed(() => {
  if (!props.metric || !props.metric.values || props.metric.values.length === 0) {
    return '--';
  }
  const sum = props.metric.values.reduce((acc, val) => acc + val, 0);
  const avg = sum / props.metric.values.length;
  return `${avg.toFixed(1)}${props.metric.unit}`;
});

const maxValue = computed(() => {
  if (!props.metric || !props.metric.values || props.metric.values.length === 0) {
    return '--';
  }
  const max = Math.max(...props.metric.values);
  return `${max.toFixed(1)}${props.metric.unit}`;
});

function initChart() {
  if (!chartRef.value) return;
  
  chartInstance = echarts.init(chartRef.value);
  updateChart();
}

function updateChart() {
  if (!chartInstance || !props.metric) return;

  const metricType = props.metric.metricType;
  const color = getMetricColor(metricType);
  const gradient = getGradientColors(metricType);

  const options: echarts.EChartsOption = {
    color: [color],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true,
    },
    xAxis: {
      type: 'time',
      axisLabel: {
        formatter: (value: number) => formatTime(value, 'HH:mm'),
        color: '#666',
        fontSize: 11,
      },
      splitLine: { show: false },
      axisLine: { lineStyle: { color: '#ddd' } },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: {
        formatter: '{value}%',
        color: '#666',
        fontSize: 11,
      },
      splitLine: {
        lineStyle: { color: '#eee', type: 'dashed' },
      },
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = params[0];
        if (!data) return '';
        const time = formatTime(data.value[0], 'MM-DD HH:mm:ss');
        const value = data.value[1].toFixed(2);
        return `${data.seriesName}<br/>${time}<br/><b>${value}%</b>`;
      },
      axisPointer: { type: 'cross' },
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#ddd',
      borderWidth: 1,
    },
    series: [
      {
        name: metricType === 'CPU' ? 'CPU 利用率' : '内存利用率',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: { color },
        lineStyle: { width: 2, color },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: gradient[0] },
              { offset: 1, color: gradient[1] },
            ],
          },
        },
        data: props.metric.values.map((value, index) => [
          props.metric!.timestamps[index] || Date.now(),
          value,
        ]),
      },
    ],
  };

  chartInstance.setOption(options, true);
}

function resizeChart() {
  if (chartInstance) {
    chartInstance.resize();
  }
}

onMounted(() => {
  nextTick(() => {
    initChart();
    window.addEventListener('resize', resizeChart);
  });
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart);
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
});

watch(() => props.metric, () => {
  nextTick(() => {
    updateChart();
  });
}, { deep: true });

watch(() => props.height, () => {
  nextTick(() => {
    resizeChart();
  });
});
</script>

<style scoped>
.metric-line-chart {
  position: relative;
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.chart-stats {
  display: flex;
  gap: 16px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat .label {
  font-size: 11px;
  color: #999;
  margin-bottom: 2px;
}

.stat .value {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.chart-container {
  width: 100%;
  height: v-bind(height);
}

.chart-empty {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #999;
}

.chart-empty i {
  font-size: 48px;
  margin-bottom: 8px;
}

.chart-empty p {
  margin: 0;
  font-size: 14px;
}
</style>
