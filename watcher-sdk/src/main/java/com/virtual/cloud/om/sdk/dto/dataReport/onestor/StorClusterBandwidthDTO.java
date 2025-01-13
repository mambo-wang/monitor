package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 20:42
 */
@Data
public class StorClusterBandwidthDTO implements Serializable {
    private static final long serialVersionUID = 6094119624999519278L;
/**
 *  {
 *           "fs_id": "ceph集群 fs ID",
 *           "storageReadBw": 1.111,
 *           "storageWriteBw": 1.111,
 *           "storageRecoverBw": 1.111,
 *           "fsReadBw": 1.111,
 *           "fsWriteBw": 1.111,
 *           "update_time": "2020-
 */
    private String clusterName;
    private Double storageReadBw;
    private Double storageWriteBw;
    private Double storageRecoverBw;
    private Double fsReadBw;
    private Double fsWriteBw;
    private String update_time;

    /**
     * 集群总带宽
     */
    private Double allBw;
    /**
     * 文件总带宽
     */
    private Double fsTotalBw;
}
