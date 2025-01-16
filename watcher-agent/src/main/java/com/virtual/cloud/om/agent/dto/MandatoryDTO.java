package com.virtual.cloud.om.agent.dto;

import lombok.Data;

@Data
public class MandatoryDTO {
    private String resourceIds;

    public MandatoryDTO(String resourceIds){
        this.resourceIds = resourceIds;
    }

    public MandatoryDTO(){

    }
}
