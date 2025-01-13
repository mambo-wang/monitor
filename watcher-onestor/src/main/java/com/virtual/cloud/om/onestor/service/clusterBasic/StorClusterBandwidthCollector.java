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
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRenderResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorCluserIdDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterBandwidthDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterIopsDTO;
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
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/8/24 20:44
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterBandwidthCollector extends DataReportCollector {
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

        String url = OnestoreUriConstants.Cluster.CLUSTER_INFO_ALL;

        List<OneStorRenderResult> oneStorRenderResult = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<List<OneStorRenderResult>>() {
        }).getBody();
        Map<String, List<List>> stringListMap = oneStorRenderResult.stream().collect(Collectors.toMap(OneStorRenderResult::getTarget, OneStorRenderResult::getDatapoints));
        List<List> pool_all_bw_rd_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_bw_rd.getValue());
        List<List> pool_all_bw_wr_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_bw_wr.getValue());
        List<List> recovery_bw_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_recovery_bw.getValue());
        List<List> fs_bw_rd_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_bw_rd.getValue());
        List<List> fs_bw_wr_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_bw_wr.getValue());
        List<List> bw_all_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_bw_all.getValue());
        List<List> bw_total_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_bw_total.getValue());
        Double pool_all_bw_rd_list_value=0.00;
        Integer pool_all_bw_rd_time=0;
        if (CollectionUtil.isNotEmpty(pool_all_bw_rd_list)){
            pool_all_bw_rd_list_value=(Double)pool_all_bw_rd_list.get(pool_all_bw_rd_list.size() - 1).get(0);
            pool_all_bw_rd_time=(Integer)pool_all_bw_rd_list.get(pool_all_bw_rd_list.size() - 1).get(1);
        }
        Double ipool_all_bw_wr_value=0.00;
        if (CollectionUtil.isNotEmpty(pool_all_bw_wr_list)){
            ipool_all_bw_wr_value=(Double)pool_all_bw_wr_list.get(pool_all_bw_wr_list.size() - 1).get(0);
        }
        Double recovery_bw_list_value=0.00;
        if (CollectionUtil.isNotEmpty(recovery_bw_list)){
            recovery_bw_list_value=(Double)recovery_bw_list.get(recovery_bw_list.size() - 1).get(0);
        }
        Double fs_bw_rd_list_value=0.00;
        if (CollectionUtil.isNotEmpty(fs_bw_rd_list)){
            fs_bw_rd_list_value=(Double)fs_bw_rd_list.get(fs_bw_rd_list.size() - 1).get(0);
        }
        Double fs_bw_wr_list_value=0.00;
        if (CollectionUtil.isNotEmpty(fs_bw_wr_list)){
            fs_bw_wr_list_value=(Double)fs_bw_wr_list.get(fs_bw_wr_list.size() - 1).get(0);
        }
        Double bw_all_list_value=0.00;
        if (CollectionUtil.isNotEmpty(bw_all_list)){
            bw_all_list_value=(Double)bw_all_list.get(bw_all_list.size() - 1).get(0);
        }
        Double bw_total_list_value=0.00;
        if (CollectionUtil.isNotEmpty(bw_total_list)){
            bw_total_list_value=(Double)bw_total_list.get(bw_total_list.size() - 1).get(0);
        }
        StorClusterBandwidthDTO storClusterBandwidthDTO=new StorClusterBandwidthDTO();
        storClusterBandwidthDTO.setClusterName(clusterId.getName());
        storClusterBandwidthDTO.setStorageReadBw(pool_all_bw_rd_list_value);
        storClusterBandwidthDTO.setStorageWriteBw(ipool_all_bw_wr_value);
        storClusterBandwidthDTO.setStorageRecoverBw(recovery_bw_list_value);
        storClusterBandwidthDTO.setFsReadBw(fs_bw_rd_list_value);
        storClusterBandwidthDTO.setFsWriteBw(fs_bw_wr_list_value);
        storClusterBandwidthDTO.setAllBw(bw_all_list_value);
        storClusterBandwidthDTO.setFsTotalBw(bw_total_list_value);
        Long time=Long.valueOf(pool_all_bw_rd_time)*1000;
        String fullDateTime = Utils.formatFullDateTime(time);
        storClusterBandwidthDTO.setUpdate_time(fullDateTime);
        DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(storClusterBandwidthDTO);
        log.info("result : {}",Lists.newArrayList(dataValueAndTagsDTO));
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_bandwidth;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
