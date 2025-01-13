package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/25 9:39
 */
@Data
public class StorClusterMemUsageDTO implements Serializable {
    private static final long serialVersionUID = 6200531581788282278L;

    private String clusterName;
    private Double memRatio;
    private String update_time;
}
