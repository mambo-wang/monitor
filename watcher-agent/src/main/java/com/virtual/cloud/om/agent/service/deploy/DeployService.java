package com.virtual.cloud.om.agent.service.deploy;

import com.virtual.cloud.om.sdk.api.DataCenterApi;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.api.TaskMgrApi;
import com.virtual.cloud.om.sdk.constant.ComponentEnum;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.dto.deploy.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 部署服务 - MySQL 单机版
 * MongoDB 版本已禁用，使用简化实现
 * @Author: w22798
 */
@Service("deployApi")
@Slf4j
public class DeployService implements DeployApi {

    @Autowired(required = false)
    private ParameterApi parameterApi;

    @Autowired(required = false)
    private TaskMgrApi taskMgrApi;

    @Autowired(required = false)
    private RealTimeLogApi realTimeLogApi;

    @Autowired(required = false)
    private DataCenterApi dataCenterApi;

    @Override
    public List<DeployVO> queryAll() {
        log.debug("[DeployService] 查询所有部署已禁用");
        return Collections.emptyList();
    }

    @Override
    public void deploy(BatchDeployVO batchDeployVO) {
        log.warn("[DeployService] 部署功能已禁用");
    }

    @Override
    public String queryMasterIp() {
        log.debug("[DeployService] 查询主节点IP已禁用");
        return null;
    }

    @Override
    public Set<String> queryIps() {
        log.debug("[DeployService] 查询IP列表已禁用");
        return Collections.emptySet();
    }

    @Override
    public void deployMaster(DeployVO deployVO) {
        log.warn("[DeployService] 主节点部署功能已禁用");
    }

    @Override
    public void restartService(SSHHost sshHost, String component) {
        log.warn("[DeployService] 重启服务功能已禁用");
    }

    @Override
    public void startupService(SSHHost sshHost, String component) {
        log.warn("[DeployService] 启动服务功能已禁用");
    }

    @Override
    public void shutdownService(SSHHost sshHost, String component) {
        log.warn("[DeployService] 关闭服务功能已禁用");
    }

    @Override
    public List<DeployQueryVO> queryStatus() {
        log.debug("[DeployService] 查询状态功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public Component queryStatus(SSHHost sshHost, String watcherHome, String component) {
        log.debug("[DeployService] 查询状态功能已禁用");
        return null;
    }

    @Override
    public void componentsManage(ComponentManage componentManage) {
        log.warn("[DeployService] 组件管理功能已禁用");
    }

    @Override
    public String queryWatcherHome(SSHHost sshHost) {
        log.debug("[DeployService] 查询Watcher目录功能已禁用");
        return null;
    }

    @Override
    public void handleHealthCheck() {
        log.debug("[DeployService] 健康检查功能已禁用");
    }

    @Override
    public boolean currentNodeIsMaster() {
        log.debug("[DeployService] 判断主节点功能已禁用");
        return false;
    }

    @Override
    public String queryHost() {
        log.debug("[DeployService] 查询主机功能已禁用");
        return null;
    }

    @Override
    public String queryWatcherHome() {
        log.debug("[DeployService] 查询Watcher目录功能已禁用");
        return null;
    }

    @Override
    public Boolean handleHealthCheckForUp() {
        log.debug("[DeployService] 健康检查功能已禁用");
        return false;
    }

    @Override
    public void addNetwork(NetworkInfoVO query) {
        log.warn("[DeployService] 添加网络功能已禁用");
    }

    @Override
    public void editNetwork(NetworkInfoVO query) throws Exception {
        log.warn("[DeployService] 编辑网络功能已禁用");
    }

    @Override
    public void sshRestartNetwork(NetworkInfoVO.NetworkInfo inner) {
        log.warn("[DeployService] SSH重启网络功能已禁用");
    }

    @Override
    public void deleteRedundantIfcfgFile(NetworkInfoVO query) {
        log.warn("[DeployService] 删除多余网络配置文件功能已禁用");
    }

    @Override
    public void sshDeleteRedundantIfcfgFile(NetworkInfoVO query) {
        log.warn("[DeployService] SSH删除多余网络配置文件功能已禁用");
    }

    @Override
    public List<NetworkVO> networkInfo() {
        log.debug("[DeployService] 查询网络信息功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public List<NetworkConfigDTO> nodesNetworkConfigInfo() {
        log.debug("[DeployService] 查询节点网络配置功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public void addDNS(String... dns) {
        log.warn("[DeployService] 添加DNS功能已禁用");
    }

    @Override
    public boolean routeAddCheckPing(RouteVo query) {
        log.debug("[DeployService] 路由添加检查功能已禁用");
        return false;
    }

    @Override
    public boolean routeEditCheckPing(RouteVo query) {
        log.debug("[DeployService] 路由编辑检查功能已禁用");
        return false;
    }

    @Override
    public void routeCheckPing(RouteCheckPingVo query) {
        log.warn("[DeployService] 路由检查功能已禁用");
    }

    @Override
    public List<RouteVo> routeList() {
        log.debug("[DeployService] 查询路由列表功能已禁用");
        return Collections.emptyList();
    }

    @Override
    public void addRoute(RouteVo query) {
        log.warn("[DeployService] 添加路由功能已禁用");
    }

    @Override
    public void editRoute(RouteVo query) {
        log.warn("[DeployService] 编辑路由功能已禁用");
    }

    @Override
    public void deleteRoute(List<String> ids) {
        log.warn("[DeployService] 删除路由功能已禁用");
    }

    @Override
    public void addStrategyRoute(NetworkInfoVO.NetworkInfo inner) {
        log.warn("[DeployService] 添加策略路由功能已禁用");
    }

    @Override
    public void addStrategyRouteSSH(NetworkInfoVO.NetworkInfo inner) {
        log.warn("[DeployService] SSH添加策略路由功能已禁用");
    }

    @Override
    public void addStrategyRouteInFile(NetworkInfoVO.NetworkInfo inner) {
        log.warn("[DeployService] 文件中添加策略路由功能已禁用");
    }
}
