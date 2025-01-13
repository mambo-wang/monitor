package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 21:15
 */
@Data
public class StorClusterCapacityDTO implements Serializable {
    private static final long serialVersionUID = -2999843544719746572L;
    /**
     *
     "fs_id": "ceph集群 fs ID",
     "total": 1.111,
     "used": 1.111,
     "update_time": "2020-12-01 14:01:32.935200"
     */
    private String clusterName;
    private Double total;
    private Double used;
    private String update_time;
}
