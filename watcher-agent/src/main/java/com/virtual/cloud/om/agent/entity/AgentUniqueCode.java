package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "AgentUniqueCode")//相当于数据库里的表名
public class AgentUniqueCode {

    @Id
    private String id;

    private String uId;
}