package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.constant.RealTimeLogTypeEnum;
import com.virtual.cloud.om.sdk.dto.LogLine;

import java.util.Optional;

/**
 * @Author: w22798
 * @Date: 2022/5/21 16:23
 */
public interface LogPatternApi {

    Optional<LogLine> parseLine(String message);

    RealTimeLogTypeEnum logType();
}
