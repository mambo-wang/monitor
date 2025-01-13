package com.virtual.cloud.om.sdk.dto.dataReport.onestor;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class OneStorRestResult implements Serializable {
    Object data;
    List<Object> result;
    Integer req_id;
}
