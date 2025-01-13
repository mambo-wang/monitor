package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/1 17:09
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlRootElement(name = "rate")
public class Rate extends Rates implements Serializable {
}
