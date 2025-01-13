package com.virtual.cloud.om.workspace.service.realtimelog;

import com.virtual.cloud.om.sdk.api.DefaultLogPatternHandler;
import com.virtual.cloud.om.sdk.api.LogPatternApi;
import com.virtual.cloud.om.sdk.constant.RealTimeLogTypeEnum;
import com.virtual.cloud.om.sdk.dto.LogLine;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @Author: w22798
 * @Date: 2022/7/9 16:44
 */
@Component
public class OthersPatternHandler extends DefaultLogPatternHandler implements LogPatternApi {

    @Override
    public Optional<LogLine> parseLine(String message) {
        return super.parseLine(message);
    }

    @Override
    public RealTimeLogTypeEnum logType() {
        return RealTimeLogTypeEnum.others_;
    }
}
