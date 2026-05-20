package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "设备信息")
public class VdiDeviceDTO implements Serializable {
    private static final long serialVersionUID = -4349174582045803152L;

    private Long id;

    /**
     * 设备唯一标识（目前为MAC地址）
     */
    @Schema(description = "设备唯一标识（目前为MAC地址）", example = "0A:5F:4C:7D:46:32")
    private String deviceId;

    /**
     * 设备显示名称
     */
    @Schema(description = "设备显示名称", example = "张三的终端")
    private String displayName;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称", example = "工位043终端")
    private String deviceName;

    /**
     * IP地址
     */
    @Schema(description = "IP地址", example = "10.125.8.80")
    private String ipAddr;

    /**
     * MAC地址
     */
    @Schema(description = "IP地址", example = "0A:5F:4C:7D:46:32")
    private String macAddr;


    /**
     * 黑名单用户：1：是；0：不是
     */
    @Schema(description = "黑名单用户：1：是；0：不是", example = "0")
    private Boolean deny = false;

    /**
     * 加入黑名单时间
     */
    @Schema(description = "加入黑名单时间，timestamp格式", example = "1569308710167")
    private Long joinDenyTime;

    /**
     * 终端注册时间
     */
    @Schema(description = "终端注册时间，timestamp格式", example = "1569308710167")
    private Long deviceRegistTime;

    /**
     * 上次登录时间
     */
    @Schema(description = "上次登录时间，timestamp格式", example = "1569308710167")
    private Long lastLoginTime;

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
     * 最后一次心跳更新时间
     */
    @Schema(description = "最后一次心跳更新时间，timestamp格式", example = "1569308710167")
    private Long lastUpdateTime;

    /**
     * 操作系统类型。0:Windows;1:Linux;2.MacOS;3.iOS;4.Android;5.uos;6.spaceOS;66.other
     */
    @Schema(description = "操作系统类型。0:Windows;1:Linux;2.MacOS;3.iOS;4.Android;5.uos;6.spaceOS;66.other", example = "0")
    private Integer osType;

    private String osDetail;

    /**
     * 设备厂商
     */
    @Schema(description = "设备厂商", example = "hpe")
    private String vendor;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号", example = "C103V")
    private String model;

    /**
     * 客户端版本号
     */
    @Schema(description = "客户端版本号", example = "E1000")
    private String clientVersion;

    /**
     * 客户端语言
     */
    @Schema(description = "客户端语言", example = "ZH")
    private String clientLanguage;

    /**
     * 客户单所在操作系统版本
     */
    @Schema(description = "客户单所在操作系统版本", example = "1703")
    private String osVersion;

    /**
     * 设备分组名字
     */
    @Schema(description = "设备分组名字", example = "group1")
    private String deviceGroupName;

    /**
     * 设备分组ID
     */
    @Schema(description = "设备分组ID")
    private Long deviceGroupId;

    @Schema(description = "描述", example = "描述")
    private String description = null;

    /**
     * 终端类型 1瘦终端 2胖终端
     */
    @Schema(description = "终端类型 1瘦终端 2胖终端 3VOI", example = "1")
    private Integer deviceType;

    /**
     * 认证类型 0.用户;1.设备; 2.短信
     */
    @Schema(description = "登录类型 0.用户;1.设备; 2.短信", example = "0")
    private Integer authType;

    /**
     * 终端在线，0：离线 1：在线
     */
    @Schema(description = "终端在线，0：离线 1：在线", example = "0")
    private Integer status;

    /**
     * 授权策略ID
     */
    @Schema(description = "授权策略ID", example = "56")
    private Long authStgId;

    /**
     * 授权策略名称
     */
    @Schema(description = "授权策略名称", example = "Default")
    private String authStgName;


    /**
     * 授权用户名称
     */
    @Schema(description = "授权用户名称", example = "张三")
    private String userName;

    /**
     * '客户端设备sn，协助盒子问题定位'
     */
    @Schema(description = "客户端设备sn", example = "11111111")
    private String sn;


    /**
     * 授权用户登录名
     */
    @Schema(description = "授权用户登录名", example = "user1")
    private String loginName;

    /**
     * 授权用户ID
     */
    @Schema(description = "授权用户ID", example = "57")
    private Long authObjectId;

    /**
     * 桌面池id
     */
    @Schema(description = "桌面池id", example = "66")
    private Long desktopPoolId;

    /**
     * 桌面池名称
     */
    @Schema(description = "桌面池名称", example = "测试桌面池")
    private String desktopPoolName;

    @Schema(description = "桌面池授权类型", example = "单用户")
    private Integer desktopPoolType;

    @Schema(description = "桌面池用户类型")
    private Integer desktopPoolUserType;

    /**
     * 用户是否登录
     */
    @Schema(description = "用户是否登录", example = "0")
    private Integer userLogined;


    /*****************云学堂，云学院字段*****************/

    /**
     * 云学堂融合字段，客户端类型，1为教师端，2为学生端
     */
    private Integer clientType;

    /**
     * 学生注册编号，仅学生客户端有值。
     */
    private Long number;

    /**
     * 学生端位置
     */
    private Integer position;

    /**
     * agent状态，根据缓存判断
     */
    @Schema(description = "agent状态", example = "abnormal")
    private String agentStatus;

    /**
     * idv虚拟机mac
     */
    @Schema(description = "idv虚拟机mac", example = "0c:da:41:1d:9d:9d")
    private String idvVmMac;

    @Schema(description = "是否开启资源监控（0、关闭；1、开启； 默认关闭）")
    private Integer reportEnable;


    /**
     * 是否开启资源监控（0、关闭；1、开启； 默认关闭）
     */
    @Schema(description = "是否开启资源监控（0、关闭；1、开启； 默认关闭）")
    private Integer vmReportEnable = 0;

    /**
     * 开启资源监控后，agent上报信息的时间间隔（默认为0，不上报）
     */
    @Schema(description = "开启资源监控后，agent上报信息的时间间隔（默认为0，不上报）")
    private Integer vmInterval = 0;

    @Schema(description = "是否是共享终端", example = "0.不是；1.是")
    private Integer isShare = 0;

    @JsonProperty("projectId")
    private String tenantId;

    private String projectName;
    @Schema(description = "终端信息", example = "瘦终端（SpaceOS）")
    private String deviceDetail;

    @Parameter(description = "前缀名称", example = "VDI")
    private String displayNamePrefix;

    @Parameter(description = "起始编号", example = "1")
    private Integer startNumber;

    @Parameter(description = "终端idList", example = "[1,2,3]")
    private List<Long> deviceIdList;


    @Schema(description = "spaceagent版本(物理机的话就是agent版本)")
    private String spaceagentVersion;

    @Schema(description = "规格uuid，云上计费功能使用")
    private String normUuid;

    @Schema(description = "设备CPU架构")
    private String cpuArch;
}
