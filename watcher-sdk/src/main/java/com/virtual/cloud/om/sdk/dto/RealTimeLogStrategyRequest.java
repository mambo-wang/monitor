package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/5/10 14:42
 */
@Data
public class RealTimeLogStrategyRequest {

    private String platform;

    private String tags;

    private List<RealTimeLogStrategyLogs> logs;
}
