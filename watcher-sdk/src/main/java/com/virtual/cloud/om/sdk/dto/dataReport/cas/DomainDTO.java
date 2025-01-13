package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("虚拟机信息")
@XmlRootElement(name = "domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class DomainDTO implements Serializable {

    private static final long serialVersionUID = 7630696172783821368L;

    public static final int STATUS_SHUTOFF = 3;
    public static final int STATUS_UNKNOWN = 1;
    public static final int STATUS_RUNNING = 2;
    public static final int STATUS_PAUSED = 4;

    /**
     * 虚拟机ID。
     */
    @ApiModelProperty(value = "虚拟机ID")
    private Long id = null;

    /**
     * 物理机ID。 *
     */
    @ApiModelProperty(value = "物理机ID")
    private Long hostId;

    /**
     * 集群ID。
     */
    @ApiModelProperty(value = "集群ID")
    private Long clusterId = null;

    /**
     * 主机池ID。
     */
    @ApiModelProperty(value = "主机池ID")
    private Long hostPoolId;

    /**
     * 虚拟机名称。
     */
    @ApiModelProperty(value = "虚拟机名称")
    private String name;

    /**
     * 虚拟机显示名称。 *
     */
    @ApiModelProperty(value = "虚拟机显示名称")
    private String title;

    /**
     * 主机名称。 *
     */
    @ApiModelProperty(value = "主机名称")
    private String hostName;

    /**
     * 虚拟机描述。 *
     */
    @ApiModelProperty(value = "虚拟机描述")
    private String description;

    /**
     * 虚拟机内存。兆。 *
     */
    @ApiModelProperty(value = "虚拟机内存")
    private Long memory;

    /**
     * 虚拟机CPU利用率 *
     */
    @ApiModelProperty(value = "虚拟机CPU利用率")
    private Double cpuRate;

    /**
     * 虚拟机内存利用率 *
     */
    @ApiModelProperty(value = "虚拟机内存利用率")
    private Double memRate;

    /**
     * 虚拟机虚拟CPU个数（CPU个数 * CPU核数）。 *
     */
    @ApiModelProperty(value = "虚拟机虚拟CPU个数（CPU个数 * CPU核数）")
    private Integer cpu;

    /**
     * 虚拟机状态 *
     */
    @ApiModelProperty(value = "虚拟机状态（转义字段）。")
    private String vmStatus;

    /**
     * 虚拟机UUID。 *
     */
    @ApiModelProperty(value = "虚拟机UUID")
    private String uuid;

    /**
     * 虚拟机安装的操作系统。取值：0:Windows;1:Linux。 *
     */
    @ApiModelProperty(value = "虚拟机安装的操作系统。取值：0:Windows;1:Linux")
    private Integer system;
    /**
     * 虚拟机安装的操作系统描述。 *
     */
    @ApiModelProperty(value = "操作系统描述")
    private String osDesc;

    /**
     * 虚拟机创建日期。
     */
    @ApiModelProperty(value = "虚拟机创建日期")
    private Date createDate;

    /**
     * 虚拟机所属标志 0:不属于用户以及用户组 1:属于用户 2:属于用户组。
     */
    @ApiModelProperty(value = "虚拟机所属标志 0:不属于用户以及用户组 1:属于用户 2:属于用户组")
    private Integer flag = 1;

    /**
     * 虚拟机类型 0:CAS虚拟机 1:VMware虚拟机。
     */
    @ApiModelProperty(value = "虚拟机类型 0:CAS虚拟机 1:VMware虚拟机")
    private Integer type = 0;

    /**
     * 是否快速部署过。
     */
    @ApiModelProperty(value = "是否快速部署过")
    private Boolean deployed = false;

    /**
     * 虚拟机HA异常状态
     */
    @ApiModelProperty(value = "虚拟机HA异常状态")
    private Integer haStatus;

    /**
     * 虚拟机HA管理状态
     */
    @ApiModelProperty(value = "虚拟机HA管理状态")
    private Integer haManage;

    /**
     * 虚拟机是否启用保护模式。 1：启用，0：不启用 *
     */
    @ApiModelProperty(value = "虚拟机是否启用保护模式。 1：启用，0或null：不启用")
    private Integer protectModel;

    /**
     * 对应UIS数据库ENABLE值
     */
    @ApiModelProperty(hidden = true)
    private Integer enable;

    /**
     * 主机状态 *
     */
    @ApiModelProperty(value = "主机状态。")
    private Integer hoststatus;

    /**
     * 主机所在集群是否启用HA
     */
    @ApiModelProperty(value = "主机所在集群是否启用HA")
    private Integer hostHaEnable;

    /**
     * castool状态 0:未运行 1:运行
     */
    @ApiModelProperty(value = "castool状态 0:未运行 1:运行")
    private Integer castoolsStatus;

    /**
     * castool版本。 *
     */
    @ApiModelProperty(value = "castool版本")
    private String castoolsVersion;

    /**
     * 虚拟机是否启用防病毒配置。1：启用，0为不启用。
     */
    @ApiModelProperty(value = "虚拟机是否启用防病毒配置。1：启用，0为不启用")
    private int antivirusEnable = 0;

    @ApiModelProperty(value = "虚拟机使用时间")
    private int uptime = 0;

    /**
     * 虚拟机状态。取值： 0:模板 1:未知 2:运行 3:关闭 4 暂停。 *
     */
    @ApiModelProperty(value = "虚拟机状态。取值： 0:模板 1:未知 2:运行 3:关闭 4 暂停。")
    private Integer status;

    /**
     * 写入domainxml文件中 是否开启内存气球功能。 0-关闭， 1-开启
     **/
    @ApiModelProperty(value = "内存气球 0:关闭，1：开启")
    private Integer autoMem = 0;

    @XmlElement(name = "ipv4Attribute")
    @ApiModelProperty(hidden = true)
    private List<RsNetInfo> ipv4Attributes;

    /*虚拟机分类 默认为1 1 普通虚拟机  101~200由VDI自定义（101，VDI镜像 ； 102 --110 预留 ； 111开始为IDV不同终端类型对应镜像）*/
    @ApiModelProperty(value = "虚拟机分类 默认为1 1 普通虚拟机")
    private Integer vmType = 1;

    @ApiModelProperty(value = "磁盘信息")
    private Integer diskInfo;

    @ApiModelProperty(value = "磁盘详情")
    private String diskDetail;

    @ApiModelProperty(value = "来源：0:Cas 1:nova。2:rest")
    private String originate;

    /**
     * 虚拟机增加物理机模拟开机:true 开启；false 关闭；
     */
    @ApiModelProperty(value = "虚拟机增加物理机模拟开机:true 开启；false 关闭")
    private Boolean cpuFeature = false;

    public String getVmStatus() {
        if (vmStatus != null && !vmStatus.isEmpty()) {
            return vmStatus;
        }
        if (status != null) {
            if (2 == status) {
                return "running";
            }
            if (3 == status) {
                return "shutOff";
            }
            if (4 == status) {
                return "paused";
            }
            return "unknown";
        }
        return vmStatus;
    }

    public Integer getStatus() {
        if (null == status && StringUtils.isNotBlank(vmStatus)){
            if (vmStatus.equals("unknown")){
                return 1;
            }
            if (vmStatus.equals("running")){
                return 2;
            }
            if (vmStatus.equals("shutOff")){
                return 3;
            }
            if (vmStatus.equals("paused")){
                return 4;
            }
            return 5;
        }
        return status;
    }
}
