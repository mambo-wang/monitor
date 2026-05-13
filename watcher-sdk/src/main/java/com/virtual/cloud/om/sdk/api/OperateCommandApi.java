package com.virtual.cloud.om.sdk.api;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.operate.CasRsTaskMsg;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.dto.websocket.WebsocketPushDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class OperateCommandApi {
    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OperateCommandApi.class);

    /**
     * 命令下发
     *
     * @param restHost
     * @param object
     * @param type
     * @param uuid
     * @return
     */
    public OperateResultDTO.DataDTO command(RestHost restHost, OperateQueryDTO.TargetObject object, OperateTypeEnum type, String uuid) {
        OperateResultDTO.DataDTO result = new OperateResultDTO.DataDTO();
        result.setOperateType(type.type);
        result.setResourceId(restHost.getResourceId());
        result.setUuid(uuid);
        result.setObjectType(this.type().type);
        final String host = restHost.getHost();
        final Integer port = restHost.getPort();
        final String protocol = restHost.getProtocol();
        final String username = restHost.getUsername();
        final String password = restHost.getPassword();
        final String platform = restHost.getPlatform();
        try {
            this.execute(platform, host, port, protocol, username, password, object, type, result);
        } catch (Exception e) {
            result.setResult(OperateResultDTO.DataDTO.ResultEnum.fail.val);
            result.setFailureMessage(e.getMessage());
            log.error("[operate command][uuid={}][objectType={}][operateType={}] operate error : {}", uuid, this.type(), type, e.getMessage());
        }
        return result;
    }

    /**
     * 执行下发的命令
     *
     * @param platform
     * @param host
     * @param port
     * @param protocol
     * @param username
     * @param password
     * @param object
     * @param type
     * @param data
     */
    public abstract void execute(String platform, String host, Integer port, String protocol, String username, String password,
                                 OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data);

    public WebsocketPushDTO refresh(RestHost restHost, OperateQueryDTO.TargetObject object, String uuid) {
        WebsocketPushTypeEnum websocketPushType = this.refreshStatusType();
        if (Objects.isNull(websocketPushType)) {
            return null;
        }
        final String host = restHost.getHost();
        final Integer port = restHost.getPort();
        final String protocol = restHost.getProtocol();
        final String username = restHost.getUsername();
        final String password = restHost.getPassword();
        final String platform = restHost.getPlatform();
        return new WebsocketPushDTO(websocketPushType, this.executeRefresh(platform, host, port, protocol, username, password, object, restHost.getResourceId(), uuid));
    }

    /**
     * 执行刷新操作(重新获取当前状态)
     *
     * @param platform
     * @param host
     * @param port
     * @param protocol
     * @param username
     * @param password
     * @param object
     * @param resourceId
     * @param uuid
     * @return
     */
    public abstract Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password,
                                          OperateQueryDTO.TargetObject object, String resourceId, String uuid);

    public abstract ObjectTypeEnum type();

    public abstract WebsocketPushTypeEnum refreshStatusType();

    @Resource
    private CasRestConnection casRestConnection;
    @Resource
    private WsTokenRestConnection wsTokenRestConnection;

    protected void doTerminalOperate(String host, Integer port, String protocol, String username, String password, OperateTypeEnum type, Long terminalId) {
        String uri;
        switch (type) {
            case restart: {
                uri = WsUriConstants.TERMINAL_RESTART;
                break;
            }
            case stop: {
                uri = WsUriConstants.TERMINAL_STOP;
                break;
            }
            case wake: {
                uri = WsUriConstants.TERMINAL_WAKE;
                break;
            }
            default: {
                throw new AppException(ErrorCodes.OPERATE_COMMAND_NOT_SUPPORTED);
            }
        }
        RpcResult rpcResult = this.wsTokenRestConnection.put(host, protocol, username, password, port, uri, JSONUtil.toJsonStr(Lists.newArrayList(terminalId)), new ParameterizedTypeReference<RpcResult>() {
        }).getBody();
        Utils.checkResult(uri, rpcResult);
    }

    /**
     * 虚拟机和桌面池的操作公用方法，因为跨了两个项目，所以放在了这里，其他实现类不需要使用这个方法
     * @param platform
     * @param host
     * @param protocol
     * @param port
     * @param username
     * @param password
     * @param objectId
     * @param type
     * @return
     */
    protected CasRsTaskMsg addTask(String platform, String host, String protocol, Integer port, String username, String password, String objectId, OperateTypeEnum type) {
        switch (type) {
            case start: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, String.format(CasUriConstants.Domain.DOMAIN_START, objectId), null, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case stop: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, String.format(CasUriConstants.Domain.DOMAIN_STOP, objectId), null, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case shutdown: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, String.format(CasUriConstants.Domain.DOMAIN_SHUT_DOWN, objectId), null, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            case restart: {
                return this.casRestConnection.put(platform, host, protocol, port, username, password, String.format(CasUriConstants.Domain.DOMAIN_RESTART, objectId), null, new ParameterizedTypeReference<CasRsTaskMsg>() {
                });
            }
            default: {
                throw new AppException(ErrorCodes.OPERATE_COMMAND_NOT_SUPPORTED);
            }
        }
    }

    protected CasRsTaskMsg queryCasTaskMsg(String platform, String host, String protocol, Integer port, String username, String password, Long msgId) {
        final String uri = String.format(CasUriConstants.MESSAGE, msgId);
        CasRsTaskMsg casRsTaskMsg = this.casRestConnection.get(platform, host, protocol, port, username, password, uri, new ParameterizedTypeReference<CasRsTaskMsg>() {
        });
        if (Objects.nonNull(casRsTaskMsg) && casRsTaskMsg.getCompleted()) {
            return casRsTaskMsg;
        }
        try {
            //每隔1秒查询下进度
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return this.queryCasTaskMsg(platform, host, protocol, port, username, password, msgId);
    }

    protected Integer getStatus(String status) {
        switch (status) {
            case "unknown":
                return 1;
            case "running":
                return 2;
            case "shutOff":
                return 3;
            case "paused":
                return 4;
            default:
                return 5;
        }
    }
}
