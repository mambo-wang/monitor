package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.RestHost;

/**
 * @Author: w22798
 * @Date: 2022/5/6 11:23
 */
public interface BatchLogApi {

    /**
     * 日志收集
     * @param restInfo 管理平台rest连接信息
     * @param endpoints 目标节点id
     * @param endpointType 目标节点类型
     * @param logName 日志名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param tempDir 临时存放路径
     */
    void batchLogCollect(RestHost restInfo, String endpoints, String endpointType, String logName, String startTime, String endTime, String tempDir);

    /**
     * 身份识别
     * @return resource： workspace/cas/uis
     */
    String whoAreYou();
}
