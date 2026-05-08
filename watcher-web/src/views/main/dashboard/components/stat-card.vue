<template>
  <div class="stat-card" :style="{ borderLeftColor: color }">
    <div class="stat-header">
      <span class="stat-title">{{ title }}</span>
      <span class="stat-trend" :class="trendClass">
        <el-icon v-if="trend === 'up'"><Top /></el-icon>
        <el-icon v-else-if="trend === 'down'"><Bottom /></el-icon>
        <span v-else>--</span>
      </span>
    </div>
    <div class="stat-body">
      <span class="stat-value">{{ formattedValue }}</span>
      <span class="stat-unit" v-if="unit">{{ unit }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { Top, Bottom } from "@element-plus/icons-vue";

interface Props {
  title: string;
  value: number | string;
  unit?: string;
  trend?: "up" | "down" | "stable";
  color?: string;
  precision?: number;
}

const props = withDefaults(defineProps<Props>(), {
  title: "",
  value: 0,
  unit: "",
  trend: "stable",
  color: "#409eff",
  precision: 2
});

const formattedValue = computed(() => {
  if (typeof props.value === "string") return props.value;
  return props.value.toFixed(props.precision);
});

const trendClass = computed(() => ({
  "trend-up": props.trend === "up",
  "trend-down": props.trend === "down",
  "trend-stable": props.trend === "stable"
}));
</script>

<style lang="scss" scoped>
.stat-card {
  background: #fff;
  border-radius: 4px;
  border-left: 4px solid;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  font-size: 12px;

  &.trend-up {
    color: #f56c6c;
  }

  &.trend-down {
    color: #67c23a;
  }

  &.trend-stable {
    color: #909399;
  }
}

.stat-body {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-unit {
  font-size: 14px;
  color: #909399;
}
</style>
