package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 20:58
 */
@Data
public class StorClusterFlowDTO implements Serializable {
    private static final long serialVersionUID = 552151049414783383L;
    /**
     * "fs_id": "ceph集群 fs ID",
     *           "storageReadFlow": 1.111,
     *           "storageWriteFlow": 1.111,
     *           "storageRecoverFlow": 1.111,
     *           "fsReadFlow": 1.111,
     *           "fsWriteFlow": 1.111,
     *           "update_time": "2020-12-01 14:01:32.935200"
     */

    private String clusterName;
    private Double storageReadFlow;
    private Double storageWriteFlow;
    private Double storageRecoverFlow;
    private Double fsReadFlow;
    private Double fsWriteFlow;
    private String update_time;
}
