package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 虚拟机网络信息
 */

@Data
@ToString
@XmlRootElement(name = "ipv4")
@XmlAccessorType(XmlAccessType.FIELD)
public class RsIpInfo implements Serializable {
    private static final long serialVersionUID = 8411576675168460677L;

    private String mask;

    private String ipAddress;
}
