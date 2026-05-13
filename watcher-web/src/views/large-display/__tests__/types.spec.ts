/**
 * 类型定义测试
 * 测试类型工具函数
 */
import { describe, it, expect } from 'vitest';
import { 
  isMonitorableType, 
  getResourceTypeName, 
  getPlatformTypeName,
  MONITORABLE_TYPES,
} from '../types';

describe('类型工具函数', () => {
  describe('MONITORABLE_TYPES', () => {
    it('应只包含 HOST 和 VM', () => {
      expect(MONITORABLE_TYPES).toEqual(['HOST', 'VM']);
    });
  });

  describe('isMonitorableType', () => {
    it('HOST 应返回 true', () => {
      expect(isMonitorableType('HOST')).toBe(true);
    });

    it('VM 应返回 true', () => {
      expect(isMonitorableType('VM')).toBe(true);
    });

    it('CLUSTER 应返回 false', () => {
      expect(isMonitorableType('CLUSTER')).toBe(false);
    });

    it('TERMINAL 应返回 false', () => {
      expect(isMonitorableType('TERMINAL')).toBe(false);
    });

    it('DESKTOP_POOL 应返回 false', () => {
      expect(isMonitorableType('DESKTOP_POOL')).toBe(false);
    });
  });

  describe('getResourceTypeName', () => {
    it('应返回 DESKTOP_POOL 的中文名称', () => {
      expect(getResourceTypeName('DESKTOP_POOL')).toBe('桌面池');
    });

    it('应返回 TERMINAL 的中文名称', () => {
      expect(getResourceTypeName('TERMINAL')).toBe('终端');
    });

    it('应返回 CLUSTER 的中文名称', () => {
      expect(getResourceTypeName('CLUSTER')).toBe('集群');
    });

    it('应返回 HOST 的中文名称', () => {
      expect(getResourceTypeName('HOST')).toBe('主机');
    });

    it('应返回 VM 的中文名称', () => {
      expect(getResourceTypeName('VM')).toBe('虚拟机');
    });

    it('未知类型应返回原始值', () => {
      expect(getResourceTypeName('UNKNOWN' as any)).toBe('UNKNOWN');
    });
  });

  describe('getPlatformTypeName', () => {
    it('应返回 WORKSPACE 的名称', () => {
      expect(getPlatformTypeName('WORKSPACE')).toBe('Workspace');
    });

    it('应返回 CAS 的名称', () => {
      expect(getPlatformTypeName('CAS')).toBe('CAS');
    });

    it('应返回 UIS 的名称', () => {
      expect(getPlatformTypeName('UIS')).toBe('UIS');
    });

    it('未知类型应返回原始值', () => {
      expect(getPlatformTypeName('UNKNOWN' as any)).toBe('UNKNOWN');
    });
  });
});

describe('接口结构', () => {
  describe('PlatformDTO', () => {
    it('应包含必要的字段', () => {
      const platform = {
        id: 1,
        name: 'Workspace',
        type: 'WORKSPACE' as const,
        status: 'NORMAL' as const,
      };

      expect(platform).toHaveProperty('id');
      expect(platform).toHaveProperty('name');
      expect(platform).toHaveProperty('type');
      expect(platform).toHaveProperty('status');
    });
  });

  describe('ResourceItemDTO', () => {
    it('应包含必要的字段', () => {
      const resource = {
        id: 1,
        name: 'Host-01',
        type: 'HOST' as const,
      };

      expect(resource).toHaveProperty('id');
      expect(resource).toHaveProperty('name');
      expect(resource).toHaveProperty('type');
    });
  });

  describe('MetricTrendDTO', () => {
    it('应包含必要的字段', () => {
      const metric: any = {
        resourceId: 1,
        metricType: 'CPU',
        unit: '%',
        timeRange: '1h',
        values: [25.5, 30.2],
        timestamps: [Date.now() - 60000, Date.now()],
      };

      expect(metric).toHaveProperty('resourceId');
      expect(metric).toHaveProperty('metricType');
      expect(metric).toHaveProperty('unit');
      expect(metric).toHaveProperty('timeRange');
      expect(metric).toHaveProperty('values');
      expect(metric).toHaveProperty('timestamps');
    });
  });

  describe('MetricSummary', () => {
    it('应包含 CPU 和内存字段', () => {
      const summary = {
        cpu: 45.5,
        memory: 60.2,
      };

      expect(summary).toHaveProperty('cpu');
      expect(summary).toHaveProperty('memory');
    });
  });

  describe('ResourceTreeNode', () => {
    it('应包含树节点必要字段', () => {
      const node = {
        id: 1,
        name: 'Host-01',
        type: 'HOST' as const,
        children: [],
        expanded: false,
        selected: false,
        loading: false,
        level: 2,
      };

      expect(node).toHaveProperty('id');
      expect(node).toHaveProperty('name');
      expect(node).toHaveProperty('type');
      expect(node).toHaveProperty('children');
      expect(node).toHaveProperty('expanded');
      expect(node).toHaveProperty('selected');
      expect(node).toHaveProperty('loading');
      expect(node).toHaveProperty('level');
    });
  });
});
