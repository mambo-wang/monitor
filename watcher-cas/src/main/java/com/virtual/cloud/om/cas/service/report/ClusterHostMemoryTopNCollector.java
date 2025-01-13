package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.RateDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


/**
 * @author:XK
 * @Date:2022/5/20 10:15
 */

/**
 * 查询集群下主机内存利用率topN
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClusterHostMemoryTopNCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        String goal = "clusterIds";
        List<String> clusterIds = getId(goal, tags);
        if (CollectionUtil.isNotEmpty(clusterIds)) {
            String clusterId = clusterIds.get(0);
            String url = String.format(CasUriConstants.Cluster.QUERY_CLUSTER_HOST_MEM_TOPN, clusterId);
            List<RateDTO> rateDTOS = null;
            try {
                rateDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<RateDTO>>() {
                        });
            } catch (Exception e) {
                log.error("cas rest fail: " + e);
                throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
            }
            // TODO 数据上报修改
//            return rateDTOS;
        }
        return Collections.emptyList();
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cluster_host_top5_mem_usage;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
