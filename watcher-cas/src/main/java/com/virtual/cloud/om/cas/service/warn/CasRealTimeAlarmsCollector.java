package com.virtual.cloud.om.cas.service.warn;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.WarnMgrApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.constant.WarnTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.RealTimeAlarmDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnEndFromAndEndToDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.DealWarnCollectTimeUtil;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasRealTimeAlarmsCollector extends WarnReportCollector {
    private final CasRestConnection casRestConnection;

    @Resource
    private WarnMgrApi warnMgrApi;

    @Autowired(required = false)
    private DealWarnCollectTimeUtil dealWarnCollectTimeUtil;

    public static final Integer NOT_PROCESS_WARN = 2;

    @Override
    protected List<WarnDataDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<WarnDataDTO> value = new CopyOnWriteArrayList<>();

        //获取采集开始时间、结束时间
        WarnEndFromAndEndToDTO fromAndEndToDTO = dealWarnCollectTimeUtil.dealEndFromAndEndTo(resourceId, WarnTypeEnum.realTime.name());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String url = String.format(CasUriConstants.Warn.WARN_REAL_TIME_ALARMS, Utils.formatFullDateTime(fromAndEndToDTO.getEndFrom()), Utils.formatFullDateTime(fromAndEndToDTO.getEndTo()), NOT_PROCESS_WARN);
        List<RealTimeAlarmDTO> alarmDTOList = Lists.newArrayList();
        try {
            alarmDTOList = this.casRestConnection.get(platform, host, protocol, port, username, password, url, new ParameterizedTypeReference<List<RealTimeAlarmDTO>>() {
            });
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
        }
        if (CollUtil.isEmpty(alarmDTOList)) {
            return Lists.newArrayList();
        }
        List<WarnDataDTO> realTimeAlarmsCASValue = alarmDTOList.stream().map(realTimeAlarm -> {
            WarnDataDTO dto = new WarnDataDTO();
            dto.setType(realTimeAlarm.getCategory());
            String eventSrc = realTimeAlarm.getEventSrc();
            dto.setSrc(eventSrc);
            dto.setName(realTimeAlarm.getEventName());
            if (eventSrc.contains(WarnConstant.eventSrc.HOST_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.HOST_OBJECTTYPE);
            }else if (eventSrc.contains(WarnConstant.eventSrc.DOMAIN_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.DOMAIN_OBJECTTYPE);
            }else if (eventSrc.contains(WarnConstant.eventSrc.CLUSTER_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.CLUSTER_OBJECTTYPE);
            }else if (eventSrc.contains(WarnConstant.eventSrc.DESK_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.DESK_OBJECTTYPE);
            }else if (eventSrc.contains(WarnConstant.eventSrc.VIRTUAL_APP_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.VIRTUAL_APP_OBJECTTYPE);
            }else if (eventSrc.contains(WarnConstant.eventSrc.TERMINAL_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.TERMINAL_OBJECTTYPE);
            }else if (eventSrc.contains(WarnConstant.eventSrc.DISTRIBUTE_STORAGE_SRC)){
                dto.setObjectType(WarnConstant.ObjectType.DISTRIBUTE_STORAGE_OBJECTTYPE);
            }else {
                dto.setObjectType(WarnConstant.ObjectType.OTHER_OBJECTTYPE);
            }
            dto.setMessage(realTimeAlarm.getEventDesc());
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String firstEventTime = dateFormat.format(realTimeAlarm.getFirstEventTime());
            LocalDateTime firstEventTimeParse = LocalDateTime.parse(firstEventTime, pattern);
            long firstEventTimeLong = LocalDateTime.from(firstEventTimeParse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            dto.setStartsAt(firstEventTimeLong);
            String eventTime = dateFormat.format(realTimeAlarm.getEventTime());
            LocalDateTime eventTimeParse = LocalDateTime.parse(eventTime, pattern);
            long eventTimeLong = LocalDateTime.from(eventTimeParse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            dto.setEndsAt(eventTimeLong);
            dto.setLevel(realTimeAlarm.getEventLevel());
            dto.setCount(realTimeAlarm.getEventCount());
            dto.setResourceId(resourceId);
            return dto;
        }).collect(Collectors.toList());
        Optional<WarnDataDTO> max = realTimeAlarmsCASValue.stream().max(Comparator.comparingLong(WarnDataDTO::getEndsAt));
        warnMgrApi.editWarnByResourceIdAndType(resourceId, max.get().getEndsAt(), WarnTypeEnum.realTime.name(), fromAndEndToDTO.getReportTime());

        value.addAll(realTimeAlarmsCASValue);
        log.info("[collect cas real time alarms][ip = {}] [value = {}][size = {}]", host, value, value.size());
        return value;
    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.cas_realtime_alarms;
    }

}
