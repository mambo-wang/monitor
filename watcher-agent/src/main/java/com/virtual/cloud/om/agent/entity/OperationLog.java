package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: w22798
 * @Date: 2022/4/26 16:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "OperationLog")//相当于数据库里的表名
public class OperationLog {

    private String module;

    private String desc;

    private String result;

    private String msg;

    private String time;

    private String operator;

    private String deleted;
}
