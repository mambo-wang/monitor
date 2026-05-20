package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/23 11:31
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class DataCenterDTO {

    @Schema(description = ("用户名-不加密"))
    private String username;

    @Schema(description = ("密码-sm4加密"))
    private String ci;

    @Schema(description = ("公钥-base64编码"))
    private String publicKey;
    private String ip;
    private Integer master;

    @Schema(description = ("能力中心Agent唯一标识"))
    private String watcherCode;
}
