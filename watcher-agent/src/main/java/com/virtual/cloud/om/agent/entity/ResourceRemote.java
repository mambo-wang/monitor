package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author:XK
 * @Date:2022/9/1 14:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "ResourceRemote")
public class ResourceRemote implements Serializable {


    private static final long serialVersionUID = -1388381067061600226L;

    @Id
    private String id;
    /**
     * 资源id
     */
    private String resourceId;
    /**
     *
     */
    private String username;
    /**
     *
     */
    private String password;

    /**
     * 开启时间 0 一天 1 3天  2 7天 为-1表示不开启
     */
    private Integer remoteType;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 结束时间字符串
     */
    private String endTimeStr;

    private String resourceName;
    private String ipAddress;
    private String platform;
    /**
     * 0 关闭 1 开启
     */
    private Integer remote;

}
