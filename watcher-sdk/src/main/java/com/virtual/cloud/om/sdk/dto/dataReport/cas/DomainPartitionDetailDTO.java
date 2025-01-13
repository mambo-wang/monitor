package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/18 14:50
 */
@Data
@XmlRootElement(name = "domainPartitionDetail")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainPartitionDetailDTO implements Serializable {

    private static final long serialVersionUID = 1099712112592099353L;
    private String partitionName;
    private Double utilization;
    private Double size;
    private Double used;


}
