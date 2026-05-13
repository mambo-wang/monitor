<template>
  <div class="metric-charts">
    <div class="charts-header">
      <h3>监控图表</h3>
      <div class="header-actions">
        <el-select v-model="selectedTimeRange" size="small" @change="handleTimeRangeChange">
          <el-option label="最近1小时" value="1h" />
          <el-option label="最近3小时" value="3h" />
          <el-option label="最近6小时" value="6h" />
          <el-option label="最近12小时" value="12h" />
          <el-option label="最近24小时" value="24h" />
        </el-select>
        <el-button size="small" @click="handleRefresh" :loading="loading">
          <i class="el-icon-refresh"></i>
        </el-button>
      </div>
    </div>

    <div class="charts-container" v-loading="loading">
      <div v-if="!selectedResource" class="charts-empty">
        <i class="el-icon-s-data"></i>
        <p>请在左侧选择主机或虚拟机查看监控数据</p>
      </div>

      <template v-else>
        <div class="resource-info">
          <span class="resource-name">{{ selectedResource.name }}</span>
          <span class="resource-type">{{ getResourceTypeName(selectedResource.type) }}</span>
          <span class="resource-ip" v-if="selectedResource.ipAddress">
            {{ selectedResource.ipAddress }}
          </span>
        </div>

        <div class="charts-grid">
          <MetricLineChart
            :metric="cpuMetric"
            title="CPU 利用率"
            height="220px"
          />
          <MetricLineChart
            :metric="memoryMetric"
            title="内存利用率"
            height="220px"
          />
        </div>

        <div v-if="error" class="charts-error">
          <i class="el-icon-warning"></i>
          <p>{{ error }}</p>
          <el-button size="small" type="primary" @click="handleRetry">
            重试
          </el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import MetricLineChart from './MetricLineChart.vue';
import { useMetrics } from '@/views/large-display/composables/useMetrics';
import type { ResourceType, TimeRange } from '@/views/large-display/types';
import { getResourceTypeName } from '@/views/large-display/types';

interface SelectedResource {
  id: number;
  name: string;
  type: ResourceType;
  ipAddress?: string;
}

interface Props {
  selectedResource: SelectedResource | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'timeRangeChange', timeRange: TimeRange): void;
}>();

const {
  cpu,
  memory,
  isLoading,
  error,
  loadMetrics,
  changeTimeRange,
  refresh,
} = useMetrics();

const cpuMetric = cpu;
const memoryMetric = memory;
const loading = isLoading;

const selectedTimeRange = ref<TimeRange>('1h');

onMounted(() => {
  if (props.selectedResource) {
    loadMetrics(props.selectedResource.id);
  }
});

watch(() => props.selectedResource, async (newResource) => {
  if (newResource) {
    await loadMetrics(newResource.id, selectedTimeRange.value);
  }
});

function handleTimeRangeChange(range: TimeRange) {
  emit('timeRangeChange', range);
  if (props.selectedResource) {
    changeTimeRange(props.selectedResource.id, range);
  }
}

function handleRefresh() {
  if (props.selectedResource) {
    refresh(props.selectedResource.id);
  }
}

function handleRetry() {
  if (props.selectedResource) {
    loadMetrics(props.selectedResource.id, selectedTimeRange.value);
  }
}
</script>

<style scoped>
.metric-charts {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 4px;
}

.charts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.charts-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.charts-container {
  flex: 1;
  padding: 16px;
  overflow: auto;
}

.charts-empty {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #999;
}

.charts-empty i {
  font-size: 64px;
  margin-bottom: 16px;
}

.charts-empty p {
  margin: 0;
  font-size: 14px;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.resource-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.resource-type {
  padding: 2px 8px;
  background: #5470c6;
  color: #fff;
  font-size: 12px;
  border-radius: 3px;
}

.resource-ip {
  color: #666;
  font-size: 13px;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

.charts-error {
  margin-top: 16px;
  padding: 16px;
  background: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 4px;
  text-align: center;
}

.charts-error i {
  font-size: 32px;
  color: #f56c6c;
  margin-bottom: 8px;
}

.charts-error p {
  margin: 0 0 12px;
  color: #f56c6c;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
