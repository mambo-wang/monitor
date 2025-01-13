package com.virtual.cloud.om.uis.service.ssh;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.SshAuthAbstract;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.EnableSSHSystemConfigDTO;
import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.VdisshCheckResult;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import com.virtual.cloud.om.sdk.utils.uis.WebDecryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/9/13 10:44
 */
@Service
@Slf4j
public class UisSshService extends SshAuthAbstract {
    @Resource
    private UisRestConnection uisRestConnection;
    @Override
    public Boolean checkUserSshAuth( String ip, String protocol, Integer port,String username,String password) {

        String uri = String.format(UisUriConstants.LOGIN, WebDecryptUtils.encryptData(username), WebDecryptUtils.encryptData(SM4Utils.webDecryptText(password)));
        ResponseEntity<String> rpcResult = null;
        try {
            rpcResult = this.uisRestConnection.post(ip, protocol, username, SM4Utils.webDecryptText(password), port, uri, "", new ParameterizedTypeReference<String>() {
            });

        } catch (AppException ex){
            if ( ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)){
                return true;
            }
           throw ex;
        }catch (Exception e) {
            throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH);
        }
        if (Objects.isNull(rpcResult)){
           return false;
        }
        log.info("[RESOURCEREMOTE] check user ssh auth result is {}", rpcResult);
        String body = rpcResult.getBody();
        if (Objects.nonNull(body)){
            VdisshCheckResult vdisshCheckResult = JSONUtil.toBean(body, VdisshCheckResult.class);
            if (!Objects.equals(vdisshCheckResult.getLoginFailErrorCode(),"0")){
               throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH_REASION,vdisshCheckResult.getLoginFailMessage());
           }
            return vdisshCheckResult.getPermissions().contains("SystemMgr.ParamSet.SystemParam");
        }
        return false;
    }

    @Override
    public Boolean modifySshAuth(Boolean flag, String ip, String protocol, Integer port,String username,String password) {
        EnableSSHSystemConfigDTO dto = new EnableSSHSystemConfigDTO();
        dto.setSshEnable(flag);
        ResponseEntity<RpcResult> post= null;
        try {
            post = uisRestConnection.post(ip, protocol, username, SM4Utils.webDecryptText(password), port,
                    UisUriConstants.SshAuth.MODIFY_ENABLE_SSH, JSONUtil.toJsonStr(dto), new ParameterizedTypeReference<RpcResult>() {
                    });
        }catch (AppException ex){
            return ex.getErrorCode().equals(ErrorCodes.NOT_FOUND);
        }catch (Exception e) {
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        if (Objects.isNull(post)){
            return true;
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
        ResponseEntity<RpcResult> rpcResultResponseEntity = null;
        try {
            rpcResultResponseEntity = uisRestConnection.get(ip, protocol, username, SM4Utils.webDecryptText(password), port, UisUriConstants.SshAuth.QUERY_ENABLE_SSH, new ParameterizedTypeReference<RpcResult>() {
            });
        }catch (AppException ex){
            if (ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)){
                throw new AppException(ErrorCodes.NOT_FOUND);
            }
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }catch (Exception e) {
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        if (Objects.isNull(rpcResultResponseEntity)) {
            log.error("[rpc-error]remote call error, response body is empty");
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        RpcResult rpcResult = rpcResultResponseEntity.getBody();
        if (Objects.isNull(rpcResult)) {
            log.error("[rpc-error]remote call error, response body is empty");
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        return (Boolean) ((LinkedHashMap)rpcResult.getData()).get("sshEnable");
    }

    @Override
    public String resourceType() {
        return Constant.RESOURCE_UIS;
    }
}
