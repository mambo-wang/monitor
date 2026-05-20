package com.virtual.cloud.om.sdk.dto.dataReport.cas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema
public class StorageBasicDTO implements Serializable {
    private static final long serialVersionUID = -5359114856946019196L;

    /** 存储池名称。 * */
    @Schema(description="存储池名称",example="1")
    private String name;

    /** 存储类型，取值为netfs、iscsi、fs。 * */
    @Schema(description="存储类型，取值为netfs、iscsi、fs")
    private String type;
    /** 存储池路径。 * */
    @Schema(description="存储池路径",example="1")
    private String path;

    /** 存储大小，单位为MB。 * */
    @Schema(description="存储大小，单位为MB")
    private Long totalSize;
    /** 存储池列表上的存储已分配容量，单位为MB。* */
    @Schema(description="存储池列表上的存储已分配容量，单位为MB")
    private Double allocation;
    /** 存储剩余大小，单位为MB。* */
    @Schema(description="存储剩余大小，单位为MB")
    private Long freeSize;
    /** 状态 1：活动,0:不活动 */
    @Schema(description="状态 1：活动,0:不活动")
    private Integer status;
    /** 是否自动启动 */
    @Schema(description="是否自动启动")
    private Boolean autoStart;

    /** 是否开启存储介质 */
    @Schema(description="是否开启存储介质")
    private Boolean symbolic;

}
