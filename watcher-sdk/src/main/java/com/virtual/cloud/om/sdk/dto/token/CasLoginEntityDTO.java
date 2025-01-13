package com.virtual.cloud.om.sdk.dto.token;


import lombok.Data;

@Data
public class CasLoginEntityDTO {
    private String name;
    private String password;
    private boolean encrypt;
}
