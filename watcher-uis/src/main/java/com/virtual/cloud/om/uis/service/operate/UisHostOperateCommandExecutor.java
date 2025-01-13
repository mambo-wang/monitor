package com.virtual.cloud.om.uis.service.operate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.Utils;
import com.virtual.cloud.om.uis.dto.UisHostInfoDTO;
import com.virtual.cloud.om.uis.dto.UisRsTaskMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
/**
 * 纯cas环境不支持调用uis接口，所以这个类暂且保留但不使用
 */
@Deprecated
public class UisHostOperateCommandExecutor
//        extends OperateCommandApi
{
    private final UisRestConnection uisRestConnection;

//    @Override
    public void execute(String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        final String hostId = object.getHostId();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        ro.setHostId(hostId);
        data.setObject(ro);
        String msgId = this.addTask(host, port, protocol, username, password, type, hostId);
        if (StrUtil.isBlank(msgId)) {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] add task msgId is null", uuid, objectType, type);
            return;
        }
        UisRsTaskMsg uisRsTaskMsg = this.queryUisTaskMsg(host, protocol, port, username, password, msgId);
        if(Objects.isNull(uisRsTaskMsg)){
            log.info("[operate command][uuid={}][objectType={}][operateType={}] queryUisTaskMsg fail !", uuid, objectType, type);
            return;
        }
        if (StateResult.FAILURE == uisRsTaskMsg.getResult()) {
            String errorMsg = uisRsTaskMsg.getDetail();
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate fail : {}", uuid, objectType, type, errorMsg);
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
            data.setFailureMessage(errorMsg);
        } else {
            log.info("[operate command][uuid={}][objectType={}][operateType={}] operate success", uuid, objectType, type);
            data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        }
        {
            String uri = UisUriConstants.HOST_INFO_LIST;
            List<UisHostInfoDTO> hosts = this.uisRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<List<UisHostInfoDTO>>() {
            }).getBody();
            if(CollUtil.isNotEmpty(hosts)){
                hosts.stream().filter(dto->dto.getId().equals(hostId)).findFirst().ifPresent(dto->{
//                    ro.setMaintain(dto.getStatus());
//                    ro.setCvkMaintain(dto.getMaintainMode());
                });
            }
        }
    }

    private String addTask(String host, Integer port, String protocol, String username, String password, OperateTypeEnum type, String hostId) {
        String uri;
        switch (type) {
            case start: {
                uri = String.format(UisUriConstants.HOST_WAKE, hostId);
                break;
            }
            case stop: {
                uri = String.format(UisUriConstants.HOST_SHUTOFF, hostId);
                break;
            }
            case restart: {
                uri = String.format(UisUriConstants.HOST_REBOOT, hostId);
                break;
            }
            case intoMaintain: {
                uri = UisUriConstants.HOST_INTO_MAINTAIN;
                break;
            }
            case exitMaintain: {
                uri = UisUriConstants.HOST_EXIT_MAINTAIN;
                break;
            }
            default: {
                throw new AppException(ErrorCodes.OPERATE_COMMAND_NOT_SUPPORTED);
            }
        }
        RpcResult rpcResult = this.uisRestConnection.put(host, protocol, username, password, port, uri, JSONUtil.toJsonStr(new QeuryDTO(hostId)), new ParameterizedTypeReference<RpcResult>() {
        }).getBody();
        Utils.checkResult(uri, rpcResult);
        return String.valueOf(rpcResult.getData());
    }

    private UisRsTaskMsg queryUisTaskMsg(String host, String protocol, Integer port, String username, String password, String msgId) {
        String uri = String.format(UisUriConstants.TASK_MESSAGE, msgId);
        UisRsTaskMsg taskMsg = this.uisRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<UisRsTaskMsg>() {
        }).getBody();
        if (Objects.nonNull(taskMsg) && StrUtil.isNotBlank(taskMsg.getComplete())) {
            return taskMsg;
        }
        try {
            //每隔1秒查询下进度
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return this.queryUisTaskMsg(host, protocol, port, username, password, msgId);
    }

    @Data
    @AllArgsConstructor
    private class QeuryDTO {
        private String id;
    }

//    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.host;
    }
}
