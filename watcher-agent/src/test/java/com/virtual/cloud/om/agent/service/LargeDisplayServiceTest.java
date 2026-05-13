package com.virtual.cloud.om.agent.service;

import com.virtual.cloud.om.agent.dto.MetricTrendDTO;
import com.virtual.cloud.om.agent.dto.PlatformDTO;
import com.virtual.cloud.om.agent.dto.ResourceItemDTO;
import com.virtual.cloud.om.agent.service.impl.LargeDisplayServiceImpl;
import com.virtual.cloud.om.agent.service.report.MetricService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * LargeDisplayService 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LargeDisplayService 单元测试")
class LargeDisplayServiceTest {

    @Mock
    private MetricService metricService;

    @InjectMocks
    private LargeDisplayServiceImpl largeDisplayService;

    /**
     * 测试获取管理平台列表
     */
    @Test
    @DisplayName("getPlatformList 应返回 3 个管理平台")
    void testGetPlatformList() {
        List<PlatformDTO> platforms = largeDisplayService.getPlatformList();
        
        assertNotNull(platforms);
        assertEquals(3, platforms.size());
        
        // 验证平台类型
        assertTrue(platforms.stream().anyMatch(p -> "WORKSPACE".equals(p.getType())));
        assertTrue(platforms.stream().anyMatch(p -> "CAS".equals(p.getType())));
        assertTrue(platforms.stream().anyMatch(p -> "UIS".equals(p.getType())));
    }

    /**
     * 测试获取平台子资源
     */
    @Test
    @DisplayName("getPlatformChildren 应返回子资源列表")
    void testGetPlatformChildren() {
        List<ResourceItemDTO> children = largeDisplayService.getPlatformChildren(1L);
        
        assertNotNull(children);
        // 当前实现返回空列表（TODO 待实现）
    }

    /**
     * 测试获取集群主机列表
     */
    @Test
    @DisplayName("getClusterHosts 应返回主机列表")
    void testGetClusterHosts() {
        List<ResourceItemDTO> hosts = largeDisplayService.getClusterHosts(1L);
        
        assertNotNull(hosts);
        // 当前实现返回空列表（TODO 待实现）
    }

    /**
     * 测试获取主机虚拟机列表
     */
    @Test
    @DisplayName("getHostVMs 应返回虚拟机列表")
    void testGetHostVMs() {
        List<ResourceItemDTO> vms = largeDisplayService.getHostVMs(1L);
        
        assertNotNull(vms);
        // 当前实现返回空列表（TODO 待实现）
    }

    /**
     * 测试获取指标趋势数据
     */
    @Test
    @DisplayName("getMetricsTrend 应返回指标趋势数据")
    void testGetMetricsTrend() {
        // Mock MetricService 返回模拟数据
        Map<String, Object> metric = new HashMap<>();
        metric.put("metricValue", 45.5);
        metric.put("reportTime", new java.util.Date());
        
        when(metricService.listMetrics(anyString(), any(), anyString(), any(), any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList(metric));
        
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(1001L, "CPU", "1h");
        
        assertNotNull(trend);
        assertEquals(1001L, trend.getResourceId());
        assertEquals("CPU", trend.getMetricType());
        assertEquals("%", trend.getUnit());
        assertEquals("1h", trend.getTimeRange());
        assertNotNull(trend.getValues());
        assertNotNull(trend.getTimestamps());
    }

    /**
     * 测试内存指标趋势数据
     */
    @Test
    @DisplayName("getMetricsTrend 支持 MEMORY 类型")
    void testGetMetricsTrendForMemory() {
        // Mock MetricService 返回模拟数据
        Map<String, Object> metric = new HashMap<>();
        metric.put("metricValue", 65.0);
        metric.put("reportTime", new java.util.Date());
        
        when(metricService.listMetrics(anyString(), any(), anyString(), any(), any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList(metric));
        
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(1001L, "MEMORY", "24h");
        
        assertNotNull(trend);
        assertEquals("MEMORY", trend.getMetricType());
        assertEquals("24h", trend.getTimeRange());
    }

    /**
     * 测试指标趋势数据为空的情况
     */
    @Test
    @DisplayName("getMetricsTrend 当 MetricService 返回空列表时应返回模拟数据")
    void testGetMetricsTrendReturnsEmpty() {
        when(metricService.listMetrics(anyString(), any(), anyString(), any(), any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList());
        
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(1001L, "CPU", "1h");
        
        assertNotNull(trend);
        assertNotNull(trend.getValues());
        assertNotNull(trend.getTimestamps());
        // 应返回模拟数据
        assertFalse(trend.getValues().isEmpty());
    }

    /**
     * 测试指标趋势数据包含多个数据点
     */
    @Test
    @DisplayName("getMetricsTrend 应正确处理多个数据点")
    void testGetMetricsTrendMultiplePoints() {
        // Mock MetricService 返回多个数据点
        Map<String, Object> metric1 = new HashMap<>();
        metric1.put("metricValue", 25.5);
        metric1.put("reportTime", new java.util.Date());
        
        Map<String, Object> metric2 = new HashMap<>();
        metric2.put("metricValue", 30.2);
        metric2.put("reportTime", new java.util.Date());
        
        Map<String, Object> metric3 = new HashMap<>();
        metric3.put("metricValue", 28.8);
        metric3.put("reportTime", new java.util.Date());
        
        when(metricService.listMetrics(anyString(), any(), anyString(), any(), any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList(metric1, metric2, metric3));
        
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(1001L, "CPU", "1h");
        
        assertNotNull(trend);
        assertEquals(3, trend.getValues().size());
        assertEquals(3, trend.getTimestamps().size());
        assertEquals(25.5, trend.getValues().get(0));
        assertEquals(30.2, trend.getValues().get(1));
        assertEquals(28.8, trend.getValues().get(2));
    }

    /**
     * 测试指标值为字符串类型时的处理
     */
    @Test
    @DisplayName("getMetricsTrend 应正确处理字符串类型的指标值")
    void testGetMetricsTrendWithStringValue() {
        Map<String, Object> metric = new HashMap<>();
        metric.put("metricValue", "45.5"); // 字符串类型
        metric.put("reportTime", new java.util.Date());
        
        when(metricService.listMetrics(anyString(), any(), anyString(), any(), any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList(metric));
        
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(1001L, "CPU", "1h");
        
        assertNotNull(trend);
        assertEquals(45.5, trend.getValues().get(0));
    }

    /**
     * 测试指标数据值为 null 的情况
     */
    @Test
    @DisplayName("getMetricsTrend 应正确处理 null 指标值")
    void testGetMetricsTrendWithNullValue() {
        Map<String, Object> metric = new HashMap<>();
        metric.put("metricValue", null);
        metric.put("reportTime", new java.util.Date());
        
        when(metricService.listMetrics(anyString(), any(), anyString(), any(), any(), anyInt(), anyInt()))
            .thenReturn(Arrays.asList(metric));
        
        MetricTrendDTO trend = largeDisplayService.getMetricsTrend(1001L, "CPU", "1h");
        
        assertNotNull(trend);
        // null 值不会被添加到列表，但时间戳会添加
        assertTrue(trend.getValues().isEmpty());
        assertEquals(1, trend.getTimestamps().size());
    }

    /**
     * 测试平台列表包含正确的状态信息
     */
    @Test
    @DisplayName("getPlatformList 返回的平台应包含状态信息")
    void testGetPlatformListHasStatus() {
        List<PlatformDTO> platforms = largeDisplayService.getPlatformList();
        
        assertNotNull(platforms);
        for (PlatformDTO platform : platforms) {
            assertNotNull(platform.getStatus());
            assertEquals("NORMAL", platform.getStatus());
        }
    }

    /**
     * 测试平台列表包含正确的描述信息
     */
    @Test
    @DisplayName("getPlatformList 返回的平台应包含描述信息")
    void testGetPlatformListHasDescription() {
        List<PlatformDTO> platforms = largeDisplayService.getPlatformList();
        
        assertNotNull(platforms);
        for (PlatformDTO platform : platforms) {
            assertNotNull(platform.getDescription());
            assertFalse(platform.getDescription().isEmpty());
        }
    }
}