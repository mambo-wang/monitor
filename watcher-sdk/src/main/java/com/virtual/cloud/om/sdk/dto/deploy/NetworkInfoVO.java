package com.virtual.cloud.om.sdk.dto.deploy;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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

    // Getters and Setters
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public NetworkInfo getInner() { return inner; }
    public void setInner(NetworkInfo inner) { this.inner = inner; }
    public NetworkInfo getOuter() { return outer; }
    public void setOuter(NetworkInfo outer) { this.outer = outer; }
    public Integer getMaster() { return master; }
    public void setMaster(Integer master) { this.master = master; }

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

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getMask() { return mask; }
        public void setMask(String mask) { this.mask = mask; }
        public String getGateway() { return gateway; }
        public void setGateway(String gateway) { this.gateway = gateway; }
        public AllocationTypeEnum getAllocation() { return allocation; }
        public void setAllocation(AllocationTypeEnum allocation) { this.allocation = allocation; }
        public String getWifi() { return wifi; }
        public void setWifi(String wifi) { this.wifi = wifi; }
        public String getWifiPwd() { return wifiPwd; }
        public void setWifiPwd(String wifiPwd) { this.wifiPwd = wifiPwd; }
        public String getDns1() { return dns1; }
        public void setDns1(String dns1) { this.dns1 = dns1; }
        public String getDns2() { return dns2; }
        public void setDns2(String dns2) { this.dns2 = dns2; }
    }

    public enum AllocationTypeEnum{
        STATIC(1,"静态分配"),
        DHCP(2,"DHCP"),
        ;

        public final Integer type;
        public final String desc;

        AllocationTypeEnum(Integer type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }
}
