package com.virtual.cloud.om.sdk.dto.inspect;

import lombok.Data;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/6/28 14:36
 */
@Data
public class InspectFailDTO implements Serializable {

    private static final long serialVersionUID = -2173779031798134234L;
    private String failureMessage;
    private Long inspectRecordId;
}
