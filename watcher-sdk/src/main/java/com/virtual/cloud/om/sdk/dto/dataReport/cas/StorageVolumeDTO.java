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
@ApiModel("主机存储卷信息")
@XmlRootElement(name = "storageVolume")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageVolumeDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 存储已使用空间, 单位为MB。 */
    @ApiModelProperty(value="存储已使用空间, 单位为MB")
    private Double allocation;

    /** 存储池列表上的存储已分配容量，单位为MB，用于展示。* */
    @ApiModelProperty(value="存储池列表上的存储已分配容量，单位为MB，用于展示")
    private String allocationForamt;

    /** 是不是模板镜像 **/
    @ApiModelProperty(value="是不是基础镜像文件")
    private Boolean baseFile;

    /** 存储卷文件格式。 */
    @ApiModelProperty(value="存储卷文件格式")
    private String format;

    /** 主机名称。 */
    @ApiModelProperty(value="主机名称")
    private String hostName;

    /** 存储卷文件名称。 */
    @ApiModelProperty(value="存储卷文件名称")
    private String name;

    /** 存储卷文件容量大小, 单位为MB。 */
    @ApiModelProperty(value="存储卷文件容量大小, 单位为MB")
    private Double size;

    /** 存储卷文件容量大小, 单位为MB，用于展示。 */
    @ApiModelProperty(value="存储卷文件容量大小, 单位为MB，用于展示")
    private String sizeFormat;

    /** 存储池名称。 */
    @ApiModelProperty(value="存储池名称")
    private String storagePoolName;

    /** 是不是模板镜像 **/
    @ApiModelProperty(value="是不是模板镜像")
    private Boolean tempImg;

    /** 存储卷类型, 如file, block。 */
    @ApiModelProperty(value="存储卷类型, 如file, block")
    private String type;

    /** 该存储卷使用者(虚拟机名称)列表。 */
    @ApiModelProperty(value="该存储卷使用者(虚拟机名称)列表")
    private String users;
}
