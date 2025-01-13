package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Onestor主机Nas节点信息DTO
 * */
@Data
@ApiModel(value = "主机Nas节点信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostNasInfoDTO {
    private Boolean subhealthy;

    private List<String> public_addresses;

    private Boolean healthy;

    private List<Double> memory;

    private int nfs_connection;

    private String nas_group_name;

    private int cifs_connection;

    private List<String> nfs_client;

    private int ftp_connection;

    private Boolean offline;

    private List<String> cifs_client;

    private String host_address;

    private String host_address_slave;

    private Double cpu;

    private List<String> ftp_client;

    private String host_name;
}
