<template>
  <div class="large-display-view">
    <div class="display-header">
      <h1>大屏概览</h1>
      <div class="header-info">
        <span class="time">{{ currentTime }}</span>
      </div>
    </div>

    <div class="display-content">
      <aside class="left-panel">
        <ResourceSelector
          ref="resourceSelectorRef"
          :platforms="platforms"
          @select="handleResourceSelect"
          @platformChange="handlePlatformChange"
        />
      </aside>

      <main class="main-panel">
        <MetricCharts
          :selectedResource="selectedResource"
          @timeRangeChange="handleTimeRangeChange"
        />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage } from 'element-plus';
import ResourceSelector from './components/ResourceSelector.vue';
import MetricCharts from './components/MetricCharts.vue';
import { useResourceTree } from './composables/useResourceTree';
import type { ResourceType, TimeRange } from './types';

const { loadPlatforms, platforms } = useResourceTree();

const resourceSelectorRef = ref<InstanceType<typeof ResourceSelector>>();

interface SelectedResource {
  id: number;
  name: string;
  type: ResourceType;
  ipAddress?: string;
}

const selectedResource = ref<SelectedResource | null>(null);
const currentTime = ref('');

let timeInterval: number | null = null;

onMounted(async () => {
  // Update current time
  updateTime();
  timeInterval = window.setInterval(updateTime, 1000);

  // Load platforms
  try {
    await loadPlatforms();
  } catch (error) {
    ElMessage.error('加载平台数据失败');
  }
});

onBeforeUnmount(() => {
  if (timeInterval) {
    clearInterval(timeInterval);
  }
});

function updateTime() {
  const now = new Date();
  const pad = (n: number) => n.toString().padStart(2, '0');
  currentTime.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
}

function handleResourceSelect(data: { id: number; name: string; type: ResourceType }) {
  selectedResource.value = {
    ...data,
    ipAddress: undefined,
  };
}

function handlePlatformChange(platformId: number) {
  // Platform changed, reset selected resource
  selectedResource.value = null;
}

function handleTimeRangeChange(timeRange: TimeRange) {
  // Time range changed, charts will auto-refresh
}
</script>

<style scoped>
.large-display-view {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.display-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
}

.display-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 500;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.time {
  font-size: 16px;
  font-family: 'Roboto Mono', monospace;
}

.display-content {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 16px;
  overflow: hidden;
}

.left-panel {
  width: 320px;
  flex-shrink: 0;
}

.main-panel {
  flex: 1;
  min-width: 0;
}

@media (max-width: 1024px) {
  .display-content {
    flex-direction: column;
  }

  .left-panel {
    width: 100%;
    height: 200px;
  }
}
</style>
