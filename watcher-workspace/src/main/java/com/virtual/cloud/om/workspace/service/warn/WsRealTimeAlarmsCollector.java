package com.virtual.cloud.om.workspace.service.warn;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.WarnMgrApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.constant.WarnTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnEndFromAndEndToDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnInfoDTO;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WsRealTimeAlarmsCollector extends WarnReportCollector {
    private final WsTokenRestConnection wsTokenRestConnection;
    private final UisRestConnection uisRestConnection;

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
        String uri = String.format(WsUriConstants.Warn.WARN_REAL_TIME_ALARMS, Utils.formatFullDateTime(fromAndEndToDTO.getEndFrom()), Utils.formatFullDateTime(fromAndEndToDTO.getEndTo()), NOT_PROCESS_WARN);
        RpcResult<List<WarnInfoDTO>> rpcResult = new RpcResult<>();
        try {
            rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcResult<List<WarnInfoDTO>>>() {
            }).getBody();
        } catch (Exception e) {
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, uri,e.getMessage());
        }
        Utils.checkResult(uri, rpcResult);
        List<WarnInfoDTO> wsRealTimeAlarmsList = rpcResult.getData();
        log.info("ws real time alarm is {}", wsRealTimeAlarmsList);
        if (CollUtil.isEmpty(wsRealTimeAlarmsList)) {
            return Lists.newArrayList();
        }
        List<WarnDataDTO> realTimeAlarmsValue = wsRealTimeAlarmsList.stream().map(realTimeAlarm -> {
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
            dto.setStartsAt(realTimeAlarm.getFirstEventTime());
            dto.setEndsAt(realTimeAlarm.getEventTime());
            dto.setLevel(realTimeAlarm.getEventLevel());
            dto.setCount(realTimeAlarm.getEventCount());
            dto.setResourceId(resourceId);
            return dto;
        }).collect(Collectors.toList());
        Optional<WarnDataDTO> max = realTimeAlarmsValue.stream().max(Comparator.comparingLong(WarnDataDTO::getEndsAt));
        warnMgrApi.editWarnByResourceIdAndType(resourceId, max.get().getEndsAt(), WarnTypeEnum.realTime.name(), fromAndEndToDTO.getReportTime());

        value.addAll(realTimeAlarmsValue);
        log.info("[collect workspace real time alarms][ip = {}] [value = {}][size = {}]", host, value, value.size());
        return value;
    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.ws_realtime_alarms;
    }

}
