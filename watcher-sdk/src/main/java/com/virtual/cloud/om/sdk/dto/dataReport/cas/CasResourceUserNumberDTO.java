package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value="id")
    private Long id;
    /** 登录名*/
    @ApiModelProperty(value="loginName")
    private String loginName;
    /** 认证类型*/
    @ApiModelProperty(value="authType")
    private String authType;
    /** 名称*/
    @ApiModelProperty(value="name")
    private String name;
    /**  操作员分组id*/
    @ApiModelProperty(value="groupId")
    private String groupId;
    /** 是否启用：1，启用；0，禁用*/
    @ApiModelProperty(value="enable")
    private String enable;



}
