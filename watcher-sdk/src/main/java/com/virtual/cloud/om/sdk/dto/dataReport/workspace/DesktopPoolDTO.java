package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "桌面池信息")
public class DesktopPoolDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    @Schema(description = "桌面池id", example = "8")
    private Long id;

    @Schema(description = "桌面池名称", example = "桌面池1")
    private String name;

    @Schema(description = "桌面池描述", example = "该桌面池仅用于测试和展示")
    private String description;

    /**
     * 分配模式 1：静态，2：动态，3：手工 4:匿名登录
     */
    @Schema(description = "分配模式 1：静态，2：动态，3：手工 4:匿名登录", example = "1")
    private Integer assignMode;

    @Schema(description = "最大虚拟机数量", example = "200")
    private Integer maxVmNum;

    @Schema(description = "桌面名称前缀", example = "cas-")
    private String desktopNamePre;

    @Schema(description = "集群id", example = "6")
    private Long clusterId;

    @Schema(description = "集群名称", example = "测试集群")
    private String clusterName;

    /**
     * 部署目标类型，0集群，1主机
     */
    @Schema(description = "部署目标类型，0集群，1主机", example = "1")
    private Integer targetType;

    @Schema(description = "虚拟机模板id", example = "1")
    private Long vmTemplateId;

    @Schema(description ="桌面镜像UUID")
    private String templateUuid;

    @Schema(description = "胖终端设备类型")
    private String terminalType;

    /**
     * 存储池名称
     */
    @Schema(description = "存储池名称", example = "存储池1")
    private String storagePoolName;

    @Schema(description = "第二存储池名称", example = "第二存储池")
    private String secondStoragePoolName;

    /**
     * 系统盘
     */
    @Schema(description = "系统盘")
    private String defaultStorage;

    /**
     * 初始化类型，0快速初始化，1完全初始化
     */
    @Schema(description = "初始化类型，0快速初始化，1完全初始化", example = "0")
    private Integer initType;

    /**
     * 用户类型，0:本地用户，1:域用户，2：设备用户
     */
    @Schema(description = "用户类型，0:本地用户，1:域用户，2：设备用户", example = "0")
    private Integer userType;

    /** 0 虚拟机, 1 物理机 2胖终端 */
    @Schema(description = "计算机类型：0 虚拟机, 1 物理机 2胖终端 3 VOI", example = "0")
    private Integer computerType;

    /** 离线使用时间，0无限期，其他值如5，表示还可以使用5天 */
    @Schema(description = "离线使用时间，0无限期，其他值如5，表示还可以使用5天", example = "0")
    private Integer offlineDate;

    /** 0 不还原, 1 还原；胖终端类型才会用到此字段*/
    @Schema(description = "0 不还原, 1 全盘还原，2 系统盘还原 ；胖终端类型才会用到此字段", example = "0")
    private Integer recoverMode;

    /**
     * 部署虚拟机时，为虚拟机创建的管理员
     */
    @Schema(description = "部署虚拟机时，为虚拟机创建的管理员", example = "admin")
    private String domainUsername;

    /**
     * 部署虚拟机时，为虚拟机创建的管理员的密码
     */
    @Schema(description = "部署虚拟机时，为虚拟机创建的管理员的密码", example = "admin")
    private String domainUserPassword;

    @Schema(description = "访问账号")
    private String accessAccount;

    @Schema(description = "访问域账号密码")
    private String accessAccountPwd;

    /** 是否实现自动初始化，0不启用，1启 ，动态手工桌面池释放后断点重启*/
    @Schema(description = "是否实现自动初始化，0不启用，1启 ，动态手工桌面池释放后断点重启", example = "0")
    private int autoInit;

    @Schema(description = "域用户类型可以保存部署虚拟机所在的OU")
    private String ou;

    @Schema(description = "域控服务器ID")
    private Long ldapId;

    /** 授权策略id */
    @Schema(description = "授权策略id", example = "3")
    private Long authStgId;

    @Schema(description = "授权策略名称", example = "default")
    private String authStgName;

    /** 桌面池分组id */
    @Schema(description = "桌面池分组id", example = "9")
    private Long desktopPoolGroupId;

    @Schema(description = "桌面池分组名称", example = "桌面池分组1")
    private String desktopPoolGroupName;

    /** 主机id，支持选择多个主机 */
    @Schema(description = "主机id")
    private List<Long> hostIdList;

    @Schema(description = "主机名称")
    private List<String> hostNameList;

    /**虚拟机个数*/
    @Schema(description = "虚拟机个数", example = "55")
    private int vmNum;

    //未分配个数
    @Schema(description = "未分配个数", example = "33")
    private int noAllocationNum;

    //虚拟桌面利用率
    @Schema(description = "虚拟桌面利用率", example = "20")
    private int vmUsedPercent;

    //已接入个数
    @Schema(description = "已接入个数", example = "10")
    private int onlineNumber;

    //虚拟机实际状态统计
    @Schema(description = "运行状态虚拟机数量", example = "1")
    private int runningNum = 0;
    @Schema(description = "暂停状态虚拟机数量", example = "1")
    private int pausedNum = 0;
    @Schema(description = "异常状态虚拟机数量", example = "1")
    private int abnormalNum = 0;
    @Schema(description = "未知状态虚拟机数量", example = "1")
    private int unknownNum = 0;
    @Schema(description = "关机状态虚拟机数量", example = "1")
    private int shutOffNum = 0;

    //域用户类型桌面池，windows操作系统的虚拟机模板，部署虚拟机时必须配置OU，这里为默认值
    @Schema(description = "域用户类型桌面池，windows操作系统的虚拟机模板，部署虚拟机时必须配置OU，这里为默认值")
    private String computerOu;

    //在线率 - 仅在大屏展示统计临时变量
    @Schema(description = "在线率 - 仅在大屏展示统计临时变量")
    private int vmOnlinePercent;

    @Schema(description = "序号起始值，用于部分对于计算机名有自定义要求的局点")
    private int startSerial;

    @JsonProperty("projectId")
    @Schema(description = "租户id，云上使用")
    private String tenantId;

    @Schema(description = "单点登录密码选项。 1.默认使用授权用户密码。2.使用固定登录密码，不设置固定密码视为无密码")
    private Integer pwdOption;

    @Schema(description = "固定密码")
    private String fixedPwd;

    private Integer desktopUserType;

    /**
     * 桌面加入的域控,可以和登录认证服务器不同
     */
    @Schema(description = "桌面加入的域控id")
    private Long desktopUserLdap;

    /**
     * IDV/VOI数据盘大小
     */
    @Schema(description = "IDV/VOI数据盘大小,单位GB")
    private Long dataDiskSize;

    /**
     * 是否应用数据盘大小到终端
     */
    @Schema(description = "是否应用数据盘大小到终端")
    private Integer isDataDiskModified = 0;

    @Schema(description = "是否开启创建还原点", example = "true")
    private Boolean voiCreateRestorePoint;

    @Schema(description = "是否开启开启软件驱动自动安装", example = "true")
    private Boolean isAutoInstallation;

    @Schema(description ="发布状态,1：立即发布，2：灰度发布，默认为1")
    private Integer publishStatus;

}
