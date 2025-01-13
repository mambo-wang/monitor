package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.dto.deploy.*;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Set;

/**
 * @Author: w22798
 * @Date: 2022/5/9 9:28
 */
public interface DeployApi {
    List<DeployVO> queryAll();


    /**
     * 批量部署
     * @param batchDeployVO
     */
    void deploy(BatchDeployVO batchDeployVO);

    String queryMasterIp();

    Set<String> queryIps();

    /**
     * 单节点部署
     * @param deployVO
     */
    void deployMaster(DeployVO deployVO);

    @SneakyThrows
    void restartService(SSHHost sshHost, String component);

    @SneakyThrows
    void startupService(SSHHost sshHost, String component);

    @SneakyThrows
    void shutdownService(SSHHost sshHost, String component);

    /**
     * 查询状态
     * @return
     */
    List<DeployQueryVO> queryStatus();

    @SneakyThrows
    Component queryStatus(SSHHost sshHost, String watcherHome, String component);

    /**
     * 组件管理
     * @param componentManage
     */
    void componentsManage(ComponentManage componentManage);

    @SneakyThrows
    String queryWatcherHome(SSHHost sshHost);

    void handleHealthCheck();

    /**
     * 当前节点是否为主节点
     * @return 是:true 否:false
     */
    boolean currentNodeIsMaster();

    String queryHost();


    /**
     * 查询采集端程序家目录
     * @return 程序家目录
     */
    String queryWatcherHome();

    /**
     * 仅查询agent的状态
     * @return
     */
    Boolean handleHealthCheckForUp();

    void addNetwork(NetworkInfoVO query);
    void editNetwork(NetworkInfoVO query) throws Exception;
    void sshRestartNetwork(NetworkInfoVO.NetworkInfo inner);
    void deleteRedundantIfcfgFile(NetworkInfoVO query);
    void sshDeleteRedundantIfcfgFile(NetworkInfoVO query);
    List<NetworkVO> networkInfo();
    List<NetworkConfigDTO> nodesNetworkConfigInfo();

    void addDNS(String... dns);

    boolean routeAddCheckPing(RouteVo query);
    boolean routeEditCheckPing(RouteVo query);
    void routeCheckPing(RouteCheckPingVo query);
    List<RouteVo> routeList();
    void addRoute(RouteVo query);
    void editRoute(RouteVo query);
    void deleteRoute(List<String> ids);

    void addStrategyRoute(NetworkInfoVO.NetworkInfo inner);
    void addStrategyRouteSSH(NetworkInfoVO.NetworkInfo inner);
    void addStrategyRouteInFile(NetworkInfoVO.NetworkInfo inner);
}
