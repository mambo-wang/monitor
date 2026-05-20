package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/1 15:49
 */
@Data
@Accessors(chain = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class SendAndReceiveDTO implements Serializable {
    private static final long serialVersionUID = 7538474608851532960L;

    private Double send;

    private Double receive;

}
