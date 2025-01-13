package com.virtual.cloud.om.sdk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostInfoDTO {
    private String cluster_ip;
    private Boolean is_stor;
    private String name;
    private Boolean is_rgw;
    private String maintain_mode;
    private String public_ip;
    private Boolean is_mds;
    private Boolean is_handy;
    private String subcluster;
    private Integer role;
    private String manage_ip;
    private String manage_ip_slave;
    private Boolean is_mon;
    private Boolean is_tgt;
    private Boolean is_nas;
    private String station_type;
    private String station;
}
