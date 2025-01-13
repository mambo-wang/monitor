package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/2 11:04
 */
@Data
public class PartitionUsageDTO implements Serializable {
    private static final long serialVersionUID = -4639281482406637712L;

    private String partition;
    private String type;
    private String mountPoint;
    private Double size;
    private Double used;
    private Double utilization;

}
