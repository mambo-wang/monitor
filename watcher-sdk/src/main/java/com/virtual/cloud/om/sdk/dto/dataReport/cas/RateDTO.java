package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/19 16:47
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RateDTO implements Serializable {
    private static final long serialVersionUID = 9181182063298815573L;
    /**
     * 主机id/虚拟机id
     */
    private String id;
    /**
     * 主机名称/虚拟机名称
     */
    private String name;
    /**
     * cpu/内存利用率
     */
    private String rate;

}
