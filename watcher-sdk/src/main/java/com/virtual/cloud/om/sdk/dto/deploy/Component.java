package com.virtual.cloud.om.sdk.dto.deploy;

import com.virtual.cloud.om.sdk.constant.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: w22798
 * @Date: 2022/4/26 20:21
 */
@ApiModel("组件")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Component {

    @ApiModelProperty(value = "组件名称")
    private String name;

    @ApiModelProperty(value = "组件状态，固定为startup/shutdown/unknown用于判断状态做操作栏按钮显隐，启动状态可以点击停止和重启，停止状态可以点击启动，未知状态不能点击任何按钮")
    private String status;

    @ApiModelProperty(value = "组件状态，根据实际情况展示组件异常情况，前端直接展示后台返回结果")
    private String detail;

    public boolean isRunning(){
        return status.equals(Constant.Deploy.STATUS_STARTUP);
    }
}
