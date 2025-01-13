package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Onestor主机基本信息上报DTO
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StorHostBasicReportDTO {
    private String host_id;

    private String fs_id;

    private String host_name;

    private int role;

    private String nodepool_name;

    private String rack_name;

    private String host_type;

    private String host_sn;

    private String host_status;

    private String mon_status;

    private String mds_status;

    private boolean nas_healthy;

    private boolean nas_subhealthy;

    private String manage_ip;

    private String manage_ip_slave;

    private String public_ip;

    private String cluster_ip;

    private String protection_domain;

    private String diskpool_list;

    /**
     * 节点池的维护模式：0-off,1-on
     */
    private int maintain_mode;

    private int disk_status_ok_num;

    private int disk_status_fail_num;

    private int disk_status_total_num;

    private double mem_total_bytes;

    private double mem_avail_bytes;

    private double mem_used_bytes;

    private double mem_used_pct;

    private double cpu_percent;

    private double total_bytes;

    private double avail_bytes;

    private double used_bytes;

    private double used_pct;

    private String soft_version;

    private String os_version;

    private String raid_firmware;

    private String cluster_name;

    private String description;
    private List<String> ethernet_ports;
    private String station_type;
    private String station;

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
