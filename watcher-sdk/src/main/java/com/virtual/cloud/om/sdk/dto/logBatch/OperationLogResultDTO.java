package com.virtual.cloud.om.sdk.dto.logBatch;

import lombok.Data;

import java.util.List;

/**
 * 主机日志收集结果
 *
 * @author uthor  2020/06/09
 */
@Data
public class OperationLogResultDTO {

    /**
     * 是否有下载文件 0:有  1：无
     */
    private Integer isReadyDownload;
    /**
     * 文件下载randomUuid
     */
   private String randomUuid;
    /**
     *时间范围
     */
    int time;
    /**
     * 日志大小
     */
    int size;
    /**
     * 节点主机列表
     */
    List<GatherLogTreeDto> gatherLogTreeDtos;
    /**
     *
     */
    Boolean targetHost;


}
