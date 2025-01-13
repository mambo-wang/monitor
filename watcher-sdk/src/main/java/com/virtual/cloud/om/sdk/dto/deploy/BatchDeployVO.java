package com.virtual.cloud.om.sdk.dto.deploy;

import com.virtual.cloud.om.sdk.dto.SSHHost;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/5/8 16:35
 */
@Data
@ApiModel("多节点部署")
public class BatchDeployVO {

    @ApiModelProperty(value = "虚IP")
    private String vip;

    @ApiModelProperty(value = "子网掩码", example = "255.255.255.0")
    private String mask = "255.255.255.0";

    @ApiModelProperty(value = "节点信息")
    private List<DeployVO> nodes;

    public SSHHost getMasterNode(){
        return SSHHost.newInstance(nodes.get(0));
    }

    public SSHHost getSlave1Node(){
        return SSHHost.newInstance(nodes.get(1));
    }

    public SSHHost getSlave2Node(){
        return SSHHost.newInstance(nodes.get(2));
    }
}
