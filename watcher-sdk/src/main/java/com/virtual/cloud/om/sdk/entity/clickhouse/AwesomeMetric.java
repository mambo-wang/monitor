package com.virtual.cloud.om.sdk.entity.clickhouse;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AwesomeMetric {

    private String platform;

    private String traceId;

    private String metric;

    private String batchNum;

    private String tags;

    private Double value;

    private Long createTime;
}
