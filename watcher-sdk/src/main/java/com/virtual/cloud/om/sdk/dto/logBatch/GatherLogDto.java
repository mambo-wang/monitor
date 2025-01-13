package com.virtual.cloud.om.sdk.dto.logBatch;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GatherLogDto implements Serializable {
    int time;
    int size;
    List<String> hosts;
    Boolean targetHost;
}
