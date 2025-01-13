package com.virtual.cloud.om.sdk.dto.dataReport.watcher;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class ComponentUsageDTO {
    private String name;
    private String usage;
}
