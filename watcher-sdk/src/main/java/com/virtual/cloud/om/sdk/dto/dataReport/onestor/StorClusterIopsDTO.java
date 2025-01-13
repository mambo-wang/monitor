package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 20:35
 */
@Data
public class StorClusterIopsDTO implements Serializable {
    private static final long serialVersionUID = -119805872605585050L;
//    {
//        "fs_id": "ceph集群 fs ID",
//            "iopsRead": 1.111,
//            "iopsWrite": 1.111,
//            "recoverOps": 1.111,
//            "opsRead": 1.111,
//            "opsWrite": 1.111,
//            "update_time": "2020-12-01 14:01:32.935200"


    private String clusterName;
    private Double iopsRead;
    private Double iopsWrite;
    private Double recoverOps;
    private Double opsRead;
    private Double opsWrite;
    private String update_time;
    /**
     * 集群总IOPS
     */
    private Double allIops;
    /**
     * 文件总OPS
     */
    private Double fsTotalOps;
    /**
     * 集群总带宽
     */
    private Double allBw;
    /**
     * 文件总带宽
     */
    private Double fsTotalBw;
}
