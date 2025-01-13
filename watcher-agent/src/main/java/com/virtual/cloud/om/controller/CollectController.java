package com.virtual.cloud.om.controller;

import cn.hutool.json.JSONUtil;
import com.virtual.cloud.om.dto.ResourceDTO;
import com.virtual.cloud.om.onestor.service.warn.OnestorRealTimeAlarmsCollector;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.deploy.BatchDeployVO;
import com.virtual.cloud.om.sdk.dto.RealTimeLogStrategyRequest;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import com.virtual.cloud.om.service.DataReportCollectorOverview;
import com.virtual.cloud.om.service.datacenter.DataCenterService;
import com.virtual.cloud.om.service.resource.ResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.assertj.core.util.Lists;
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

    @Resource
    private OnestorRealTimeAlarmsCollector onestorRealTimeAlarmsCollector;

    @ApiOperation(value = "日志实时上报任务下发接口demo")
    @PostMapping("/realtime-log")
    public RpcResult realTimeLog(@RequestBody List<RealTimeLogStrategyRequest> request){

        realTimeLogApi.handleRealTimeLogStrategy(request);
        return RpcResult.success("success");
    }

    @PostMapping("/resources")
    public RpcResult<Void> createResources(@RequestBody List<ResourceDTO> dtos){
        this.resourceService.resources(dtos);
        return RpcResult.success();
    }

    @PostMapping("/batch")
    public RpcResult<Void> batchDeploy(@RequestBody BatchDeployVO batchDeployVO) {
        batchDeployVO.setMask("255.255.255.0");
        deployApi.deploy(batchDeployVO);
        return RpcResult.success("成功");
    }

    @GetMapping("/onestorwarns")
    public RpcListLoadResult<WarnDataDTO> onestor(){

        List<WarnDataDTO> warnDataDTOS = onestorRealTimeAlarmsCollector.collect(ReportResourceEnum.onestor.name(),
                "10.132.37.61", "http", 80,
                "sdfas", "sdfa", "warn", "999");
        return RpcListLoadResult.success(warnDataDTOS);
    }
}
