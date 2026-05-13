package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.agent.dto.MetricTrendDTO;
import com.virtual.cloud.om.agent.dto.PlatformDTO;
import com.virtual.cloud.om.agent.dto.ResourceItemDTO;
import com.virtual.cloud.om.agent.service.LargeDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大屏概览 API 控制器
 * 提供资源选择和监控图表数据的 REST API
 */
@RestController
@RequestMapping("/api")
public class LargeDisplayController {
    
    @Autowired
    private LargeDisplayService largeDisplayService;
    
    /**
     * 获取管理平台列表（Workspace/CAS/UIS）
     * GET /api/platform/list
     */
    @GetMapping("/platform/list")
    public Map<String, Object> getPlatformList() {
        List<PlatformDTO> platforms = largeDisplayService.getPlatformList();
        return buildSuccessResponse(platforms);
    }
    
    /**
     * 获取平台下的子资源（桌面池、终端、集群）
     * GET /api/platform/{id}/children
     */
    @GetMapping("/platform/{id}/children")
    public Map<String, Object> getPlatformChildren(@PathVariable("id") Long platformId) {
        List<ResourceItemDTO> children = largeDisplayService.getPlatformChildren(platformId);
        return buildSuccessResponse(children);
    }
    
    /**
     * 获取集群下的主机列表
     * GET /api/cluster/{id}/hosts
     */
    @GetMapping("/cluster/{id}/hosts")
    public Map<String, Object> getClusterHosts(@PathVariable("id") Long clusterId) {
        List<ResourceItemDTO> hosts = largeDisplayService.getClusterHosts(clusterId);
        return buildSuccessResponse(hosts);
    }
    
    /**
     * 获取主机下的虚拟机列表
     * GET /api/host/{id}/vms
     */
    @GetMapping("/host/{id}/vms")
    public Map<String, Object> getHostVMs(@PathVariable("id") Long hostId) {
        List<ResourceItemDTO> vms = largeDisplayService.getHostVMs(hostId);
        return buildSuccessResponse(vms);
    }
    
    /**
     * 获取指标趋势数据
     * GET /api/metrics/trend
     */
    @GetMapping("/metrics/trend")
    public Map<String, Object> getMetricsTrend(
            @RequestParam(required = false) Long resourceId,
            @RequestParam(required = false) String metricType,
            @RequestParam(defaultValue = "1h") String timeRange) {
        // 参数验证
        if (resourceId == null) {
            return buildErrorResponse("resourceId 参数不能为空");
        }
        if (metricType == null || metricType.trim().isEmpty()) {
            return buildErrorResponse("metricType 参数不能为空");
        }
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(resourceId, metricType, timeRange);
        return buildSuccessResponse(trend);
    }
    
    /**
     * 构建错误响应
     */
    private Map<String, Object> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
    
    /**
     * 构建成功响应
     */
    private Map<String, Object> buildSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }
}