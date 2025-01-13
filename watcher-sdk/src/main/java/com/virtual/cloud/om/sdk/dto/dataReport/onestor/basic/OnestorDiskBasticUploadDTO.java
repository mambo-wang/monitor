package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import lombok.Data;

/**
 * @author:XK
 * @Date:2022/12/12 17:39
 */
@Data
public class OnestorDiskBasticUploadDTO {
    /**
     * "diskpoolName": "str",
     *                     "hostName": "str",
     *                     "logicalDiskName": "str",
     *                     "logicalDiskSize": 0,
     *                     "logicalDiskType": "str",
     *                     "usedBytes": 0,
     *                     "physicalDiskSize": 0,
     *                     "encyrptConfig": "str",
     *                     "usedPct": 0.0,
     *                     "status": "str"
     */
    private String hostName;
    private String diskpoolName;
    private String logicalDiskName;
    private Long logicalDiskSize;
    private String logicalDiskType;
    private Long usedBytes;
    private Double physicalDiskSize;
    private String encyrptConfig;
    private Double usedPct;
    private String status;
}
