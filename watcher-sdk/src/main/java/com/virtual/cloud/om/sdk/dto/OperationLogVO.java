package com.virtual.cloud.om.sdk.dto;

import com.virtual.cloud.om.sdk.utils.DateTimeTool;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class OperationLogVO implements Serializable {

    public static final String MODULE_AGENT = "Watcher-Agent";
    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAILURE = "FAILURE";

    private static final long serialVersionUID = 3744662747742736932L;

    private String module;

    private String desc;

    private String result;

    private String msg;

    private String time;

    private String operator;

    private String deleted;

    public static String success(String module, String desc, String msg, String operator) {
        OperationLogVO log = OperationLogVO.builder()
                .deleted("n")
                .desc(desc)
                .module(module)
                .operator(operator)
                .result(OperationLogVO.RESULT_SUCCESS)
                .time(DateTimeTool.formatFullDateTime(System.currentTimeMillis()))
                .msg(msg)
                .build();
        return SerializeUtils.toJson(log);
    }

    public static String failure(String module, String desc, String msg, String operator) {
        OperationLogVO log = OperationLogVO.builder()
                .deleted("n")
                .desc(desc)
                .module(module)
                .operator(operator)
                .result(OperationLogVO.RESULT_FAILURE)
                .time(DateTimeTool.formatFullDateTime(System.currentTimeMillis()))
                .msg(msg)
                .build();
        return SerializeUtils.toJson(log);
    }
}
