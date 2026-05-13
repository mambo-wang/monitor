/**
 * useMetrics - Monitor data fetching composable
 * Fetches real-time monitoring data (CPU, Memory) for selected resources
 * 监控数据使用实时查询，不入库也不查数据库
 */
import { ref, computed, watch } from 'vue';
import type { MetricTrendDTO, TimeRange } from '@/views/large-display/types';
import { getMetricsTrend, getMultipleMetrics } from '@/api/largeDisplay';

export interface MetricsState {
  cpu: MetricTrendDTO | null;
  memory: MetricTrendDTO | null;
  loading: boolean;
  error: string | null;
  timeRange: TimeRange;
}

export function useMetrics() {
  const state = ref<MetricsState>({
    cpu: null,
    memory: null,
    loading: false,
    error: null,
    timeRange: '1h',
  });

  /**
   * Load metrics for a resource
   */
  async function loadMetrics(resourceId: number, timeRange?: TimeRange) {
    if (!resourceId) {
      clearMetrics();
      return;
    }

    try {
      state.value.loading = true;
      state.value.error = null;

      const range = timeRange || state.value.timeRange;
      const metrics = await getMultipleMetrics(resourceId, range);

      state.value.cpu = metrics.cpu;
      state.value.memory = metrics.memory;
      state.value.timeRange = range;
    } catch (err) {
      state.value.error = err instanceof Error ? err.message : 'Failed to load metrics';
      console.error('[useMetrics] loadMetrics error:', err);
    } finally {
      state.value.loading = false;
    }
  }

  /**
   * Load single metric type
   */
  async function loadSingleMetric(
    resourceId: number,
    metricType: 'CPU' | 'MEMORY',
    timeRange?: TimeRange
  ): Promise<MetricTrendDTO | null> {
    if (!resourceId) return null;

    try {
      const range = timeRange || state.value.timeRange;
      return await getMetricsTrend(resourceId, metricType, range);
    } catch (err) {
      console.error('[useMetrics] loadSingleMetric error:', err);
      return null;
    }
  }

  /**
   * Refresh current metrics
   */
  async function refresh(resourceId: number) {
    if (!resourceId) return;
    await loadMetrics(resourceId);
  }

  /**
   * Change time range and reload metrics
   */
  async function changeTimeRange(resourceId: number, timeRange: TimeRange) {
    state.value.timeRange = timeRange;
    await loadMetrics(resourceId, timeRange);
  }

  /**
   * Clear all metrics
   */
  function clearMetrics() {
    state.value.cpu = null;
    state.value.memory = null;
    state.value.error = null;
  }

  /**
   * Get formatted metric value
   */
  function getFormattedValue(metric: MetricTrendDTO | null, index: number = -1): string {
    if (!metric || !metric.values || metric.values.length === 0) {
      return '--';
    }

    const value = index >= 0 && index < metric.values.length 
      ? metric.values[index] 
      : metric.values[metric.values.length - 1];

    return `${value.toFixed(1)}${metric.unit}`;
  }

  /**
   * Get latest metric value
   */
  function getLatestValue(metric: MetricTrendDTO | null): string {
    return getFormattedValue(metric, -1);
  }

  /**
   * Get average metric value
   */
  function getAverageValue(metric: MetricTrendDTO | null): string {
    if (!metric || !metric.values || metric.values.length === 0) {
      return '--';
    }

    const sum = metric.values.reduce((acc, val) => acc + val, 0);
    const avg = sum / metric.values.length;
    return `${avg.toFixed(1)}${metric.unit}`;
  }

  /**
   * Get max metric value
   */
  function getMaxValue(metric: MetricTrendDTO | null): string {
    if (!metric || !metric.values || metric.values.length === 0) {
      return '--';
    }

    const max = Math.max(...metric.values);
    return `${max.toFixed(1)}${metric.unit}`;
  }

  /**
   * Get min metric value
   */
  function getMinValue(metric: MetricTrendDTO | null): string {
    if (!metric || !metric.values || metric.values.length === 0) {
      return '--';
    }

    const min = Math.min(...metric.values);
    return `${min.toFixed(1)}${metric.unit}`;
  }

  // Computed
  const hasMetrics = computed(() => 
    state.value.cpu !== null || state.value.memory !== null
  );

  const isLoading = computed(() => state.value.loading);
  const error = computed(() => state.value.error);
  const currentTimeRange = computed(() => state.value.timeRange);

  return {
    // State
    state,
    cpu: computed(() => state.value.cpu),
    memory: computed(() => state.value.memory),
    hasMetrics,
    isLoading,
    error,
    currentTimeRange,

    // Actions
    loadMetrics,
    loadSingleMetric,
    refresh,
    changeTimeRange,
    clearMetrics,

    // Utilities
    getFormattedValue,
    getLatestValue,
    getAverageValue,
    getMaxValue,
    getMinValue,
  };
}
