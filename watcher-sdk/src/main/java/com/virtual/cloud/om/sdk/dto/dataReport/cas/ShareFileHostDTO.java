package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@XmlRootElement(name = "host")
@XmlAccessorType(XmlAccessType.FIELD)
public class ShareFileHostDTO implements Serializable {

    @ApiModelProperty(value = "主机ID")
    private long id;

    private Boolean enableStorNode;

    private Boolean enableBackupNetwork;

}
