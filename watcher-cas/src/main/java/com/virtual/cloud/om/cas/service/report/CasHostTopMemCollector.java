package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostRateDTO;
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
 * @Date:2022/5/20 15:24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CasHostTopMemCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        String goal = "hostIds";
        List<String> hostIds = getId(goal, tags);
        if (CollectionUtil.isNotEmpty(hostIds)) {
            String hostId = hostIds.get(0);
            String url = String.format(CasUriConstants.Host.QUERY_HOST_TOP_MEM_TOPN, hostId);
            List<HostRateDTO> hostRateDTOS = null;
            try {
                hostRateDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<HostRateDTO>>() {
                        });
            } catch (Exception e) {
                log.error("cas rest fail: " + e);
                throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
            }
            // TODO 数据上报修改
//            return hostRateDTOS;
        }
        return Collections.emptyList();
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.host_mem_TopN;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
