package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Restful Web Services 接口返回的虚拟机网络信息实体类。
 *
 * @author z01500
 */
@Data
@XmlRootElement(name = "network")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainNetworkDTO implements Serializable {

    private static final long serialVersionUID = 2093525599973936332L;

//    /**
//     * 记录ID。
//     */
//    @Schema(description = "记录ID")
//    private Long id = null;

    /**
     * 网卡MAC地址。 *
     */
    @Schema(description = "网卡MAC地址")
    private String mac;

    /**
     * 　转发模式。 *
     */
    @Schema(description = "虚拟机网卡转发模式。可能的值：VEPA、VXLAN、VXLAN(CAS)、VEB")
    private String mode;

    /**网络策略模板名称。*/
    @Schema(description = "网络策略模板名称")
    private String profileName;

    /**
     * 网卡IP地址。 *
     */
    @Schema(description = "网卡IP地址，当查询的是真实虚拟机而非模板时，本字段有效")
    private String ipAddr;

    /**网卡ipv6地址**/
    @Schema(description = "网卡ipv6地址")
    private String ipv6;

    /**
     * 虚拟交换机名称。 *
     */
    @Schema(description = "虚拟交换机名称")
    private String vsName;

    /**
     * 　vlan id。 *
     */
    @Schema(description = "vlan id")
    private Integer vlan;

}
