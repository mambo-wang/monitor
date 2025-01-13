package com.virtual.cloud.om.sdk.dto.logBatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
public class VmLogDTO implements Serializable {

    private static final long serialVersionUID = 7023049458840163010L;
    private Long id;
    private String title;
    private String ipStr;
    private String logDirPath;
    private String fileName;
    private Integer status;
    private Date collectionTime;
    private Date endTime;
    private String description;
    @JsonProperty("projectId")
    private String tenantId;
    private String projectName;
}
