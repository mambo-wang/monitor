package com.virtual.cloud.om.sdk.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户注册申请详情DTO（含历史记录）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserRegisterRequestDetailDTO extends UserRegisterRequestDTO {

    /**
     * 该用户名历史申请记录列表
     */
    private List<UserRegisterRequestDTO> historyList;
}
