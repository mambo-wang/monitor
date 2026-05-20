package com.virtual.cloud.om.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class DataCenterConfigDTO {

    private String ip;

    @Schema(description = ("用户名-不加密"))
    private String username;

    @Schema(description = ("密码-加密"))
    private String password;

    @Schema(description = ("机构名称"))
    private String comName;

    @Schema(description = ("组织名称"))
    private String orgName;
}
