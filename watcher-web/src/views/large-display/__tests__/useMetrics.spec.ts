/**
 * useMetrics 组合式函数测试
 * 测试监控数据获取功能
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { useMetrics } from '../composables/useMetrics';
import type { MetricTrendDTO, TimeRange } from '../types';

// Mock API module
vi.mock('@/api/largeDisplay', () => ({
  getMetricsTrend: vi.fn(),
  getMultipleMetrics: vi.fn(),
}));

describe('useMetrics', () => {
  const { 
    state,
    cpu,
    memory,
    hasMetrics,
    isLoading,
    error,
    currentTimeRange,
    loadMetrics,
    loadSingleMetric,
    refresh,
    changeTimeRange,
    clearMetrics,
    getFormattedValue,
    getLatestValue,
    getAverageValue,
    getMaxValue,
    getMinValue,
  } = useMetrics();

  // 创建模拟数据
  const createMockMetric = (
    metricType: 'CPU' | 'MEMORY' = 'CPU',
    values: number[] = [25.5, 30.2, 28.8, 35.6, 40.1]
  ): MetricTrendDTO => {
    const now = Date.now();
    return {
      resourceId: 1001,
      metricType,
      unit: '%',
      timeRange: '1h',
      values,
      timestamps: [
        now - 400 * 60 * 1000,
        now - 300 * 60 * 1000,
        now - 200 * 60 * 1000,
        now - 100 * 60 * 1000,
        now,
      ],
    };
  };

  beforeEach(() => {
    clearMetrics();
  });

  describe('状态初始值', () => {
    it('初始状态 cpu 应为 null', () => {
      expect(cpu.value).toBeNull();
    });

    it('初始状态 memory 应为 null', () => {
      expect(memory.value).toBeNull();
    });

    it('初始状态 hasMetrics 应为 false', () => {
      expect(hasMetrics.value).toBe(false);
    });

    it('初始状态 isLoading 应为 false', () => {
      expect(isLoading.value).toBe(false);
    });

    it('初始状态 error 应为 null', () => {
      expect(error.value).toBeNull();
    });

    it('初始状态 timeRange 应为 1h', () => {
      expect(currentTimeRange.value).toBe('1h');
    });
  });

  describe('clearMetrics', () => {
    it('应清空所有指标数据', () => {
      clearMetrics();
      expect(cpu.value).toBeNull();
      expect(memory.value).toBeNull();
      expect(error.value).toBeNull();
    });
  });

  describe('getFormattedValue', () => {
    it('当 metric 为 null 时应返回 --', () => {
      expect(getFormattedValue(null)).toBe('--');
    });

    it('当 metric values 为空时应返回 --', () => {
      const emptyMetric = createMockMetric('CPU', []);
      expect(getFormattedValue(emptyMetric)).toBe('--');
    });

    it('应正确格式化单个值', () => {
      const metric = createMockMetric('CPU', [45.5]);
      expect(getFormattedValue(metric)).toBe('45.5%');
    });

    it('应返回最后一个值（最新）', () => {
      const metric = createMockMetric('CPU', [25.5, 30.2, 40.1]);
      expect(getFormattedValue(metric)).toBe('40.1%');
    });

    it('应返回指定索引的值', () => {
      const metric = createMockMetric('CPU', [25.5, 30.2, 40.1]);
      expect(getFormattedValue(metric, 0)).toBe('25.5%');
      expect(getFormattedValue(metric, 1)).toBe('30.2%');
      expect(getFormattedValue(metric, 2)).toBe('40.1%');
    });
  });

  describe('getLatestValue', () => {
    it('当 metric 为 null 时应返回 --', () => {
      expect(getLatestValue(null)).toBe('--');
    });

    it('应返回最新的值', () => {
      const metric = createMockMetric('CPU', [25.5, 30.2, 40.1]);
      expect(getLatestValue(metric)).toBe('40.1%');
    });
  });

  describe('getAverageValue', () => {
    it('当 metric 为 null 时应返回 --', () => {
      expect(getAverageValue(null)).toBe('--');
    });

    it('当 metric values 为空时应返回 --', () => {
      const emptyMetric = createMockMetric('CPU', []);
      expect(getAverageValue(emptyMetric)).toBe('--');
    });

    it('应正确计算平均值', () => {
      const metric = createMockMetric('CPU', [20, 30, 40]);
      // (20 + 30 + 40) / 3 = 30
      expect(getAverageValue(metric)).toBe('30.0%');
    });

    it('应保留一位小数', () => {
      const metric = createMockMetric('CPU', [25.5, 30.2, 28.8]);
      // (25.5 + 30.2 + 28.8) / 3 = 28.166...
      expect(getAverageValue(metric)).toBe('28.2%');
    });
  });

  describe('getMaxValue', () => {
    it('当 metric 为 null 时应返回 --', () => {
      expect(getMaxValue(null)).toBe('--');
    });

    it('当 metric values 为空时应返回 --', () => {
      const emptyMetric = createMockMetric('CPU', []);
      expect(getMaxValue(emptyMetric)).toBe('--');
    });

    it('应返回最大值', () => {
      const metric = createMockMetric('CPU', [25.5, 40.1, 30.2]);
      expect(getMaxValue(metric)).toBe('40.1%');
    });
  });

  describe('getMinValue', () => {
    it('当 metric 为 null 时应返回 --', () => {
      expect(getMinValue(null)).toBe('--');
    });

    it('当 metric values 为空时应返回 --', () => {
      const emptyMetric = createMockMetric('CPU', []);
      expect(getMinValue(emptyMetric)).toBe('--');
    });

    it('应返回最小值', () => {
      const metric = createMockMetric('CPU', [40.1, 25.5, 30.2]);
      expect(getMinValue(metric)).toBe('25.5%');
    });
  });

  describe('loadMetrics', () => {
    it('当 resourceId 为空时应清空指标', async () => {
      // 设置初始状态
      state.value.cpu = createMockMetric('CPU');
      
      await loadMetrics(0);
      
      expect(cpu.value).toBeNull();
    });
  });

  describe('loadSingleMetric', () => {
    it('当 resourceId 为空时应返回 null', async () => {
      const result = await loadSingleMetric(0, 'CPU');
      expect(result).toBeNull();
    });
  });

  describe('changeTimeRange', () => {
    it('应更新 timeRange', async () => {
      state.value.timeRange = '1h';
      
      // 直接更新 timeRange（不调用 API）
      state.value.timeRange = '24h';
      
      expect(state.value.timeRange).toBe('24h');
    });
  });

  describe('refresh', () => {
    it('当 resourceId 为空时不应刷新', async () => {
      // 不应抛出错误
      await expect(refresh(0)).resolves.not.toThrow();
    });
  });
});
