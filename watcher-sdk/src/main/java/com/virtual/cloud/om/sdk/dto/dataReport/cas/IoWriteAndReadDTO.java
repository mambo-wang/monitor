package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/5/24 17:45
 */
@Data
public class IoWriteAndReadDTO implements Serializable {

    private static final long serialVersionUID = 8624041115607811702L;

    private Double write;

    private Double read;
}
