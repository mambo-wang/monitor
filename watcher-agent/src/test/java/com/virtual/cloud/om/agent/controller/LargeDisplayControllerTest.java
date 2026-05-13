package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.agent.dto.MetricTrendDTO;
import com.virtual.cloud.om.agent.dto.PlatformDTO;
import com.virtual.cloud.om.agent.dto.ResourceItemDTO;
import com.virtual.cloud.om.agent.service.LargeDisplayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * LargeDisplayController 单元测试
 * 
 * 测试 API 端点、参数验证、响应格式
 */
@ExtendWith(MockitoExtension.class)
class LargeDisplayControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LargeDisplayService largeDisplayService;

    @InjectMocks
    private LargeDisplayController largeDisplayController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(largeDisplayController).build();
    }

    /**
     * 测试 GET /api/platform/list - 获取管理平台列表
     */
    @Test
    void testGetPlatformList() throws Exception {
        // 准备测试数据
        List<PlatformDTO> platforms = Arrays.asList(
            createPlatformDTO(1L, "Workspace", "WORKSPACE"),
            createPlatformDTO(2L, "CAS", "CAS"),
            createPlatformDTO(3L, "UIS", "UIS")
        );
        
        when(largeDisplayService.getPlatformList()).thenReturn(platforms);

        // 执行测试
        mockMvc.perform(get("/api/platform/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    /**
     * 测试 GET /api/platform/{id}/children - 获取平台下的资源列表
     */
    @Test
    void testGetPlatformChildren() throws Exception {
        Long platformId = 1L;
        List<ResourceItemDTO> children = Arrays.asList(
            createResourceItemDTO(101L, "Cluster-1", "CLUSTER"),
            createResourceItemDTO(102L, "Pool-1", "DESKTOP_POOL"),
            createResourceItemDTO(103L, "Terminal-1", "TERMINAL")
        );
        
        when(largeDisplayService.getPlatformChildren(anyLong())).thenReturn(children);

        // 执行测试
        mockMvc.perform(get("/api/platform/{id}/children", platformId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    /**
     * 测试 GET /api/cluster/{id}/hosts - 获取集群下的主机列表
     */
    @Test
    void testGetClusterHosts() throws Exception {
        Long clusterId = 101L;
        List<ResourceItemDTO> hosts = Arrays.asList(
            createResourceItemDTO(1001L, "Host-1", "HOST"),
            createResourceItemDTO(1002L, "Host-2", "HOST")
        );
        
        when(largeDisplayService.getClusterHosts(anyLong())).thenReturn(hosts);

        // 执行测试
        mockMvc.perform(get("/api/cluster/{id}/hosts", clusterId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    /**
     * 测试 GET /api/host/{id}/vms - 获取主机下的虚拟机列表
     */
    @Test
    void testGetHostVMs() throws Exception {
        Long hostId = 1001L;
        List<ResourceItemDTO> vms = Arrays.asList(
            createResourceItemDTO(2001L, "VM-1", "VIRTUAL_MACHINE"),
            createResourceItemDTO(2002L, "VM-2", "VIRTUAL_MACHINE")
        );
        
        when(largeDisplayService.getHostVMs(anyLong())).thenReturn(vms);

        // 执行测试
        mockMvc.perform(get("/api/host/{id}/vms", hostId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    /**
     * 测试 GET /api/metrics/trend - 获取指标趋势数据
     */
    @Test
    void testGetMetricsTrend() throws Exception {
        Long resourceId = 1001L;
        String metricType = "CPU";
        String timeRange = "1h";
        
        MetricTrendDTO trendDTO = createMetricTrendDTO();
        when(largeDisplayService.getMetricsTrend(anyLong(), anyString(), anyString()))
            .thenReturn(trendDTO);

        // 执行测试
        mockMvc.perform(get("/api/metrics/trend")
                .param("resourceId", String.valueOf(resourceId))
                .param("metricType", metricType)
                .param("timeRange", timeRange)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.metricType").value("CPU"))
                .andExpect(jsonPath("$.data.values").isArray());
    }

    /**
     * 测试参数验证 - resourceId 为空
     */
    @Test
    void testGetMetricsTrendWithInvalidParams() throws Exception {
        // resourceId 为空返回 400 错误
        mockMvc.perform(get("/api/metrics/trend")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("resourceId 参数不能为空"));
    }

    /**
     * 测试参数验证 - metricType 为空
     */
    @Test
    void testGetMetricsTrendWithEmptyMetricType() throws Exception {
        // metricType 为空返回 400 错误
        mockMvc.perform(get("/api/metrics/trend")
                .param("resourceId", "1001")
                .param("metricType", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("metricType 参数不能为空"));
    }

    // ==================== 辅助方法 ====================

    private PlatformDTO createPlatformDTO(Long id, String name, String type) {
        PlatformDTO dto = new PlatformDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setType(type);
        dto.setDescription(name + " 管理平台");
        return dto;
    }

    private ResourceItemDTO createResourceItemDTO(Long id, String name, String type) {
        ResourceItemDTO dto = new ResourceItemDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setType(type);
        dto.setStatus("NORMAL");
        return dto;
    }

    private MetricTrendDTO createMetricTrendDTO() {
        MetricTrendDTO dto = new MetricTrendDTO();
        dto.setResourceId(1001L);
        dto.setMetricType("CPU");
        dto.setUnit("%");
        dto.setValues(Arrays.asList(25.5, 30.2, 28.8, 35.6, 40.1));
        dto.setTimestamps(Arrays.asList(
            System.currentTimeMillis() - 400 * 60 * 1000,
            System.currentTimeMillis() - 300 * 60 * 1000,
            System.currentTimeMillis() - 200 * 60 * 1000,
            System.currentTimeMillis() - 100 * 60 * 1000,
            System.currentTimeMillis()
        ));
        return dto;
    }
}
