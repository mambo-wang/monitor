package com.virtual.cloud.om.agent.service;

import com.virtual.cloud.om.agent.dto.MetricTrendDTO;
import com.virtual.cloud.om.agent.dto.PlatformDTO;
import com.virtual.cloud.om.agent.dto.ResourceItemDTO;

import java.util.List;

/**
 * 大屏概览服务接口
 */
public interface LargeDisplayService {
    
    /**
     * 获取管理平台列表（Workspace/CAS/UIS）
     * @return 管理平台列表
     */
    List<PlatformDTO> getPlatformList();
    
    /**
     * 获取平台下的子资源（桌面池、终端、集群）
     * @param platformId 平台 ID
     * @return 子资源列表
     */
    List<ResourceItemDTO> getPlatformChildren(Long platformId);
    
    /**
     * 获取集群下的主机列表
     * @param clusterId 集群 ID
     * @return 主机列表
     */
    List<ResourceItemDTO> getClusterHosts(Long clusterId);
    
    /**
     * 获取主机下的虚拟机列表
     * @param hostId 主机 ID
     * @return 虚拟机列表
     */
    List<ResourceItemDTO> getHostVMs(Long hostId);
    
    /**
     * 获取指标趋势数据
     * @param resourceId 资源 ID
     * @param metricType 指标类型（CPU/MEMORY）
     * @param timeRange 时间范围（1h/24h/7d）
     * @return 指标趋势数据
     */
    MetricTrendDTO getMetricsTrend(Long resourceId, String metricType, String timeRange);
}