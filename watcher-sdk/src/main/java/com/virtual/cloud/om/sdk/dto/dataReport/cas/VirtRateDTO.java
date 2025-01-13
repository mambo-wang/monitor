package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import com.virtual.cloud.om.sdk.dto.dataReport.cas.RateDTO;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/20 10:46
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtRateDTO extends RateDTO implements Serializable {
    private static final long serialVersionUID = -5759271343190909829L;
    /**
     * 主机id
     */

    private String hostId;
    /**
     * 集群id
     */
    private  String clusterId;
}
