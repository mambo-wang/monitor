<template>
  <div class="metric-gauge">
    <div class="gauge-header">
      <span class="gauge-title">{{ title }}</span>
    </div>
    <div class="gauge-body">
      <chart ref="chartRef" :option="chartOption" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import chart from "@/components/charts/index.vue";

interface Threshold {
  value: number;
  color: string;
  label?: string;
}

interface Props {
  title: string;
  value: number;
  max?: number;
  unit?: string;
  thresholds?: Threshold[];
}

const props = withDefaults(defineProps<Props>(), {
  title: "",
  value: 0,
  max: 100,
  unit: "%",
  thresholds: () => [
    { value: 60, color: "#67c23a" },
    { value: 80, color: "#e6a23c" },
    { value: 100, color: "#f56c6c" }
  ]
});

const chartRef = ref();

const getThresholdColor = (val: number) => {
  for (const threshold of props.thresholds) {
    if (val <= threshold.value) {
      return threshold.color;
    }
  }
  return props.thresholds[props.thresholds.length - 1].color;
};

const chartOption = computed(() => {
  const percent = (props.value / props.max) * 100;
  const color = getThresholdColor(percent);

  return {
    series: [
      {
        type: "gauge",
        startAngle: 220,
        endAngle: -40,
        min: 0,
        max: props.max,
        splitNumber: 5,
        radius: "90%",
        axisLine: {
          lineStyle: {
            width: 12,
            color: props.thresholds.map((t, i, arr) => {
              const nextVal = arr[i + 1]?.value ?? props.max;
              return [nextVal / props.max, t.color];
            })
          }
        },
        pointer: {
          icon: "path://M12.8,0.7l12,40.1H0.7L12.8,0.7z",
          length: "55%",
          width: 8,
          offsetCenter: [0, "-10%"],
          itemStyle: {
            color: color
          }
        },
        axisTick: {
          length: 8,
          lineStyle: {
            color: "auto",
            width: 1
          }
        },
        splitLine: {
          length: 12,
          lineStyle: {
            color: "auto",
            width: 2
          }
        },
        axisLabel: {
          color: "#606266",
          fontSize: 10,
          distance: -50,
          formatter: (val: number) => `${val}${props.unit}`
        },
        title: {
          offsetCenter: [0, "70%"],
          fontSize: 12,
          color: "#909399"
        },
        detail: {
          valueAnimation: true,
          fontSize: 24,
          offsetCenter: [0, "30%"],
          formatter: (val: number) => `${val.toFixed(1)}${props.unit}`,
          color: color
        },
        data: [
          {
            value: props.value,
            name: props.title
          }
        ]
      }
    ]
  };
});
</script>

<style lang="scss" scoped>
.metric-gauge {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.gauge-header {
  margin-bottom: 8px;
}

.gauge-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.gauge-body {
  height: 200px;
}
</style>
