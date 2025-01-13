package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
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
public class DomainVideoDTO implements Serializable {

    private static final long serialVersionUID = -8950307639947191691L;

    @ApiModelProperty(value = "记录ID")
    private String type;

    @ApiModelProperty(value = "记录ID")
    private Integer vram;

    @ApiModelProperty(value = "记录ID")
    private String pciView;

}
