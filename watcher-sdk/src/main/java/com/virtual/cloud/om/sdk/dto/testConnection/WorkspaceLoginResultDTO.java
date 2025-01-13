package com.virtual.cloud.om.sdk.dto.testConnection;

import com.virtual.cloud.om.sdk.dto.RegionDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by x19765 on 2020/11/17.
 */
@Data
public class WorkspaceLoginResultDTO implements Serializable{
    private static final long serialVersionUID = 1;

    private Boolean loginStatus;

    private String errorMessage;

    private String mac;

    private RegionDTO regionDTO;

    private String platEdition;

    private String resourceUuid;

    private String viewScreenLoginAddr;

}
