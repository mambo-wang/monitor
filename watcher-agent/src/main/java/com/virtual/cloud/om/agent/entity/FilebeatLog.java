package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: w22798
 * @Date: 2022/5/04 12:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "FilebeatLog")//相当于数据库里的表名
public class FilebeatLog {

    private String path;

    private String message;

    private String time;

    private String level;

    private String tags;

    private String resource;
}
