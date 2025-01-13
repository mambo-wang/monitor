package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/12/12 20:24
 */
@Data
public class OnestorAvgDTO implements Serializable {
    private static final long serialVersionUID = -9134828454497646314L;
//    "hostName": "主机名称",
//            "oneMin": 1.111,
//            "fiveMin": 1.111,
//            "fifteenMin": 1.111

    private String hostName;
    private Double oneMin;
    private Double fiveMin;
    private Double fifteenMin;
}
