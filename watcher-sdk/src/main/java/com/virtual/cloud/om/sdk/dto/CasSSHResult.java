package com.virtual.cloud.om.sdk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/6 18:32
 */
@Data
@XmlRootElement(name = "parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class CasSSHResult implements Serializable {
    private static final long serialVersionUID = -6780972679620336460L;

    private Long id;
    private String name;
    private String value;
}
