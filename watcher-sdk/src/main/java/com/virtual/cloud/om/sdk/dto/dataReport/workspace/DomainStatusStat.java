package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("虚拟机状态")
@Data
public class DomainStatusStat implements Serializable {
    private static final long serialVersionUID = 4473933589539665045L;

    /** 总数 */
    @ApiModelProperty(value = "虚拟机总数")
    private int total = 0;

    /** 运行 */
    @ApiModelProperty(value = "运行状态虚拟机总数")
    private int running = 0;

    /** 暂停 */
    @ApiModelProperty(value = "暂停状态虚拟机总数")
    private int paused = 0;

    /** 异常 */
    @ApiModelProperty(value = "异常状态虚拟机总数")
    private int abnormal = 0;

    /** 未知 */
    @ApiModelProperty(value = "未知状态虚拟机总数")
    private int unknown = 0;

    /** 关闭 */
    @ApiModelProperty(value = "关闭状态虚拟机总数")
    private int shutOff = 0;
//    /** running + abnormal */
//    private int run = 0;
//    /** total - running - abnormal */
//    private int shut = 0;

    /** 在线 */
    @ApiModelProperty(value = "在线状态虚拟机总数")
    private int onlineNumber = 0;

    /** 已分配 */
    @ApiModelProperty(value = "已分配虚拟机总数")
    private int allocationNum = 0;

    /** 未分配 */
    @ApiModelProperty(value = "未分配虚拟机总数")
    private int noAllocationNum = 0;

}
