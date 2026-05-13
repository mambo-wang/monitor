/**
 * useChartData - Chart data formatting composable
 * Formats metric data for ECharts visualization
 */
import { computed, type Ref } from 'vue';
import type { MetricTrendDTO, ChartSeriesData } from '@/views/large-display/types';

export interface ChartDataOptions {
  showArea?: boolean;
  smooth?: boolean;
  color?: string;
  gradientColor?: [string, string];
}

export function useChartData(
  cpuMetric: Ref<MetricTrendDTO | null>,
  memoryMetric: Ref<MetricTrendDTO | null>
) {
  /**
   * Format timestamp to time string
   */
  function formatTime(timestamp: number, format: 'HH:mm' | 'HH:mm:ss' | 'MM-DD HH:mm' | 'MM-DD HH:mm:ss' = 'HH:mm'): string {
    const date = new Date(timestamp);
    const pad = (n: number) => n.toString().padStart(2, '0');
    
    switch (format) {
      case 'HH:mm:ss':
        return `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
      case 'MM-DD HH:mm':
        return `${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`;
      case 'MM-DD HH:mm:ss':
        return `${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
      default:
        return `${pad(date.getHours())}:${pad(date.getMinutes())}`;
    }
  }

  /**
   * Convert MetricTrendDTO to ECharts series data format
   */
  function toSeriesData(
    metric: MetricTrendDTO | null,
    options: ChartDataOptions = {}
  ): ChartSeriesData | null {
    if (!metric || !metric.values || !metric.timestamps || metric.values.length === 0) {
      return null;
    }

    const {
      showArea = false,
      smooth = true,
      color = '#5470C6',
      gradientColor = ['rgba(84, 112, 198, 0.5)', 'rgba(84, 112, 198, 0.1)'],
    } = options;

    const data: [number, number][] = metric.values.map((value, index) => [
      metric.timestamps[index] || Date.now(),
      value,
    ]);

    const result: ChartSeriesData = {
      name: getMetricTypeName(metric.metricType),
      type: 'line',
      data,
      smooth,
    };

    if (showArea) {
      result.areaStyle = {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: gradientColor[0] },
            { offset: 1, color: gradientColor[1] },
          ],
        },
      };
    }

    if (color) {
      result.itemStyle = { color };
    }

    return result;
  }

  /**
   * Get metric type display name
   */
  function getMetricTypeName(type: string): string {
    const names: Record<string, string> = {
      CPU: 'CPU 利用率',
      MEMORY: '内存利用率',
      DISK: '磁盘利用率',
      NETWORK: '网络流量',
    };
    return names[type] || type;
  }

  /**
   * Get color for metric type
   */
  function getMetricColor(type: string): string {
    const colors: Record<string, string> = {
      CPU: '#5470C6',
      MEMORY: '#73C0DE',
      DISK: '#EE6666',
      NETWORK: '#91CC75',
    };
    return colors[type] || '#5470C6';
  }

  /**
   * Get gradient colors for area style
   */
  function getGradientColors(type: string): [string, string] {
    const colors: Record<string, [string, string]> = {
      CPU: ['rgba(84, 112, 198, 0.6)', 'rgba(84, 112, 198, 0.1)'],
      MEMORY: ['rgba(115, 192, 222, 0.6)', 'rgba(115, 192, 222, 0.1)'],
      DISK: ['rgba(238, 102, 102, 0.6)', 'rgba(238, 102, 102, 0.1)'],
      NETWORK: ['rgba(145, 204, 117, 0.6)', 'rgba(145, 204, 117, 0.1)'],
    };
    return colors[type] || ['rgba(84, 112, 198, 0.6)', 'rgba(84, 112, 198, 0.1)'];
  }

  /**
   * Get X-axis time formatter
   */
  function getXAxisFormatter() {
    return (value: number) => formatTime(value, 'HH:mm');
  }

  /**
   * Get tooltip formatter
   */
  function getTooltipFormatter() {
    return (params: any) => {
      const data = params[0];
      if (!data) return '';
      const time = formatTime(data.value[0], 'MM-DD HH:mm:ss');
      const value = data.value[1].toFixed(2);
      return `${data.seriesName}<br/>${time}<br/>${value}%`;
    };
  }

  /**
   * Get base chart options
   */
  function getBaseOptions(metricType: string) {
    const color = getMetricColor(metricType);
    const gradient = getGradientColors(metricType);
    const name = getMetricTypeName(metricType);

    return {
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
          formatter: getXAxisFormatter(),
          color: '#666',
        },
        splitLine: {
          show: false,
        },
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 100,
        axisLabel: {
          formatter: '{value}%',
          color: '#666',
        },
        splitLine: {
          lineStyle: {
            color: '#eee',
            type: 'dashed',
          },
        },
      },
      tooltip: {
        trigger: 'axis',
        formatter: getTooltipFormatter(),
        axisPointer: {
          type: 'cross',
        },
      },
    };
  }

  // Computed series data for CPU
  const cpuSeries = computed(() => {
    return toSeriesData(cpuMetric.value, {
      showArea: true,
      smooth: true,
      color: getMetricColor('CPU'),
      gradientColor: getGradientColors('CPU'),
    });
  });

  // Computed series data for Memory
  const memorySeries = computed(() => {
    return toSeriesData(memoryMetric.value, {
      showArea: true,
      smooth: true,
      color: getMetricColor('MEMORY'),
      gradientColor: getGradientColors('MEMORY'),
    });
  });

  // CPU chart options
  const cpuChartOptions = computed(() => {
    const base = getBaseOptions('CPU');
    if (cpuSeries.value) {
      return {
        ...base,
        series: [cpuSeries.value],
      };
    }
    return base;
  });

  // Memory chart options
  const memoryChartOptions = computed(() => {
    const base = getBaseOptions('MEMORY');
    if (memorySeries.value) {
      return {
        ...base,
        series: [memorySeries.value],
      };
    }
    return base;
  });

  /**
   * Generate combined chart options for both CPU and Memory
   */
  const combinedChartOptions = computed(() => {
    const series: ChartSeriesData[] = [];
    
    if (cpuSeries.value) series.push(cpuSeries.value);
    if (memorySeries.value) series.push(memorySeries.value);

    return {
      color: [getMetricColor('CPU'), getMetricColor('MEMORY')],
      legend: {
        data: series.map(s => s.name),
        bottom: 0,
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '15%',
        top: '10%',
        containLabel: true,
      },
      xAxis: {
        type: 'time',
        axisLabel: {
          formatter: getXAxisFormatter(),
          color: '#666',
        },
        splitLine: {
          show: false,
        },
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 100,
        axisLabel: {
          formatter: '{value}%',
          color: '#666',
        },
        splitLine: {
          lineStyle: {
            color: '#eee',
            type: 'dashed',
          },
        },
      },
      tooltip: {
        trigger: 'axis',
        formatter: getTooltipFormatter(),
        axisPointer: {
          type: 'cross',
        },
      },
      series,
    };
  });

  return {
    // Utilities
    formatTime,
    toSeriesData,
    getMetricTypeName,
    getMetricColor,
    getGradientColors,
    getBaseOptions,
    getXAxisFormatter,
    getTooltipFormatter,

    // Computed
    cpuSeries,
    memorySeries,
    cpuChartOptions,
    memoryChartOptions,
    combinedChartOptions,
  };
}
