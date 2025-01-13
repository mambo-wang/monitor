package com.virtual.cloud.om.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/10 10:58
 * 保存数据中心配置
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "DataCenterConfig")//相当于数据库里的表名
public class DataCenterConfig {

    @Id
    private String id;

    private String ip;

    @ApiModelProperty("用户名-不加密")
    private String username;

    @ApiModelProperty("密码-加密")
    private String password;

    private String port;

    private Integer datacenterType;
}
