package com.virtual.cloud.om.sdk.dto.dataReport.workspace;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("桌面池信息")
public class RestDesktopPoolDTO implements Serializable {
    private static final long serialVersionUID = 5297068521319733641L;
    private Long id;

    private String name;

    private String description;

    private Integer abnormalNum;

    private Integer shutOffNum;

    private Integer pausedNum;

    private Integer runningNum;

    private Integer unknownNum;

    private Integer clusterId;

    private String clusterName;

    private String desktopNamePre;

    private Integer initType;

    private Integer maxVmNum;

    private Integer vmNum;

    private Integer userType;

    private Long authStgId;

    private String authStgName;

    private String ou;

    private String defaultStorage;

    private String storagePoolName;

    private String secondStoragePoolName;

    private Integer targetType;

    private String templateUuid;


    private List<String> vswitches;
    private int allocationNum;

    /**
     * 桌面镜像id
     */
    private Long vmTemplateId;

    /**
     * 桌面镜像id
     */
    private Long imageId;

    /**
     * 桌面池分组id
     */
    private Long desktopPoolGroupId;
}
