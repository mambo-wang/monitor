package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/21 9:47
 */

/**
 * 主机实时性能信息
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class HostPerformanceDTO implements Serializable {
    private static final long serialVersionUID = 2809103931824195368L;
    /**
     * cpu利用率
     */
    private String cpuRate;
    /**
     * 内存利用率
     */
    private String memRate;
    /**
     *io
     */
    private String io;
    /**
     * 网络吞吐量
     */
    private String network;
    /**
     * 间隔时间  ms
     */
    private String periodMillis;

}
