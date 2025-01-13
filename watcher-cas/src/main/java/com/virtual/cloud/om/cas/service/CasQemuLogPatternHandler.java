package com.virtual.cloud.om.cas.service;

import com.virtual.cloud.om.sdk.api.DefaultLogPatternHandler;
import com.virtual.cloud.om.sdk.api.LogPatternApi;
import com.virtual.cloud.om.sdk.constant.RealTimeLogTypeEnum;
import com.virtual.cloud.om.sdk.dto.LogLine;
import com.virtual.cloud.om.sdk.utils.TimeUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class CasQemuLogPatternHandler extends DefaultLogPatternHandler implements LogPatternApi {

    private static final String pattenExp = "([\\d,\\-,\\s\\:]*).*\\s:\\s([^\\[\\]]*):(.*)";

    private Pattern pattern = Pattern.compile(pattenExp);

    @Override
    public Optional<LogLine> parseLine(String message) {

        // 匹配单行日志
        Matcher matcher = pattern.matcher(message);
        boolean isFind = matcher.find();

        if (isFind) {
            // 匹配成功, 则创建日志模型
            LogLine logLine = new LogLine();

            // 赋值每个字段
            logLine.setTime(matcher.group(1));
            logLine.setTimestamp(TimeUtils.convertToTimestamp(matcher.group(1)));

            logLine.setMessage(matcher.group(3));
            return Optional.of(logLine);
        }

        //匹配失败, 返回空
        return Optional.empty();
    }

    @Override
    public RealTimeLogTypeEnum logType() {
        return RealTimeLogTypeEnum.cas_qemu;
    }
}
