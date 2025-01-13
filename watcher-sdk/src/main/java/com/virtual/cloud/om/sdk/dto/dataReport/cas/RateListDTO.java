package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author:XK
 * @Date:2022/6/7 19:47
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlRootElement(name = "list")
public class RateListDTO {
    private String rate;
    private String time;
}
