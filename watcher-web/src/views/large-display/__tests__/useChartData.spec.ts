/**
 * useChartData 组合式函数测试
 * 测试图表数据格式化功能
 */
import { describe, it, expect, vi } from 'vitest';
import { useChartData } from '../composables/useChartData';
import type { MetricTrendDTO } from '../types';

describe('useChartData', () => {
  // 测试数据
  const createMockMetric = (metricType: 'CPU' | 'MEMORY'): MetricTrendDTO => {
    const now = Date.now();
    return {
      resourceId: 1001,
      metricType,
      unit: '%',
      timeRange: '1h',
      values: [25.5, 30.2, 28.8, 35.6, 40.1],
      timestamps: [
        now - 400 * 60 * 1000,
        now - 300 * 60 * 1000,
        now - 200 * 60 * 1000,
        now - 100 * 60 * 1000,
        now,
      ],
    };
  };

  const { 
    formatTime, 
    getMetricTypeName, 
    getMetricColor, 
    getGradientColors,
    getXAxisFormatter,
    getTooltipFormatter,
  } = useChartData(
    { value: null } as any,
    { value: null } as any
  );

  describe('formatTime', () => {
    it('应正确格式化时间为 HH:mm 格式', () => {
      const timestamp = new Date('2026-05-13T10:30:00').getTime();
      const result = formatTime(timestamp, 'HH:mm');
      expect(result).toBe('10:30');
    });

    it('应正确格式化时间为 HH:mm:ss 格式', () => {
      const timestamp = new Date('2026-05-13T10:30:45').getTime();
      const result = formatTime(timestamp, 'HH:mm:ss');
      expect(result).toBe('10:30:45');
    });

    it('应正确格式化时间为 MM-DD HH:mm 格式', () => {
      const timestamp = new Date('2026-05-13T10:30:00').getTime();
      const result = formatTime(timestamp, 'MM-DD HH:mm');
      expect(result).toBe('05-13 10:30');
    });

    it('应正确格式化时间为 MM-DD HH:mm:ss 格式', () => {
      const timestamp = new Date('2026-05-13T10:30:45').getTime();
      const result = formatTime(timestamp, 'MM-DD HH:mm:ss');
      expect(result).toBe('05-13 10:30:45');
    });

    it('应使用默认格式 HH:mm', () => {
      const timestamp = new Date('2026-05-13T10:30:00').getTime();
      const result = formatTime(timestamp);
      expect(result).toBe('10:30');
    });
  });

  describe('getMetricTypeName', () => {
    it('应返回 CPU 的中文名称', () => {
      expect(getMetricTypeName('CPU')).toBe('CPU 利用率');
    });

    it('应返回 MEMORY 的中文名称', () => {
      expect(getMetricTypeName('MEMORY')).toBe('内存利用率');
    });

    it('应返回 DISK 的中文名称', () => {
      expect(getMetricTypeName('DISK')).toBe('磁盘利用率');
    });

    it('应返回 NETWORK 的中文名称', () => {
      expect(getMetricTypeName('NETWORK')).toBe('网络流量');
    });

    it('应返回未知类型的原始名称', () => {
      expect(getMetricTypeName('UNKNOWN')).toBe('UNKNOWN');
    });
  });

  describe('getMetricColor', () => {
    it('应返回 CPU 的颜色', () => {
      expect(getMetricColor('CPU')).toBe('#5470C6');
    });

    it('应返回 MEMORY 的颜色', () => {
      expect(getMetricColor('MEMORY')).toBe('#73C0DE');
    });

    it('应返回 DISK 的颜色', () => {
      expect(getMetricColor('DISK')).toBe('#EE6666');
    });

    it('应返回 NETWORK 的颜色', () => {
      expect(getMetricColor('NETWORK')).toBe('#91CC75');
    });

    it('应返回默认颜色', () => {
      expect(getMetricColor('UNKNOWN')).toBe('#5470C6');
    });
  });

  describe('getGradientColors', () => {
    it('应返回 CPU 的渐变色', () => {
      const colors = getGradientColors('CPU');
      expect(colors).toEqual(['rgba(84, 112, 198, 0.6)', 'rgba(84, 112, 198, 0.1)']);
    });

    it('应返回 MEMORY 的渐变色', () => {
      const colors = getGradientColors('MEMORY');
      expect(colors).toEqual(['rgba(115, 192, 222, 0.6)', 'rgba(115, 192, 222, 0.1)']);
    });

    it('应返回默认渐变色', () => {
      const colors = getGradientColors('UNKNOWN');
      expect(colors).toEqual(['rgba(84, 112, 198, 0.6)', 'rgba(84, 112, 198, 0.1)']);
    });
  });

  describe('getXAxisFormatter', () => {
    it('应返回时间格式化函数', () => {
      const formatter = getXAxisFormatter();
      expect(typeof formatter).toBe('function');
    });

    it('格式化函数应返回 HH:mm 格式', () => {
      const formatter = getXAxisFormatter();
      const timestamp = new Date('2026-05-13T10:30:00').getTime();
      expect(formatter(timestamp)).toBe('10:30');
    });
  });

  describe('getTooltipFormatter', () => {
    it('应返回 tooltip 格式化函数', () => {
      const formatter = getTooltipFormatter();
      expect(typeof formatter).toBe('function');
    });

    it('格式化函数应正确格式化数据', () => {
      const formatter = getTooltipFormatter();
      const params = [{
        seriesName: 'CPU 利用率',
        value: [new Date('2026-05-13T10:30:00').getTime(), 45.5],
      }];
      const result = formatter(params);
      expect(result).toContain('CPU 利用率');
      expect(result).toContain('05-13');
      expect(result).toContain('45.50%');
    });

    it('格式化函数应处理空参数', () => {
      const formatter = getTooltipFormatter();
      const result = formatter([]);
      expect(result).toBe('');
    });
  });
});
