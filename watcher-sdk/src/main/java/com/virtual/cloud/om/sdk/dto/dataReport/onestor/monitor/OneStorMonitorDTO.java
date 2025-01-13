package com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneStorMonitorDTO {
    private String target;
    private List<List> datapoints;

    public Integer getDataPointsLength(){
        return datapoints.size();
    }

}
