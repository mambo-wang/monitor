package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel
public class NetworkInfoVO {
    @ApiModelProperty("节点名称")
    private String nodeName;
    @ApiModelProperty("内网网卡")
    private NetworkInfo inner;
    @ApiModelProperty("外网网卡")
    private NetworkInfo outer;
    @ApiModelProperty("是否主节点：0-否，1-是")
    private Integer master;


    @Data
    @ApiModel
    public static class NetworkInfo {
        @ApiModelProperty("网卡名")
        private String name;
        @ApiModelProperty("ip")
        private String ip;
        @ApiModelProperty("掩码")
        private String mask;
        @ApiModelProperty("网关")
        private String gateway;
        @ApiModelProperty("分配方式")
        private AllocationTypeEnum allocation = AllocationTypeEnum.STATIC;
        @ApiModelProperty("wifi名")
        private String wifi;
        @ApiModelProperty("wifi密码")
        private String wifiPwd;
        @ApiModelProperty("dns1")
        private String dns1;
        @ApiModelProperty("dns2")
        private String dns2;
    }

    @AllArgsConstructor
    public enum AllocationTypeEnum{
        STATIC(1,"静态分配"),
        DHCP(2,"DHCP"),
        ;

        public final Integer type;
        public final String desc;
    }
}
