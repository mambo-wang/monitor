package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.dto.ExportLogReq;
import com.virtual.cloud.om.sdk.dto.LogLine;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/5/4 14:25
 */
@RestController
@RequestMapping("/log")
public class LogController {

    @Resource
    private RealTimeLogApi realTimeLogApi;

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ApiOperation(value = "在线检索日志")
    public RpcListLoadResult<LogLine> exportLog(@RequestBody ExportLogReq req) {
        List<LogLine> logLineReportDTOList = realTimeLogApi.searchAll(req.getPlatform(),req.getResourceId(),req.getType(),
                req.getTargetId(),req.getPath(),req.getQuery(),req.getStartTime(),req.getEndTime(),req.getSortDir(),"timestamp",req.getLogNum(), req.getLevel());

        return RpcListLoadResult.success(logLineReportDTOList);
    }
}
