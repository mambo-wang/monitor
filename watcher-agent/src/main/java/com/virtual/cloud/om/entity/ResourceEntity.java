package com.virtual.cloud.om.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: w22798
 * @Date: 2022/4/26 19:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Resource")//相当于数据库里的表名
@ApiModel
public class ResourceEntity {
    @ApiModelProperty("资源类型")
    private String platform;
    @ApiModelProperty("资源ID")
    @Id
    private String id;
    @ApiModelProperty("IP地址")
    private String ipAddress;
    @ApiModelProperty("端口")
    private Integer port;
    @ApiModelProperty("rest认证用户名")
    private String ac;
    @ApiModelProperty("rest认证用户密码")
    private String ci;
    @ApiModelProperty("访问协议HTTP/HTTPS")
    private String protocol;
    @ApiModelProperty("认证类型，目前默认统一为Digest")
    private String authType;
    @ApiModelProperty("管理节点用户一般为root")
    private String serverUsername;
    @ApiModelProperty("管理节点密码")
    private String serverPassword;
    @ApiModelProperty("管理节点端口号")
    private Integer serverPort;
    @ApiModelProperty("是否激活状态：0-否，1-是")
    private Integer active;
    @ApiModelProperty("创建时间")
    private String createTime;
    @ApiModelProperty("更新时间")
    private String updateTime;
}
