package com.virtual.cloud.om.sdk.api;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class DataReportCollector {


    /**
     * @param goal 目标id 如 hostId
     * @param tags
     * @return
     */
    public static List<String> getId(String goal, String tags) {
        List<String> idList = new ArrayList<>();
        String[] ids = tags.split(";");
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].contains(goal)) {
                String replace = ids[i].replace(goal + "=", "");
                String[] goalIds = replace.split(",");
                List<String> goalIdList = Arrays.asList(goalIds);
                idList.addAll(goalIdList);
            }
        }
        return idList;
    }


    /**
     * 获取数据上报接口报文的data字段值
     *
     * @param tags
     * @return
     */
    public List<ReportDTO> data(RestHost restHost, String tags) {
        long time = System.currentTimeMillis();
        final String host = restHost.getHost();
        final String resourceId = restHost.getResourceId();
        log.info("[data collect][resourceId={}][{}][{}] start now", resourceId, host, metric());
        final String protocol = restHost.getProtocol();
        final Integer port = restHost.getPort();
        final String username = restHost.getUsername();
        final String password = restHost.getPassword();
        final String platform = restHost.getPlatform();
        long collectTimeMs = System.currentTimeMillis();
        List<DataValueAndTagsDTO> vats = collect(platform, host, protocol, port, username, password, tags, resourceId);
        log.info("[data collect][resourceId={}][{}][{}] end , time is {} ms", resourceId, host, metric(), System.currentTimeMillis() - time);
        List<ReportDTO> result;
        if (CollUtil.isEmpty(vats)) {
            ReportDTO dto = new ReportDTO();
            dto.setMetric(metric().metric);
            dto.setType(valueType());
            dto.setTags(tags);
            dto.setValue(Lists.newArrayList());
            result = Lists.newArrayList(dto);
        } else {
            log.info("----<<DataValueAndTagsDTOS>>--- : "+vats.toString());
            result = vats.stream().map(vat -> {
                ReportDTO dto = new ReportDTO();
                dto.setMetric(metric().metric);
                dto.setType(valueType());
                dto.setValue(vat.getValue());
                dto.setTags(vat.getTags());
                dto.setTimestamp(vat.getTimestamp());
                return dto;
            }).collect(Collectors.toList());
        }
        // 因cas收集器有使用管理平台返回的统计时间，这里统一处理为采集端时间
//        log.info("上报结果 vats result: {}",vats);
        result.stream().filter(dto -> Objects.isNull(dto.getTimestamp())).forEach(dto -> dto.setTimestamp(collectTimeMs));
        log.debug("上报结果 result: {}", result);
        return result;
    }

    /**
     * 获取data.value的值
     *
     * @param platform
     * @param host
     * @param protocol
     * @param port
     * @param username
     * @param password
     * @param tags
     * @param resourceId
     * @return
     */
    protected abstract List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password,
                                                         String tags, String resourceId);

    /**
     * 获取上报数据的策略名称
     *
     * @return
     */
    public abstract DataReportTypeByMetricEnum metric();


    /**
     * data.type 的值，决定了 data.value 的类型
     *
     * @return
     */
    protected abstract ReportDataTypeEnum valueType();
}