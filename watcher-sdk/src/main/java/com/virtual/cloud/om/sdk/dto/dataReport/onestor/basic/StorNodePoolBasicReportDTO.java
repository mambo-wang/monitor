package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * Onestor节点池基本信息上报DTO
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorNodePoolBasicReportDTO {
    private String nodepool_name;

    private String fs_id;

    private String cluster_type;

    /**
     * 是否为主节点池：0-false,1-true
     */
    private Integer is_master_subcluster;

    private String nodepool_type;

    private String safe_level;
    /**
     * 是否开启保护域：0-false,1-true
     */
    private Integer safe_domain_flag;

    private int safe_domain_num;

    private List<String> node_list;

    private Map<String, Integer> diskpool_info;

    /**
     * 节点池的维护模式：0-off,1-on
     */
    private Integer maintain_mode;

    private int num_maintain_host;

    private String manage_network;

    private String manage_network_slave;

    private String leader_ip;

    private String vip_type;

    private String vip;

    private String vip_attached;

    private String description;
    private String background_connect_type;
    private String double_live_name;
    private String double_live;
    private String vip_station_b_attached;
    private String vip_station_b;
    private Double min_memory_capacity;
    private Double min_cpu_score;

    public void setIs_master_subcluster(Boolean value) {
        this.is_master_subcluster = value ? 1 : 0;
    }

    public void setSafe_domain_flag(String value) {
        Integer intValue = 0;
        if (value.equals("false")) {
            intValue = 0;
        } else if (value.equals("true")) {
            intValue = 1;
        }
        this.safe_domain_flag = intValue;
    }

    public void setMaintain_mode(String value) {
        Integer intValue = 0;
        if (value.equals("off")) {
            intValue = 0;
        } else if (value.equals("on")) {
            intValue = 1;
        }
        this.maintain_mode = intValue;
    }
}
