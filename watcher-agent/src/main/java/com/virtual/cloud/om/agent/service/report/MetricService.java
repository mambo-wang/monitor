package com.virtual.cloud.om.agent.service.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
import com.virtual.cloud.om.sdk.entity.mysql.MetricData;
import com.virtual.cloud.om.sdk.entity.mysql.Resource;
import com.virtual.cloud.om.sdk.mapper.MetricDataMapper;
import com.virtual.cloud.om.sdk.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 指标数据服务 - 提供指标查询、上报、趋势分析等功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricService {

    private final MetricDataMapper metricDataMapper;
    private final ResourceMapper resourceMapper;
    private final DataReportService dataReportService;

    /**
     * 查询支持的指标类型
     */
    public List<Map<String, String>> getMetricTypes() {
        List<Map<String, String>> types = new ArrayList<>();
        for (ReportMetricEnum metric : ReportMetricEnum.values()) {
            Map<String, String> type = new HashMap<>();
            type.put("code", metric.name());
            type.put("name", metric.name());
            type.put("desc", metric.name());
            type.put("static", String.valueOf(metric.staticMetric));
            types.add(type);
        }
        return types;
    }

    /**
     * 查询支持的平台类型
     */
    public List<Map<String, String>> getPlatforms() {
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
        return platforms;
    }

    /**
     * 查询指标数据列表
     * 如果有resourceId和metricType，调用DataReportService实时采集数据
     * 否则从数据库查询
     */
    public List<Map<String, Object>> listMetrics(
            String resourceId, String platform, String metricType,
            String startTime, String endTime, int page, int size) {

        // 如果有resourceId和metricType，调用DataReportService实时采集数据
        if (resourceId != null && !resourceId.isEmpty() && metricType != null && !metricType.isEmpty()) {
            return collectRealTimeMetrics(resourceId, metricType);
        }

        // 否则从数据库查询
        return queryMetricsFromDb(resourceId, platform, metricType, startTime, endTime, page, size);
    }

    /**
     * 实时采集指标数据
     */
    private List<Map<String, Object>> collectRealTimeMetrics(String resourceId, String metricType) {
        try {
            List<ReportDTO> reportData = dataReportService.reportWithResult(
                    "resourceId=" + resourceId,
                    metricType
            );

            List<Map<String, Object>> resultList = Lists.newArrayList();
            for (ReportDTO dto : reportData) {
                Map<String, Object> item = new HashMap<>();
                item.put("metricName", dto.getMetric() != null ? dto.getMetric().name() : metricType);
                item.put("metricType", metricType);
                item.put("metricValue", dto.getValue());
                item.put("metricUnit", "");
                item.put("tags", dto.getTags());
                item.put("reportTime", dto.getTimestamp() != null ?
                        LocalDateTime.ofInstant(new Date(dto.getTimestamp()).toInstant(), java.time.ZoneId.systemDefault()) :
                        LocalDateTime.now());
                resultList.add(item);
            }

            log.info("[MetricService] collectRealTimeMetrics - collected {} records", resultList.size());
            return resultList;
        } catch (Exception e) {
            log.error("[MetricService] collectRealTimeMetrics error", e);
            return Lists.newArrayList();
        }
    }

    /**
     * 从数据库查询指标数据
     */
    private List<Map<String, Object>> queryMetricsFromDb(
            String resourceId, String platform, String metricType,
            String startTime, String endTime, int page, int size) {

        LambdaQueryWrapper<MetricData> wrapper = new LambdaQueryWrapper<>();

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

        List<Map<String, Object>> resultList = Lists.newArrayList();
        for (MetricData record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("metricName", record.getMetricName());
            item.put("metricType", record.getMetricType());
            item.put("metricValue", record.getMetricValue());
            item.put("metricUnit", record.getMetricUnit());
            item.put("resourceId", record.getResourceId());
            item.put("platform", record.getPlatform());
            item.put("reportTime", record.getReportTime());
            resultList.add(item);
        }

        return resultList;
    }

    /**
     * 查询资源最新指标数据
     */
    public List<MetricData> getLatestMetrics(String resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            log.warn("[MetricService] getLatestMetrics - resource not found: {}", resourceId);
            return null;
        }

        List<MetricData> metrics = new ArrayList<>();

        // 查询该资源最新的各类指标
        Set<String> metricTypes = new HashSet<>();
        for (DataReportTypeByMetricEnum type : DataReportTypeByMetricEnum.values()) {
            metricTypes.add(type.name());
        }

        for (String metricType : metricTypes) {
            LambdaQueryWrapper<MetricData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MetricData::getResourceId, resourceId)
                    .eq(MetricData::getMetricType, metricType)
                    .orderByDesc(MetricData::getReportTime)
                    .last("LIMIT 1");

            MetricData metric = metricDataMapper.selectOne(wrapper);
            if (metric != null) {
                metrics.add(metric);
            }
        }

        return metrics;
    }

    /**
     * 查询指标趋势数据
     */
    public List<MetricData> getMetricTrend(String resourceId, String metricType, int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);

        LambdaQueryWrapper<MetricData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetricData::getResourceId, resourceId)
                .eq(MetricData::getMetricType, metricType)
                .ge(MetricData::getReportTime, startTime)
                .orderByAsc(MetricData::getReportTime);

        return metricDataMapper.selectList(wrapper);
    }

    /**
     * 上报指标数据
     */
    public void reportMetrics(List<MetricData> metrics) {
        log.info("[MetricService] report metrics, count: {}", metrics.size());
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
    }

    /**
     * 检查资源是否存在
     */
    public Resource checkResourceExists(String resourceId) {
        return resourceMapper.selectById(resourceId);
    }

    /**
     * 获取资源指标汇总
     */
    public Map<String, Object> getMetricSummary(String resourceId) {
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return null;
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("resourceId", resourceId);
        summary.put("ipAddress", resource.getIpAddress());
        summary.put("platform", resource.getPlatform());

        // 统计指标数量
        LambdaQueryWrapper<MetricData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetricData::getResourceId, resourceId);

        Long totalCount = metricDataMapper.selectCount(wrapper);
        summary.put("totalMetrics", totalCount);

        // 获取最近上报时间
        wrapper.orderByDesc(MetricData::getReportTime).last("LIMIT 1");
        MetricData latestMetric = metricDataMapper.selectOne(wrapper);
        if (latestMetric != null) {
            summary.put("lastReportTime", latestMetric.getReportTime());
        }

        return summary;
    }
}
