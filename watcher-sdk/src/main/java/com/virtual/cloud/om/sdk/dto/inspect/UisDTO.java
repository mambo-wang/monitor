package com.virtual.cloud.om.sdk.dto.inspect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/25 16:41
 */
@Data
public class UisDTO  implements Serializable {
    private static final long serialVersionUID = 5159251914572276314L;

    private String virtualIp;
    private String virtualMask;
    private String mysqlSize;
    private String oneStorVip;
    private Master master;
    private Controller controller;
    private Slave slave;

}
