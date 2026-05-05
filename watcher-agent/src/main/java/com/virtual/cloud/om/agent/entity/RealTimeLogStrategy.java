package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;

/**
 * @Author: w22798
 * @Date: 2022/5/10 14:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//相当于数据库里的表名
public class RealTimeLogStrategy {

    private String resourceId;

    private String platform;

    private Set<String> targets;

    private Set<String> logPaths;
}
