package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Onestor节点池基本信息DTO
 */
@Data
@ApiModel(value = "节点池基本信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorNodePoolBasicDTO {
    private int safe_domain_num;

    private String safe_level;

    private String leader_ip;

    private String vip;

    private String vip_attached;

    /**
     * 是否为主节点池：0-false,1-true
     */
    @JsonProperty("is_master_subcluster")
    private boolean is_master_subcluster;

    private String nodepool_name;

    private String cluster_type;

    private Map<String, Integer> diskpool_info;

    private List<String> node_list;

    private String safe_domain_flag;

    private String description;

    private String vip_type;

    private int min_memory_capacity;

    private String maintain_mode;

    private String manage_network_slave;

    private String fsid;

    private String nodepool_type;

    private String background_connect_type;

    private int num_maintain_host;

    private double min_cpu_score;

    private String manage_network;
    private String double_live_name;
    private Object double_live;
    private String vip_station_b_attached;
    private String vip_station_b;


}
