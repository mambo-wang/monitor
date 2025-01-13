package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * CAS接口返回的物理机存储实体类。
 */
@Data
@ApiModel("主机存储池信息")
@XmlRootElement(name = "storagePool")
@XmlAccessorType(XmlAccessType.FIELD)
public class StoragePoolDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 存储池名称。 * */
    @ApiModelProperty(value="存储池名称",example="1")
    private String name;

    /** 存储池别名。 * */
    @ApiModelProperty(value="存储池别名",example="1")
    private String title;

    /** 存储池路径。 * */
    @ApiModelProperty(value="存储池路径",example="1")
    private String path;

    /** 存储类型，取值为netfs、iscsi、fs。 * */
    @ApiModelProperty(value="存储类型，取值为netfs、iscsi、fs")
    private String type;

    /** 存储大小，单位为MB。 * */
    @ApiModelProperty(value="存储大小，单位为MB")
    private Long totalSize;

    /** 存储剩余大小，单位为MB。* */
    @ApiModelProperty(value="存储剩余大小，单位为MB")
    private Long freeSize;

    /** 存储池列表上的存储已分配容量，单位为MB。* */
    @ApiModelProperty(value="存储池列表上的存储已分配容量，单位为MB")
    private Double allocation;

    /** 状态 1：活动,0:不活动 */
    @ApiModelProperty(value="状态 1：活动,0:不活动")
    private Integer status;

    /** 是否自动启动 */
    @ApiModelProperty(value="是否自动启动")
    private Boolean autoStart;

    /** 是否开启存储介质 */
    @ApiModelProperty(value="是否开启存储介质")
    private Boolean symbolic;
}
