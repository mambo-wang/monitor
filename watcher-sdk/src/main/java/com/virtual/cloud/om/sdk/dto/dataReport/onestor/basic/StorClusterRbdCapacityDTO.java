package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/12/30 14:11
 */
@Data
public class StorClusterRbdCapacityDTO implements Serializable {
    private static final long serialVersionUID = -3637908630950868158L;
/**
 *  "fs_id": "ceph集群 fs ID",
 *                     "rbd_total_space": 100,
 *                     "rbd_avail_space": 100,
 *                     "rbd_used_space": 100,
 *                     "rbd_flag": true
 */
    private String fs_id;
    private Double rbd_total_space;
    private Double rbd_avail_space;
    private Double rbd_used_space;
    private Boolean rbd_flag;
}
