package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;

public interface PlatformTestConnectionApi {

    /**
     * 鉴权认证连接
     *
     *
     * @param platform
     * @param ipAddress
     * @param port
     * @param username
     * @param pwd
     * @param protocol
     * @param authTyp
     * @return  鉴权认证的错误信息，如果认证成功则返回null
     */
    String connection(String platform,String ipAddress, Integer port, String username, String pwd, String protocol, String authTyp);

    /**
     * 平台类型
     *
     * @return
     */
    ReportResourceEnum platform();
}
