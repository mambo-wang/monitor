package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 16:55
 */
@Data
public class StorClusterRgwCapacityDTO implements Serializable {
    private static final long serialVersionUID = 2001796342320576379L;
    /**
     *  {
     *           "fs_id": "ceph集群 fs ID",
     *           "rgw_total_space": 100,
     *           "rgw_avail_space": 100,
     *           "rgw_used_space": 100,
     *           "rgw_flag": true
     *         }
     */
    private String fs_id;
    private Double rgw_total_space;
    private Double rgw_avail_space;
    private Double rgw_used_space;
    private Boolean rgw_flag;

}
