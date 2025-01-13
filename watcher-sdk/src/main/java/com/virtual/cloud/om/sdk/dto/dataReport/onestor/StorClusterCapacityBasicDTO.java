package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 16:37
 */
@Data
public class StorClusterCapacityBasicDTO implements Serializable {
    private static final long serialVersionUID = 6476046133296178410L;
    /**
     *  "metric": "stor_cluster_rbd_capacity",
     *       "type": "json",
     *       "tags": "resourceId=123",
     *       "value": [
     *         {
     *          "fs_id": "ceph集群 fs ID",
     *        11 "total_bytes": 100,
     *         "total_valid_bytes": 100,
     *        11 "avail_bytes": 100,
     *         "avail_valid_bytes": 100,
     *         "used_bytes": 100,
     *         "used_valid_bytes": 100,
     *         "pools_used_bytes": 100
     *         }
     */
    private String fs_id;
//    private Double rbd_total_space;
//    private Double rbd_avail_space;
//    private Double rbd_used_space;
//    private Boolean rbd_flag;

    private Long total_bytes;
    private Long total_valid_bytes;
    private Long avail_bytes;
    private Long avail_valid_bytes;
    private Long used_bytes;
    private Long used_valid_bytes;
    private Long pools_used_bytes;
}
