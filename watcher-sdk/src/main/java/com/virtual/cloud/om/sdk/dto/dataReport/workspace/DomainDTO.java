package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@Schema(description = "虚拟机详情")
public class DomainDTO implements Serializable {
    private static final long serialVersionUID = -8444581379564409479L;
    /**
     * 1:未知 2:运行 3:关闭 4 暂停。
     */
    public static final int Unknown = 1;
    public static final int RUNNING = 2;
    public static final int ShutOff = 3;
    public static final int PAUSED = 4;
    public static final int ABNORMAL = 5;

    public static final int MAINTENANCE_MODE_ON = 0;
    public static final int MAINTENANCE_MODE_WAIT = 1;
    public static final int MAINTENANCE_MODE_OFF = 2;

    public static final int AGENT_ABNORMAL = 1;
    public static final int AGENT_ADDINGDOMAIN = 2;

    /**
     * 虚拟机id。
     **/
    @Schema(description = "虚拟机id", example = "7")
    private Long id;

    /**
     * 物理机ID。
     **/
    @Schema(description = "虚拟机所在主机ID", example = "7")
    private Long hostId;

    /**
     * 虚拟机所在主机名称。
     */
    @Schema(description = "虚拟机所在主机名称", example = "host1")
    private String hostName;

    /**
     * 集群ID。
     */
    @Schema(description = "集群ID", example = "7")
    private Long clusterId;

    /**
     * 主机池ID。
     */
    @Schema(description = "主机池ID", example = "7")
    private Long hostPoolId;

    /**
     * 虚拟机标题名称。
     **/
    @Schema(description = "虚拟机标题名称", example = "title1")
    private String title;

    /**
     * 计算机名
     **/
    @Schema(description = "计算机名", example = "com1")
    private String computerName;

    /**
     * 虚拟机CPU个数。
     **/
    @Schema(description = "虚拟机CPU个数", example = "2")
    private Integer cpu;

    /**
     * 虚拟机内存总大小。
     **/
    @Schema(description = "虚拟机内存总大小,单位MB", example = "2")
    private Long memory;

    /**
     * 虚拟机存储总容量。 *
     */
    @Schema(description = " 存储总容量")
    private Long storageCapacity;

    /**虚拟机状态：当虚拟机的HA_STATUS不为0但有别的值时为"ha_exception",
     * 当虚拟机的STATUS为3时值为"shutoff",当虚拟机的STATUS为4时值为"paused",其他为"unknow"*/
    /**
     * 1:未知 2:运行 3:关闭 4 暂停。5:异常
     */
    //todo 用int还是String？domain中是int，domaindisplay中是string
    @Schema(description = "虚拟机状态1:未知 2:运行 3:关闭 4 暂停。5:异常", example = "4")
    private Integer status;
    /**
     * 虚拟机安装的操作系统。取值：0:Windows;1:Linux,2:BSD
     **/
    @Schema(description = "虚拟机安装的操作系统。取值：0:Windows;1:Linux,2:BSD", example = "0")
    private Integer system;

    /**
     * 虚拟机状态。取值：1:虚拟机 2:模板 3:删除的虚拟机 4：创建中
     **/
    @Schema(description = "虚拟机状态。取值：1:虚拟机 2:模板 3:删除的虚拟机 4：创建中", example = "1")
    private Integer enable;

    /**
     * 虚拟机是否启用保护模式。 1：启用，0：不启用
     **/
    @Schema(description = " 虚拟机是否启用保护模式。 1：启用，0：不启用", example = "1")
    private Integer protectModel;

    /**
     * castool状态 0WEIYUNXING 1 YUNXING
     */
    @Schema(description = "castool状态 0WEIYUNXING 1 YUNXING", example = "0")
    private Integer castoolsStatus;

    /**
     * castool版本
     */
    @Schema(description = "castool版本")
    private String castoolsVersion;

    @Schema(description = "agent版本")
    private String agentVersion;

    @Schema(description = "agent通信状态，0：正常，1：异常")
    private Integer agentStatus;

    @Schema(description = "ip地址")
    private String ipv4;

    @Schema(description = "ipv6地址")
    private String ipv6;

    /**
     * 虚拟机CPU利用率。
     */
    @Schema(description = "虚拟机CPU利用率")
    private Double cpuRate;

    /**
     * 虚拟机内存利用率。
     */
    @Schema(description = "虚拟机内存利用率")
    private Double memRate;

    /**
     * castools状态：noRunnig未运行、running运行、--其他
     */
    @Schema(description = "castools状态：noRunnig未运行、running运行、--其他", example = "running")
    private String castools;

    /**
     * 桌面池名称
     */
    @Schema(description = "桌面池名称", example = "pool")
    private String desktopPoolName;

    /**
     * 桌面池id
     */
    @Schema(description = "桌面池id", example = "7")
    private Long desktopPoolId;

    @Schema(description = "桌面池授权ldapId")
    private Long ldapId;

    /**
     * 桌面池类型
     */
    @Schema(description = "桌面池类型", example = "vdi")
    private Integer desktopPoolType;

    @Schema(description = "桌面池用户类型")
    private Integer desktopPoolUserType;

    @Schema(description = "用户类型，和桌面池用户类型限购替哪个，云上判断共享桌面用户使用")
    private Integer userType;

    /**
     * 授权用户或者设备名称
     */
    @Schema(description = "授权用户或者设备名称", example = "user1")
    private String loginName;

    /**
     * 授权用户的用户姓名
     */
    @Schema(description = "授权用户的用户姓名", example = "cmy")
    private String userName;

    /**
     * 授权用户或者设备ID
     */
    @Schema(description = "授权用户或者设备ID", example = "7")
    private Long authObjectId;


    /**
     * 共享桌面池单个虚拟机对应多个用户的数量
     */
    @Schema(description = "共享桌面池对应的授权用户数量")
    private Integer shareAuthNum;

    /**
     * 授权类型
     */
    @Schema(description = "虚机授权关联的数据类型,1：用户 0：分组 2：设备 3：设备分组", example = "0")
    private Integer authType;
//
//    /**
//     * 桌面池的用户类型：本地用户、域用户、设备用户
//     */
//    private Integer desktopPoolUserType;

    /**
     * 授权策略ID
     */
    @Schema(description = "授权策略ID", example = "7")
    private Long authStgId;

    /**
     * 授权策略名称
     */
    @Schema(description = "授权策略名称")
    private String authStgName;

    /**
     * 设备IP
     */
    @Schema(description = "设备IP")
    private String authDeviceIp;

    /**
     * 设备MAC
     */
    @Schema(description = "设备MAC")
    private String authDeviceMac;

    /**
     * 虚拟机登录时间
     */
    @Schema(description = "虚拟机登录时间")
    private Long loginTime;

    /**
     * 虚拟机闲置时长(*天*小时*分)描述
     */
    @Schema(description = "虚拟机闲置时间(*天*小时*分)描述")
    private String idleTime;

    /**
     * 虚拟机闲置时长
     */
    @Schema(description = "虚拟机闲置时间")
    private Long idleTimeMillis;

    /**
     * 维护状态：0表示维护中，1表示正常状态
     */
    @Schema(description = "维护状态：0表示维护中，1表示等待定时任务进入维护模式，2表示正常状态", example = "0")
    private Integer maintenanceStatus = MAINTENANCE_MODE_OFF;

    /**
     * 进入维护模式的时间
     */
    @Schema(description = "进入维护模式的时间")
    private String maintenanceTime;

    /**
     * 登录IP地址。 *  todo,有ipv4了,怎么还有这个?
     */
    @Schema(description = "登录IP地址")
    private String ipAddr;

    /**
     * 虚拟机状态 *
     */
    @Schema(description = "虚拟机状态")
    private String vmStatus;

    @Schema(description = "虚拟机任务状态")
    private String taskStatus;

    /**
     * 虚拟机安装的操作系统版本。 *
     */
    @Schema(description = "虚拟机安装的操作系统版本")
    private String osVersion;

    /**
     * 虚拟机安装的操作系统详细版本
     */
    @Schema(description = "虚拟机安装的操作系统详细内核版本，如：win10专业版---Windows 10 Pro, 64-bit")
    private String osDesc;
    /**
     * 集群名称
     */
    @Schema(description = "集群名称")
    private String clusterName;

    @Schema(description = "虚拟机实体名称")
    private String domainName;

    @Schema(description = "idv对应虚拟机mac")
    private String idvVmMac;


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


    public Integer getProtectModel() {
        if (protectModel != null) {
            return protectModel;
        } else {
            return 0;
        }
    }

    /**
     * 云学堂所需
     */
    private String ip;     //todo,有ipv4了,怎么还有这个?
    private String mac;
    private String terminalIp;
    private String terminalMac;
    private String hostIp;
    private Long classroomId;
    private String classroomName;
    private Integer order;

    private Integer computerType;

    /**
     * 云桌面创建时间
     */
    private Date createTime;

    private String uuid;

    private String templateName;

    private String templateUuid;

    private String imageUuid;

    @JsonProperty("projectId")
    private String tenantId;

    private String projectName;

    private String userUuid;

    @Schema(description = "桌面池授权类型：1：静态或单用户，2：动态或多用户，3：手工或匿名用户,4:匿名登录（其中单用户，多用户，匿名用户为IDV类型）", example = "1")
    private Integer assignMode;

    public void setStatus(Integer status) {
        if (status.equals(DomainDTO.ABNORMAL)) {
            if (null != this.status) {
                if (this.status.equals(DomainDTO.RUNNING)) {
                    //只针对运行状态的虚拟机判断agent状态是否正常
                    this.status = status;
                }
            } else {
                this.status = status;
            }
        } else {
            this.status = status;
        }
    }

    //idv 前端用id传值
    public String getUuid() {
        return StringUtils.isEmpty(uuid) ?
                id != null ? id.toString() : ""
                : uuid;
    }

    private String displayName;

    @Schema(description = "是否是VIP桌面：0：VIP桌面，1：普通桌面")
    private Integer deskType;

    @Schema(description = "开机运行时间，单位：分钟")
    private int uptime = 0;

    @Schema(description = "系统盘更新或重建时间")
    private Long rebuildTime;

    @Schema(description = "规格类型，云上使用")
    private String normUuid;

    @Schema(description = "部署绑定numa")
    private int isDeployBind = 0;
}
