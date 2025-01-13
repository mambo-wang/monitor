package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@ApiModel("集群信息")
@XmlRootElement(name = "cluster")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 集群ID。 */
    @ApiModelProperty(value = "集群ID")
    private Long id = 0L;

    /** 主机池ID。*/
    @ApiModelProperty(value = "主机池ID")
    private Long hostPoolId = 0L;

    /** 主机池名称。*/
    @ApiModelProperty(value = "主机池名称")
    private String hostPoolName = "";

    /** 集群名称。 **/
    @ApiModelProperty(value = "集群名称")
    private String name = "";

    /** 集群描述。 **/
    @ApiModelProperty(value = "集群描述")
    private String description = "";

    /** 是否启用HA  0:不启用HA 1:启用HA。**/
    @ApiModelProperty(value = "是否启用HA  0:不启用HA 1:启用HA")
    private Integer enableHA = 0;

    /** 启动优先级  0:低级  1:中级 2:高级**/
    @ApiModelProperty(value = "启动优先级  0:低级  1:中级 2:高级")
    private Integer priority = 1;

    /** 是否启用LB 0:不启用负载均衡 1:启用负载均衡。**/
    @ApiModelProperty(value = "是否启用LB 0:不启用负载均衡 1:启用负载均衡")
    private Integer enableLB = 0;

    /** 持续时间。**/
    @ApiModelProperty(value = "持续时间")
    private Integer persistTime = 0;

    /** 检查间隔。**/
    @ApiModelProperty(value = "检查间隔")
    private Integer checkInterval = 0;

    /** 是否启用电源智能管理 0:不启用电源智能管理 1:启用电源智能管理。**/
    @ApiModelProperty(value = "是否启用电源智能管理 0:不启用电源智能管理 1:启用电源智能管理")
    private Integer enableIPM = 0;

    /** 持续时间。**/
    @ApiModelProperty(value = "持续时间")
    private Integer persistTimeIPM = 0;

    /** 检查间隔。**/
    @ApiModelProperty(value = "检查间隔")
    private Integer checkIntervalIPM = 0;

    /** 操作员分组ID。*/
    @ApiModelProperty(hidden = true)
    private Long operatorGroupId = null;

    /** 操作员分组编码。 */
    @ApiModelProperty(hidden = true)
    private String operatorGroupCode = null;

    /** 子结点个数。*/
    @ApiModelProperty(value = "子结点个数")
    private Integer childNum = null;

    @ApiModelProperty(hidden = true)
    private Integer enableSLB = null;

    @ApiModelProperty(hidden = true)
    private Integer slbPersistTime = null;

    @ApiModelProperty(hidden = true)
    private Integer slbCheckInterval = null;

    @ApiModelProperty(hidden = true)
    private Integer enableStorageHA = null;

    @ApiModelProperty(hidden = true)
    private Integer haControlStrategy = null;

    @ApiModelProperty(hidden = true)
    private Integer enableBusinessHA = null;

    @ApiModelProperty(hidden = true)
    private Integer triggerAction = null;

    @ApiModelProperty(hidden = true)
    private Integer containerCluster = null;

    @ApiModelProperty(hidden = true)
    private Integer localDiskHa = null;

    @ApiModelProperty(hidden = true)
    private Long localDiskTime = null;

    @ApiModelProperty(hidden = true)
    private Long strategyDelayTime = null;
}
