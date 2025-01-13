package com.virtual.cloud.om.entity;

import io.swagger.annotations.ApiModelProperty;
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
@Document(collection = "DataCenterAuthCount")//相当于数据库里的表名
public class DataCenterAuthCount {

    @Id
    private String id;

    @ApiModelProperty("认证次数  网络不通20次自动断开 需要用户手动重连")
    private Integer count;
}
