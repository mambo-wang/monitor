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
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.VipDeskAlarmDTO;
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
public class WsVIPDeskAlarmsCollector extends WarnReportCollector {
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
        WarnEndFromAndEndToDTO fromAndEndToDTO = dealWarnCollectTimeUtil.dealEndFromAndEndTo(resourceId, WarnTypeEnum.vipDesk.name());
        String uri = String.format(WsUriConstants.Warn.WARN_VIP_DESK_ALARMS, fromAndEndToDTO.getEndFrom(), fromAndEndToDTO.getEndTo(), NOT_PROCESS_WARN);
        RpcResult<List<VipDeskAlarmDTO>> rpcResult = new RpcResult<>();
        try {
            rpcResult = this.wsTokenRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcResult<List<VipDeskAlarmDTO>>>() {
            }).getBody();
        } catch (AppException e) {
            log.error("collect wsVipDeskAlarms is fail:" + e.getErrorMessage());
        }
        Utils.checkResult(uri, rpcResult);
        List<VipDeskAlarmDTO> vipDeskAlarmDTOList = rpcResult.getData();
        if (CollUtil.isEmpty(vipDeskAlarmDTOList)) {
            return Lists.newArrayList();
        }
        List<WarnDataDTO> vipDeskAlarmsValue = vipDeskAlarmDTOList.stream().map(vipDeskAlarm -> {
            WarnDataDTO dto = new WarnDataDTO();
            dto.setType(WarnConstant.warnType.VIP_DESK_WARN_TYPE);
            dto.setSrc(vipDeskAlarm.getDeskName());
            dto.setName(WarnConstant.eventName.VIP_DESK_WARN_NAME);
            dto.setObjectType(WarnConstant.ObjectType.VIP_DESK_OBJECTTYPE);
            dto.setMessage(vipDeskAlarm.getDeskName() + WarnConstant.eventName.VIP_DESK_WARN_NAME);
            dto.setStartsAt(vipDeskAlarm.getCreateTime());
            dto.setEndsAt(vipDeskAlarm.getLastTime());
            dto.setLevel(WarnConstant.warnLevel.WARN_LEVEL_WARNING);
            dto.setCount(vipDeskAlarm.getCount());
            dto.setResourceId(resourceId);
            return dto;
        }).collect(Collectors.toList());
        Optional<WarnDataDTO> max = vipDeskAlarmsValue.stream().max(Comparator.comparingLong(WarnDataDTO::getEndsAt));
        warnMgrApi.editWarnByResourceIdAndType(resourceId, max.get().getEndsAt(), WarnTypeEnum.vipDesk.name(), fromAndEndToDTO.getReportTime());

        value.addAll(vipDeskAlarmsValue);
        log.info("[collect workspace vip desk alarms][ip = {}] [value = {}][size = {}]", host, value, value.size());
        return value;
    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.vipdesktop_alarms;
    }

}
