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
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterFlowDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.Data;
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
 * @Date:2022/8/24 20:59
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterFlowCollector extends DataReportCollector {
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
        List<List> pool_all_data_rd_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_data_rd.getValue());
        List<List> pool_all_data_wr_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_data_wr.getValue());
        List<List> pool_all_data_rc_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_pool_all_data_rc.getValue());
        List<List> fs_data_rd_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_data_rd.getValue());
        List<List> fs_data_wr_list = stringListMap.get(OneStorClusterMonitorEnum.big_cluster_fs_data_wr.getValue());
        Double all_data_rd_value=0.00;
        Integer pool_all_data_rd_time=0;
        if (CollectionUtil.isNotEmpty(pool_all_data_rd_list)){
            all_data_rd_value=(Double)pool_all_data_rd_list.get(pool_all_data_rd_list.size() - 1).get(0);
            pool_all_data_rd_time=(Integer)pool_all_data_rd_list.get(pool_all_data_rd_list.size() - 1).get(1);
        }
        Double pool_all_data_wr_value=0.00;
        if (CollectionUtil.isNotEmpty(pool_all_data_wr_list)){
            pool_all_data_wr_value=(Double)pool_all_data_wr_list.get(pool_all_data_wr_list.size() - 1).get(0);
        }
        Double pool_all_data_rc_value=0.00;
        if (CollectionUtil.isNotEmpty(pool_all_data_rc_list)){
            pool_all_data_rc_value=(Double)pool_all_data_rc_list.get(pool_all_data_rc_list.size() - 1).get(0);
        }
        Double fs_data_rd_value=0.00;
        if (CollectionUtil.isNotEmpty(fs_data_rd_list)){
            fs_data_rd_value=(Double)fs_data_rd_list.get(fs_data_rd_list.size() - 1).get(0);
        }
        Double fs_data_wr_value=0.00;
        if (CollectionUtil.isNotEmpty(fs_data_wr_list)){
            fs_data_wr_value=(Double)fs_data_wr_list.get(fs_data_wr_list.size() - 1).get(0);
        }
        Long time = Long.valueOf(pool_all_data_rd_time) * 1000;
        String formatFullDateTime = Utils.formatFullDateTime(time);
        StorClusterFlowDTO storClusterFlowDTO =new StorClusterFlowDTO();
        storClusterFlowDTO.setClusterName(clusterId.getName());
        storClusterFlowDTO.setStorageReadFlow(all_data_rd_value);
        storClusterFlowDTO.setStorageWriteFlow(pool_all_data_wr_value);
        storClusterFlowDTO.setStorageRecoverFlow(pool_all_data_rc_value);
        storClusterFlowDTO.setFsReadFlow(fs_data_rd_value);
        storClusterFlowDTO.setFsWriteFlow(fs_data_wr_value);
        storClusterFlowDTO.setUpdate_time(formatFullDateTime);
        DataValueAndTagsDTO dataValueAndTagsDTO =new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setValue(storClusterFlowDTO);
        log.info("stor_cluster_flow : {}",Lists.newArrayList(dataValueAndTagsDTO));
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_flow;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
