package com.virtual.cloud.om.workspace.service.warn;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.WarnMgrApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.constant.WarnTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.DeviceAlarmDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnEndFromAndEndToDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
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
public class WsTerminalAlarmsCollector extends WarnReportCollector {
    private final WsTokenRestConnection wsTokenRestConnection;

    @Resource
    private WarnMgrApi warnMgrApi;

    @Autowired(required = false)
    private DealWarnCollectTimeUtil dealWarnCollectTimeUtil;

    public static final Integer NOT_PROCESS_WARN = 2;

    @Override
    protected List<WarnDataDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<WarnDataDTO> value = new CopyOnWriteArrayList<>();

        //获取采集开始时间、结束时间
        WarnEndFromAndEndToDTO fromAndEndToDTO = dealWarnCollectTimeUtil.dealEndFromAndEndTo(resourceId, WarnTypeEnum.terminal.name());
        String uri = String.format(WsUriConstants.Warn.WARN_TERMINAL_ALARMS, 0, 100, NOT_PROCESS_WARN);
        RpcResult<List<DeviceAlarmDTO>> rpcResult = new RpcResult<>();
        try {
            rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcResult<List<DeviceAlarmDTO>>>() {
            }).getBody();
        } catch (AppException e) {
            log.error("collect wsTerminalAlarms is fail:" + e.getErrorMessage());
        }
        Utils.checkResult(uri, rpcResult);
        List<DeviceAlarmDTO> deviceAlarmDTOList = rpcResult.getData();
        if (CollUtil.isEmpty(deviceAlarmDTOList)) {
            return Lists.newArrayList();
        }
        List<WarnDataDTO> terminalAlarmsValue = deviceAlarmDTOList.stream().filter(deviceAlarmDTO ->
                deviceAlarmDTO.getLastAlarmTime().longValue() >= fromAndEndToDTO.getEndFrom().longValue()
                        && deviceAlarmDTO.getLastAlarmTime().longValue() <= fromAndEndToDTO.getEndTo().longValue()).map(terminalAlarm -> {
            WarnDataDTO dto = new WarnDataDTO();
            dto.setType(WarnConstant.warnType.TERMINAL_WARN_TYPE);
            dto.setSrc(terminalAlarm.getDeviceName() + terminalAlarm.getAlarmSource());
            dto.setName(WarnConstant.eventName.TERMINAL_WARN_NAME);
            dto.setObjectType(WarnConstant.ObjectType.TERMINAL_OBJECTTYPE);
            dto.setMessage(terminalAlarm.getDescription());
            dto.setStartsAt(terminalAlarm.getFirstAlarmTime());
            dto.setEndsAt(terminalAlarm.getLastAlarmTime());
            dto.setLevel(WarnConstant.warnLevel.WARN_LEVEL_WARNING);
            dto.setCount(WarnConstant.count.WARN_COUNT);
            dto.setResourceId(resourceId);
            return dto;
        }).collect(Collectors.toList());
        Optional<WarnDataDTO> max = terminalAlarmsValue.stream().max(Comparator.comparingLong(WarnDataDTO::getEndsAt));
        warnMgrApi.editWarnByResourceIdAndType(resourceId, max.get().getEndsAt(), WarnTypeEnum.terminal.name(), fromAndEndToDTO.getReportTime());

        value.addAll(terminalAlarmsValue);
        log.info("[collect workspace terminal alarms][ip = {}] [value = {}][size = {}]", host, value, value.size());
        return value;
    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.terminal_alarms;
    }

}
