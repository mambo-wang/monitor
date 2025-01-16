package com.virtual.cloud.om.agent.service.onestor;

import com.virtual.cloud.om.agent.entity.ResourceEntity;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import com.virtual.cloud.om.agent.service.report.DataReportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

/**
 * @author:XK
 * @Date:2022/9/20 14:17
 */
@SpringBootTest
public class basicTest {
    @Resource
    private DataReportService dataReportService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Test
    void report(){
        ResourceEntity resourceEntity=new ResourceEntity();
        resourceEntity.setId("1");
        resourceEntity.setAc("admin");
        resourceEntity.setCi(SM4Utils.webEncryptText("Admin@123"));
        resourceEntity.setIpAddress("10.132.37.145");
        resourceEntity.setPlatform("onestor");
        resourceEntity.setPort(80);
        resourceEntity.setProtocol("http");
        resourceEntity.setServerUsername("SDS_Admin");
        resourceEntity.setServerPassword(SM4Utils.webEncryptText("Admin@123"));
        resourceEntity.setServerPort(22);
        resourceEntity.setActive(1);
        resourceEntity.setAuthType("Digest");
        mongoTemplate.save(resourceEntity);

    }
    @Test
    void test_stor_cluster_bandwidth(){
        String tags="resourceId=293";
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_bandwidth.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_monitor.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_pg.name());
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_rbd_capacity.name());//待補充
        dataReportService.report(tags, DataReportTypeByMetricEnum.node_bandwidth.name());//待補充

    }
    @Test
    void test_stor_cluster_basic(){
        String tags="resourceId=293";
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_pg.name());
        dataReportService.report(tags, DataReportTypeByMetricEnum.storage_iops.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_monitor.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_host_mem_usage.name());
    }
    @Test
    void test_stor_cluster_capacity_basic(){
        String tags="resourceId=189";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_capacity_basic.name());
    }
    @Test
    void test_stor_cluster_capacity(){
        String tags="resourceId=188";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_capacity.name());
    }
    @Test
    void test_stor_cluster_cpu_usage(){
        String tags="resourceId=18";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_cpu_usage.name());
    }
    @Test
    void test_stor_cluster_disk_delay(){
        String tags="resourceId=188";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_disk_delay.name());
    }
    @Test
    void test_stor_cluster_disk_load(){
        String tags="resourceId=291";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_disk_load.name());
    }
    @Test
    void test_stor_cluster_flow(){
        String tags="resourceId=293";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_flow.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_fs_capacity.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_iops.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.storage_bandwidth.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_bandwidth.name());
    }
    @Test
    void test_stor_cluster_fs_capacity(){
        String tags="resourceId=188";
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_fs_capacity.name());
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_rgw_capacity.name());
    }
    @Test
    void test_stor_cluster_iops(){
        String tags="resourceId=188";
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_iops.name());
    }
    @Test
    void test_stor_cluster_mem_usage(){
        String tags="resourceId=293";
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_mem_usage.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_disk_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_nodepool_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.storage_bandwidth.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_monitor.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_rgw_capacity.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_capacity_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_storage_pool_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_pg.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_diskpool_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.node_bandwidth.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_diskpool_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.node_bandwidth.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_host_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.storage_iops.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.storage_bandwidth.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.storage_iops.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.node_cpu_usage.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.node_mem_usage.name());
        dataReportService.report(tags, DataReportTypeByMetricEnum.stor_cluster_capacity_basic.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.node_flow.name());
//        dataReportService.report(tags, DataReportTypeByMetricEnum.node_bandwidth.name());
    }
    @Test
    void reportOneStorData(){
        //填写OneStor的资源ID
        String tags = "resourceId=293";
        ReportMetricEnum enu;
        enu =
                //节点池基本//字段有点问题
//                ReportMetricEnum.stor_nodepool_basic
                //主机池基本
//                ReportMetricEnum.stor_host_basic
                // 硬盘池基本
//                ReportMetricEnum.stor_diskpool_basic
                //存储池基本
//                ReportMetricEnum.stor_storage_pool_basic
//                ReportMetricEnum.stor_disk_basic
//                ReportMetricEnum.stor_disk_basic
                ReportMetricEnum.stor_cluster_bandwidth
//                ReportMetricEnum.stor_cluster_pg


                //节点池监控数据
//                ReportMetricEnum.node_iops
//                ReportMetricEnum.stor_host_nic
//                ReportMetricEnum.stor_host_sys_avg_load
//                ReportMetricEnum.stor_host_basic
//        ReportMetricEnum.node_bandwidth
//        ReportMetricEnum.node_flow
//        ReportMetricEnum.node_capacity
//        ReportMetricEnum.node_disk_delay
//        ReportMetricEnum.node_disk_load

        //主机监控数据
//        ReportMetricEnum.host_iops
//        ReportMetricEnum.host_bandwidth
//        ReportMetricEnum.host_capacity
//        ReportMetricEnum.host_cpu_usage
//        ReportMetricEnum.host_disk_delay
//        ReportMetricEnum.host_disk_load

        //存储池监控数据
//        ReportMetricEnum.storage_iops
//        ReportMetricEnum.storage_bandwidth

        //硬盘池监控数据
//        ReportMetricEnum.diskpool_iops
//        ReportMetricEnum.diskpool_bandwidth
//        ReportMetricEnum.diskpool_capaciy
        ;
        this.dataReportService.report(tags,enu.name());
    }
}
