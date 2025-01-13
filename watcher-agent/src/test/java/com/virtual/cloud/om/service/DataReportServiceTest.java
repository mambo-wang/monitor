package com.virtual.cloud.om.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.dto.DataReportDTO;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DesktopPoolDTO;
import com.virtual.cloud.om.service.report.DataReportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;

import javax.annotation.Resource;

@SpringBootTest
class DataReportServiceTest {
    @Resource
    private DataReportCollectorOverview dataReportCollectorOverview;
    @Resource
    private DataReportService dataReportService;
    @Resource
    private WsTokenRestConnection wsTokenRestConnection;

    @Test
    void report(){
        String tags = "resourceId=12";
        this.dataReportService.report(tags,ReportMetricEnum.watcher_component_cpu_usage.name());
    }

    @Test
    void report_desktop_pool_basic() {
        String uri = WsUriConstants.DesktopPool.QUERY_DESKTOPPOOLS_LIST;
        RpcListLoadResult<DesktopPoolDTO> rpcResult = this.wsTokenRestConnection.get("10.99.224.137", "http","admin","Cloud@1234", 8083,
                uri, new ParameterizedTypeReference<RpcListLoadResult<DesktopPoolDTO>>() {
                }).getBody();
        System.out.println(rpcResult);
    }

    @Test
    void report_desktop_pool_vm_relation() {
        String tags = "resourceId=111";
        DataReportCollector collector = dataReportCollectorOverview.getCollector(ReportMetricEnum.desktop_pool_vm_relation);
        DataReportDTO dto = new DataReportDTO();
        dto.setWatcherCode("");
//        dto.setData(Lists.newArrayList(collector.data(tags)));
        dto.setReportTimestamp(System.currentTimeMillis());
        dto.setTraceId(UUID.fastUUID().toString());
//        dto.setPlatform(collector.resource());
        System.out.println(JSONUtil.toJsonStr(dto));
    }

    @Test
    void report_terminal_basic() {
        String tags = "resourceId=111";
        DataReportCollector collector = dataReportCollectorOverview.getCollector(ReportMetricEnum.terminal_basic);
        DataReportDTO dto = new DataReportDTO();
        dto.setWatcherCode("");
//        dto.setData(Lists.newArrayList(collector.data(tags)));
        dto.setReportTimestamp(System.currentTimeMillis());
        dto.setTraceId(UUID.fastUUID().toString());
//        dto.setPlatform(collector.resource());
        System.out.println(JSONUtil.toJsonStr(dto));
    }
}