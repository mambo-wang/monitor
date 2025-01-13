package com.virtual.cloud.om.sdk.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GatherOneStorLogDto implements Serializable {

    /**10位数字时间戳*/
    private Long endTime;

    private Long startTime;

    /**导出系统日志的显示语言*/
    private String language;

    /**需要导出的日志模块
     * 可选值：
     * BLOCK,OBJECT,NAS,CLUSTER_STORAGE,
     * CLUSTER_MANAGEMENT,HANDY,OS,
     * MAINTENANCE_MANAGEMENT
     * */
    private List<String> moduleHistory;

    /**需要导出日志的节点的管理网地址IP列表*/
    private List<String> nodes;

    /**是否需要清理已收集的历史日志有效值*/
    private Boolean reserved;

}
