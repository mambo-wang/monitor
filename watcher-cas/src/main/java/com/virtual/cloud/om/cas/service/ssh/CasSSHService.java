package com.virtual.cloud.om.cas.service.ssh;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.sdk.api.SshAuthAbstract;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.token.cas.CasTokenRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.CasSSHResult;
import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import com.virtual.cloud.om.sdk.dto.VdisshCheckResult;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/9/13 9:38
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CasSSHService extends SshAuthAbstract {
    @Resource
    private CasRestConnection casRestConnection;
    @Resource
    private CasTokenRestConnection casTokenRestConnection;
    @Override
    public Boolean checkUserSshAuth( String ip, String protocol, Integer port,String username,String password) {
        String uri = String.format(CasUriConstants.SshAuth.CAS_LOGIN, System.currentTimeMillis(),username, SM4Utils.webDecryptText(password));
        String rpcResult = null;
        try {
             rpcResult = this.casRestConnection.post(Constant.RESOURCE_CAS, ip, protocol, port, username, SM4Utils.webDecryptText(password)
                    , uri, HttpEntity.EMPTY, new ParameterizedTypeReference<String>() {
                    });

        }catch (AppException ex){
            if (ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)){
                return true;
            }
            throw ex;
        }catch (Exception e) {
            throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH);
        }
        if (Objects.nonNull(rpcResult)){
            VdisshCheckResult vdisshCheckResult = JSONUtil.toBean(rpcResult, VdisshCheckResult.class);
            log.info("[RESOURCEREMOTE] check user ssh auth result is {}", rpcResult);
            if (!Objects.equals(vdisshCheckResult.getLoginFailErrorCode(),"0")){
                throw new AppException(ErrorCodes.WATCHER_CHECK_USER_SSH_AUTH_REASION,vdisshCheckResult.getLoginFailMessage());
            }
            return vdisshCheckResult.getPermissions().contains("SystemMgr.SystemConfigMgr.SystemParam");
        }
        return false;
    }

    @Override
    public Boolean modifySshAuth(Boolean flag, String ip, String protocol, Integer port,String username,String password) {
        CasSSHResult casSSHResult=new CasSSHResult();
        casSSHResult.setName("ssh.enable");
        if (flag){
            casSSHResult.setValue("1");
        }else {
            casSSHResult.setValue("0");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, casTokenRestConnection.refreshToken(ip, protocol, port, username, SM4Utils.webDecryptText(password)));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        HttpEntity httpEntity=new HttpEntity(JSONUtil.toJsonStr(casSSHResult),headers);
        String modifyResult = null;
        try {
            modifyResult = casRestConnection.post(Constant.RESOURCE_CAS, ip, protocol, port, username, SM4Utils.webDecryptText(password), CasUriConstants.SshAuth.MODIFY_CAS_SSH_AUTH, httpEntity, new ParameterizedTypeReference<String>() {
            });
        }catch (AppException ex){
            if(ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)||ex.getErrorCode().equals(ErrorCodes.HTTP_CLIENT_ERROR)){
                return true;
            }
            return false;
        }catch (Exception e) {
            log.info("[RESOURCEREMOTE]  modify  ssh auth  success  resource : {}",ip);
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        if (Objects.isNull(modifyResult)){
            throw new AppException(ErrorCodes.MODIFY_SSH_AUTH_FAIL);
        }
        log.info("[RESOURCEREMOTE]  modify  ssh auth  success  resource : {}",ip);
        return modifyResult.contains("true");
    }

    @Override
    public Boolean getSshType(String ip, String protocol, String username, String password, Integer port) {
        ResponseEntity<List<CasSSHResult>> listResponseEntity=null;
        try {
            listResponseEntity = casTokenRestConnection.get(ip, protocol, username, SM4Utils.webDecryptText(password), port, CasUriConstants.SshAuth.FIND_SSH_CONF, new ParameterizedTypeReference<List<CasSSHResult>>() {
            });
        }catch (AppException ex){
            if (ex.getErrorCode().equals(ErrorCodes.NOT_FOUND)||ex.getErrorCode().equals(ErrorCodes.HTTP_CLIENT_ERROR)){
                throw new AppException(ErrorCodes.NOT_FOUND);
            }
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }catch (Exception e) {
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        if (Objects.isNull(listResponseEntity)){
            throw new AppException(ErrorCodes.REOSURE_NO_USABLE);
        }
        List<CasSSHResult> casSSHResults = listResponseEntity.getBody();
        if (CollectionUtil.isEmpty(casSSHResults)){
            throw new AppException(ErrorCodes.NOT_FOUND);
        }
        return casSSHResults.get(0).getValue().equals("1");
    }

    @Override
    public String resourceType() {
        return Constant.RESOURCE_CAS;
    }
}
