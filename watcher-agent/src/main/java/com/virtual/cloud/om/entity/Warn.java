package com.virtual.cloud.om.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "Warn")//相当于数据库里的表名
public class Warn implements Serializable {
    private static final long serialVersionUID = -5081419830740368061L;

    @ApiModelProperty(value="资源id")
    private String resourceId;

    /** 最新告警时间 */
    @ApiModelProperty(value="最新告警时间")
    private Long eventTime;

    /** 告警类型 */
    @ApiModelProperty(value="告警类型")
    private String type;

    /** 最新上报时间 */
    @ApiModelProperty(value="最新上报时间")
    private Long reportTime;

}

