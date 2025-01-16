package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: w22798
 * @Date: 2022/4/26 19:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Deploy")//相当于数据库里的表名
public class Deploy {

    private String ip;

    private String username;

    private String password;

    private Boolean isMaster;
}
