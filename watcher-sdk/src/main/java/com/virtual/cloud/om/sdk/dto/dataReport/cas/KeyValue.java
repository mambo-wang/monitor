package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;
import lombok.Value;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "keyValue")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class KeyValue implements Serializable {

    private String key;
    private String value;
}