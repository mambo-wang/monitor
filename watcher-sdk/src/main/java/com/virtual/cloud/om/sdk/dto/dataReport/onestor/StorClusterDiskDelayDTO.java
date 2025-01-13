package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/8/25 10:14
 */
@Data
public class StorClusterDiskDelayDTO implements Serializable {
    private static final long serialVersionUID = 346838367654687619L;

    private String clusterName;
    private Double latRead;
    private Double latWrite;
    private String update_time;
}
