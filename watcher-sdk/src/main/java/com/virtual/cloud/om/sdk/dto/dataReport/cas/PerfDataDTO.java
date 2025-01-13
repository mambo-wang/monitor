package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/6/7 19:29
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "perfData")
@Data
public class PerfDataDTO  implements Serializable {
    private static final long serialVersionUID = 969804268947153636L;
    String name;

    List<RateListDTO> list;
}
