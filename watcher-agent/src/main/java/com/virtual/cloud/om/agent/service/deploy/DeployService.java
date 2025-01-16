package com.virtual.cloud.om.agent.service.deploy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jcraft.jsch.JSchException;
import com.virtual.cloud.om.agent.dto.WebsocketWatcherRouteOperateResult;
import com.virtual.cloud.om.agent.dto.WebsocketWatcherRouteQueryResult;
import com.virtual.cloud.om.agent.entity.*;
import com.virtual.cloud.om.agent.repository.DeployRepository;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.config.kafka.KafkaConsole;
import com.virtual.cloud.om.sdk.constant.ComponentEnum;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.TaskDTO;
import com.virtual.cloud.om.sdk.dto.deploy.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.*;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 采集节点管理
 * @Author: w22798
 * @Date: 2022/4/26 20:00
 */
@Service("deployApi")
@Slf4j
@SuppressWarnings("all")
public class DeployService implements DeployApi, ApplicationRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DeployRepository deployRepository;

    @Autowired
    private ParameterApi parameterApi;

    @Autowired
    private TaskMgrApi taskMgrApi;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ClusterService clusterService;



    private RealTimeLogApi realTimeLogApi;

    @Autowired
    public void setRealTimeLogApi(RealTimeLogApi realTimeLogApi) {
        this.realTimeLogApi = realTimeLogApi;
    }

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private DataCenterApi dataCenterApi;

    @Autowired
    private KafkaConsole kafkaConsole;

    private StringManager sm = StringManager.getManager("Common");

    @Autowired
    public void setDataCenterApi(DataCenterApi dataCenterApi) {
        this.dataCenterApi = dataCenterApi;
    }


    /**
     * 保存节点信息到数据库
     *
     * @param deployVO 节点信息
     */
    private void saveDeployInfo(DeployVO deployVO) {

        Query query = new Query(Criteria.where("ip").is(deployVO.getIp()));
        boolean exists = mongoTemplate.exists(query, Deploy.class);
        if (exists) {
            Update update = new Update()
                    .set("username", deployVO.getUsername())
                    .set("password", deployVO.getPassword())
                    .set("isMaster", deployVO.getIsMaster());
            mongoTemplate.updateFirst(query, update, Deploy.class);
        } else {
            Deploy deploy = new Deploy();
            BeanUtils.copyProperties(deployVO, deploy);
            mongoTemplate.save(deploy);
        }
    }

    /**
     * 查询所有节点信息(搭建成功的节点)
     *
     * @return 节点信息
     */
    @Override
    public List<DeployVO> queryAll() {
        List<Deploy> deploys = mongoTemplate.findAll(Deploy.class);
        Set<String> localIps = IpUtil.queryLocalIps();
        Optional<String> clusterResult = parameterApi.queryParameterByTypeAndName(Constant.Parameter.SYS_CONF, Constant.Parameter.NAME_CLUSTER_RESULT);
        boolean clusterSuccess = clusterResult.isPresent() && clusterResult.get().equals(Constant.Parameter.NAME_CLUSTER_RESULT_SUCCESS);

        return deploys.stream()
                .filter(d -> clusterSuccess || localIps.contains(d.getIp()))
                .map(deploy -> {
                    DeployVO deployVO = new DeployVO();
                    BeanUtils.copyProperties(deploy, deployVO);
                    return deployVO;
                }).collect(Collectors.toList());
    }

    private Collection<String> queryIps(DeployVO deployVO) {
        try {
            String url = "http://" + deployVO.getIp() + ":" + serverPort + contextPath + "/deploy/localIps";
            RpcListLoadResult<String> result = restTemplate.getForEntity(url, RpcListLoadResult.class).getBody();
            Utils.checkResult(url, result);
            return result.getData();
        } catch (Exception e) {
            log.error("[deploy] query ips of {} fail", deployVO.getIp(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public String queryMasterIp() {
        Query query = new Query(Criteria.where("isMaster").is(true));
        Deploy deploy = mongoTemplate.findOne(query, Deploy.class);
        return Optional.ofNullable(deploy).map(Deploy::getIp).orElseThrow(() -> new AppException(ErrorCodes.DEPLOY_NOT_COMPLETED));
    }

    @Override
    public Set<String> queryIps() {

        return mongoTemplate.findAll(Deploy.class).stream().map(Deploy::getIp).collect(Collectors.toSet());
    }

    /**
     * 单节点部署
     *
     * @param deployVO 节点信息
     */
    @Override
    @SneakyThrows
    public void deployMaster(DeployVO deployVO) {

        checkValid(deployVO);

        //修改/etc/hosts ip，修改kafka server.properties ip
        String masterRemoveHosts = "sed -i '/watcher/d' /etc/hosts";
        SSHHost sshHost = SSHHost.newInstance(deployVO.getIp(), deployVO.getUsername(), SM4Utils.webDecryptText(deployVO.getPassword()));
        String masterRemoveHostResult = SSHTools.execute(sshHost, masterRemoveHosts);
        log.info("[deploy] remove master hosts result is {}", masterRemoveHostResult);

        String masterHost001 = "sed -i '$a %s watcher001 watcher001' /etc/hosts";
        String cmdWatcher001 = String.format(masterHost001, deployVO.getIp());
        String masterModifyHostResult = SSHTools.execute(sshHost, cmdWatcher001);
        log.info("[deploy] modify master hosts result is {}", masterModifyHostResult);

        modifyKafkaListeners(sshHost, deployVO);

        saveDeployInfo(deployVO);

        // 保存网络配置
        this.queryAndSaveNodeNetworkConfig(deployVO);
    }

    private void checkValid(DeployVO master) {
        Query query = new Query(Criteria.where("isMaster").is(true));
        Deploy deploy = mongoTemplate.findOne(query, Deploy.class);
        if (Objects.nonNull(deploy) && !StringUtils.equals(master.getIp(), deploy.getIp())) {
            throw new AppException(ErrorCodes.DEPLOY_ALREADY_CONFIG);
        }

        //校验本机是否有该配置的ip
        Set<String> localIps = IpUtil.queryLocalIps();
        if (!localIps.contains(master.getIp())) {
            throw new AppException(ErrorCodes.MASTER_IP_ERROR);
        }

    }

    /**
     * 多节点部署
     *
     * @param batchDeployVO 节点信息
     */
    @Override
    @SneakyThrows
    public void deploy(BatchDeployVO batchDeployVO) {

        checkValid(batchDeployVO);

        //3、保存配置
        batchDeployVO.getNodes().forEach(this::saveDeployInfo);
        parameterApi.editParamByTypeAndName(batchDeployVO.getVip(), Parameter.SYS_CONF, Parameter.NAME_VIP);

        //2、节点部署
        try {
            clusterService.deployCluster(batchDeployVO);
            parameterApi.editParamByTypeAndName(Constant.Parameter.NAME_CLUSTER_RESULT_SUCCESS, Parameter.SYS_CONF, Parameter.NAME_CLUSTER_RESULT);
        } catch (Exception e) {
            log.error("[deploy] cluster fail", e);
            parameterApi.editParamByTypeAndName(Constant.Parameter.NAME_CLUSTER_RESULT_FAILURE, Parameter.SYS_CONF, Parameter.NAME_CLUSTER_RESULT);
            throw e;
        }

        // 保存网络配置
        batchDeployVO.getNodes().forEach(this::queryAndSaveNodeNetworkConfig);
    }

    private void queryAndSaveNodeNetworkConfig(DeployVO vo){
        try{
            String networkConfig = SSHTools.execute(SSHHost.newInstance(vo), String.format("cat %s", Constant.Deploy.NETWORKS_CONFIG_FILE_PATH));
            this.saveNodeNetworkConfig(JSONUtil.toBean(networkConfig, NetworkInfoVO.class));
        }catch (Exception e){
            // 理论上是不会存在这个异常情况的
            log.error("[deploy] save network config fail",e);
        }
    }

    private void saveNodeNetworkConfig(NetworkInfoVO networkInfo) {
        NetworkInfoVO.NetworkInfo inner = networkInfo.getInner();
        NetworkInfoVO.NetworkInfo outer = networkInfo.getOuter();
        NetworkConfig.NetworkConfigBuilder builder = NetworkConfig.builder();
        builder.nodeName(inner.getIp()).innerIp(inner.getIp()).createTime(System.currentTimeMillis())
                .innerMask(inner.getMask()).innerGateway(inner.getGateway())
                .innerAllocation(inner.getAllocation().name()).innerName(inner.getName());
        if (Objects.nonNull(outer)) {
            builder.outerIp(outer.getIp()).outerMask(outer.getMask()).outerGateway(outer.getGateway())
                    .dns1(outer.getDns1()).dns2(outer.getDns2())
                    .outerAllocation(outer.getAllocation().name()).outerName(outer.getName())
                    .outerWifi(StrUtil.isNotBlank(outer.getWifi()) ? outer.getWifi() : Strings.EMPTY)
                    .outerWifiPwd(StrUtil.isNotBlank(outer.getWifiPwd()) ? outer.getWifiPwd() : Strings.EMPTY);
        }
        builder.updateTime(System.currentTimeMillis());
        this.mongoTemplate.save(builder.build());
    }

    private void editNodeNetworkConfig(NetworkInfoVO networkInfo) {
        NetworkInfoVO.NetworkInfo inner = networkInfo.getInner();
        this.mongoTemplate.findAll(NetworkConfig.class).stream().filter(nc->nc.getNodeName().equals(inner.getIp())).findFirst().ifPresent(nc->{
            nc.setInnerMask(inner.getMask());
            nc.setInnerGateway(inner.getGateway());
            String nodeName = networkInfo.getNodeName();
            NetworkInfoVO.NetworkInfo outer = networkInfo.getOuter();
            if(Objects.nonNull(outer)){
                nc.setOuterIp(outer.getIp());
                nc.setOuterMask(outer.getMask());
                nc.setOuterGateway(outer.getGateway());
                nc.setDns1(outer.getDns1());
                nc.setDns2(outer.getDns2());
                nc.setOuterAllocation(outer.getAllocation().name());
                nc.setOuterName(outer.getName());
                nc.setOuterWifi(StrUtil.isNotBlank(outer.getWifi()) ? outer.getWifi() : Strings.EMPTY);
                nc.setOuterWifiPwd(StrUtil.isNotBlank(outer.getWifiPwd()) ? outer.getWifiPwd() : Strings.EMPTY);
            }else{
                nc.setOuterIp(Strings.EMPTY);
                nc.setOuterMask(Strings.EMPTY);
                nc.setOuterGateway(Strings.EMPTY);
                nc.setDns1(Strings.EMPTY);
                nc.setDns2(Strings.EMPTY);
                nc.setOuterAllocation(Strings.EMPTY);
                nc.setOuterName(Strings.EMPTY);
                nc.setOuterWifi(Strings.EMPTY);
                nc.setOuterWifiPwd(Strings.EMPTY);
            }
            nc.setUpdateTime(System.currentTimeMillis());
            this.mongoTemplate.save(nc);
        });
    }

    @SneakyThrows
    private void checkValid(BatchDeployVO batchDeployVO) {

        //校验是否已部署
        Optional<String> vip = parameterApi.queryParameterByTypeAndName(Parameter.SYS_CONF, Parameter.NAME_VIP);
        if (vip.isPresent() && !StringUtils.equals(batchDeployVO.getVip(), vip.get())) {
            throw new AppException(ErrorCodes.DEPLOY_ALREADY_CONFIG);
        }

        //校验本机是否有该配置的ip
        Set<String> localIps = IpUtil.queryLocalIps();
        if (!localIps.contains(batchDeployVO.getMasterNode().getIp())) {
            throw new AppException(ErrorCodes.MASTER_IP_ERROR);
        }

        //检查是否修改了从节点IP
        Set<String> ips = mongoTemplate.findAll(Deploy.class).stream().map(Deploy::getIp).collect(Collectors.toSet());
        Set<String> ipsToSave = batchDeployVO.getNodes().stream().map(DeployVO::getIp).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(ips) && !ipsToSave.containsAll(ips)) {
            throw new AppException(ErrorCodes.DEPLOY_ALREADY_CONFIG);
        }

        //校验vip是否可达
        boolean reachable = IpUtil.isIpReachable(batchDeployVO.getVip());
        if (!vip.isPresent() && reachable) {
            throw new AppException(ErrorCodes.DEPLOY_VIP_USED);
        }

        //检查从节点是否可以正常连接
        try {
            SSHTools.createSession(batchDeployVO.getSlave1Node()).disconnect();
        } catch (JSchException e) {
            throw new AppException(ErrorCodes.DEPLOY_NODE_CONNECT_FAIL, batchDeployVO.getSlave1Node().getIp());
        }
        try {
            SSHTools.createSession(batchDeployVO.getSlave2Node()).disconnect();
        } catch (JSchException e) {
            throw new AppException(ErrorCodes.DEPLOY_NODE_CONNECT_FAIL, batchDeployVO.getSlave1Node().getIp());
        }

        //检查vip和master ip以及backup ip是否在同一个网段
        boolean match = batchDeployVO.getNodes().stream().allMatch(deployVO -> IpUtil.isInOneRange(deployVO.getIp(), batchDeployVO.getVip(), batchDeployVO.getMask()));
        if (!match) {
            throw new AppException(ErrorCodes.NET_NOT_IN_RANGE);
        }

        //检查子网掩码是否合法：
        IpUtil.convertPointToNumber(batchDeployVO.getMask());
    }

    public void modifyKafkaListeners(String ip) {
        Query query = new Query(Criteria.where("ip").is(ip));
        Deploy deploy = mongoTemplate.findOne(query, Deploy.class);
        if (Objects.isNull(deploy)) {
            return;
        }
        SSHHost sshHost = SSHHost.newInstance(deploy.getIp(), deploy.getUsername(), SM4Utils.webDecryptText(deploy.getPassword()));

        DeployVO deployVO = new DeployVO();
        BeanUtils.copyProperties(deploy, deployVO);
        modifyKafkaListeners(sshHost, deployVO);
    }

    @SneakyThrows
    private void modifyKafkaListeners(SSHHost sshHost, DeployVO deployVO) {
        String watcherHome = queryWatcherHome(sshHost);

        String cmdConf = "sed -i 's#watcher001#%s#g' " + watcherHome + "/components/kafka/kafka_2.12-3.0.0/config/server.properties";
        String cmdModifyKafkaConf = String.format(cmdConf, deployVO.getIp());
        SSHTools.execute(sshHost, cmdModifyKafkaConf);

        restartService(sshHost, Constant.Deploy.SERVICE_NAME_KAFKA);

        //创建topic，监听
        realTimeLogApi.createAndConsumeFilebeatLogTopic(2, 1);
    }

    @SneakyThrows
    @Override
    public void restartService(SSHHost sshHost, String component) {

        log.info("[deploy] component {} restart", component);

        if (StringUtils.equals(component, "主机")) {
            restartHost(sshHost);
            return;
        }
        String watcherHome = queryWatcherHome(sshHost);
        log.info("[deploy] get watcher home result {}", watcherHome);
        String shutdownResult = SSHTools.executeNoException(sshHost, "service " + component + " restart");
        log.info("[deploy] get restart {} result {}", component, shutdownResult);
    }

    @SneakyThrows
    private void restartHost(SSHHost sshHost) {
        String shutdownResult = SSHTools.execute(sshHost, "reboot");
        log.info("[deploy] reboot result {}", shutdownResult);
    }

    @SneakyThrows
    @Override
    public void startupService(SSHHost sshHost, String component) {

        log.info("[deploy] component {} startup", component);

        if (StringUtils.equals(component, "主机")) {
            throw new AppException(ErrorCodes.OPERATE_NOT_SUPPORTED);
        }

        String startupResult = SSHTools.execute(sshHost, "service " + component + " restart");
        log.info("[deploy] get startupResult result {}", startupResult);
    }

    @SneakyThrows
    @Override
    public void shutdownService(SSHHost sshHost, String component) {

        log.info("[deploy] component {} shutdown", component);

        if (StringUtils.equals(component, "主机")) {
            shutdownHost(sshHost);
            return;
        }

        String shutdownResult = SSHTools.execute(sshHost, "service " + component + " stop");
        log.info("[deploy] get shutdownResult result {}", shutdownResult);
    }

    @SneakyThrows
    private void shutdownHost(SSHHost sshHost) {
        String shutdownResult = SSHTools.execute(sshHost, "shutdown -h now");
        log.info("[deploy] shutdown result {}", shutdownResult);
    }

    /**
     * 查询节点状态
     *
     * @return 节点及组件状态
     */
    @Override
    public List<DeployQueryVO> queryStatus() {
        List<DeployVO> deployVOS = queryAll();
        ForkJoinPool customThreadPool = new ForkJoinPool(3);
        List<DeployQueryVO> deployQueryVOS = customThreadPool.submit(() ->
                deployVOS.parallelStream().map(this::queryStatus).collect(Collectors.toList())).fork().join();
        return deployQueryVOS;
    }

    /**
     * 查询组件状态
     *
     * @param deployVO 节点信息
     * @return 组件状态
     */
    @SneakyThrows
    private DeployQueryVO queryStatus(DeployVO deployVO) {
        DeployQueryVO deployQueryVO1 = new DeployQueryVO();
        BeanUtils.copyProperties(deployVO, deployQueryVO1);

        List<Component> components = new ArrayList<>();
        //主机状态异常
        boolean isHostReachable = IpUtil.isHostReachable(deployVO.getIp(), (int) TimeUnit.SECONDS.toMillis(5));
        if (!isHostReachable) {
            components.add(Component.builder().name("主机").status(Constant.Deploy.STATUS_UNKNOWN).detail("连接失败").build());
            deployQueryVO1.setComponents(components);
            return deployQueryVO1;
        }

        //没有正确安装软件
        SSHHost sshHost = SSHHost.newInstance(deployVO.getIp(), deployVO.getUsername(), SM4Utils.webDecryptText(deployVO.getPassword()));
        String watcherHome = queryWatcherHome(sshHost);
        if (StringUtils.isEmpty(watcherHome) || watcherHome.contains("No such file or directory") || watcherHome.contains("没有那个文件或目录")) {
            components.add(Component.builder().name("能力中心Agent服务").status(Constant.Deploy.STATUS_UNKNOWN).detail("没有正确安装").build());
            deployQueryVO1.setComponents(components);
            return deployQueryVO1;
        }
        Component agentStatus = queryWatcherStatus(sshHost, watcherHome);
        components.add(agentStatus);
        components.add(queryStatus(sshHost, watcherHome, Constant.Deploy.SERVICE_NAME_NGINX));
        components.add(queryStatus(sshHost, watcherHome, Constant.Deploy.SERVICE_NAME_KAFKA));
        components.add(queryStatus(sshHost, watcherHome, Constant.Deploy.SERVICE_NAME_MONGODB));
        components.add(queryStatus(sshHost, watcherHome, Constant.Deploy.SERVICE_NAME_ZOOKEEPER));

        deployQueryVO1.setComponents(components);

        Optional<String> vipOp = parameterApi.queryParameterByTypeAndName(Parameter.SYS_CONF, Parameter.NAME_VIP);
        Optional<String> result = parameterApi.queryParameterByTypeAndName(Parameter.SYS_CONF, Constant.Parameter.NAME_CLUSTER_RESULT);
        vipOp.ifPresent(vip -> {
            if (result.isPresent() && result.get().equals(Constant.Parameter.NAME_CLUSTER_RESULT_SUCCESS)) {
                components.add(queryStatus(sshHost, watcherHome, Constant.Deploy.SERVICE_NAME_KEEPALIVED));
            }
            if (agentStatus.isRunning() && queryIps(deployVO).contains(vip)) {
                deployQueryVO1.setVip(vip);
            }
        });
        return deployQueryVO1;
    }

    @Override
    @SneakyThrows
    public Component queryStatus(SSHHost sshHost, String watcherHome, String component) {

        String nginxStatus = SSHTools.execute(sshHost, "bash " + watcherHome + "/components/" + component + "/status.sh");
        if (nginxStatus.contains("running")) {
            return Component.builder().name(component).status(Constant.Deploy.STATUS_STARTUP).detail(sm.getString("components.status.running")).build();
        } else if (nginxStatus.contains("shutdown")) {
            return Component.builder().name(component).status(Constant.Deploy.STATUS_SHUTDOWN).detail(sm.getString("components.status.stopped")).build();
        } else {
            return Component.builder().name(component).status(Constant.Deploy.STATUS_UNKNOWN).detail(sm.getString("components.status.unknown")).build();
        }

    }

    @SneakyThrows
    private Component queryWatcherStatus(SSHHost sshHost, String watcherHome) {
        String nginxStatus = SSHTools.execute(sshHost, "bash " + watcherHome + "/bin/status.sh");
        if (nginxStatus.contains("running")) {
            return Component.builder().name(Constant.Deploy.SERVICE_NAME_AGENT).status(Constant.Deploy.STATUS_STARTUP).detail(sm.getString("components.status.running")).build();
        } else if (nginxStatus.contains("shutdown")) {
            return Component.builder().name(Constant.Deploy.SERVICE_NAME_AGENT).status(Constant.Deploy.STATUS_SHUTDOWN).detail(sm.getString("components.status.stopped")).build();
        } else {
            return Component.builder().name(Constant.Deploy.SERVICE_NAME_AGENT).status(Constant.Deploy.STATUS_UNKNOWN).detail(sm.getString("components.status.unknown")).build();
        }

    }

    @Override
    public void componentsManage(ComponentManage componentManage) {
        Deploy deploy = deployRepository.findByIp(componentManage.getIp());
        SSHHost sshHost = SSHHost.newInstance(deploy.getIp(), deploy.getUsername(), SM4Utils.webDecryptText(deploy.getPassword()));

        String operate = componentManage.getOperate();
        String name = componentManage.getName();

        switch (operate) {
            case Constant.OPERATE_STARTUP:
                startupService(sshHost, name);
                break;
            case Constant.OPERATE_RESTART:
                restartService(sshHost, name);
                break;
            case Constant.OPERATE_SHUTDOWN:
                shutdownService(sshHost, name);
                break;
            default:
                break;
        }
    }

    @SneakyThrows
    @Override
    public String queryWatcherHome(SSHHost sshHost) {
        String watcherHome = SSHTools.execute(sshHost, "cat /etc/watcher_home");
        watcherHome = watcherHome.replace("\n", "");
        return watcherHome;
    }

    public void addFilebeatCheckTask() {
        //每30分钟执行一次所有指定主机Filebeat的运行状态检查。
        TaskDTO taskFromDB = taskMgrApi.queryByName(Task.TASK_FILEBEAT_CHECK);

        if (Objects.nonNull(taskFromDB)) {
            taskMgrApi.delete(taskFromDB);
        }

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(Task.TASK_FILEBEAT_CHECK);
        taskDTO.setTaskName(Task.TASK_FILEBEAT_CHECK);
        taskDTO.setTaskType(Task.TASK_FILEBEAT_CHECK);
        taskDTO.setCycleType(Task.CYCLE_TYPE_MINUTES);
        taskDTO.setCycleDay(30);
        taskDTO.setAvailableStartTime(System.currentTimeMillis());
        taskDTO.setDescription("Filebeat运行状态检查任务");
        taskDTO.setCreatedTime(Utils.formatFullDateTime(System.currentTimeMillis()));
        taskMgrApi.addTask(taskDTO);
    }


    @Override
    public void handleHealthCheck() {
        //只在当前主节点执行健康检查任务
        if (!currentNodeIsMaster()) {
            return;
        }
        log.info("[deploy][health-check] start now!");
        long time = System.currentTimeMillis();

        realTimeLogApi.clearCache();

        List<DeployQueryVO> deployQueryVOS = queryStatus();

        deployQueryVOS.forEach(deployQueryVO -> {
            deployQueryVO.getComponents().forEach(component -> {
                if (component.getStatus().equals(Constant.Deploy.STATUS_SHUTDOWN)) {
                    log.info("[deploy][health-check] component {} startup", component.getName());
                    if(component.getName().equalsIgnoreCase(ComponentEnum.kafka.name())){
                        String cmd = "rm -rf " + queryWatcherHome(SSHHost.newInstance(deployQueryVO)) + "/components/kafka/kafka_2.12-3.0.0/logs/";
                        SSHTools.executeNoException(SSHHost.newInstance(deployQueryVO), cmd);
                    }
                    startupService(deployQueryVO, component);
                }
            });
        });
        //判断部署完成了，补刀创建主题topic
        createIfNotExistTopic();
        log.info("[deploy][health-check] end! time = {}ms", (System.currentTimeMillis() - time));
    }

    private void createIfNotExistTopic(){
        try {
            if (dataCenterApi.getInitStep() >= Constant.DataCenter.STEP_DEPLOY) {
                Optional<String> vip = parameterApi.queryParameterByTypeAndName(Constant.Parameter.SYS_CONF, Constant.Parameter.NAME_VIP);
                if (vip.isPresent()) {
                    realTimeLogApi.createAndConsumeFilebeatLogTopic(6, 2);
                } else {
                    realTimeLogApi.createAndConsumeFilebeatLogTopic(2, 1);
                }
            }
        } catch (Exception e) {
            log.error("[deploy] create kafka topic fail", e);
        }
    }

    @Override
    public boolean currentNodeIsMaster() {
        Optional<String> vipOp = parameterApi.queryParameterByTypeAndName(Parameter.SYS_CONF, Parameter.NAME_VIP);
        //单节点部署，当前节点肯定为主节点
        if (!vipOp.isPresent()) {
            return true;
        }
        //多节点部署，ip列表中包含vip为主节点
        Set<String> localIps = IpUtil.queryLocalIps();
        return localIps.contains(vipOp.get());
    }

    @Override
    public String queryWatcherHome() {
        Set<String> localIps = IpUtil.queryLocalIps();
        Deploy currentNode = mongoTemplate.findAll(Deploy.class).stream().filter(d -> localIps.contains(d.getIp())).findAny().get();
        SSHHost sshHost = SSHHost.newInstance(currentNode.getIp(), currentNode.getUsername(), SM4Utils.webDecryptText(currentNode.getPassword()));
        return queryWatcherHome(sshHost);
    }

    @Override
    public String queryHost() {
        Set<String> localIps = IpUtil.queryLocalIps();
        return queryAll().stream()
                .filter(d -> !localIps.contains(d.getIp()))
                .map(deployVO -> SSHHost.newInstance(deployVO.getIp(), deployVO.getUsername(), SM4Utils.webDecryptText(deployVO.getPassword())))
                .map(d -> d.getIp() + "," + d.getUser() + "," + d.getPassword())
                .collect(Collectors.joining(" "));
    }

    private void startupService(DeployQueryVO deployQueryVO, Component component) {
        try {
            SSHHost sshHost = SSHHost.newInstance(deployQueryVO.getIp(), deployQueryVO.getUsername(), SM4Utils.webDecryptText(deployQueryVO.getPassword()));
            startupService(sshHost, component.getName());
        } catch (Exception e) {
            log.error("[deploy][health-check] component {} startup fail", component.getName(), e);
        }
    }

    /**
     * 容器启动后执行方法
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        addFilebeatCheckTask();
    }

    /**
     * 设置 NeworkManager 服务开机自启动
     */
    private void networkManagerEnable(){
        FuncUtil.runCommand(new String[]{"sh","-c","systemctl enable NetworkManager"},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
    }

    @Override
    public Boolean handleHealthCheckForUp() {
        //只在当前主节点执行健康检查任务
        List<DeployQueryVO> deployQueryVOS = queryStatus();
        for (int i = 0; i < deployQueryVOS.size(); i++) {
            boolean ipReachable = IpUtil.isIpReachable(deployQueryVOS.get(i).getIp());
            if (!ipReachable){
                return ipReachable;
            }
        }
        return true;
    }

    final String namePrefix = "NAME";
    final String devicePrefix = "DEVICE";
    final String uuidPrefix = "UUID";
    final String typePrefix = "TYPE";
    final String ipPrefix = "IPADDR";
    final String maskPrefix = "NETMASK";
    final String gatewayPrefix = "GATEWAY";
    final String bootprotoPrefix = "BOOTPROTO";
    final String macPrefix = "MACADDR";

    // 因集群部署时会清空mongodb的数据，所以在添加网卡配置时保存在一个文件中
    // 当开始部署后由主节点将其他节点的文件内容读取出，统一存入mongodb中保存
    @Override
    public void addNetwork(NetworkInfoVO query) {
        List<NetworkVO> networkVOS = this.networkInfo();
        List<String> networkNames = networkVOS.stream().map(NetworkVO::getName).collect(Collectors.toList());
        NetworkInfoVO.NetworkInfo inner = query.getInner();
        NetworkInfoVO.NetworkInfo outer = query.getOuter();
        String innerName = inner.getName();
        if(!networkNames.contains(innerName)){
            throw new AppException(ErrorCodes.DEPLOY_NETWORK_NOT_EXIST,innerName);
        }
        {

            List<String> lines = this.getIfcfgFileLines(innerName);
            if (CollUtil.isEmpty(lines)) {
                lines = Arrays.stream(Constant.Deploy.IFCFG_FILE_CONTENT_DEMO.split("\n")).collect(Collectors.toList());
            }
            lines.add(new StringBuilder(typePrefix).append("=").append("Ethernet").toString());
            lines.add(new StringBuilder(bootprotoPrefix).append("=").append("static").toString());
            lines.add(new StringBuilder(ipPrefix).append("=").append(inner.getIp()).toString());
            lines.add(new StringBuilder(maskPrefix).append("=").append(inner.getMask()).toString());
            lines.add(new StringBuilder(namePrefix).append("=").append(innerName).toString());
            lines.add(new StringBuilder(devicePrefix).append("=").append(innerName).toString());
            lines.add(new StringBuilder(macPrefix).append("=").append(this.getMacByNetworkName(innerName)).toString());
            this.addNetworkEchoIfcfgFile(innerName, lines);
        }
        // 配置外网网卡
        if (Objects.nonNull(outer)) {
            String outerName = outer.getName();
            if(!networkNames.contains(outerName)){
                throw new AppException(ErrorCodes.DEPLOY_NETWORK_NOT_EXIST,outerName);
            }
            // 配置外网网卡和内网网卡是同一个网卡
            if (outerName.equals(innerName)) {
                String vOuterName = new StringBuilder(innerName).append(":1").toString();
                List<String> lines = this.getIfcfgFileLines(innerName);
                lines.add(new StringBuilder(typePrefix).append("=").append("Ethernet").toString());
                lines.add(new StringBuilder(bootprotoPrefix).append("=").append("static").toString());
                lines.add(new StringBuilder(ipPrefix).append("=").append(outer.getIp()).toString());
                lines.add(new StringBuilder(maskPrefix).append("=").append(outer.getMask()).toString());
                lines.add(new StringBuilder(gatewayPrefix).append("=").append(outer.getGateway()).toString());
                lines.add(new StringBuilder(namePrefix).append("=").append(vOuterName).toString());
                lines.add(new StringBuilder(devicePrefix).append("=").append(vOuterName).toString());
                lines.add(new StringBuilder(macPrefix).append("=").append(this.getMacByNetworkName(vOuterName)).toString());
                this.addNetworkEchoIfcfgFile(vOuterName, lines);
                return;
            }
            // 配置外网网卡，和内网网卡不是同一个网卡
            String wifi = outer.getWifi();
            String wifiPwd = outer.getWifiPwd();
            List<String> lines = this.getIfcfgFileLines(outerName);
            if (CollUtil.isEmpty(lines)) {
                lines = this.getIfcfgFileLines(innerName);
            }
            if (StrUtil.isNotBlank(wifi)) {
                // 无线只支持DHCP
                if (outer.getAllocation() == NetworkInfoVO.AllocationTypeEnum.STATIC) {
                    throw new AppException(ErrorCodes.DEPLOY_NETWORK_WIFI_DHCP);
                }
                // 如果已存在当前wiki的连接
                try{
                    FuncUtil.runCommand(new String[]{"sh", "-c", String.format("nmcli c delete %s", wifi)},
                            Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).replace("\n", Strings.EMPTY);
                    log.error("[deploy][network] nmcli c delete {} success",wifi);
                }catch (Exception e){
                    log.error("[deploy][network] nmcli c delete {} error",wifi);
                }
                // 连接无线网
                log.info("[deploy][network] nmcli dev wifi connect...");
                String cmd = String.format("nmcli dev wifi connect '%s' %s wep-key-type key ifname %s", wifi,
                        StrUtil.isBlank(wifiPwd) ? Strings.EMPTY : String.format("password '%s'", SM4Utils.webDecryptText(wifiPwd)), outerName);
                log.info("[deploy][network] nmcli dev wifi cmd = {}", cmd);
                String result = FuncUtil.runCommand(new String[]{"sh", "-c", cmd},
                        Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).replace("\n", Strings.EMPTY);
                log.info("[deploy][network] nmcli dev wifi connected");
                if (!result.contains("successfully")) {
                    log.error("[deploy][network] nmcli dev wifi fail: ",result);
                    throw new AppException(ErrorCodes.SSH_FAIL, result);
                }
                log.info("[deploy][network] nmcli dev wifi result : {}", result);
                lines.add(new StringBuilder(typePrefix).append("=").append("Wireless").toString());
            }else{
                lines.add(new StringBuilder(typePrefix).append("=").append("Ethernet").toString());
            }
            if (outer.getAllocation() == NetworkInfoVO.AllocationTypeEnum.STATIC) {
                lines.add(new StringBuilder(bootprotoPrefix).append("=").append("static").toString());
                lines.add(new StringBuilder(ipPrefix).append("=").append(outer.getIp()).toString());
                lines.add(new StringBuilder(maskPrefix).append("=").append(outer.getMask()).toString());
                lines.add(new StringBuilder(gatewayPrefix).append("=").append(outer.getGateway()).toString());

            }else{
                lines.add(new StringBuilder(bootprotoPrefix).append("=").append("dhcp").toString());
            }
            lines.add(new StringBuilder(namePrefix).append("=").append(outerName).toString());
            lines.add(new StringBuilder(devicePrefix).append("=").append(outerName).toString());
            lines.add(new StringBuilder(macPrefix).append("=").append(this.getMacByNetworkName(outerName)).toString());
            this.addNetworkEchoIfcfgFile(outerName, lines);
        }
    }

    @Override
    public void editNetwork(NetworkInfoVO query) throws Exception {
        List<NetworkVO> networkVOS = this.networkInfo(query);
        List<String> networkNames = networkVOS.stream().map(NetworkVO::getName).collect(Collectors.toList());
        NetworkInfoVO.NetworkInfo inner = query.getInner();
        NetworkInfoVO.NetworkInfo outer = query.getOuter();
        String innerName = inner.getName();
        if(!networkNames.contains(innerName)){
            throw new AppException(ErrorCodes.DEPLOY_NETWORK_NOT_EXIST,innerName);
        }
        Optional<Deploy> first = this.mongoTemplate.findAll(Deploy.class).stream().filter(d -> d.getIp().equals(inner.getIp())).findFirst();
        if (!first.isPresent()) {
            return;
        }
        Deploy deploy = first.get();
        this.mongoTemplate.findAll(NetworkConfig.class)
                .stream().filter(nc -> nc.getNodeName().equals(inner.getIp())).findFirst().ifPresent(nc -> {
                    SSHHost sshHost = SSHHost.newInstance(deploy.getIp(), deploy.getUsername(), SM4Utils.webDecryptText(deploy.getPassword()));
                    try {
                        {
                            List<String> lines = this.getIfcfgFileLines(innerName, sshHost);
                            lines.add(new StringBuilder(typePrefix).append("=").append("Ethernet").toString());
                            lines.add(new StringBuilder(bootprotoPrefix).append("=").append("static").toString());
                            lines.add(new StringBuilder(ipPrefix).append("=").append(inner.getIp()).toString());
                            lines.add(new StringBuilder(maskPrefix).append("=").append(inner.getMask()).toString());
                            lines.add(new StringBuilder(gatewayPrefix).append("=").append(inner.getGateway()).toString());
                            lines.add(new StringBuilder(namePrefix).append("=").append(innerName).toString());
                            lines.add(new StringBuilder(devicePrefix).append("=").append(innerName).toString());
                            lines.add(new StringBuilder(macPrefix).append("=").append(this.getMacByNetworkName(sshHost, innerName)).toString());
                            this.editNetworkEchoIfcfgFile(innerName, lines, sshHost);
                        }
                        // 配置外网网卡
                        if (Objects.nonNull(outer)) {
                            String outerName = outer.getName();
                            if(!networkNames.contains(outerName)){
                                throw new AppException(ErrorCodes.DEPLOY_NETWORK_NOT_EXIST,outerName);
                            }
                            // 配置外网网卡和内网网卡是同一个网卡
                            if (outerName.equals(innerName)) {
                                String vOuterName = new StringBuilder(innerName).append(":1").toString();
                                List<String> lines = this.getIfcfgFileLines(innerName, sshHost);
                                lines.add(new StringBuilder(typePrefix).append("=").append("Ethernet").toString());
                                lines.add(new StringBuilder(bootprotoPrefix).append("=").append("static").toString());
                                lines.add(new StringBuilder(ipPrefix).append("=").append(outer.getIp()).toString());
                                lines.add(new StringBuilder(maskPrefix).append("=").append(outer.getMask()).toString());
                                lines.add(new StringBuilder(gatewayPrefix).append("=").append(outer.getGateway()).toString());
                                lines.add(new StringBuilder(namePrefix).append("=").append(vOuterName).toString());
                                lines.add(new StringBuilder(devicePrefix).append("=").append(vOuterName).toString());
                                lines.add(new StringBuilder(macPrefix).append("=").append(this.getMacByNetworkName(sshHost, vOuterName)).toString());
                                this.editNetworkEchoIfcfgFile(vOuterName, lines, sshHost);
                                this.sshEditDNS(sshHost, nc, outer.getDns1(), outer.getDns2());
                                this.editNodeNetworkConfig(query);
                                return;
                            }
                            // 配置外网网卡，和内网网卡不是同一个网卡
                            String wifi = outer.getWifi();
                            String wifiPwd = outer.getWifiPwd();
                            List<String> lines = this.getIfcfgFileLines(outerName, sshHost);
                            if (CollUtil.isEmpty(lines)) {
                                lines = this.getIfcfgFileLines(innerName, sshHost);
                            }
                            if (StrUtil.isNotBlank(wifi)) {
                                // 无线只支持DHCP
                                if (outer.getAllocation() == NetworkInfoVO.AllocationTypeEnum.STATIC) {
                                    throw new AppException(ErrorCodes.DEPLOY_NETWORK_WIFI_DHCP);
                                }
                                // 如果已存在当前wiki的连接，要先删除连接，避免连接wifi异常
                                try{
                                    SSHTools.execute(sshHost, String.format("nmcli c delete %s", wifi));
                                    log.error("[deploy][network] nmcli c delete {} success",wifi);
                                }catch (Exception e){
                                    log.error("[deploy][network] nmcli c delete {} error",wifi);
                                }
                                // 连接无线网
                                String cmd = String.format("nmcli dev wifi connect '%s' %s wep-key-type key ifname %s", wifi,
                                        StrUtil.isBlank(wifiPwd) ? Strings.EMPTY : String.format("password '%s'", SM4Utils.webDecryptText(wifiPwd)), outerName);
                                log.info("[deploy][network] nmcli dev wifi cmd = {}",cmd);
                                String result = SSHTools.execute(sshHost, cmd).replace("\n", Strings.EMPTY);
                                if (!result.contains("successfully")) {
                                    log.error("[deploy][network] nmcli dev wifi fail: ", result);
                                    throw new AppException(ErrorCodes.SSH_FAIL, result);
                                }
                                lines.add(new StringBuilder(typePrefix).append("=").append("Wireless").toString());
                            } else {
                                lines.add(new StringBuilder(typePrefix).append("=").append("Ethernet").toString());
                            }
                            if (outer.getAllocation() == NetworkInfoVO.AllocationTypeEnum.STATIC) {
                                lines.add(new StringBuilder(bootprotoPrefix).append("=").append("static").toString());
                                lines.add(new StringBuilder(ipPrefix).append("=").append(outer.getIp()).toString());
                                lines.add(new StringBuilder(maskPrefix).append("=").append(outer.getMask()).toString());
                                lines.add(new StringBuilder(gatewayPrefix).append("=").append(outer.getGateway()).toString());
                            } else {
                                lines.add(new StringBuilder(bootprotoPrefix).append("=").append("dhcp").toString());
                            }
                            lines.add(new StringBuilder(namePrefix).append("=").append(outerName).toString());
                            lines.add(new StringBuilder(devicePrefix).append("=").append(outerName).toString());
                            lines.add(new StringBuilder(macPrefix).append("=").append(this.getMacByNetworkName(sshHost, outerName)).toString());
                            this.editNetworkEchoIfcfgFile(outerName, lines, sshHost);
                            this.sshEditDNS(sshHost, nc, outer.getDns1(), outer.getDns2());
                        }
                        this.editNodeNetworkConfig(query);
                    }catch (AppException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new AppException(ErrorCodes.SSH_FAIL, e.getMessage());
                    }
                });
    }

    @Override
    public void sshRestartNetwork(NetworkInfoVO.NetworkInfo inner) {
        List<DeployQueryVO> watchers = this.queryStatus();
        watchers.stream().filter(w -> w.getIp().equals(inner.getIp())).findFirst().ifPresent(w -> {
            this.sshRestartNetwork(SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build());
        });
    }

    private void sshRestartNetwork(SSHHost sshHost){
        try {
            SSHTools.execute(sshHost, "service network restart");
            log.error("[deploy][ip={}] service network restart success", sshHost.getIp());
        } catch (Exception e) {
            log.error("[deploy][ip={}] service network restart fail: {}", sshHost.getIp(), e);
        }
    }

    private String getMacByNetworkName(String name){
        return FuncUtil.runCommand(new String[]{"sh", "-c",
                        String.format("ifconfig %s | grep -w ether | awk '{print $2}'", name)},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).replace("\n",Strings.EMPTY);
    }

    private String getMacByNetworkName(SSHHost sshHost, String name) throws Exception {
        return SSHTools.execute(sshHost,String.format("ifconfig %s | grep -w ether | awk '{print $2}'", name)).replace("\n",Strings.EMPTY);
    }

    private void addNetworkEchoIfcfgFile(String networkName, List<String> lines) {
        StringBuilder sb = new StringBuilder();
        List<String> collect = lines.stream().filter(line -> !line.startsWith(typePrefix)).collect(Collectors.toList());
        lines.stream().filter(line -> line.startsWith(typePrefix)).collect(Collectors.toCollection(() -> collect));
        collect.forEach(line -> sb.append(line).append("\n"));
        FuncUtil.runCommand(new String[]{"sh", "-c",
                        String.format("echo '%s' > /etc/sysconfig/network-scripts/ifcfg-%s", sb, networkName)},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        log.info("[deploy] echo local ifcfg-{} success", networkName);
    }

    private void editNetworkEchoIfcfgFile(String networkName, List<String> lines, SSHHost sshHost) {
        StringBuilder sb = new StringBuilder();
        List<String> collect = lines.stream().filter(line -> !line.startsWith(typePrefix)).collect(Collectors.toList());
        lines.stream().filter(line -> line.startsWith(typePrefix)).collect(Collectors.toCollection(() -> collect));
        collect.forEach(line -> sb.append(line).append("\n"));
        try {
            SSHTools.execute(sshHost, String.format("echo '%s' > /etc/sysconfig/network-scripts/ifcfg-%s", sb, networkName));
        } catch (Exception e) {
            log.error("[deploy] edit host={} network config fail:{}", sshHost.getIp(), e.getMessage());
        }
    }

    @Override
    public void deleteRedundantIfcfgFile(NetworkInfoVO query) {
        log.info("[deploy] network delete redundant ifcfg file");
        // 本机网卡
        String innerName = query.getInner().getName();
        List<String> networkNames = Lists.newArrayList(innerName, "lo");
        if (Objects.nonNull(query.getOuter())) {
            String outerName = query.getOuter().getName();
            networkNames.add(innerName.equals(outerName) ? new StringBuilder(outerName).append(":1").toString() : outerName);
            if (StrUtil.isNotBlank(query.getOuter().getWifi())) {
                networkNames.add(query.getOuter().getWifi());
            }
        }
        log.info("[deploy] network names in [{}]", JSONUtil.toJsonStr(networkNames));
        // 本机网卡配置文件
        String[] ifcfgFiles = FuncUtil.runCommand(new String[]{"sh", "-c", "ls -l /etc/sysconfig/network-scripts/ifcfg-* | awk '{print $9}'"},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).split("\n");
        log.info("[deploy] ifcfg file names in [{}]", JSONUtil.toJsonStr(ifcfgFiles));
        // 将无法对应本机网卡的网卡配置文件删掉，避免network服务无法重启
        if (ifcfgFiles.length > 0) {
            Arrays.stream(ifcfgFiles).map(ifcfg -> ifcfg.substring(ifcfg.lastIndexOf("/") + 1))
                    .filter(ifcfg -> !networkNames.stream().filter(ifcfg::endsWith).findFirst().isPresent())
                    .forEach(ifcfg -> {
                        String cmd = String.format("rm -rf /etc/sysconfig/network-scripts/%s", ifcfg);
                        if (cmd.endsWith("-scripts/")) {
                            return;
                        }
                        FuncUtil.runCommand(new String[]{"sh", "-c", cmd},
                                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
                    });
        }
    }

    @Override
    public void sshDeleteRedundantIfcfgFile(NetworkInfoVO query) {
        log.info("[deploy] network delete redundant ifcfg file");
        // 本机网卡
        NetworkInfoVO.NetworkInfo inner = query.getInner();
        String innerName = inner.getName();
        List<String> networkNames = Lists.newArrayList(innerName, "lo");
        if (Objects.nonNull(query.getOuter())) {
            String outerName = query.getOuter().getName();
            networkNames.add(innerName.equals(outerName) ? new StringBuilder(outerName).append(":1").toString() : outerName);
            if (StrUtil.isNotBlank(query.getOuter().getWifi())) {
                networkNames.add(query.getOuter().getWifi());
            }
        }
        log.info("[deploy] network names in [{}]", JSONUtil.toJsonStr(networkNames));
        // 本机网卡配置文件
        List<DeployQueryVO> watchers = this.queryStatus();
        watchers.stream().filter(w -> w.getIp().equals(inner.getIp())).findFirst().ifPresent(watcher->{
            SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(watcher.getPassword())).user(watcher.getUsername()).ip(watcher.getIp()).build();
            try{
                String[] ifcfgFiles = SSHTools.execute(sshHost, "ls -l /etc/sysconfig/network-scripts/ifcfg-* | awk '{print $9}'").split("\n");
                log.info("[deploy] ifcfg file names in [{}]", JSONUtil.toJsonStr(ifcfgFiles));
                // 将无法对应本机网卡的网卡配置文件删掉，避免network服务无法重启
                if (ifcfgFiles.length > 0) {
                    Arrays.stream(ifcfgFiles).map(ifcfg -> ifcfg.substring(ifcfg.lastIndexOf("/") + 1))
                            .filter(ifcfg -> !networkNames.stream().filter(ifcfg::endsWith).findFirst().isPresent())
                            .forEach(ifcfg -> {
                                String cmd = String.format("rm -rf /etc/sysconfig/network-scripts/%s", ifcfg);
                                if (cmd.endsWith("-scripts/")) {
                                    return;
                                }
                                try {
                                    SSHTools.execute(sshHost, cmd);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    throw new AppException(ErrorCodes.SSH_FAIL,e.getMessage());
                                }
                            });
                }
            }catch (AppException e){
                throw e;
            }catch (Exception e){
                e.printStackTrace();
                throw new AppException(ErrorCodes.SSH_FAIL,e.getMessage());
            }
        });
    }

    private List<String> getIfcfgFileLines(String name) {
        String fileExist = FuncUtil.runCommand(new String[]{"sh", "-c", String.format("find /etc/sysconfig/network-scripts -name ifcfg-%s", name)},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        if(StrUtil.isBlank(fileExist.replace("\n", Strings.EMPTY))){
            return Lists.newArrayList();
        }
        String ifcfg = FuncUtil.runCommand(new String[]{"sh", "-c", String.format("cat /etc/sysconfig/network-scripts/ifcfg-%s", name)},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        if (StrUtil.isBlank(ifcfg.replace("\n", Strings.EMPTY))) {
            return Lists.newArrayList();
        }
        // 配置文件格式不正确
        if (!Arrays.stream(ifcfg.split("\n")).filter(line -> line.startsWith("TYPE=")).findFirst().isPresent()) {
            return Lists.newArrayList();
        }
        // ip 掩码 网关 这些配置都不要，使用用户填写的
        // uuid删掉
        return Arrays.stream(ifcfg.split("\n"))
                .filter(line -> !line.startsWith(uuidPrefix) && !line.startsWith(ipPrefix) &&
                        !line.startsWith(bootprotoPrefix) && !line.startsWith(typePrefix) &&
                        !line.startsWith(maskPrefix) && !line.startsWith(gatewayPrefix) &&
                        !line.startsWith(namePrefix) && !line.startsWith(devicePrefix) &&
                        !line.startsWith(macPrefix)
                ).collect(Collectors.toList());
    }

    private List<String> getIfcfgFileLines(String name, SSHHost sshHost) throws Exception {
        String fileExist = SSHTools.execute(sshHost, String.format("find /etc/sysconfig/network-scripts -name ifcfg-%s", name));
        if(StrUtil.isBlank(fileExist.replace("\n", Strings.EMPTY))){
            return Lists.newArrayList();
        }
        String ifcfg = SSHTools.execute(sshHost, String.format("cat /etc/sysconfig/network-scripts/ifcfg-%s", name));
        if (StrUtil.isBlank(ifcfg.replace("\n", Strings.EMPTY))) {
            return Lists.newArrayList();
        }
        // 配置文件格式不正确
        if (!Arrays.stream(ifcfg.split("\n")).filter(line -> line.startsWith("TYPE=")).findFirst().isPresent()) {
            return Lists.newArrayList();
        }
        // ip 掩码 网关 这些配置都不要，使用用户填写的
        // uuid删掉
        return Arrays.stream(ifcfg.split("\n"))
                .filter(line -> !line.startsWith(uuidPrefix) && !line.startsWith(ipPrefix) &&
                        !line.startsWith(bootprotoPrefix) && !line.startsWith(typePrefix) &&
                        !line.startsWith(maskPrefix) && !line.startsWith(gatewayPrefix) &&
                        !line.startsWith(namePrefix) && !line.startsWith(devicePrefix) &&
                        !line.startsWith(macPrefix)
                ).collect(Collectors.toList());
    }

    @Override
    public List<NetworkVO> networkInfo() {
        // 开启wifi使用的 NetworkManager 服务
        FuncUtil.runCommand(new String[]{"sh","-c","service NetworkManager start"},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        String str = FuncUtil.runCommand(new String[]{"sh", "-c", "ip a | grep 'link/'"},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        return Arrays.stream(str.split("\n")).filter(StrUtil::isNotBlank).map(s -> {
            String name = FuncUtil.runCommand(new String[]{"sh", "-c",
                                    String.format("ip a | grep -B 1 '%s' | head -1 | awk '{print $2}'", s)},
                            Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))))
                    .replace("\n", Strings.EMPTY).replace(":", Strings.EMPTY);
            if ("lo".equals(name)) {
                return null;
            }
            NetworkVO vo = new NetworkVO();
            vo.setName(name);
            /*
             * 命名规范：
             * en 开头表示Ethernet
             * wl 开头表示WLAN
             * ww 开头表示无线广域网WWAN
             */
            vo.setWifi(name.startsWith("e") ? 0 : 1);
            vo.setIp(FuncUtil.runCommand(new String[]{"sh", "-c",
                            String.format("ifconfig | grep -A 1 '%s' | tail -1 | grep -w 'inet' | awk '{print $2}'", name)},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).replace("\n", Strings.EMPTY));
            vo.setMask(FuncUtil.runCommand(new String[]{"sh", "-c",
                            String.format("ifconfig | grep -A 1 '%s' | tail -1 | grep -w 'inet' | awk '{print $4}'", name)},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).replace("\n", Strings.EMPTY));
            vo.setGateway(FuncUtil.runCommand(new String[]{"sh", "-c",
                            String.format("route -n | grep '%s' | grep UG | tail -1 | awk '{print $2}'", name)},
                    Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))).replace("\n", Strings.EMPTY));
            return vo;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<NetworkVO> networkInfo(NetworkInfoVO query) {
        NetworkInfoVO.NetworkInfo inner = query.getInner();
        List<DeployQueryVO> watchers = this.queryStatus();
        Optional<DeployQueryVO> first = watchers.stream().filter(w -> w.getIp().equals(inner.getIp())).findFirst();
        DeployQueryVO watcher = first.get();
        SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(watcher.getPassword())).user(watcher.getUsername()).ip(watcher.getIp()).build();
        try{
            // 开启wifi使用的 NetworkManager 服务
            SSHTools.execute(sshHost, "service NetworkManager start");
            String str = SSHTools.execute(sshHost,"ip a | grep 'link/'");
            return Arrays.stream(str.split("\n")).filter(StrUtil::isNotBlank).map(s -> {
                try{
                    String name = SSHTools.execute(sshHost,String.format("ip a | grep -B 1 '%s' | head -1 | awk '{print $2}'", s))
                            .replace("\n", Strings.EMPTY).replace(":", Strings.EMPTY);;
                    if ("lo".equals(name)) {
                        return null;
                    }
                    NetworkVO vo = new NetworkVO();
                    vo.setName(name);
                    /*
                     * 命名规范：
                     * en 开头表示Ethernet
                     * wl 开头表示WLAN
                     * ww 开头表示无线广域网WWAN
                     */
                    vo.setWifi(name.startsWith("e") ? 0 : 1);
                    vo.setIp(SSHTools.execute(sshHost, String.format("ifconfig | grep -A 1 '%s' | tail -1 | grep -w 'inet' | awk '{print $2}'", name)).replace("\n", Strings.EMPTY));
                    vo.setMask(SSHTools.execute(sshHost, String.format("ifconfig | grep -A 1 '%s' | tail -1 | grep -w 'inet' | awk '{print $4}'", name)).replace("\n", Strings.EMPTY));
                    vo.setGateway(SSHTools.execute(sshHost, String.format("route -n | grep '%s' | grep UG | tail -1 | awk '{print $2}'", name)).replace("\n", Strings.EMPTY));
                    return vo;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
            throw new AppException(ErrorCodes.SSH_FAIL,e.getMessage());
        }
    }

    @Override
    public List<NetworkConfigDTO> nodesNetworkConfigInfo() {
        List<NetworkConfig> all = this.mongoTemplate.findAll(NetworkConfig.class);
        List<DeployQueryVO> watchers = this.queryStatus();
        return all.stream().map(nc -> {
            NetworkConfigDTO networkConfigDTO = BeanUtil.toBean(nc, NetworkConfigDTO.class);
            watchers.stream().filter(w -> w.getIp().equals(networkConfigDTO.getInnerIp())).findFirst().ifPresent(watcher->{
                SSHHost sshHost = SSHHost.builder().ip(networkConfigDTO.getInnerIp()).port(22)
                        .user(watcher.getUsername()).password(SM4Utils.webDecryptText(watcher.getPassword())).build();
                try{
                    if(StrUtil.isNotBlank(networkConfigDTO.getOuterName())){
                        networkConfigDTO.setOuterIp(SSHTools.execute(sshHost, String.format("ifconfig | grep -A 1 '%s' | tail -1 | grep -w 'inet' | awk '{print $2}'", networkConfigDTO.getOuterName())).replace("\n", Strings.EMPTY));
                        networkConfigDTO.setOuterMask(SSHTools.execute(sshHost, String.format("ifconfig | grep -A 1 '%s' | tail -1 | grep -w 'inet' | awk '{print $4}'", networkConfigDTO.getOuterName())).replace("\n", Strings.EMPTY));
                        networkConfigDTO.setOuterGateway(SSHTools.execute(sshHost, String.format("route -n | grep '%s' | grep UG | tail -1 | awk '{print $2}'", networkConfigDTO.getOuterName())).replace("\n", Strings.EMPTY));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            return networkConfigDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public void addDNS(String... dns) {
        Arrays.stream(dns).filter(StrUtil::isNotBlank).forEach(s -> FuncUtil.runCommand(new String[]{"sh", "-c",
                        String.format("echo 'nameserver %s' >> /etc/resolv.conf", dns)},
                Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1)))));
    }

    public void sshEditDNS(SSHHost sshHost, NetworkConfig nc, String dns1, String dns2) {
        List<String> dnsList = Lists.newArrayList(dns1, dns2).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        List<String> orgDnsList = Lists.newArrayList(nc.getDns1(), nc.getDns2()).stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
        orgDnsList.forEach(dns -> {
            try {
                SSHTools.execute(sshHost, String.format("sed -i '/nameserver %s/d' /etc/resolv.conf", dns));
            } catch (Exception e) {
                throw new AppException(ErrorCodes.SSH_FAIL, e.getMessage());
            }
        });
        dnsList.forEach(dns -> {
            try {
                SSHTools.execute(sshHost, String.format("echo 'nameserver %s' >> /etc/resolv.conf", dns));
            } catch (Exception e) {
                throw new AppException(ErrorCodes.SSH_FAIL, e.getMessage());
            }
        });
    }

    @Override
    public boolean routeAddCheckPing(RouteVo query) {
        List<RouteConfig> all = this.mongoTemplate.findAll(RouteConfig.class);
        Optional<String> first = all.stream().filter(r -> {
            return IpUtil.netSegByIpAndMask(r.getTargetIp(),r.getTargetMask()).equals(IpUtil.netSegByIpAndMask(query.getTargetIp(),query.getTargetMask()))
                    && r.getVia().equals(query.getVia()) && r.getTargetMask().equals(query.getTargetMask())
                    && query.getWatchers().stream().filter(r.getWatchers()::contains).findFirst().isPresent();
        }).map(r -> query.getWatchers().stream().filter(r.getWatchers()::contains).findFirst().get()).findFirst();
        if (first.isPresent()) {
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_MOST, first.get());
        }
        RouteConfig routeConfig = BeanUtil.copyProperties(query, RouteConfig.class);
        List<DeployQueryVO> watchers = this.queryStatus();
        List<String> watcherIps = query.getWatchers();
        // 需要配置路由的采集端
        List<DeployQueryVO> needRouteWatchers = watchers.stream().filter(w -> watcherIps.contains(w.getIp())).collect(Collectors.toList());
        // 通过下一跳、目标地址测试的采集端
        List<DeployQueryVO> testPassWatchers = needRouteWatchers.stream().map(w -> {
            try {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                // 测试下一跳
                this.testPing(sshHost, routeConfig.getVia());
                // 添加路由配置
                this.addRouteInCmd(sshHost, routeConfig);
                // 测试目标地址
                this.testPing(sshHost, routeConfig.getTargetIp());
                // 测试通过删掉路由记录
                this.deleteRouteInCmd(sshHost, routeConfig);
            } catch (Exception e) {
                // 抛出 Exception 说明测试验证不通过
                // 删除配置好的路由文件并
                return null;
            }
            return w;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        // 如果通过测试的采集端数量不等于需要配置路由的采集端数量
        // 那么本次批量操作集体失败，将采集端已配置好的路由删掉，并直接跳出方法，路由记录不入库
        if (needRouteWatchers.size() != testPassWatchers.size()) {
            needRouteWatchers.forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                this.deleteRouteInCmd(sshHost, routeConfig);
            });
            return false;
        }
        return true;
    }

    @Override
    public boolean routeEditCheckPing(RouteVo query) {
        List<RouteConfig> all = this.mongoTemplate.findAll(RouteConfig.class);
        Optional<RouteConfig> first1 = all.stream().filter(r -> r.getId().equals(query.getId())).findFirst();
        if(!first1.isPresent()){
            return false;
        }
        Optional<String> first = all.stream().filter(r -> {
            return !r.getId().equals(query.getId())
                    && IpUtil.netSegByIpAndMask(r.getTargetIp(),r.getTargetMask()).equals(IpUtil.netSegByIpAndMask(query.getTargetIp(),query.getTargetMask()))
                    && r.getVia().equals(query.getVia()) && r.getTargetMask().equals(query.getTargetMask())
                    && !query.getWatchers().stream().filter(w->!r.getWatchers().contains(w)).findFirst().isPresent();
        }).map(r -> query.getWatchers().stream().filter(r.getWatchers()::contains).findFirst().get()).findFirst();
        if (first.isPresent()) {
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_MOST, first.get());
        }
        List<String> watcherIps = query.getWatchers();
        RouteConfig r = first1.get();
        RouteConfig routeConfig = BeanUtil.copyProperties(query, RouteConfig.class);
        routeConfig.setCreateTime(r.getCreateTime());
        routeConfig.setUpdateTime(System.currentTimeMillis());
        List<DeployQueryVO> watchers = this.queryStatus();
        List<String> oldWatcherIps = r.getWatchers();
        List<String> updateList = watcherIps.stream().filter(oldWatcherIps::contains).collect(Collectors.toList());
        List<String> addList = watcherIps.stream().filter(ip -> !oldWatcherIps.contains(ip)).collect(Collectors.toList());
        List<String> deleteList = oldWatcherIps.stream().filter(ip -> !watcherIps.contains(ip)).collect(Collectors.toList());
        List<DeployQueryVO> add_passList = watchers.stream().filter(w -> addList.contains(w.getIp())).map(w -> {
            try {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                // 测试下一跳
                this.testPing(sshHost, routeConfig.getVia());
                // 添加路由配置
                this.addRouteInCmd(sshHost, routeConfig);
                // 测试目标地址
                this.testPing(sshHost, routeConfig.getTargetIp());
                // 测试通过删掉路由记录
                this.deleteRouteInCmd(sshHost, routeConfig);
            } catch (AppException e) {
                // 抛出 AppException 说明测试目标地址ip不通
                // 删除配置好的路由文件并
                return null;
            }
            return w;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (addList.size() != add_passList.size()) {
            watchers.stream().filter(w -> addList.contains(w.getIp())).forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                this.deleteRouteInCmd(sshHost, routeConfig);
            });
            return false;
        }
        List<DeployQueryVO> update_passList = watchers.stream().filter(w -> updateList.contains(w.getIp())).map(w -> {
            try {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                // 测试下一跳
                this.testPing(sshHost, routeConfig.getVia());
                // 修改路由配置
                {
                    final String delLine = this.makeRouteFileContentCmdLine(r, "del");
                    final String addLine = this.makeRouteFileContentCmdLine(routeConfig, "add");
                    this.editRouteInCmd(sshHost, delLine, addLine);
                }
                // 测试目标地址
                this.testPing(sshHost, routeConfig.getTargetIp());
                // 路由记录修改回来
                {
                    final String delLine = this.makeRouteFileContentCmdLine(routeConfig, "del");
                    final String addLine = this.makeRouteFileContentCmdLine(r, "add");
                    this.editRouteInCmd(sshHost, delLine, addLine);
                }
            } catch (AppException e) {
                // 抛出 AppException 说明测试目标地址ip不通
                // 删除配置好的路由文件并
                return null;
            }
            return w;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (update_passList.size() != updateList.size()) {
            watchers.stream().filter(w -> updateList.contains(w.getIp())).forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                r.setDev(dev);
                routeConfig.setDev(dev);
                final String oldLine = this.makeRouteFileContentCmdLine(r, "add");
                final String newLine = this.makeRouteFileContentCmdLine(routeConfig, "del");
                // 修改路由配置
                this.editRouteInCmd(sshHost, newLine, oldLine);
            });
            add_passList.forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                this.deleteRouteInCmd(sshHost, routeConfig);
            });
            return false;
        }
        return true;
    }

    @Override
    public void routeCheckPing(RouteCheckPingVo query) {
        final String target = query.getTarget();
        List<DeployQueryVO> watchers = this.queryStatus();
        query.getWatchers().forEach(watcherIp -> {
            // 不在集群内的采集端不考虑
            if (!watchers.stream().map(DeployQueryVO::getIp).collect(Collectors.toList()).contains(watcherIp)) {
                throw new AppException(ErrorCodes.DEPLOY_ROUTE_NOT_FOUND_IP, watcherIp);
            }
            Optional<DeployQueryVO> first = watchers.stream().filter(w -> w.getIp().equals(watcherIp)).findFirst();
            if (first.isPresent()) {
                DeployQueryVO w = first.get();
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(watcherIp).build();
                // 测试目标ip
                this.testPing(sshHost, target);
            }
        });
    }

    @Override
    public List<RouteVo> routeList() {
        List<RouteConfig> all = this.mongoTemplate.findAll(RouteConfig.class);
        return all.stream().map(r -> {
            RouteVo routeVo = BeanUtil.copyProperties(r, RouteVo.class);
            return routeVo;
        }).collect(Collectors.toList());
    }

    @Override
    public void addRoute(RouteVo query) {
        if(!this.routeAddCheckPing(query)){
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_PING_VIA_FAIL,query.getVia(),query.getTargetIp());
        }
        RouteConfig routeConfig = BeanUtil.copyProperties(query, RouteConfig.class);
        routeConfig.setCreateTime(System.currentTimeMillis());
        routeConfig.setUpdateTime(System.currentTimeMillis());
        List<DeployQueryVO> watchers = this.queryStatus();
        List<String> watcherIps = query.getWatchers();
        // 需要配置路由的采集端
        List<DeployQueryVO> needRouteWatchers = watchers.stream().filter(w -> watcherIps.contains(w.getIp())).collect(Collectors.toList());
        // 通过下一跳、目标地址测试的采集端
        needRouteWatchers.stream().forEach(w->{
            SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
            String dev = this.getDevName(sshHost);
            routeConfig.setDev(dev);
            // 添加路由配置
            this.addRouteInCmd(sshHost, routeConfig);
            this.addRouteInFile(sshHost, routeConfig);
        });
        this.mongoTemplate.save(routeConfig);
    }

    @Override
    public void editRoute(RouteVo query) {
        if(!this.routeEditCheckPing(query)){
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_PING_VIA_FAIL,query.getVia(),query.getTargetIp());
        }
        List<RouteConfig> all = this.mongoTemplate.findAll(RouteConfig.class);
        List<String> watcherIps = query.getWatchers();
        all.stream().filter(r -> r.getId().equals(query.getId())).findFirst().ifPresent(r -> {
            RouteConfig routeConfig = BeanUtil.copyProperties(query, RouteConfig.class);
            routeConfig.setCreateTime(r.getCreateTime());
            routeConfig.setUpdateTime(System.currentTimeMillis());
            List<DeployQueryVO> watchers = this.queryStatus();
            List<String> oldWatcherIps = r.getWatchers();
            List<String> addList = watcherIps.stream().filter(ip -> !oldWatcherIps.contains(ip)).collect(Collectors.toList());
            List<String> updateList = watcherIps.stream().filter(oldWatcherIps::contains).collect(Collectors.toList());
            List<String> deleteList = oldWatcherIps.stream().filter(ip -> !watcherIps.contains(ip)).collect(Collectors.toList());
            watchers.stream().filter(w -> addList.contains(w.getIp())).forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                routeConfig.setDev(dev);
                // 添加路由配置
                this.addRouteInCmd(sshHost, routeConfig);
                this.addRouteInFile(sshHost, routeConfig);
            });
            watchers.stream().filter(w -> updateList.contains(w.getIp())).forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                r.setDev(dev);
                routeConfig.setDev(dev);
                final String delLine = this.makeRouteFileContentCmdLine(r, "del");
                final String addLine = this.makeRouteFileContentCmdLine(routeConfig, "add");
                // 修改路由配置
                this.editRouteInCmd(sshHost, delLine, addLine);
                this.editRouteInFile(sshHost, r, routeConfig);
            });
            watchers.stream().filter(w -> deleteList.contains(w.getIp())).forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                r.setDev(dev);
                this.deleteRouteInCmd(sshHost, r);
                this.deleteRouteInFile(sshHost, r);
            });
            this.mongoTemplate.save(routeConfig);
        });
    }

    @Override
    public void deleteRoute(List<String> ids) {
        this.mongoTemplate.findAll(RouteConfig.class).stream().filter(r -> ids.contains(r.getId())).forEach(r -> {
            List<DeployQueryVO> watchers = this.queryStatus();
            List<String> watcherIps = r.getWatchers();
            List<NetworkConfig> networks = this.mongoTemplate.findAll(NetworkConfig.class);
            watchers.stream().filter(w -> watcherIps.contains(w.getIp())).forEach(w -> {
                SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
                String dev = this.getDevName(sshHost);
                r.setDev(dev);
                this.deleteRouteInCmd(sshHost, r);
                this.deleteRouteInFile(sshHost, r);
            });
            this.mongoTemplate.remove(r);
        });
    }

    /**
     * 将数据库记录转化为文件中的一行内容，格式如下：
     * 192.168.0.0/16 via 172.16.0.1 dev eth1
     * 192.168.0.0 目标ip
     * /16 目标子网掩码
     * via 172.16.0.1 下一跳
     * dev eth1 出接口
     */
    private String makeRouteFileContentLine(RouteConfig query, String dev) {
        StringBuilder sb = new StringBuilder();
        sb.append(query.getTargetIp()).append("/").append(IpUtil.convertPointToNumber(query.getTargetMask())).append(" ");
        sb.append("via").append(" ").append(query.getVia()).append(" ");
        if (StrUtil.isNotBlank(query.getDev())) {
            sb.append("dev").append(" ").append(dev);
        }
        return sb.toString();
    }

    private String makeRouteFileContentCmdLine(RouteConfig query, String type) {
        StringBuilder sb = new StringBuilder();
//        sb.append("route add -net 10.99.224.0 netmask 255.255.255.0 gw 10.99.234.1 dev ens3");
        sb.append("route ");
        sb.append(type);
        sb.append(" -net ");
        sb.append(IpUtil.netSegByIpAndMask(query.getTargetIp(),query.getTargetMask()));
        sb.append(" netmask ");
        sb.append(query.getTargetMask());
        sb.append(" gw ");
        sb.append(query.getVia());
        sb.append(" dev ");
        sb.append(query.getDev());
//        sb.append(query.getTargetIp()).append("/").append(IpUtil.convertPointToNumber(query.getTargetMask())).append(" ");
//        sb.append("via").append(" ").append(query.getVia()).append(" ");
//        if (StrUtil.isNotBlank(query.getDev())) {
//            sb.append("dev").append(" ").append(dev);
//        }
        return sb.toString();
    }

    private void testPing(SSHHost sshHost, String target) {
        // 测试ping
        log.info("测试ip[{}] -> ip[{}] 是否可以ping通......",sshHost.getIp(),target);
        try {
            // 期望执行结果 ： 1 packets transmitted, 1 received, 0% packet loss, time 0ms DEPLOY_ROUTE_PING_VIA_FAIL
            // 1 receuved 表示通
            String execute = SSHTools.execute(sshHost,String.format("ping -q -c1 %s | tail -2 | head -1 | awk '{print $4}'", target)).replace("\n", Strings.EMPTY);
            if (!"1".equals(execute)) {
                log.info("测试ip[{}] -> ip[{}] ping fail",sshHost.getIp(),target);
                throw new AppException(ErrorCodes.DEPLOY_ROUTE_PING_FAIL, sshHost.getIp(), target);
            }
            log.info("测试ip[{}] -> ip[{}] ping success",sshHost.getIp(),target);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_PING_FAIL, sshHost.getIp(), target);
        }
    }

    private void addRouteInCmd(SSHHost sshHost, RouteConfig routeConfig) {
        try {
            SSHTools.execute(sshHost,this.makeRouteFileContentCmdLine(routeConfig, "add"));
        } catch (Exception e) {
            log.error("[deploy][ip={}] add route fail: {}", sshHost.getIp(), e);
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    private void addRouteInFile(SSHHost sshHost, RouteConfig routeConfig) {
        String dev = routeConfig.getDev();
        try {
            SSHTools.execute(sshHost,
                    String.format("echo '%s' >> /etc/sysconfig/network-scripts/route-%s", this.makeRouteFileContentLine(routeConfig, dev), dev));
        } catch (Exception e) {
            log.error("[deploy][ip={}] add route fail: {}", sshHost.getIp(), e);
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    private void editRouteInCmd(SSHHost sshHost, String oldLine, String newLine) {
        try {
            SSHTools.execute(sshHost,oldLine);
            SSHTools.execute(sshHost,newLine);
            log.info("[deploy][ip={}] edit route success", sshHost.getIp());
        } catch (Exception e) {
            log.error("[deploy][ip={}] edit route fail: {}", sshHost.getIp(), e);
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    private void editRouteInFile(SSHHost sshHost, RouteConfig org, RouteConfig dst) {
        try {
            final String oldLine = this.makeRouteFileContentCmdLine(org, "del");
            final String newLine = this.makeRouteFileContentCmdLine(dst, "add");
            SSHTools.execute(sshHost,
                    String.format("sed -i 's/%s/%s/g' /etc/sysconfig/network-scripts/route-%s", oldLine.replace("/", "\\/"), newLine.replace("/", "\\/"), org.getDev()));
            log.info("[deploy][ip={}] edit route success", sshHost.getIp());
        } catch (Exception e) {
            log.error("[deploy][ip={}] edit route fail: {}", sshHost.getIp(), e);
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    public void deleteRouteInCmd(SSHHost sshHost, RouteConfig routeConfig) {
        try {
            SSHTools.execute(sshHost,this.makeRouteFileContentCmdLine(routeConfig, "del"));
            log.info("[deploy][ip={}] delete route success", sshHost.getIp());
        } catch (Exception e) {
            log.error("[deploy][ip={}] delete route fail: {}", sshHost.getIp(), e);
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    public void deleteRouteInFile(SSHHost sshHost, RouteConfig routeConfig) {
        String dev = routeConfig.getDev();
        try {
            SSHTools.execute(sshHost,
                    String.format("sed -i '/%s/d' /etc/sysconfig/network-scripts/route-%s", this.makeRouteFileContentLine(routeConfig, dev).replace("/", "\\/"), dev));
            log.info("[deploy][ip={}] delete route success", sshHost.getIp());
        } catch (Exception e) {
            log.error("[deploy][ip={}] delete route fail: {}", sshHost.getIp(), e);
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    private String getDevName(SSHHost sshHost) {
        try {
            return SSHTools.execute(sshHost,String.format("ip a | grep -B 2 '%s' | head -1 | awk '{print $2}'",sshHost.getIp())).replace(":",Strings.EMPTY).replace("\n",Strings.EMPTY);
        } catch (Exception e) {
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, sshHost.getIp(), e.getMessage());
        }
    }

    public void addStrategyRoute(NetworkInfoVO.NetworkInfo inner){
        //ip rule add from 172.53.5.31 table 100
        //ip route add default via 172.53.0.1 table 100
        try {
            String rule = String.format("ip rule add from %s table 100", inner.getIp());
            log.info("[addStrategyRoute] cmd rule = {}",rule);
            FuncUtil.runCommand(new String[]{"sh", "-c",rule},Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            String route = String.format("ip route add default via %s table 100", inner.getGateway());
            log.info("[addStrategyRoute] cmd route = {}",route);
            FuncUtil.runCommand(new String[]{"sh", "-c",route},Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        } catch (Exception e) {
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, inner.getIp(), e.getMessage());
        }
    }

    @Override
    public void addStrategyRouteSSH(NetworkInfoVO.NetworkInfo inner){
        List<DeployQueryVO> watchers = this.queryStatus();
        watchers.stream().filter(w -> w.getIp().equals(inner.getIp())).findFirst().ifPresent(w -> {
            SSHHost sshHost = SSHHost.builder().port(22).password(SM4Utils.webDecryptText(w.getPassword())).user(w.getUsername()).ip(w.getIp()).build();
            try {
                SSHTools.execute(sshHost,String.format("ip rule add from %s table 100",inner.getIp()));
                SSHTools.execute(sshHost,String.format("ip route add default via %s table 100",inner.getGateway()));
            } catch (Exception e) {
                throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, inner.getIp(), e.getMessage());
            }
        });
        //ip rule add from 172.53.5.31 table 100
        //ip route add default via 172.53.0.1 table 100
    }

    public void addStrategyRouteInFile(NetworkInfoVO.NetworkInfo inner){
        //ip rule add from 172.53.5.31 table 100
        //ip route add default via 172.53.0.1 table 100
        try {
            FuncUtil.runCommand(new String[]{"sh", "-c",String.format("echo 'ip rule add from %s table 100' >> /etc/rc.d/rc.local",inner.getIp())},Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
            FuncUtil.runCommand(new String[]{"sh", "-c",String.format("echo 'ip route add default via %s table 100' >> /etc/rc.d/rc.local",inner.getGateway())},Integer.valueOf(String.valueOf(TimeUnit.HOURS.toMillis(1))));
        } catch (Exception e) {
            throw new AppException(ErrorCodes.DEPLOY_ROUTE_ADD_ERROR, inner.getIp(), e.getMessage());
        }
    }
}
