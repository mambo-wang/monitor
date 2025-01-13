package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/20 14:11
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class HostRateDTO extends RateDTO implements Serializable {

    private static final long serialVersionUID = 2067465554449967306L;
    private String hostId;
}
