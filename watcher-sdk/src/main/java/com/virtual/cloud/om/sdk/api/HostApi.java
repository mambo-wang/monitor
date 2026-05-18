package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.SSHHost;

import java.util.Set;

/**
 * @Author: w22798
 * @Date: 2022/5/13 9:32
 */
public interface HostApi {

    /**
     * 返回ssh连接信息
     *
     * @param cvmHost rest连接信息(cas/uis/workspace平台登录信息)
     * @param endpoint 对象（在此仅为主机id）
     * @return 服务器ssh连接信息
     */
    SSHHost getHost(RestHost cvmHost, String endpoint);

    /**
     * 查询主机id
     * @param cvmHost
     * @return
     */
    Set<String> queryHostIds(RestHost cvmHost);

    /**
     * 身份识别
     * @return resource： workspace/cas/uis
     */
    ReportResourceEnum whoAreYou();
}
