package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * 虚拟机网卡信息
 */
@Data
@ToString
@XmlRootElement(name = "ipv4Attribute")
@XmlAccessorType(XmlAccessType.FIELD)
public class RsNetInfo implements Serializable {
    private static final long serialVersionUID = -5082144139748358426L;

    private String mac;
    /**
     * 网卡绑定的IP信息，存在一张网卡多个IP的情况
     */
    @XmlElement(name = "ipv4")
    private List<RsIpInfo> ipv4s;
}