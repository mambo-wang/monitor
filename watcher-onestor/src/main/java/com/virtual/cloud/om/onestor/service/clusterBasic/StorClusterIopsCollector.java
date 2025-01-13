package com.virtual.cloud.om.onestor.service.clusterBasic;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.OneStorClusterMonitorEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.*;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/8/24 17:00
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterIopsCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Resource
    private StorUtils storUtils;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        StorCluserIdDTO clusterId = storUtils.getClusterId(platform, host, protocol, port, username, password, tags, resourceId);
        if (Objects.isNull(clusterId.getId())){
            log.info("ONESTOR StorCluserIdDTO is empty host : {}",host);
            return Collections.emptyList();
        }

        String url = OnestoreUriConstants.Cluster.STOR_CLUSTER_MONITOR
                + Constant.TARGET + OneStorClusterMonitorEnum.pool_all_iops_rd.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.pool_all_iops_wr.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_recovery_ops.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_fs_ops_rd.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_fs_ops_wr.getValue()

                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_diskstat_util_max.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_pool_all_iops_all.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_fs_ops_total.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_pool_all_bw_all.getValue()
                +Constant.TARGET+OneStorClusterMonitorEnum.big_cluster_fs_bw_total.getValue()

                ;


        List<OneStorRenderResult> oneStorRenderResult = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<List<OneStorRenderResult>>() {
        }).getBody();
        Map<String, List<List>> stringListMap = oneStorRenderResult.stream().collect(Collectors.toMap(OneStorRenderResult::getTarget, OneStorRenderResult::getDatapoints));
        List<List> iops_rd_list = stringListMap.get(OneStorClusterMonitorEnum.pool_all_iops_rd.getValue());
        List<List> iops_wr_list = stringListMap.get(OneStorClusterMonitorEnum.pool_all_iops_wr.getValue());
        List<List> recovery_ops_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_recovery_ops.getValue());
        List<List> fs_ops_rd_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_ops_rd.getValue());
        List<List> fs_ops_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_ops_wr.getValue());

        List<List> bw_total_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_bw_total.getValue());
        List<List> iops_all_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_iops_all.getValue());
        List<List> ops_total_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_ops_total.getValue());
        List<List> bw_all_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_bw_all.getValue());
        Double iops_rd_list_value=0.00;
        Integer iops_rd_list_time=0;
        if (CollectionUtil.isNotEmpty(iops_rd_list)){
            iops_rd_list_value=(Double)iops_rd_list.get(iops_rd_list.size() - 1).get(0);
            iops_rd_list_time=(Integer)iops_rd_list.get(iops_rd_list.size() - 1).get(1);
        }
        Double iops_wr_list_value=0.00;
        if (CollectionUtil.isNotEmpty(iops_wr_list)){
            iops_wr_list_value=(Double)iops_wr_list.get(iops_wr_list.size() - 1).get(0);
        }
        Double recovery_ops_list_value=0.00;
        if (CollectionUtil.isNotEmpty(recovery_ops_list)){
            recovery_ops_list_value=(Double)recovery_ops_list.get(recovery_ops_list.size() - 1).get(0);
        }
        Double fs_ops_rd_list_value=0.00;
        if (CollectionUtil.isNotEmpty(fs_ops_rd_list)){
            fs_ops_rd_list_value=(Double)fs_ops_rd_list.get(fs_ops_rd_list.size() - 1).get(0);
        }
       Double fs_ops_list_value=0.00;
        if (CollectionUtil.isNotEmpty(fs_ops_list)){
            fs_ops_list_value=(Double)fs_ops_list.get(fs_ops_list.size() - 1).get(0);
        }

        Double bw_total_list_value=0.00;
        if (CollectionUtil.isNotEmpty(bw_total_list)){
            bw_total_list_value=(Double)bw_total_list.get(bw_total_list.size() - 1).get(0);
        }
        Double iops_all_list_value=0.00;
        if (CollectionUtil.isNotEmpty(iops_all_list)){
            iops_all_list_value=(Double)iops_all_list.get(iops_all_list.size() - 1).get(0);
        }
        Double ops_total_list_value=0.00;
        if (CollectionUtil.isNotEmpty(ops_total_list)){
            ops_total_list_value=(Double)ops_total_list.get(ops_total_list.size() - 1).get(0);
        }
        Double bw_all_list_value=0.00;
        if (CollectionUtil.isNotEmpty(bw_all_list)){
            bw_all_list_value=(Double)bw_all_list.get(bw_all_list.size() - 1).get(0);
        }
        Long time = Long.valueOf(iops_rd_list_time) * 1000;
        String formatFullDateTime = Utils.formatFullDateTime(time);
        StorClusterIopsDTO storClusterIopsDTO=new StorClusterIopsDTO();
        storClusterIopsDTO.setClusterName(clusterId.getName());
        storClusterIopsDTO.setIopsRead(iops_rd_list_value);
        storClusterIopsDTO.setIopsWrite(iops_wr_list_value);
        storClusterIopsDTO.setRecoverOps(recovery_ops_list_value);
        storClusterIopsDTO.setOpsRead(fs_ops_rd_list_value);
        storClusterIopsDTO.setOpsWrite(fs_ops_list_value);
        storClusterIopsDTO.setUpdate_time(formatFullDateTime);
        storClusterIopsDTO.setAllIops(iops_all_list_value);
        storClusterIopsDTO.setFsTotalOps(ops_total_list_value);
        storClusterIopsDTO.setAllBw(bw_total_list_value);
        storClusterIopsDTO.setFsTotalBw(bw_all_list_value);
        DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(storClusterIopsDTO);
        log.info("stor_cluster_iops : {}",Lists.newArrayList(dataValueAndTagsDTO));
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_iops;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
