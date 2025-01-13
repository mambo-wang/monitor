package com.virtual.cloud.om.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/5/12 18:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RealTimeLogUploadDTO {

    private String watcherCode;

    private List<LogLine> logs;
}
