package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Onestor主机角色DTO
 * */
@Data
@ApiModel(value = "主机角色信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorHostRoleDTO {
    private String cluster_ip;

    @JsonProperty("is_stor")
    private boolean is_stor;

    private String name;

    @JsonProperty("is_rgw")
    private boolean is_rgw;

    private String maintain_mode;

    private String public_ip;

    @JsonProperty("is_mds")
    private boolean is_mds;

    @JsonProperty("is_handy")
    private boolean is_handy;

    private String subcluster;

    private int role;

    private String manage_ip;

    private String manage_ip_slave;

    @JsonProperty("is_mon")
    private boolean is_mon;

    @JsonProperty("is_tgt")
    private boolean is_tgt;

    @JsonProperty("is_nas")
    private boolean is_nas;


}
