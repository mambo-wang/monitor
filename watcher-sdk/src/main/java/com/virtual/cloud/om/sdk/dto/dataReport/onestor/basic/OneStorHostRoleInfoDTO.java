package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Onestor主机角色信息DTO
 * */
@Data
@ApiModel(value = "主机角色信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostRoleInfoDTO {
    private String slave_manage_ip;

    private String cluster_ip;

    private Boolean is_stor;

    private String name;

    private Boolean is_rgw;

    private String public_ip;

    private Boolean is_mds;

    private Boolean is_handy;

    private String subcluster;

    private int role;

    private String manage_ip;

    private Boolean is_mon;

    private Boolean is_tgt;

    private Boolean is_nas;
}
