package com.virtual.cloud.om.sdk.dto.logBatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author z13465 2020/2/12
 */

@Data
@ToString
public class TerminalLogDTO {
    private Long id;
    private String taskName;
    private Long deviceId;
    private String logDirPath;
    private String fileName;
    private Integer status;
    private Integer succNum;
    private Integer failNum;
    private Date collectionTime;
    private Date endTime;
    @JsonProperty("projectId")
    private String tenantId;
    private String projectName;
}
