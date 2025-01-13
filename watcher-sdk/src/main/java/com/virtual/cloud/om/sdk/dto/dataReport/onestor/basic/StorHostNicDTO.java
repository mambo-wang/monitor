package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/12/12 21:59
 */
@Data
public class StorHostNicDTO implements Serializable {
    private static final long serialVersionUID = 437802567155802568L;

    private Double inputPacketStats;
    private Double outputPacketStats;
    private Double inputPackets;
    private Double outputPackets;

    private Double inputPacketsDropped;
    private Double outputPacketsDropped;
    private Double inputErrPackets;
    private Double outputErrPackets;
    private String hostName;
}
