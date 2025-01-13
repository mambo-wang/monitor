package com.virtual.cloud.om.workspace.service.ssh;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.SshAuthAbstract;
import com.virtual.cloud.om.sdk.config.token.workspace.WsTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/9/13 10:37
 */
@Slf4j
@Service
public class WorkspaceSshService extends SshAuthAbstract {
    @Resource
    private WsTokenRestConnection wsTokenRestConnection;
    @Override
    public Boolean checkUserSshAuth( String ip, String protocol, Integer port,String username,String password) {
        ResponseEntity<VdisshCheckResult> post=null;

        try {
            VdiLoginInfoDTO vdiLoginInfoDTO=new VdiLoginInfoDTO();
            vdiLoginInfoDTO.setLoginName(SM4Utils.webEncryptText(username));
            vdiLoginInfoDTO.setPwd(password);
            post = wsTokenRestConnection.post(ip, protocol, username, SM4Utils.webDecryptText(password), port, WsUriConstants.Sync.WS_SPACE_CONSOLE_LOGIN, JSONUtil.toJsonStr(vdiLoginInfoDTO), new ParameterizedTypeReference<VdisshCheckResult>() {
            });
        }catch (AppException ex){
            log.error("[RESOURCEREMOTE]  check user ssh auth fail : {}",ex);
            if (ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)){
                return true;
            }
            throw ex;
        }catch (Exception e) {
            log.error("[RESOURCEREMOTE]  check user ssh auth fail : {}",e);
            throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH);
        }
        if (Objects.isNull(post)){
            return false;
        }
        VdisshCheckResult rpcResult = post.getBody();
        if (Objects.nonNull(rpcResult)){
            if (!Objects.equals(rpcResult.getLoginFailErrorCode(),"0")){
                throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH_REASION,rpcResult.getLoginFailMessage());
            }
            return rpcResult.getPermissions().contains("SystemMgr.ParamSet.SystemParam.Mng");
        }
        return false;
    }

    @Override
    public Boolean modifySshAuth(Boolean flag, String ip, String protocol, Integer port,String username,String password) {
        ResponseEntity<RpcResult> post=null;
        EnableSSHSystemConfigDTO dto = new EnableSSHSystemConfigDTO();
        dto.setSshEnable(flag);
        String str = JSONUtil.toJsonStr(dto);
        try {
            post = wsTokenRestConnection.post(ip,  protocol, username, SM4Utils.webDecryptText(password), port,
                    WsUriConstants.SshAuth.VDI_MODIFY_SSH_AUTH, str, new ParameterizedTypeReference<RpcResult>() {
                    });

        } catch (AppException ex){
            return ex.getErrorCode().equals(ErrorCodes.NOT_FOUND);
        }catch (Exception e) {
            log.info("[RESOURCEREMOTE]  modify  ssh auth  success  resource : {}",ip);
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        if (Objects.isNull(post)){
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        RpcResult modifyResult = post.getBody();
        if (Objects.isNull(modifyResult)){
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        if (Objects.equals(modifyResult.getState(),RpcResult.FAILURE)){
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        log.info("[RESOURCEREMOTE]  modify  ssh auth  success  resource : {}",ip);
        return true;
    }

    @Override
    public Boolean getSshType(String ip, String protocol, String username, String password, Integer port) {
        ResponseEntity<RpcResult> responseEntity=null;
        try {
             responseEntity = wsTokenRestConnection.get(ip, protocol, username, password, port, WsUriConstants.SshAuth.VDI_QUERY_SSH_AUTH, new ParameterizedTypeReference<RpcResult>() {
            });
        }catch (AppException ex){
            if (ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)){
                throw new AppException(ErrorCodes.NOT_FOUND);
            }
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }catch (Exception e) {
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        if (Objects.isNull(responseEntity)) {
            log.error("[rpc-error]remote call error, response body is empty");
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        RpcResult rpcResult = responseEntity.getBody();
        if (Objects.isNull(rpcResult)) {
            log.error("[rpc-error]remote call error, response body is empty");
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        return (Boolean) ((LinkedHashMap)rpcResult.getData()).get("sshEnable");
    }

    @Override
    public String resourceType() {
        return Constant.RESOURCE_WORKSPACE;
    }

}
