package com.virtual.cloud.om.sdk.dto;

import com.virtual.cloud.om.sdk.utils.DateTimeTool;
import com.virtual.cloud.om.sdk.utils.SerializeUtils;
import lombok.Data;

import java.io.Serializable;

@Data
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String module;
        private String desc;
        private String result;
        private String msg;
        private String time;
        private String operator;
        private String deleted;

        public Builder module(String module) { this.module = module; return this; }
        public Builder desc(String desc) { this.desc = desc; return this; }
        public Builder result(String result) { this.result = result; return this; }
        public Builder msg(String msg) { this.msg = msg; return this; }
        public Builder time(String time) { this.time = time; return this; }
        public Builder operator(String operator) { this.operator = operator; return this; }
        public Builder deleted(String deleted) { this.deleted = deleted; return this; }

        public OperationLogVO build() {
            OperationLogVO vo = new OperationLogVO();
            vo.module = this.module;
            vo.desc = this.desc;
            vo.result = this.result;
            vo.msg = this.msg;
            vo.time = this.time;
            vo.operator = this.operator;
            vo.deleted = this.deleted;
            return vo;
        }
    }

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
