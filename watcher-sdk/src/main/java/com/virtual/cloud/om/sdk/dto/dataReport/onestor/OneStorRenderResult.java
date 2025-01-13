package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import lombok.Data;

import java.util.List;

/**
 * @author:XK
 * @Date:2022/8/24 20:18
 */
@Data
public class OneStorRenderResult {
    private String target;
    private List<List> datapoints;
}
