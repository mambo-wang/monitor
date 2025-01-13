package com.virtual.cloud.om.workspace.service;

import com.virtual.cloud.om.sdk.api.PlatformTestConnectionApi;
import com.virtual.cloud.om.sdk.config.rest.common.RestType;
import com.virtual.cloud.om.sdk.config.rest.workspace.WsRestConnection;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.testConnection.WorkspaceLoginInfoDTO;
import com.virtual.cloud.om.sdk.dto.testConnection.WorkspaceLoginResultDTO;
import com.virtual.cloud.om.sdk.utils.Utils;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkspaceTestConnectionApi implements PlatformTestConnectionApi {
    private final WsRestConnection wsRestConnection;

    @Override
    public String connection(String platform, String ipAddress, Integer port, String username, String pwd, String protocol, String authTyp) {
        String uri = WsUriConstants.Test.TEST_CONNECTION;
        WorkspaceLoginInfoDTO loginInfo = new WorkspaceLoginInfoDTO();
        loginInfo.setLoginName(SM4Utils.webEncryptText(username));
        loginInfo.setPwd(SM4Utils.webEncryptText(pwd));
        loginInfo.setLoginIp(ipAddress);
        loginInfo.setPort(port);
        loginInfo.setProtocol(protocol);
        RpcResult<WorkspaceLoginResultDTO> rpcResult = wsRestConnection.post(ipAddress, protocol, port, uri, loginInfo, new RestType<RpcResult<WorkspaceLoginResultDTO>>() {
        });
        Utils.checkResult(uri, rpcResult);
        WorkspaceLoginResultDTO data = rpcResult.getData();
        return data.getLoginStatus() ? null : data.getErrorMessage();
    }

    @Override
    public ReportResourceEnum platform() {
        return ReportResourceEnum.workspace;
    }
}
