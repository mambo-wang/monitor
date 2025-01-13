package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/25 10:37
 */
@Data
public class StorClusterDiskLoadDTO implements Serializable {
    private static final long serialVersionUID = 7567822314443054334L;

    private String clusterName;
    private Double utilAvg;
    private Double utilMax;
    private String update_time;
}
