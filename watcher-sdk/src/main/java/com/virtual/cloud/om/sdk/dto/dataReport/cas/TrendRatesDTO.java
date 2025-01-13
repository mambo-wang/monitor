package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * @author:XK
 * @Date:2022/5/24 15:54
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "trendRate")
@Data
public class TrendRatesDTO implements Serializable {
    private static final long serialVersionUID = -2527797099387416355L;
    //"4c:e9:e4:9d:ce:33-接收",   //主机ip-接受/发送
    private String name;
    // "eth3",  //名称
    private String value;

    private List<Rates> rates;
}
