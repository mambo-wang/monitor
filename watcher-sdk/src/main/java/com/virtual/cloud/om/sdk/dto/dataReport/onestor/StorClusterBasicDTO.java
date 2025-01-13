package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/23 21:44
 */
@Data
public class StorClusterBasicDTO implements Serializable {
    /**
     *  "fs_id": "ceph集群 fs ID",
     *           "cluster_name": "集群名称",
     *           "client_name": "客户名称",
     *           "update_time": "2020-12-01 14:01:32.935200"
     */
    private static final long serialVersionUID = 7639964545086961876L;

    private String fs_id;
    private String cluster_name;
    private String client_name;
    private String update_time;
}
