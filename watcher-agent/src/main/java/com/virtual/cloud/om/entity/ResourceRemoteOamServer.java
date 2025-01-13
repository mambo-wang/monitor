package com.virtual.cloud.om.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author:XK
 * @Date:2022/9/1 17:25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "ResourceRemoteOamServer")
public class ResourceRemoteOamServer {
    /**
     * 用户id（租户id）
     */
    @Id
    private  String id;

    private  String userId;

    private  String resourceId;
    private  String username;
    private  String password;
    /**
     * 0 1 2 为-1 表示不开启 0：1次 1：1天 2：3天
     */
    private  Integer remoteType;

    private  Long  endTime;
    /**
     * 开始时间到结束时间 2022-09-02 09:05:10 ~ 2022-09-03 09:05:10
     */
    private String useTime;


}
