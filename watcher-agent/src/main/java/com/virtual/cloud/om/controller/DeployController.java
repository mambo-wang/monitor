package com.virtual.cloud.om.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Sets;
import com.virtual.cloud.om.WatcherAgentApplication;
import com.virtual.cloud.om.dto.UpdateStepDTO;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.deploy.*;
import com.virtual.cloud.om.sdk.utils.FuncUtil;
import com.virtual.cloud.om.sdk.utils.IpUtil;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.service.datacenter.DataCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * @Author: w22798
 * @Date: 2022/4/26 19:50
 */
@Slf4j
@RestController
@RequestMapping("/deploy")
@Api(tags = "采集结点部署")
public class DeployController {

    @Autowired
    private DeployApi deployApi;

    @Autowired
    private DataCenterService dataCenterService;

    private StringManager sm_common = StringManager.getManager("Common");

    @ApiOperation(value = "多节点部署")
    @PostMapping("/batch")
    public RpcResult<Void> batchDeploy(@RequestBody BatchDeployVO batchDeployVO){
        try {
            if(StringUtils.isEmpty(batchDeployVO.getMask())){
                batchDeployVO.setMask("255.255.255.0");
            }
            deployApi.deploy(batchDeployVO);
            dataCenterService.updateStep(Constant.DataCenter.STEP_DEPLOY);
            return RpcResult.success("成功");
        } catch (Exception exception) {
            exception.printStackTrace();
            dataCenterService.updateStep(Constant.DataCenter.STEP_DEPLOY_AUTH_FAIL);
            return RpcResult.fail(exception.getMessage());
        }
    }

    @ApiOperation(value = "单节点部署")
    @PostMapping("/single")
    public RpcResult<Void> singleDeploy(@RequestBody DeployVO deployVO){
        try {
            deployApi.deployMaster(deployVO);
            dataCenterService.updateStep(Constant.DataCenter.STEP_DEPLOY);
            return RpcResult.success("成功");
        } catch (Exception exception) {
            exception.printStackTrace();
            dataCenterService.updateStep(Constant.DataCenter.STEP_DEPLOY_AUTH_FAIL);
            return RpcResult.fail(exception.getMessage());
        }
    }

    @ApiOperation(value = "获取节点状态")
    @GetMapping
    public RpcListLoadResult<DeployQueryVO> queryDeployInfo(){
        List<DeployQueryVO> deployQueryVOS = deployApi.queryStatus();
        return RpcListLoadResult.success(deployQueryVOS);
    }

    @ApiOperation(value = "组件服务管理")
    @PutMapping("/manage")
    public RpcResult<Void> operateComponent(@RequestBody ComponentManage componentManage) {

        deployApi.componentsManage(componentManage);
        return RpcResult.success("success");
    }

    @ApiOperation(value = "重启spring boot应用")
    @GetMapping("/refresh")
    public RpcResult<Void> refreshApplication() {
        ExecutorService threadPool = new ThreadPoolExecutor(1,1,0, TimeUnit.SECONDS,new ArrayBlockingQueue<>( 1 ),new ThreadPoolExecutor.DiscardOldestPolicy ());
        threadPool.execute(() -> {
            WatcherAgentApplication.context.close();
            WatcherAgentApplication.context = SpringApplication.run(WatcherAgentApplication.class, WatcherAgentApplication.args);
        });
        threadPool.shutdown();
        return RpcResult.success("success refreshed!");
    }

    @ApiOperation(value = "获取节点信息")
    @GetMapping("/host")
    public String queryHost(){
        String deployQueryVOS = deployApi.queryHost();
        return deployQueryVOS;
    }

    @ApiOperation(value = "获取节点信息")
    @GetMapping("/localIps")
    public RpcListLoadResult<String> queryLocalIps(){
        Set<String> localIps = IpUtil.queryLocalIps();
        return RpcListLoadResult.success(new ArrayList<>(localIps));
    }

    @ApiOperation(value = "keepalived notify")
    @PutMapping("/keepalived/notify/{masterOrBackup}")
    public RpcResult notify(@PathVariable(value = "masterOrBackup")String masterOrBackup){
        if(StringUtils.equals(masterOrBackup, "master")){
            log.info("[deploy] this node is master node");
        }

        if(StringUtils.equals(masterOrBackup, "backup")){
            log.info("[deploy] this node is slave node");
        }

        return RpcResult.success();
    }

    @ApiOperation(value = "修改部署步骤")
    @PutMapping(value = "/step")
    public RpcResult updateStep(@RequestBody UpdateStepDTO query){
        this.dataCenterService.updateStep(query.getStep());
        return RpcResult.success();
    }

    @ApiOperation(value = "获取本地网卡")
    @GetMapping(value = "/network")
    public RpcResult networks() {
        return RpcResult.success(this.deployApi.networkInfo());
    }

    @ApiOperation(value = "配置本地网卡")
    @PostMapping(value = "/network")
    public RpcResult addNetworkInfo(@RequestBody NetworkInfoVO query) {
        try {
            // 配置网络信息
            this.deployApi.addNetwork(query);
            // 删除多余网卡配置文件
            this.deployApi.deleteRedundantIfcfgFile(query);
            // 添加dns
            if (Objects.nonNull(query.getOuter())) {
                this.deployApi.addDNS(query.getOuter().getDns1(), query.getOuter().getDns2());
            }
            String result = FuncUtil.runCommand(new String[]{"sh", "-c", "service network restart"},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            log.info("重启 network 服务，{}", result);
            if (result.contains("FAILED") || result.contains("失败")) {
                return RpcResult.fail(result);
            }
            // 配置策略路由
            NetworkInfoVO.NetworkInfo inner = query.getInner();
            if(StrUtil.isNotBlank(inner.getGateway())){
                this.deployApi.addStrategyRoute(query.getInner());
                this.deployApi.addStrategyRouteInFile(query.getInner());
            }
            // 配置网络信息在部署mongodb之前，将网络信息写在文件内
            FuncUtil.runCommand(new String[]{"sh", "-c",
                            String.format("echo '%s' > %s", JSONUtil.toJsonStr(query), Constant.Deploy.NETWORKS_CONFIG_FILE_PATH)},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            this.dataCenterService.updateStep(Constant.DataCenter.STEP_DEPLOY_AUTH_FAIL);
            return RpcResult.success(sm_common.getString("add.success"));
        } catch (Exception e) {
            this.dataCenterService.updateStep(Constant.DataCenter.STEP_NETWORK);
            e.printStackTrace();
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "配置外网网卡")
    @PutMapping(value = "/network")
    public RpcResult editNetworkInfo(@RequestBody NetworkInfoVO query) {
        try {
            // 配置网络信息
            this.deployApi.editNetwork(query);
            // 删除多余网卡配置文件
            this.deployApi.sshDeleteRedundantIfcfgFile(query);
            this.deployApi.sshRestartNetwork(query.getInner());
            NetworkInfoVO.NetworkInfo inner = query.getInner();
            if(StrUtil.isNotBlank(inner.getGateway())){
                this.deployApi.addStrategyRouteSSH(query.getInner());
            }
            return RpcResult.success(sm_common.getString("edit.success"));
        } catch (Exception e) {
            e.printStackTrace();
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "校验节点是否为部署阶段之前的主节点")
    @GetMapping(value = "/network/master")
    public RpcResult checkNodeIfMaster() {
        try {
            String config = FuncUtil.runCommand(new String[]{"sh", "-c", String.format("cat %s", Constant.Deploy.NETWORKS_CONFIG_FILE_PATH)},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            if(StrUtil.isNotBlank(config)){
                return RpcResult.success(JSONUtil.toBean(config, NetworkInfoVO.class));
            }
            return RpcResult.success();
        } catch (Exception e) {
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "获取所有节点的网络配置")
    @GetMapping(value = "/network/config/nodes")
    public RpcResult getNodesNetworkConfig() {
        try {
            return RpcResult.success(this.deployApi.nodesNetworkConfigInfo());
        } catch (Exception e) {
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "获取指定节点的网络配置信息")
    @GetMapping(value = "/network/config/node")
    public RpcResult networkInfoDetail(String nodeName) {
        try {
            List<NetworkConfigDTO> networkConfigDTOS = this.deployApi.nodesNetworkConfigInfo();
            Optional<NetworkConfigDTO> first = networkConfigDTOS.stream().filter(d -> d.getNodeName().equals(nodeName)).findFirst();
            if(first.isPresent()){
                return RpcResult.success(first.get());
            }
            return RpcResult.fail("未找到对应节点");
        } catch (Exception e) {
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "获取wifi列表")
    @GetMapping(value = "/network/wifis")
    public RpcResult getWifis() {
        try {
            // 开启wifi
            FuncUtil.runCommand(new String[]{"sh","-c","nmcli r wifi on"},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            // 扫描wifi
            String result = FuncUtil.runCommand(new String[]{"sh", "-c", "nmcli dev wifi | grep -v SSID | awk '{print $1}'"},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            if(StrUtil.isNotBlank(result)){
                return RpcResult.success(Sets.newHashSet(result.split("\n")));
            }
            return RpcResult.success();
        } catch (Exception e) {
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "添加路由时测试ip是否通")
    @PostMapping(value = "/route/add/check")
    public RpcResult routeAddCheckPing(@RequestBody RouteVo query) {
        this.deployApi.routeAddCheckPing(query);
        return RpcResult.success();
    }

    @ApiOperation(value = "编辑路由时测试ip是否通")
    @PostMapping(value = "/route/edit/check")
    public RpcResult routeEditCheckPing(@RequestBody RouteVo query) {
        this.deployApi.routeEditCheckPing(query);
        return RpcResult.success();
    }

    @ApiOperation(value = "路由管理测试ip是否通")
    @PostMapping(value = "/route/check")
    public RpcResult routeCheckPing(@RequestBody RouteCheckPingVo query) {
        this.deployApi.routeCheckPing(query);
        return RpcResult.success();
    }

    @ApiOperation(value = "路由列表")
    @GetMapping(value = "/route")
    public RpcResult routeList() {
        try{
            return RpcResult.success(this.deployApi.routeList());
        }catch (Exception e){
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "添加路由")
    @PostMapping(value = "/route")
    public RpcResult addRoute(@RequestBody RouteVo query) {
        try{
            this.deployApi.addRoute(query);
            return RpcResult.success(sm_common.getString("add.success"));
        }catch (Exception e){
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "修改路由")
    @PutMapping(value = "/route")
    public RpcResult editRoute(@RequestBody RouteVo query) {
        try{
            this.deployApi.editRoute(query);
            return RpcResult.success(sm_common.getString("edit.success"));
        }catch (Exception e){
            return RpcResult.fail(e.getMessage());
        }
    }

    @ApiOperation(value = "删除路由")
    @DeleteMapping(value = "/route")
    public RpcResult deleteRoute(@RequestBody List<String> ids) {
        try{
            this.deployApi.deleteRoute(ids);
            return RpcResult.success(sm_common.getString("delete.success"));
        }catch (Exception e){
            return RpcResult.fail(e.getMessage());
        }
    }
}
