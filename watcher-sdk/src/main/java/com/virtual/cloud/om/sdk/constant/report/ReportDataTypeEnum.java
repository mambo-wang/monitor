package com.virtual.cloud.om.sdk.constant.report;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ReportDataTypeEnum {
    json("json串"),
    text("字符串"),
    gauge("数值"),
    counter("计数器"),
    histogra("直方图"),
    summary("统计"),
    ;

    public final String title;
}
