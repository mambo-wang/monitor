package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

/**
 * @author:XK
 * @Date:2022/8/25 15:51
 */
@Data
public class Cluster_capacity_detail {

    private Double rbd_avail_space;
    private Double rbd_used_space;
    private Boolean rbd_flag;
    private Double fs_avail_space;
    private Double rgw_avail_space;
    private Double rgw_used_space;
    private Double rbd_total_space;
    private Double fs_used_space;
    private Boolean fs_flag;
    private Double fs_total_space;
    private Boolean rgw_flag;
    private Double rgw_total_space;
}
