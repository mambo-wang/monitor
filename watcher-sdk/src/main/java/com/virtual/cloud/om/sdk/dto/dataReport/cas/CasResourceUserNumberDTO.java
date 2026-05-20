package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@ToString
@XmlRootElement(name = "operator")
@XmlAccessorType(XmlAccessType.FIELD)
public class CasResourceUserNumberDTO {
    private static final long serialVersionUID = 7630696172783821368L;
    /** id*/
    @Schema(description="id")
    private Long id;
    /** 登录名*/
    @Schema(description="loginName")
    private String loginName;
    /** 认证类型*/
    @Schema(description="authType")
    private String authType;
    /** 名称*/
    @Schema(description="name")
    private String name;
    /**  操作员分组id*/
    @Schema(description="groupId")
    private String groupId;
    /** 是否启用：1，启用；0，禁用*/
    @Schema(description="enable")
    private String enable;



}
