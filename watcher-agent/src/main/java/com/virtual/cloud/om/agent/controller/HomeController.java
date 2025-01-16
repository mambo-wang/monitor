package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.agent.entity.Parameter;
import com.virtual.cloud.om.log.entity.OperationLog;
import com.virtual.cloud.om.log.service.LogService;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.config.rest.common.RestType;
import com.virtual.cloud.om.sdk.config.rest.workspace.WsRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.uri.WsUriConstants;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.agent.service.deploy.DeployService;
import com.virtual.cloud.om.agent.service.parameter.ParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.*;

/**
 * @author z13465 2022/4/14
 */
@RestController
@RequestMapping("/")
public class HomeController {

    @Value("${who.are.you}")
    private String properties;

    /**
     * watcher部署位置
     */
    @Value("${watcher.home}")
    private String watcherHome;

    @Autowired
    private LogService logService;

    @Autowired
    DeployService deployService;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private WsRestConnection wsRestConnection;

    @Autowired
    private CasRestConnection casRestConnection;

    @GetMapping("/workspace/desktoppools")
    public String testWorkspace(@ModelAttribute RestHost restHost) {

        String url = String.format(WsUriConstants.DesktopPool.QUERY_DESKTOPPOOL_LIST_REST_CENTER, 0);
        return wsRestConnection.get(restHost.getHost(), restHost.getProtocol(), restHost.getPort(), url, new RestType<String>() {
        });
    }

    @GetMapping("/cas/hosts")
    public String testCas(@ModelAttribute RestHost restHost) {
        String url = CasUriConstants.Host.HOST_BASIC_INFO_ALL;
        return casRestConnection.get(restHost.getPlatform(), restHost.getHost(), restHost.getProtocol(), restHost.getPort(), restHost.getUsername(), restHost.getPassword(), url, new ParameterizedTypeReference<String>() {
        });
    }

    @GetMapping
    public String helloWorld() {

        return "HelloWorld，active properties is " + properties + " home is " + watcherHome;
    }

    /**
     * 参数配置
     */
    @PostMapping("/parameter")
    public RpcResult<Parameter> editParameter(@RequestBody Parameter parameter) {
        parameterService.editParamByTypeAndName(parameter.getValue(), parameter.getType(), parameter.getName());
        return RpcResult.success(parameterService.queryParameterByTypeAndName(parameter.getType(), parameter.getName()).get());
    }

    @GetMapping("/log")
    public RpcListLoadResult<OperationLog> queryLog(@ModelAttribute OperationLog operationLog) {
        return RpcListLoadResult.success(logService.findLogs(operationLog));
    }

    @DeleteMapping("/log")
    public RpcResult<Long> removeLog(@RequestParam(name = "time") String time, @RequestParam(name = "per") boolean persistent) {
        long result = logService.deleteLogBeforeTime(time, persistent);
        return RpcResult.success(result);
    }

    @GetMapping("/enableKafkaDebug")
    public RpcResult<String> enableDebug(@RequestParam(value = "ip") String ip) {
        deployService.modifyKafkaListeners(ip);

        return RpcResult.success("success");
    }

}
