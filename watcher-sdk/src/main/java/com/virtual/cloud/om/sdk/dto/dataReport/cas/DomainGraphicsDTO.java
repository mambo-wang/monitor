package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@ToString
@XmlRootElement(name="storage")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainGraphicsDTO implements Serializable {

    private static final long serialVersionUID = -8950307639947191691L;

    private String type;

    private Integer port;

    private String address;

    private String password;

    private String kayboardMap;
}
