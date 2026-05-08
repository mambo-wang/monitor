import request from "@/utils/system/request";

export interface MetricQueryParams {
  resourceId?: string
  metricType?: string
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}

export interface ResourceTreeNode {
  id: string
  label: string
  type: 'cluster' | 'host' | 'domain' | 'desktop_pool' | 'storage_pool' | 'vm'
  platform: 'cas' | 'workspace' | 'uis' | 'onestor'
  children?: ResourceTreeNode[]
  status?: 'healthy' | 'warning' | 'error'
  parentId?: string
}

export interface MetricData {
  timestamp: number
  value: number
}

export interface MetricResponse {
  metricType: string
  data: MetricData[]
  unit?: string
}

export interface MetricType {
  type: string
  name: string
  unit?: string
}

export interface Platform {
  id: string
  name: string
  value: string
}

export function getMetricData(params: MetricQueryParams) {
  return request({
    url: "/metric/list",
    method: "get",
    params
  });
}

export function getResourceTree() {
  return request({
    url: "/resource/list",
    method: "get"
  });
}

export function getMetricTypes() {
  return request({
    url: "/metric/types",
    method: "get"
  });
}

export function getPlatforms() {
  return request({
    url: "/metric/platforms",
    method: "get"
  });
}

export function getResourceChildren(resourceId: string) {
  return request({
    url: `/resource/children/${resourceId}`,
    method: "get"
  });
}

export default {
  getMetricData,
  getResourceTree,
  getMetricTypes,
  getPlatforms,
  getResourceChildren
};
