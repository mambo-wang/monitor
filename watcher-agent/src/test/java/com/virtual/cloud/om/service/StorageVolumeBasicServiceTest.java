package com.virtual.cloud.om.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.dto.DataReportDTO;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.service.report.DataReportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class StorageVolumeBasicServiceTest {
    @Resource
    private DataReportCollectorOverview dataReportCollectorOverview;
    @Resource
    private DataReportService dataReportService;

    @Test
    void report(){
        String tags = "resourceId=50;hostId=29;";
        this.dataReportService.report(tags,ReportMetricEnum.storage_volume_basic.name());
    }

    @Test
    void report_storage_pool_basic() {
        String tags = "resourceId=50;hostId=29;";
        DataReportCollector collector = dataReportCollectorOverview.getCollector(ReportMetricEnum.storage_volume_basic);
        DataReportDTO dto = new DataReportDTO();
        dto.setWatcherCode("");
//        dto.setData(Lists.newArrayList(collector.data(tags)));
        dto.setReportTimestamp(System.currentTimeMillis());
        dto.setTraceId(UUID.fastUUID().toString());
//        dto.setPlatform(collector.resource());
        System.out.println(JSONUtil.toJsonStr(dto));
    }

}