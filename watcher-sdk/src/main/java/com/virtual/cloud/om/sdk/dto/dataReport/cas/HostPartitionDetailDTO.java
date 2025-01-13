package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/2 11:05
 */

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlRootElement(name = "hostPartitionDetail")
public class HostPartitionDetailDTO implements Serializable {

    private static final long serialVersionUID = 470802834321551138L;
    /**
     * 分区名称
     */
    private String partitionName;
    /**
     * 分区类型
     */
    private String partitionType;
    /**
     * 分区挂载地址
     */
    private String mountedDir;
    /**
     * 容量
     */
    private String size;
    /**
     * 已使用
     */
    private String used;
    /**
     * 占用率
     */
    private String utilization;
}
