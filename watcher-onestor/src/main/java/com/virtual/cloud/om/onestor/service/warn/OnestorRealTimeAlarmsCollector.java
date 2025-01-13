package com.virtual.cloud.om.onestor.service.warn;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.WarnMgrApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.constant.WarnTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRequestDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorWarnRealDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnEndFromAndEndToDTO;
import com.virtual.cloud.om.sdk.utils.DealWarnCollectTimeUtil;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnestorRealTimeAlarmsCollector extends WarnReportCollector {

    @Resource
    private WarnMgrApi warnMgrApi;

    @Autowired(required = false)
    private DealWarnCollectTimeUtil dealWarnCollectTimeUtil;

    public static final String ALARM_STATUS = "un_recovery";

    private final OnestorRestConnection onestorRestConnection;

    @Override
    public List<WarnDataDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<WarnDataDTO> value = new CopyOnWriteArrayList<>();

        //获取采集开始时间、结束时间
        WarnEndFromAndEndToDTO fromAndEndToDTO = dealWarnCollectTimeUtil.dealEndFromAndEndTo(resourceId, WarnTypeEnum.realTime.name());

        //请求参数
        OneStorRequestDTO requestDTO = new OneStorRequestDTO();
        List<Object> listAlarmLevel = new ArrayList<>();
        listAlarmLevel.add("warning");
        listAlarmLevel.add("minor");
        listAlarmLevel.add("major");
        listAlarmLevel.add("critical");
        listAlarmLevel.add("self_define");
        requestDTO.setAlarm_level(listAlarmLevel);
        List<Object> listAlarmModel = new ArrayList<>();
        requestDTO.setAlarm_module(listAlarmModel);
        requestDTO.setBegin_time(fromAndEndToDTO.getEndFrom()/1000);
        log.info("onestor采集开始时间 {}",fromAndEndToDTO.getEndFrom()/1000);
        requestDTO.setEnd_time(fromAndEndToDTO.getEndTo()/1000);
        log.info("onestor采集结束时间 {}",fromAndEndToDTO.getEndTo()/1000);
        String[] arrays = {ALARM_STATUS};
        requestDTO.setAlarm_status(arrays);
        List<OneStorWarnRealDTO> oneStorWarnRealDTOList = Lists.newArrayList();

        try {
            OneStorRestResult oneStorRestResult = this.onestorRestConnection.post(host, protocol, username, password, port, OnestoreUriConstants.Warn.ONESTORE_ALARM_REAL, SerializeUtils.toJson(requestDTO), new ParameterizedTypeReference<OneStorRestResult>() {
            }).getBody();
            String onestorResult = JSONUtil.parseObj(oneStorRestResult.getData()).get("result").toString();
            oneStorWarnRealDTOList = SerializeUtils.json2Array(onestorResult, OneStorWarnRealDTO.class);
        } catch (Exception e) {
            log.error("onestor rest fail: " + e.getMessage());
        }
        if (CollUtil.isEmpty(oneStorWarnRealDTOList)) {
            return Lists.newArrayList();
        }
        List<WarnDataDTO> realTimeAlarmsCASValue = oneStorWarnRealDTOList.stream().map(realTimeAlarm -> {
            WarnDataDTO dto = new WarnDataDTO();
            dto.setType(realTimeAlarm.getType());
            dto.setSrc(realTimeAlarm.getNodepool_name());
            dto.setName(realTimeAlarm.getAlarm_module() + WarnConstant.eventName.ONESTOR_WARN_NAME);
            dto.setObjectType(realTimeAlarm.getObjectType());
            dto.setMessage(realTimeAlarm.getAlarm_content());
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime firstEventTimeParse = LocalDateTime.parse(realTimeAlarm.getAlarm_time(), pattern);
            long firstEventTimeLong = LocalDateTime.from(firstEventTimeParse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            dto.setStartsAt(firstEventTimeLong);
            LocalDateTime eventTimeParse = LocalDateTime.parse(realTimeAlarm.getAlarm_time(), pattern);
            long eventTimeLong = LocalDateTime.from(eventTimeParse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            dto.setEndsAt(eventTimeLong);
            dto.setLevel(realTimeAlarm.getLevel());
            dto.setCount(WarnConstant.count.WARN_COUNT);
            dto.setResourceId(resourceId);
            return dto;
        }).collect(Collectors.toList());
        Optional<WarnDataDTO> max = realTimeAlarmsCASValue.stream().max(Comparator.comparingLong(WarnDataDTO::getEndsAt));
        warnMgrApi.editWarnByResourceIdAndType(resourceId, max.get().getEndsAt(), WarnTypeEnum.realTime.name(), fromAndEndToDTO.getReportTime());
        value.addAll(realTimeAlarmsCASValue);
        log.info("[collect onestor warn][ip = {}] [value = {}][size = {}]", host, value,value.size());
        return value;
    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.stor_alarm_count;
    }

}
