package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostDetailInfoDTO;
import com.virtual.cloud.om.sdk.utils.DateTimeTool;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/6/6 17:08
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CpuAllocateRateCollector extends DataReportCollector {

    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = getId(Constant.Tags.HOST_IDS, tags);
        if (CollectionUtil.isEmpty(hostIds)) {
            List<String> hostIdsList = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
            if (CollectionUtil.isEmpty(hostIdsList)) {
                return Collections.emptyList();
            }
            hostIds = hostIdsList;
        }
        List<DataValueAndTagsDTO> finalResult = hostIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Host.QUERY_HOST_BY_HOST_ID, s);
            HostDetailInfoDTO hostDetailInfoDTOS = null;
            try {
                hostDetailInfoDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<HostDetailInfoDTO>() {
                        });
            } catch (Exception e) {
                log.error("cas cpu_allocate_rate is fail : " + e);
            }
            if (Objects.isNull(hostDetailInfoDTOS)) {
                return null;
            }
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(hostDetailInfoDTOS.getCpuSuperRatio());
            Long time = null;
            try {
                time = DateTimeTool.stringToLong(hostDetailInfoDTOS.getSystemTime());
                dataValueAndTagsDTO.setTimestamp(time);
                String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.HOST_ID, s);
                dataValueAndTagsDTO.setTags(tagsTo);
            } catch (ParseException e) {
                log.error("time parse is fail : " + hostDetailInfoDTOS.getSystemTime());
            }
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        log.debug("[cpu_allocate_rate]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }


    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.cpu_allocate_rate;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }

}
