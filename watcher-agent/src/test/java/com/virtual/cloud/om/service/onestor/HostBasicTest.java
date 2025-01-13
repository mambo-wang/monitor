package com.virtual.cloud.om.service.onestor;

import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.service.report.DataReportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author:XK
 * @Date:2022/9/20 16:44
 */
@SpringBootTest
public class HostBasicTest {
    @Resource
    private DataReportService dataReportService;
    @Test
    void test_stor_host_basic(){
        //to do
        String tags="resourceId=293";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_nodepool_basic.name());
    }
    @Test
    void test_stor_storage_pool_basic(){
        //ok
        String tags="resourceId=1";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_storage_pool_basic.name());
    }
    @Test
    void test_stor_nodepool_basic(){
        //ok
        String tags="resourceId=1";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_nodepool_basic.name());
    }
    @Test
    void test_stor_diskpool_basic(){
        //ok
        String tags="resourceId=1";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_diskpool_basic.name());
    }
}
