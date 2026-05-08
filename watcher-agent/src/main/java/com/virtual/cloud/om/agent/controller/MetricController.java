package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.entity.mysql.MetricData;
import com.virtual.cloud.om.agent.service.report.MetricService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    private MetricService metricService;

    @GetMapping("/types")
    @ApiOperation(value = "查询支持的指标类型")
    public RpcResult<List<Map<String, String>>> getMetricTypes() {
        return RpcResult.success(metricService.getMetricTypes());
    }

    @GetMapping("/platforms")
    @ApiOperation(value = "查询支持的平台类型")
    public RpcResult<List<Map<String, String>>> getPlatforms() {
        return RpcResult.success(metricService.getPlatforms());
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询指标数据列表")
    public RpcListLoadResult<Map<String, Object>> list(
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String metricType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        log.info("[MetricController] list - resourceId: {}, metricType: {}", resourceId, metricType);

        List<Map<String, Object>> resultList = metricService.listMetrics(
                resourceId, platform, metricType, startTime, endTime, page, size);
        return RpcListLoadResult.success(resultList);
    }

    @GetMapping("/latest/{resourceId}")
    @ApiOperation(value = "查询资源最新指标数据")
    public RpcResult<List<MetricData>> getLatestMetrics(@PathVariable String resourceId) {
        List<MetricData> metrics = metricService.getLatestMetrics(resourceId);
        if (metrics == null) {
            return RpcResult.fail("资源不存在");
        }
        return RpcResult.success(metrics);
    }

    @GetMapping("/trend/{resourceId}/{metricType}")
    @ApiOperation(value = "查询指标趋势数据")
    public RpcResult<List<MetricData>> getMetricTrend(
            @PathVariable String resourceId,
            @PathVariable String metricType,
            @RequestParam(defaultValue = "1") int hours) {

        List<MetricData> records = metricService.getMetricTrend(resourceId, metricType, hours);
        return RpcResult.success(records);
    }

    @PostMapping("/report")
    @ApiOperation(value = "上报指标数据")
    public RpcResult<Void> report(@RequestBody List<MetricData> metrics) {
        log.info("[MetricController] report metrics, count: {}", metrics.size());
        try {
            metricService.reportMetrics(metrics);
            return RpcResult.success("指标数据上报成功");
        } catch (Exception e) {
            log.error("[MetricController] report metrics error", e);
            return RpcResult.fail("指标数据上报失败: " + e.getMessage());
        }
    }

    @GetMapping("/summary/{resourceId}")
    @ApiOperation(value = "获取资源指标汇总")
    public RpcResult<Map<String, Object>> getSummary(@PathVariable String resourceId) {
        Map<String, Object> summary = metricService.getMetricSummary(resourceId);
        if (summary == null) {
            return RpcResult.fail("资源不存在");
        }
        return RpcResult.success(summary);
    }
}
