package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

/**
 * @author:XK
 * @Date:2022/8/23 21:50
 */
@Data
public class StorClusterBasicGetDTO {
    /**
     * “client_name”: “client”, #客户名
     * “unistor_cluster_name”: “unistor”},  #集群名称
     */
    private String client_name;

    private String unistor_cluster_name;
}
