package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@Data
@ToString
@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostDiskRateDTO implements Serializable {

    private static final long serialVersionUID = 7630696172783821368L;

    private List<KeyValue> keyValue;

}
