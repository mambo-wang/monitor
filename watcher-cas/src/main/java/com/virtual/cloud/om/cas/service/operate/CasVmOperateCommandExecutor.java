package com.virtual.cloud.om.cas.service.operate;

import cn.hutool.core.util.StrUtil;
import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.DomainDetailInfoDTO;
import com.virtual.cloud.om.sdk.dto.operate.CasRsTaskMsg;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.dto.operate.RefreshStatusResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CasVmOperateCommandExecutor extends OperateCommandApi {
    private final CasRestConnection casRestConnection;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final String domainId = object.getDomainId();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setDomainId(domainId);
        data.setObject(ro);
        String domainBasicInfoUri = String.format(CasUriConstants.Domain.DOMAIN_BASIC_INFO, domainId);
        DomainDetailInfoDTO domainDetailInfoDTO = this.casRestConnection.get(platform, host, protocol, port, username, password, domainBasicInfoUri, new ParameterizedTypeReference<DomainDetailInfoDTO>() {
        });
        if (Objects.isNull(domainDetailInfoDTO)) {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] cant find vm id by domainId={}", uuid, objectType, type, domainId);
            return;
        }
        final String domainUuid = domainDetailInfoDTO.getUuid();
        CasRsTaskMsg casRsTaskMsg = this.addTask(platform, host, protocol, port, username, password, domainUuid, type);
        log.info("[operate command][uuid={}][objectType={}][operateType={}] operate task complete !", uuid, objectType, type);
        casRsTaskMsg = this.queryCasTaskMsg(platform, host, protocol, port, username, password, casRsTaskMsg.getMsgId());
        if (Objects.isNull(casRsTaskMsg)) {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] queryCasTaskMsg fail !", uuid, objectType, type);
            return;
        }
        if (StateResult.FAILURE == casRsTaskMsg.getResult()) {
            String errorMsg = StrUtil.isNotBlank(casRsTaskMsg.getFailMsg()) ? casRsTaskMsg.getFailMsg() : casRsTaskMsg.getDetail();
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate fail : {}", uuid, objectType, type, errorMsg);
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
            data.setFailureMessage(errorMsg);
        } else {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        }
        // 查询虚拟机当前状态
        // 因为安全关闭需要一段时间才会生效，当任务执行成功之后立即查当前状态返回的仍然是开机，所以当执行安全关闭操作的任务执行成功后，就不再查询当前状态而是直接返回关闭状态
        if (OperateTypeEnum.stop != type) {
            String uri = String.format(CasUriConstants.Domain.QUERY_DOMAIN_STATUS, domainId);
            Integer status = this.getStatus(this.casRestConnection.get(platform, host, protocol, port, username, password, uri, new ParameterizedTypeReference<String>() {
            }));
            ro.setDomainStatus(status);
        } else {
            ro.setDomainStatus(this.getStatus("shutOff"));
        }
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        final ObjectTypeEnum objectType = this.type();
        final String domainId = object.getDomainId();
        RefreshStatusResultDTO.Domain domain = new RefreshStatusResultDTO.Domain();
        domain.setDomainId(domainId);
        domain.setUuid(uuid);
        domain.setResourceId(resourceId);
        try {
            Integer status = this.getStatus(this.casRestConnection.get(platform, host, protocol, port, username, password,
                    String.format(CasUriConstants.Domain.QUERY_DOMAIN_STATUS, domainId), new ParameterizedTypeReference<String>() {
                    }));
            log.info("[refresh status][uuid={}][objectType={}] select domain status success id={}", uuid, objectType, domainId);
            domain.setDomainStatus(status);
        } catch (Exception e) {
            domain.setFailMsg(e.getMessage());
            log.error("[refresh status][uuid={}][objectType={}] select domain status error id={} : {}", uuid, objectType, domainId, e);
        }
        return domain;
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.vm;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return WebsocketPushTypeEnum.reportDomainStatus;
    }
}
