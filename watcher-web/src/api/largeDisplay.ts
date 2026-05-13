/**
 * Large Display Overview - API Layer
 * 监控数据使用实时查询，不入库也不查数据库
 */
import request from '@/utils/system/request';
import type {
  PlatformDTO,
  ResourceItemDTO,
  MetricTrendDTO,
  TimeRange,
} from '@/views/large-display/types';

/**
 * API base path
 */
const BASE_URL = '/api';

/**
 * Get management platform list
 * @returns Promise<PlatformDTO[]>
 */
export async function getPlatformList(): Promise<PlatformDTO[]> {
  const response = await request.get<{ data: PlatformDTO[] }>(`${BASE_URL}/platform/list`);
  return response.data?.data || [];
}

/**
 * Get platform children (Desktop Pools, Terminals, Clusters)
 * @param platformId - Platform ID
 * @returns Promise<ResourceItemDTO[]>
 */
export async function getPlatformChildren(platformId: number): Promise<ResourceItemDTO[]> {
  const response = await request.get<{ data: ResourceItemDTO[] }>(
    `${BASE_URL}/platform/${platformId}/children`
  );
  return response.data?.data || [];
}

/**
 * Get cluster hosts
 * @param clusterId - Cluster ID
 * @returns Promise<ResourceItemDTO[]>
 */
export async function getClusterHosts(clusterId: number): Promise<ResourceItemDTO[]> {
  const response = await request.get<{ data: ResourceItemDTO[] }>(
    `${BASE_URL}/cluster/${clusterId}/hosts`
  );
  return response.data?.data || [];
}

/**
 * Get host virtual machines
 * @param hostId - Host ID
 * @returns Promise<ResourceItemDTO[]>
 */
export async function getHostVMs(hostId: number): Promise<ResourceItemDTO[]> {
  const response = await request.get<{ data: ResourceItemDTO[] }>(
    `${BASE_URL}/host/${hostId}/vms`
  );
  return response.data?.data || [];
}

/**
 * Get metric trend data (Real-time collection)
 * @param resourceId - Resource ID (Host or VM)
 * @param metricType - Metric type: CPU, MEMORY
 * @param timeRange - Time range: 1h, 3h, 6h, 12h, 24h
 * @returns Promise<MetricTrendDTO>
 */
export async function getMetricsTrend(
  resourceId: number,
  metricType: string,
  timeRange: TimeRange = '1h'
): Promise<MetricTrendDTO> {
  const response = await request.get<{ data: MetricTrendDTO }>(
    `${BASE_URL}/metrics/trend`,
    { params: { resourceId, metricType, timeRange } }
  );
  return response.data?.data || {
    resourceId,
    metricType: metricType as MetricTrendDTO['metricType'],
    unit: '%',
    timeRange,
    values: [],
    timestamps: [],
  };
}

/**
 * Get multiple metrics at once (CPU and Memory)
 * @param resourceId - Resource ID (Host or VM)
 * @param timeRange - Time range
 * @returns Promise<{ cpu: MetricTrendDTO; memory: MetricTrendDTO }>
 */
export async function getMultipleMetrics(
  resourceId: number,
  timeRange: TimeRange = '1h'
): Promise<{ cpu: MetricTrendDTO; memory: MetricTrendDTO }> {
  const [cpu, memory] = await Promise.all([
    getMetricsTrend(resourceId, 'CPU', timeRange),
    getMetricsTrend(resourceId, 'MEMORY', timeRange),
  ]);
  return { cpu, memory };
}
