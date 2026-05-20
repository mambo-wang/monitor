package com.virtual.cloud.om.agent.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



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
//相当于数据库里的表名
public class DataCenterConfig {

    
    private String id;

    private String ip;

    @Schema(description = ("用户名-不加密"))
    private String username;

    @Schema(description = ("密码-加密"))
    private String password;

    private String port;

    private Integer datacenterType;
}
