package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * CAS接口返回的物理机存储实体类。
 */
@Data
@Schema(description = "主机存储卷信息")
public class StorageVolumeBasicDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 存储池名称。 */
    @Schema(description="存储池名称")
    private String storagePoolName;

    /** 存储卷文件名称。 */
    @Schema(description="存储卷文件名称")
    private String name;

    /** 存储卷文件容量大小, 单位为MB。 */
    @Schema(description="存储卷文件容量大小, 单位为MB")
    private Double totalSize;

    /** 存储已使用空间, 单位为MB。 */
    @Schema(description="存储已使用空间, 单位为MB")
    private Double allocation;

    /** 存储卷文件格式。 */
    @Schema(description="存储卷文件格式")
    private String format;

    /** 是不是模板镜像 **/
    @Schema(description="是不是基础镜像文件")
    private String baseFile;

    /** 该存储卷使用者(虚拟机名称)列表。 */
    @Schema(description="该存储卷使用者(虚拟机名称)列表")
    private String users;

    /** 是不是模板镜像 **/
    @Schema(description="是不是基础镜像文件")
    private Boolean isBaseFile;

    /** 是不是模板镜像 **/
    @Schema(description="是不是模板镜像")
    private Boolean isTempImg;
}
