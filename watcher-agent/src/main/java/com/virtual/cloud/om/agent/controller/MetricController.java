package com.virtual.cloud.om.agent.controller;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.entity.mysql.MetricData;
import com.virtual.cloud.om.sdk.entity.mysql.Resource;
import com.virtual.cloud.om.sdk.mapper.MetricDataMapper;
import com.virtual.cloud.om.sdk.mapper.ResourceMapper;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 指标查询 REST 接口
 */
@RestController
@RequestMapping("/metric")
@Api(tags = "指标查询")
@Slf4j
@CrossOrigin
public class MetricController {

    @Autowired
    private MetricDataMapper metricDataMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @GetMapping("/types")
    @ApiOperation(value = "查询支持的指标类型")
    public RpcResult<List<Map<String, String>>> getMetricTypes() {
        List<Map<String, String>> types = new ArrayList<>();
        for (ReportMetricEnum metric : ReportMetricEnum.values()) {
            Map<String, String> type = new HashMap<>();
            type.put("code", metric.name());
            type.put("name", metric.name());
            type.put("desc", metric.name());
            type.put("static", String.valueOf(metric.staticMetric));
            types.add(type);
        }
        return RpcResult.success(types);
    }

    @GetMapping("/platforms")
    @ApiOperation(value = "查询支持的平台类型")
    public RpcResult<List<Map<String, String>>> getPlatforms() {
        List<Map<String, String>> platforms = new ArrayList<>();
        for (ReportResourceEnum platform : ReportResourceEnum.values()) {
            if (platform.name().equals("hccAgent")) {
                continue;
            }
            Map<String, String> p = new HashMap<>();
            p.put("code", platform.name().toLowerCase());
            p.put("name", platform.name());
            platforms.add(p);
        }
        return RpcResult.success(platforms);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询指标数据列表")
    public RpcListLoadResult<MetricData> list(
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetricData> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        if (resourceId != null && !resourceId.isEmpty()) {
            wrapper.eq(MetricData::getResourceId, resourceId);
        }
        if (platform != null && !platform.isEmpty()) {
            wrapper.eq(MetricData::getPlatform, platform);
        }
        if (metricType != null && !metricType.isEmpty()) {
            wrapper.eq(MetricData::getMetricType, metricType);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(MetricData::getReportTime, LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(MetricData::getReportTime, LocalDateTime.parse(endTime));
        }
        
        wrapper.orderByDesc(MetricData::getReportTime);
        List<MetricData> records = metricDataMapper.selectList(wrapper);
        return RpcListLoadResult.success(records);
    }

    @GetMapping("/latest/{resourceId}")
    @ApiOperation(value = "查询资源最新指标数据")
    public RpcResult<List<MetricData>> getLatestMetrics(@PathVariable String resourceId) {
        List<MetricData> metrics = new ArrayList<>();
        
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return RpcResult.fail("资源不存在");
        }
        
        // 查询该资源最新的各类指标
        Set<String> metricTypes = new HashSet<>();
        for (DataReportTypeByMetricEnum type : DataReportTypeByMetricEnum.values()) {
            metricTypes.add(type.name());
        }
        
        for (String metricType : metricTypes) {
            com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetricData> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
            wrapper.eq(MetricData::getResourceId, resourceId)
                   .eq(MetricData::getMetricType, metricType)
                   .orderByDesc(MetricData::getReportTime)
                   .last("LIMIT 1");
            
            MetricData metric = metricDataMapper.selectOne(wrapper);
            if (metric != null) {
                metrics.add(metric);
            }
        }
        
        return RpcResult.success(metrics);
    }

    @GetMapping("/trend/{resourceId}/{metricType}")
    @ApiOperation(value = "查询指标趋势数据")
    public RpcResult<List<MetricData>> getMetricTrend(
            @PathVariable String resourceId,
            @PathVariable String metricType,
            @RequestParam(defaultValue = "1") int hours) {
        
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetricData> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(MetricData::getResourceId, resourceId)
               .eq(MetricData::getMetricType, metricType)
               .ge(MetricData::getReportTime, startTime)
               .orderByAsc(MetricData::getReportTime);
        
        List<MetricData> records = metricDataMapper.selectList(wrapper);
        return RpcResult.success(records);
    }

    @PostMapping("/report")
    @ApiOperation(value = "上报指标数据")
    public RpcResult<Void> report(@RequestBody List<MetricData> metrics) {
        log.info("[MetricController] report metrics, count: {}", metrics.size());
        try {
            for (MetricData metric : metrics) {
                if (metric.getId() == null || metric.getId().isEmpty()) {
                    metric.setId(UUID.randomUUID().toString());
                }
                if (metric.getCreateTime() == null) {
                    metric.setCreateTime(LocalDateTime.now());
                }
                if (metric.getReportTime() == null) {
                    metric.setReportTime(LocalDateTime.now());
                }
                metricDataMapper.insert(metric);
            }
            return RpcResult.success("指标数据上报成功");
        } catch (Exception e) {
            log.error("[MetricController] report metrics error", e);
            return RpcResult.fail("指标数据上报失败: " + e.getMessage());
        }
    }

    @GetMapping("/summary/{resourceId}")
    @ApiOperation(value = "获取资源指标汇总")
    public RpcResult<Map<String, Object>> getSummary(@PathVariable String resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return RpcResult.fail("资源不存在");
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("resourceId", resourceId);
        summary.put("ipAddress", resource.getIpAddress());
        summary.put("platform", resource.getPlatform());
        
        // 统计指标数量
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MetricData> wrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(MetricData::getResourceId, resourceId);
        
        Long totalCount = metricDataMapper.selectCount(wrapper);
        summary.put("totalMetrics", totalCount);
        
        // 获取最近上报时间
        wrapper.orderByDesc(MetricData::getReportTime).last("LIMIT 1");
        MetricData latestMetric = metricDataMapper.selectOne(wrapper);
        if (latestMetric != null) {
            summary.put("lastReportTime", latestMetric.getReportTime());
        }
        
        return RpcResult.success(summary);
    }
}
