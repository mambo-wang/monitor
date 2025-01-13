package com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Onestor主机MDS节点信息DTO
 * */
@Data
@ApiModel(value = "主机MDS节点信息")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorHostMdsInfoDTO {
    private String status;

    private double cpu_usage;

    private String ip_addr;

    private String name;

    private String ip_addr_slave;

    private String mds;

    private List<Double> memory_usage;
}
