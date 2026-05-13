/**
 * Large Display Overview - Type Definitions
 * 监控数据使用实时查询，不入库也不查数据库
 */

/**
 * Management Platform Types
 */
export type PlatformType = 'WORKSPACE' | 'CAS' | 'UIS';

/**
 * Platform Status
 */
export type PlatformStatus = 'NORMAL' | 'WARNING' | 'ERROR' | 'OFFLINE';

/**
 * Resource Types in the hierarchy
 */
export type ResourceType = 
  | 'DESKTOP_POOL'    // 桌面池
  | 'TERMINAL'        // 终端
  | 'CLUSTER'         // 集群
  | 'HOST'            // 主机
  | 'VM';             // 虚拟机

/**
 * Metric Types for monitoring
 */
export type MetricType = 'CPU' | 'MEMORY' | 'DISK' | 'NETWORK';

/**
 * Time range options for metric queries
 */
export type TimeRange = '1h' | '3h' | '6h' | '12h' | '24h' | '7d';

/**
 * Management Platform DTO
 */
export interface PlatformDTO {
  id: number;
  name: string;
  type: PlatformType;
  description?: string;
  status: PlatformStatus;
  icon?: string;
}

/**
 * Resource Item DTO (Desktop Pool, Terminal, Cluster, Host, VM)
 */
export interface ResourceItemDTO {
  id: number;
  name: string;
  type: ResourceType;
  parentId?: number;
  parentType?: ResourceType;
  status?: PlatformStatus;
  ipAddress?: string;
  /** Only available for HOST and VM */
  cpuUsage?: number;
  /** Only available for HOST and VM */
  memoryUsage?: number;
  /** Only available for HOST and VM */
  metrics?: MetricSummary;
  children?: ResourceItemDTO[];
  expanded?: boolean;
  loading?: boolean;
}

/**
 * Metric Summary for HOST and VM
 */
export interface MetricSummary {
  cpu: number;
  memory: number;
  disk?: number;
  network?: number;
}

/**
 * Metric Trend Data Point
 */
export interface MetricDataPoint {
  timestamp: number;
  value: number;
}

/**
 * Metric Trend DTO - Real-time collected data
 */
export interface MetricTrendDTO {
  resourceId: number;
  metricType: MetricType;
  unit: string;
  timeRange: TimeRange;
  values: number[];
  timestamps: number[];
}

/**
 * Chart Data formatted for ECharts
 */
export interface ChartSeriesData {
  name: string;
  type: 'line';
  data: [number, number][]; // [timestamp, value]
  smooth?: boolean;
  itemStyle?: {
    color?: string;
  };
  areaStyle?: {
    color?: {
      type: 'linear';
      x: number;
      y: number;
      x2: number;
      y2: number;
      colorStops: Array<{ offset: number; color: string }>;
    };
  };
}

/**
 * Resource Tree Node for UI
 */
export interface ResourceTreeNode {
  id: number;
  name: string;
  type: ResourceType;
  status?: PlatformStatus;
  ipAddress?: string;
  metrics?: MetricSummary;
  children: ResourceTreeNode[];
  expanded: boolean;
  selected: boolean;
  loading: boolean;
  /** Level in tree: 0=Platform, 1=Pool/Terminal/Cluster, 2=Host, 3=VM */
  level: number;
}

/**
 * API Response wrapper
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

/**
 * API Error Response
 */
export interface ApiError {
  code: number;
  message: string;
  data?: null;
}

/**
 * Platform list API response
 */
export type PlatformListResponse = ApiResponse<PlatformDTO[]>;

/**
 * Resource list API response
 */
export type ResourceListResponse = ApiResponse<ResourceItemDTO[]>;

/**
 * Metric trend API response
 */
export type MetricTrendResponse = ApiResponse<MetricTrendDTO>;

/**
 * Large Display View State
 */
export interface LargeDisplayState {
  platforms: PlatformDTO[];
  selectedPlatform: PlatformDTO | null;
  selectedResource: ResourceItemDTO | null;
  selectedResourceType: ResourceType | null;
  resourceTree: ResourceTreeNode[];
  cpuMetrics: MetricTrendDTO | null;
  memoryMetrics: MetricTrendDTO | null;
  loading: boolean;
  error: string | null;
}

/**
 * Selectable resource types (those with monitoring data)
 */
export const MONITORABLE_TYPES: ResourceType[] = ['HOST', 'VM'];

/**
 * Check if a resource type has monitoring capabilities
 */
export function isMonitorableType(type: ResourceType): boolean {
  return MONITORABLE_TYPES.includes(type);
}

/**
 * Get display name for resource type
 */
export function getResourceTypeName(type: ResourceType): string {
  const names: Record<ResourceType, string> = {
    DESKTOP_POOL: '桌面池',
    TERMINAL: '终端',
    CLUSTER: '集群',
    HOST: '主机',
    VM: '虚拟机',
  };
  return names[type] || type;
}

/**
 * Get display name for platform type
 */
export function getPlatformTypeName(type: PlatformType): string {
  const names: Record<PlatformType, string> = {
    WORKSPACE: 'Workspace',
    CAS: 'CAS',
    UIS: 'UIS',
  };
  return names[type] || type;
}
