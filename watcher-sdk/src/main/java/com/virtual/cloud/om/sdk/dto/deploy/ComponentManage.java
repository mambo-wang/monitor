package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: w22798
 * @Date: 2022/5/8 17:00
 */
@Data
@ApiModel("组件管理")
public class ComponentManage {

    @ApiModelProperty(value = "节点IP地址")
    private String ip;

    @ApiModelProperty(value = "组件名称")
    private String name;

    @ApiModelProperty(value = "操作类型：restart/startup/shutdown")
    private String operate;
}
