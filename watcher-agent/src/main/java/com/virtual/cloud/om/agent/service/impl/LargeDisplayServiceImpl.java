package com.virtual.cloud.om.agent.service.impl;

import com.virtual.cloud.om.agent.dto.MetricTrendDTO;
import com.virtual.cloud.om.agent.dto.PlatformDTO;
import com.virtual.cloud.om.agent.dto.ResourceItemDTO;
import com.virtual.cloud.om.agent.service.LargeDisplayService;
import com.virtual.cloud.om.agent.service.report.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 大屏概览服务实现类
 * 监控数据使用实时采集，不入库也不查数据库
 */
@Service
public class LargeDisplayServiceImpl implements LargeDisplayService {
    
    @Autowired
    private MetricService metricService;
    
    @Override
    public List<PlatformDTO> getPlatformList() {
        // TODO: 从数据库查询管理平台列表
        // 暂时返回模拟数据
        PlatformDTO workspace = new PlatformDTO();
        workspace.setId(1L);
        workspace.setName("Workspace");
        workspace.setType("WORKSPACE");
        workspace.setDescription("Workspace 管理平台");
        workspace.setStatus("NORMAL");
        
        PlatformDTO cas = new PlatformDTO();
        cas.setId(2L);
        cas.setName("CAS");
        cas.setType("CAS");
        cas.setDescription("CAS 管理平台");
        cas.setStatus("NORMAL");
        
        PlatformDTO uis = new PlatformDTO();
        uis.setId(3L);
        uis.setName("UIS");
        uis.setType("UIS");
        uis.setDescription("UIS 管理平台");
        uis.setStatus("NORMAL");
        
        return Arrays.asList(workspace, cas, uis);
    }
    
    @Override
    public List<ResourceItemDTO> getPlatformChildren(Long platformId) {
        // TODO: 从数据库查询平台下的子资源（桌面池、终端、集群）
        return Arrays.asList();
    }
    
    @Override
    public List<ResourceItemDTO> getClusterHosts(Long clusterId) {
        // TODO: 从数据库查询集群下的主机列表
        return Arrays.asList();
    }
    
    @Override
    public List<ResourceItemDTO> getHostVMs(Long hostId) {
        // TODO: 从数据库查询主机下的虚拟机列表
        return Arrays.asList();
    }
    
    @Override
    public MetricTrendDTO getMetricsTrend(Long resourceId, String metricType, String timeRange) {
        // 实时采集指标数据，不入库也不查数据库
        MetricTrendDTO dto = new MetricTrendDTO();
        dto.setResourceId(resourceId);
        dto.setMetricType(metricType);
        dto.setUnit("%");
        dto.setTimeRange(timeRange);
        
        List<Map<String, Object>> metrics = metricService.listMetrics(
            String.valueOf(resourceId), 
            null, 
            metricType, 
            null, 
            null, 
            1, 
            100
        );
        
        if (metrics != null && !metrics.isEmpty()) {
            List<Double> values = new ArrayList<>();
            List<Long> timestamps = new ArrayList<>();
            
            for (Map<String, Object> metric : metrics) {
                Object value = metric.get("metricValue");
                if (value != null) {
                    if (value instanceof Number) {
                        values.add(((Number) value).doubleValue());
                    } else {
                        try {
                            values.add(Double.parseDouble(value.toString()));
                        } catch (NumberFormatException e) {
                            values.add(0.0);
                        }
                    }
                }
                
                Object reportTime = metric.get("reportTime");
                if (reportTime instanceof Date) {
                    timestamps.add(((Date) reportTime).getTime());
                } else if (reportTime != null) {
                    timestamps.add(System.currentTimeMillis());
                } else {
                    timestamps.add(System.currentTimeMillis());
                }
            }
            
            dto.setValues(values);
            dto.setTimestamps(timestamps);
        } else {
            // 如果没有数据，返回模拟数据用于测试
            dto.setValues(Arrays.asList(25.5, 30.2, 28.8, 35.6, 40.1));
            dto.setTimestamps(Arrays.asList(
                System.currentTimeMillis() - 400 * 60 * 1000,
                System.currentTimeMillis() - 300 * 60 * 1000,
                System.currentTimeMillis() - 200 * 60 * 1000,
                System.currentTimeMillis() - 100 * 60 * 1000,
                System.currentTimeMillis()
            ));
        }
        
        return dto;
    }
}