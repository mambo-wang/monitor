package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.agent.dto.ResourceDTO;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.deploy.BatchDeployVO;
import com.virtual.cloud.om.sdk.dto.RealTimeLogStrategyRequest;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.agent.service.DataReportCollectorOverview;
import com.virtual.cloud.om.agent.service.datacenter.DataCenterService;
import com.virtual.cloud.om.agent.service.resource.ResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: w22798
 * @Date: 2022/5/5 15:14
 */
@RestController
@Api(tags = "采集接口demo")
@RequestMapping("/collect")
public class CollectController {

    @Resource
    private RealTimeLogApi realTimeLogApi;

    @Resource
    private ResourceService resourceService;

    @Autowired
    private DeployApi deployApi;

    @Autowired
    private DataCenterService dataCenterService;


    @ApiOperation(value = "日志实时上报任务下发接口demo")
    @PostMapping("/realtime-log")
    public RpcResult realTimeLog(@RequestBody List<RealTimeLogStrategyRequest> request){

        realTimeLogApi.handleRealTimeLogStrategy(request);
        return RpcResult.success("success");
    }

    @PostMapping("/resources")
    public RpcResult<Void> createResources(@RequestBody List<ResourceDTO> dtos){
        // 资源同步 - 委托给 ResourceController
        resourceService.syncResources(dtos);
        return RpcResult.success();
    }

    @PostMapping("/batch")
    public RpcResult<Void> batchDeploy(@RequestBody BatchDeployVO batchDeployVO) {
        batchDeployVO.setMask("255.255.255.0");
        deployApi.deploy(batchDeployVO);
        return RpcResult.success("成功");
    }
}
