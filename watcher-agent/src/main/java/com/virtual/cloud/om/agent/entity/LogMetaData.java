package com.virtual.cloud.om.agent.entity;

import com.virtual.cloud.om.sdk.dto.RealTimeLogStrategyLogs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Objects;

/**
 * @Author: w22798
 * @Date: 2022/5/23 8:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//相当于数据库里的表名
public class LogMetaData {

    private String logPath;

    private String logType;

    private String targetType;

    public static LogMetaData convert(RealTimeLogStrategyLogs logs){
        return LogMetaData.builder().logPath(logs.getLogPath()).logType(logs.getLogType()).targetType(logs.getTargetType()).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogMetaData that = (LogMetaData) o;
        return getLogPath().equals(that.getLogPath()) &&
                getLogType().equals(that.getLogType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogPath(), getLogType());
    }
}
