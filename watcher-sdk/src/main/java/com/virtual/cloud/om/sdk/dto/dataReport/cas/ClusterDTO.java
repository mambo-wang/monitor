package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Data
@Schema(description = "集群信息")
@XmlRootElement(name = "cluster")
@XmlAccessorType(XmlAccessType.FIELD)
public class ClusterDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 集群ID。 */
    @Schema(description = "集群ID")
    private Long id = 0L;

    /** 主机池ID。*/
    @Schema(description = "主机池ID")
    private Long hostPoolId = 0L;

    /** 主机池名称。*/
    @Schema(description = "主机池名称")
    private String hostPoolName = "";

    /** 集群名称。 **/
    @Schema(description = "集群名称")
    private String name = "";

    /** 集群描述。 **/
    @Schema(description = "集群描述")
    private String description = "";

    /** 是否启用HA  0:不启用HA 1:启用HA。**/
    @Schema(description = "是否启用HA  0:不启用HA 1:启用HA")
    private Integer enableHA = 0;

    /** 启动优先级  0:低级  1:中级 2:高级**/
    @Schema(description = "启动优先级  0:低级  1:中级 2:高级")
    private Integer priority = 1;

    /** 是否启用LB 0:不启用负载均衡 1:启用负载均衡。**/
    @Schema(description = "是否启用LB 0:不启用负载均衡 1:启用负载均衡")
    private Integer enableLB = 0;

    /** 持续时间。**/
    @Schema(description = "持续时间")
    private Integer persistTime = 0;

    /** 检查间隔。**/
    @Schema(description = "检查间隔")
    private Integer checkInterval = 0;

    /** 是否启用电源智能管理 0:不启用电源智能管理 1:启用电源智能管理。**/
    @Schema(description = "是否启用电源智能管理 0:不启用电源智能管理 1:启用电源智能管理")
    private Integer enableIPM = 0;

    /** 持续时间。**/
    @Schema(description = "持续时间")
    private Integer persistTimeIPM = 0;

    /** 检查间隔。**/
    @Schema(description = "检查间隔")
    private Integer checkIntervalIPM = 0;

    /** 操作员分组ID。*/
    @Schema(hidden = true)
    private Long operatorGroupId = null;

    /** 操作员分组编码。 */
    @Schema(hidden = true)
    private String operatorGroupCode = null;

    /** 子结点个数。*/
    @Schema(description = "子结点个数")
    private Integer childNum = null;

    @Schema(hidden = true)
    private Integer enableSLB = null;

    @Schema(hidden = true)
    private Integer slbPersistTime = null;

    @Schema(hidden = true)
    private Integer slbCheckInterval = null;

    @Schema(hidden = true)
    private Integer enableStorageHA = null;

    @Schema(hidden = true)
    private Integer haControlStrategy = null;

    @Schema(hidden = true)
    private Integer enableBusinessHA = null;

    @Schema(hidden = true)
    private Integer triggerAction = null;

    @Schema(hidden = true)
    private Integer containerCluster = null;

    @Schema(hidden = true)
    private Integer localDiskHa = null;

    @Schema(hidden = true)
    private Long localDiskTime = null;

    @Schema(hidden = true)
    private Long strategyDelayTime = null;
}
