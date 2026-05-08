<template>
  <div class="time-range-selector">
    <el-dropdown @command="onTimeRangeSelect" trigger="click">
      <el-button size="default">
        <el-icon class="el-icon--left"><Clock /></el-icon>
        {{ selectedLabel }}
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="item in timeRanges"
            :key="item.value"
            :command="item.value"
            :class="{ 'is-active': selectedRange === item.value }"
          >
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <el-dropdown @command="onRefreshSelect" trigger="click" class="refresh-dropdown">
      <el-button size="default">
        <el-icon class="el-icon--left"><Refresh /></el-icon>
        {{ refreshLabel }}
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="item in refreshOptions"
            :key="item.value"
            :command="item.value"
            :class="{ 'is-active': selectedRefresh === item.value }"
          >
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useI18n } from "vue-i18n";
import { Clock, Refresh, ArrowDown } from "@element-plus/icons-vue";

interface TimeRange {
  label: string;
  value: string;
  duration: number;
}

const emit = defineEmits<{
  change: [range: {
    startTime: string;
    endTime: string;
    refreshInterval: number;
  }];
}>();

const { t } = useI18n();

const selectedRange = ref("1h");
const selectedRefresh = ref(0);
let refreshTimer: ReturnType<typeof setInterval> | null = null;

const timeRanges = computed<TimeRange[]>(() => [
  { label: t("message.dashboard.last15min"), value: "15m", duration: 15 * 60 * 1000 },
  { label: t("message.dashboard.last1h"), value: "1h", duration: 60 * 60 * 1000 },
  { label: t("message.dashboard.last6h"), value: "6h", duration: 6 * 60 * 60 * 1000 },
  { label: t("message.dashboard.last24h"), value: "24h", duration: 24 * 60 * 60 * 1000 },
  { label: t("message.dashboard.last7d"), value: "7d", duration: 7 * 24 * 60 * 60 * 1000 }
]);

const refreshOptions = computed(() => [
  { label: t("message.dashboard.refreshOff"), value: 0 },
  { label: t("message.dashboard.refresh10s"), value: 10000 },
  { label: t("message.dashboard.refresh30s"), value: 30000 },
  { label: t("message.dashboard.refresh1min"), value: 60000 },
  { label: t("message.dashboard.refresh5min"), value: 300000 }
]);

const selectedLabel = computed(() => {
  const range = timeRanges.value.find(r => r.value === selectedRange.value);
  return range?.label || "";
});

const refreshLabel = computed(() => {
  const option = refreshOptions.value.find(o => o.value === selectedRefresh.value);
  return option?.label || t("message.dashboard.refreshOff");
});

const calculateTimeRange = (rangeValue: string) => {
  const now = new Date();
  const range = timeRanges.value.find(r => r.value === rangeValue);
  const startTime = new Date(now.getTime() - (range?.duration || 60 * 60 * 1000));

  return {
    startTime: startTime.toISOString(),
    endTime: now.toISOString()
  };
};

const emitTimeRange = () => {
  const range = calculateTimeRange(selectedRange.value);
  emit("change", {
    ...range,
    refreshInterval: selectedRefresh.value
  });
};

const onTimeRangeSelect = (command: string) => {
  selectedRange.value = command;
  emitTimeRange();
};

const onRefreshSelect = (command: number) => {
  selectedRefresh.value = command;
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }

  if (command > 0) {
    refreshTimer = setInterval(() => {
      emitTimeRange();
    }, command);
  }

  emitTimeRange();
};

onMounted(() => {
  emitTimeRange();
});

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
});
</script>

<style lang="scss" scoped>
.time-range-selector {
  display: flex;
  gap: 12px;
}

.refresh-dropdown {
  margin-left: 8px;
}

:deep(.el-dropdown-menu__item.is-active) {
  color: var(--el-color-primary);
  font-weight: 600;
}
</style>
