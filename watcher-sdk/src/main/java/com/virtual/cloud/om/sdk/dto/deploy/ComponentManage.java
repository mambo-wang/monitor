package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: w22798
 * @Date: 2022/5/8 17:00
 */
@Data
@Schema(description = "组件管理")
public class ComponentManage {

    @Schema(description = "节点IP地址")
    private String ip;

    @Schema(description = "组件名称")
    private String name;

    @Schema(description = "操作类型：restart/startup/shutdown")
    private String operate;
}
