package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 16:48
 */
@Data
public class StorClusterFsCapacityDTO implements Serializable {
    private static final long serialVersionUID = -4477460808459367882L;
    /**
     *
     "metric": "stor_cluster_fs_capacity",
     "type": "json",
     "tags": "resourceId=123",
     "value": [
     {
     "fs_id": "ceph集群 fs ID",
     "fs_total_space": 100,
     "fs_avail_space": 100,
     "fs_used_space": 100,
     "fs_flag": true
     }
     */

    private String fs_id;
    private Double fs_total_space;
    private Double fs_avail_space;
    private Double fs_used_space;
    private Boolean fs_flag;
}
