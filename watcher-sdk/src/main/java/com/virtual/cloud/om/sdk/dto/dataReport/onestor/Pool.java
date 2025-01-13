package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

/**
 * @author:XK
 * @Date:2022/8/25 15:50
 */
@Data
public class Pool {
    private Cluster_capacity_detail cluster_capacity_detail;
    private Long avail_bytes;
    private Long avail_valid_bytes;
    private Long used_bytes;
    private Long used_valid_bytes;
    private Long total_bytes;
    private Long total_valid_bytes;
    private Long pools_used_bytes;
}
