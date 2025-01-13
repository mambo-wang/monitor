package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Onestor主机监控节点信息DTO
 * */
@Data
@ApiModel(value = "主机监控节点信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostMonitorInfoDTO {
    private String cluster_ip;

    private String raid_firmware;

    private List ethernet_ports;

    private String host_sn;

    private String mon_status;

    private Mem mem;

    private String os_version;

    private String host_status;

    private String nodepool_name;

    private String host_name;

    private String public_ip;

    private String soft_version;

    private String host_id;

    private String manage_ip;

    private String manage_ip_slave;

    private double cpu;

    @Data
    public static class Mem
    {
        private Double avail_bytes;

        private Double used_pct;

        private Double used_bytes;

        private Double total_bytes;
    }
    }
