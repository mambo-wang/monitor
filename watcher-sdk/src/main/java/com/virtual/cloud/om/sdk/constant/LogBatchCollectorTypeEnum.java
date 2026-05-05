package com.virtual.cloud.om.sdk.constant;

import java.util.Arrays;
import java.util.Optional;

@SuppressWarnings("all")
public enum LogBatchCollectorTypeEnum {
    workspace_domain(ReportResourceEnum.workspace,LogBatchTypeEnum.domain),
    workspace_terminal(ReportResourceEnum.workspace,LogBatchTypeEnum.terminal),
    workspace_host(ReportResourceEnum.workspace,LogBatchTypeEnum.host),
    uis_host(ReportResourceEnum.uis,LogBatchTypeEnum.host),
    cas_host(ReportResourceEnum.cas,LogBatchTypeEnum.host),
    onestor_host(ReportResourceEnum.onestor,LogBatchTypeEnum.host),
    agent(ReportResourceEnum.hccAgent,LogBatchTypeEnum.agent),
    nginx(ReportResourceEnum.hccAgent,LogBatchTypeEnum.nginx),
    kafka(ReportResourceEnum.hccAgent,LogBatchTypeEnum.kafka),
    mongodb(ReportResourceEnum.hccAgent,LogBatchTypeEnum.mongodb),
    zookeeper(ReportResourceEnum.hccAgent,LogBatchTypeEnum.zookeeper),
    keepalived(ReportResourceEnum.hccAgent,LogBatchTypeEnum.keepalived)
    ;

    public final ReportResourceEnum platform;
    public final LogBatchTypeEnum type;

    LogBatchCollectorTypeEnum(ReportResourceEnum platform, LogBatchTypeEnum type) {
        this.platform = platform;
        this.type = type;
    }

    public static LogBatchCollectorTypeEnum getByPlatformAndType(ReportResourceEnum platform, LogBatchTypeEnum type){
        Optional<LogBatchCollectorTypeEnum> first = Arrays.stream(values()).filter(e -> e.platform.equals(platform) && e.type.equals(type)).findFirst();
        return first.isPresent()?first.get():null;
    }
}
