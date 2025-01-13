package com.virtual.cloud.om.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/10 14:34
 * 部署目前步骤
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "OadWatcherStatus")//相当于数据库里的表名
public class OadWatcherStatus {

    @Id
    private String id;

    /**0:部署和认证都未完成 1:完成部署  2：完成部署认证 */
    private Integer step;
}
