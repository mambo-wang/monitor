package com.virtual.cloud.om.agent.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class WarnStrategyDTO implements Serializable {

    private static final long serialVersionUID = -4335726185825973993L;
    /**
     * 监控端点类型 例：workspace cas uis
     */
    private String platform;
    /**
     * 频率
     */
    private Integer frequency;
    /**
     * 类型 例：minute（分钟）
     */
    private String unit;
    /**
     * 采集指标类型
     */
    private String metric;
    /**
     * 执行目标服务器
     */
    private String tags;
    /**
     * 策略的hash值
     */
    private String hash;
}
