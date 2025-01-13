package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/24 10:40
 */

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@XmlRootElement(name = "rates")
public class Rates implements Serializable {
    private static final long serialVersionUID = 5057642404664499299L;

    private String rate;

    private String time;
}
