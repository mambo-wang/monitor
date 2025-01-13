package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Onestor主机存储节点信息DTO
 * */
@Data
@ApiModel(value = "存储节点信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostStorInfoDTO {
    private String cluster_ip;

    private String host_sn;

    private String protection_domain;

    private String public_ip;

    private String soft_version;

    private List<String> diskpool_list;

    private StorHostBasicDTO.Capacity capacity;

    private String os_version;

    private String rack_name;

    private String raid_firmware;

    private List<String> ethernet_ports;

    private String description;

    private StorHostBasicDTO.Mem mem;

    private String maintain_mode;

    private String host_id;

    private String manage_ip_slave;

    private StorHostBasicDTO.DiskStatus disk_status;

    private String host_type;

    private String host_status;

    private String nodepool_name;

    private String host_name;

    private String manage_ip;

    private Double cpu;

    private boolean support_raid;

    private String station_type;






    @Data
    public class Capacity
    {
        private Double avail_bytes;

        private Double used_pct;

        private Double used_bytes;

        private Double total_bytes;
    }

    @Data
    public class Mem {
        private Double avail_bytes;

        private Double used_pct;

        private Double used_bytes;

        private Double total_bytes;
    }

    @Data
    public class DiskStatus{
        private Integer fail;

        private Integer total;

        private Integer ok;
    }
}
