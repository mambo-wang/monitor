package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/24 21:19
 */
@Data
public class StorClusterCpuUsageDTO implements Serializable {
    private static final long serialVersionUID = 7627578553721071901L;
    private String clusterName;
    private Double cpuRatio;
    private String update_time;

}
