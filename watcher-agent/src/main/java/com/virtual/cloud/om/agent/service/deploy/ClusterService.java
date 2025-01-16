package com.virtual.cloud.om.agent.service.deploy;

import com.virtual.cloud.om.agent.entity.Parameter;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import com.virtual.cloud.om.sdk.api.RealTimeLogApi;
import com.virtual.cloud.om.sdk.constant.ComponentEnum;
import com.virtual.cloud.om.sdk.dto.deploy.BatchDeployVO;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.FileUtil;
import com.virtual.cloud.om.sdk.utils.IpUtil;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author: w22798
 * @Date: 2022/5/30 10:19
 */

@Slf4j
@Component
public class ClusterService {

    @Resource
    private DeployApi deployApi;

    @Resource
    private ParameterApi parameterApi;

    @Resource
    private RealTimeLogApi realTimeLogApi;

    public static final String KEEPALIVED_CONF_PATH = "/components/keepalived/keepalived-2.0.20/conf/keepalived.conf";

    //步数
    public final static String CLUSTER_STEP_ZERO = "0";
    public final static Integer CLUSTER_STEP_ONE = 1;
    public final static Integer CLUSTER_STEP_TWO = 2;
    public final static Integer CLUSTER_STEP_THREE = 3;
    public final static Integer CLUSTER_STEP_FOUR = 4;
    public final static Integer CLUSTER_STEP_FIVE = 5;
    public final static Integer CLUSTER_STEP_SIX = 6;
    public final static Integer CLUSTER_STEP_SEVEN = 7;
    public final static Integer CLUSTER_STEP_EIGHT = 8;
    public final static Integer CLUSTER_STEP_NINE = 9;
    public final static Integer CLUSTER_STEP_TEN = 10;

    public void deployCluster(BatchDeployVO batchDeployVO) {

        String watcherHome = deployApi.queryWatcherHome(batchDeployVO.getMasterNode());

        //拷贝安装包到其他两个节点, 默认与主节点统一部署路径, 搭建集群过程中关闭防火墙，搭建完成后开启
        if(getClusterStep() < CLUSTER_STEP_ONE){
            log.info("[cluster] step 1 copyPackages start");
            closeFireWalld(batchDeployVO);
            copyPackages(batchDeployVO, watcherHome);
            modifyClusterStep(CLUSTER_STEP_ONE);
            log.info("[cluster] step 1 copyPackages end");
        }

        //初始化
        if(getClusterStep() < CLUSTER_STEP_TWO){
            log.info("[cluster] step 2 initBase start");
            initBase(batchDeployVO, watcherHome);
            modifyClusterStep(CLUSTER_STEP_TWO);
            log.info("[cluster] step 2 initBase end");
        }

        //修改hosts文件
        if(getClusterStep() < CLUSTER_STEP_THREE){
            log.info("[cluster] step 3 modifyHosts start");
            modifyHosts(batchDeployVO);
            modifyClusterStep(CLUSTER_STEP_THREE);
            log.info("[cluster] step 3 modifyHosts end");
        }

        //keepalived集群搭建
        if(getClusterStep() < CLUSTER_STEP_FOUR){
            log.info("[cluster] step 4 keepalivedCluster start");
            keepalivedCluster(batchDeployVO, watcherHome);
            modifyClusterStep(CLUSTER_STEP_FOUR);
            log.info("[cluster] step 4 keepalivedCluster end");
        }

        //mongodb集群搭建
        if(getClusterStep() < CLUSTER_STEP_FIVE) {
            log.info("[cluster] step 5 mongodbCluster start");
            mongodbCluster(batchDeployVO, watcherHome);
            modifyClusterStep(CLUSTER_STEP_FIVE);
            log.info("[cluster] step 5 mongodbCluster end");
        }

        //zookeeper集群搭建
        if(getClusterStep() < CLUSTER_STEP_SIX){
            log.info("[cluster] step 6 zookeeperCluster start");
            zookeeperCluster(batchDeployVO, watcherHome);
            modifyClusterStep(CLUSTER_STEP_SIX);
            log.info("[cluster] step 6 zookeeperCluster end");
        }

        //kafka集群搭建
        if(getClusterStep() < CLUSTER_STEP_SEVEN){
            log.info("[cluster] step 7 kafkaCluster start");
            kafkaCluster(batchDeployVO, watcherHome);
            modifyClusterStep(CLUSTER_STEP_SEVEN);
            log.info("[cluster] step 7 kafkaCluster end");
        }

        //NTP服务器
        if(getClusterStep() < CLUSTER_STEP_EIGHT){
            log.info("[cluster] step 8 ntpCluster start");
            ntpCluster(batchDeployVO,watcherHome);
            modifyClusterStep(CLUSTER_STEP_EIGHT);
            log.info("[cluster] step 8 ntpCluster end");
        }

        //websocket连接，上报虚IP地址
        if(getClusterStep() < CLUSTER_STEP_NINE){
            log.info("[cluster] step 9 websocketCluster start");
            modifyClusterStep(CLUSTER_STEP_NINE);
            log.info("[cluster] step 9 websocketCluster end");
        }

        //...其他步骤

        //后处理（重启从节点agent服务、创建topic、修改防火墙）
        if(getClusterStep() < CLUSTER_STEP_TEN){
            log.info("[cluster] step 10 restart agent start");
            stopThenStartAgent(batchDeployVO);
            //创建topic，监听
            realTimeLogApi.createAndConsumeFilebeatLogTopic(6, 2);
            openFireWalld(batchDeployVO,watcherHome);
            modifyClusterStep(CLUSTER_STEP_TEN);
            log.info("[cluster] step 10 restart agent end");
        }
    }

    @SneakyThrows
    private void openFireWalld(BatchDeployVO batchDeployVO, String watcherHome){
        String cmdMaster = "sh " + watcherHome + "/bin/openfirewalld.sh "+batchDeployVO.getSlave1Node().getIp()+ " "+batchDeployVO.getSlave2Node().getIp();
        String cmdS1 = "sh " + watcherHome + "/bin/openfirewalld.sh "+batchDeployVO.getMasterNode().getIp()+ " "+batchDeployVO.getSlave2Node().getIp();
        String cmdS2 = "sh " + watcherHome + "/bin/openfirewalld.sh "+batchDeployVO.getMasterNode().getIp()+ " " +batchDeployVO.getSlave1Node().getIp();
        log.info("[cluster] openFireWalld cmdMaster : "+cmdMaster);
        log.info("[cluster] openFireWalld cmdS1 : "+cmdS1);
        log.info("[cluster] openFireWalld cmdS2 : "+cmdS2);
        SSHTools.execute(batchDeployVO.getMasterNode(), cmdMaster);
        SSHTools.execute(batchDeployVO.getSlave1Node(), cmdS1);
        SSHTools.execute(batchDeployVO.getSlave2Node(),cmdS2);
    }

    @SneakyThrows
    private void closeFireWalld(BatchDeployVO batchDeployVO){
        try {
            String cmd = "systemctl stop firewalld.service";
            SSHTools.execute(batchDeployVO.getMasterNode(), cmd);
            SSHTools.execute(batchDeployVO.getSlave1Node(), cmd);
            SSHTools.execute(batchDeployVO.getSlave2Node(), cmd);
        } catch (Exception e) {
            log.error("[cluster] service firewalld stop fail", e);
        }
    }


    private Integer getClusterStep() {
        String step = parameterApi.queryParameterByTypeAndName(Parameter.SYS_CONF, Parameter.NAME_CLUSTER_STEP).orElse(CLUSTER_STEP_ZERO);
        return Integer.parseInt(step);
    }

    private void modifyClusterStep(Integer step) {
        parameterApi.editParamByTypeAndName(step.toString(), Parameter.SYS_CONF, Parameter.NAME_CLUSTER_STEP);
    }


    private void copyPackages(BatchDeployVO batchDeployVO, String watcherHome){
        String destPath = StringUtils.substringBeforeLast(watcherHome, "/");
        if(!theHostInstalledWatcher(batchDeployVO.getSlave1Node())){
            FileUtil.copyFile(batchDeployVO.getMasterNode(), "/tmp/oad-watcher", batchDeployVO.getSlave1Node(), destPath);
        }
        if(!theHostInstalledWatcher(batchDeployVO.getSlave2Node())){
            FileUtil.copyFile(batchDeployVO.getMasterNode(), "/tmp/oad-watcher", batchDeployVO.getSlave2Node(), destPath);
        }
    }

    private boolean theHostInstalledWatcher(SSHHost sshHost){
        return SSHTools.isExistFile("/etc/watcher_home", sshHost);
    }

    @SneakyThrows
    private void initBase(BatchDeployVO batchDeployVO, String watcherHome) {
        String cmd = "bash " + watcherHome + "/bin/init_slave.sh";
        if(!theHostInstalledWatcher(batchDeployVO.getSlave1Node())){
            SSHTools.executeSshCmd(SSHTools.createSession(batchDeployVO.getSlave1Node()), cmd, TimeUnit.MINUTES.toMillis(1));
        }
        if(!theHostInstalledWatcher(batchDeployVO.getSlave2Node())){
            SSHTools.executeSshCmd(SSHTools.createSession(batchDeployVO.getSlave2Node()), cmd, TimeUnit.MINUTES.toMillis(1));
        }
    }

    @SneakyThrows
    private void modifyHosts(BatchDeployVO batchDeployVO){
        //删除已有的center1配置
        String removeHosts = "sed -i '/watcher/d' /etc/hosts";
        String masterHost1 = "sed -i '$a %s watcher001 watcher001' /etc/hosts";
        String masterHost2 = "sed -i '$a %s watcher002 watcher002' /etc/hosts";
        String masterHost3 = "sed -i '$a %s watcher003 watcher003' /etc/hosts";

        String watcher001 = String.format(masterHost1, batchDeployVO.getMasterNode().getIp());
        String watcher002 = String.format(masterHost2, batchDeployVO.getSlave1Node().getIp());
        String watcher003 = String.format(masterHost3, batchDeployVO.getSlave2Node().getIp());

        SSHTools.execute(batchDeployVO.getMasterNode(), removeHosts);
        SSHTools.execute(batchDeployVO.getMasterNode(), watcher001);
        SSHTools.execute(batchDeployVO.getMasterNode(), watcher002);
        SSHTools.execute(batchDeployVO.getMasterNode(), watcher003);

        SSHTools.execute(batchDeployVO.getSlave1Node(), removeHosts);
        SSHTools.execute(batchDeployVO.getSlave1Node(), watcher001);
        SSHTools.execute(batchDeployVO.getSlave1Node(), watcher002);
        SSHTools.execute(batchDeployVO.getSlave1Node(), watcher003);

        SSHTools.execute(batchDeployVO.getSlave2Node(), removeHosts);
        SSHTools.execute(batchDeployVO.getSlave2Node(), watcher001);
        SSHTools.execute(batchDeployVO.getSlave2Node(), watcher002);
        SSHTools.execute(batchDeployVO.getSlave2Node(), watcher003);
        log.info("[cluster] modify hosts file start");
        deployApi.restartService(batchDeployVO.getMasterNode(), ComponentEnum.kafka.name());
        deployApi.restartService(batchDeployVO.getSlave1Node(), ComponentEnum.kafka.name());
        deployApi.restartService(batchDeployVO.getSlave2Node(), ComponentEnum.kafka.name());
    }

    @SneakyThrows
    public void keepalivedCluster(BatchDeployVO batchDeployVO, String watcherHome) {

        //修改vip信息
        String modifyVip = "sed -i 's#127.0.0.1/24#" + batchDeployVO.getVip() + "/" + IpUtil.convertPointToNumber(batchDeployVO.getMask()) + "#g' " + watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getMasterNode(), modifyVip);
        SSHTools.execute(batchDeployVO.getSlave1Node(), modifyVip);
        SSHTools.execute(batchDeployVO.getSlave2Node(), modifyVip);

        //修改网卡信息
        String masterNet = "sed -i 's/ens3/" + getRemoteNetwork(batchDeployVO.getMasterNode()) + "/g' " + watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getMasterNode(), masterNet);
        String slave1Net = "sed -i 's/ens3/" + getRemoteNetwork(batchDeployVO.getSlave1Node()) + "/g' " + watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getSlave1Node(), slave1Net);
        String slave2Net = "sed -i 's/ens3/" + getRemoteNetwork(batchDeployVO.getSlave2Node()) + "/g' " +  watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getSlave2Node(), slave2Net);

        //修改virtual_router_id
        String modifyRouter = "sed -i 's/virtual_router_id 70/virtual_router_id " + StringUtils.substringAfterLast(batchDeployVO.getVip(), ".") + "/g' " + watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getMasterNode(), modifyRouter);
        SSHTools.execute(batchDeployVO.getSlave1Node(), modifyRouter);
        SSHTools.execute(batchDeployVO.getSlave2Node(), modifyRouter);

        //修改priority
        String modifyPrioritySlave1 = "sed -i 's/priority 100/priority 99/g' " + watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getSlave1Node(), modifyPrioritySlave1);
        String modifyPrioritySlave2 = "sed -i 's/priority 100/priority 98/g' " + watcherHome + KEEPALIVED_CONF_PATH;
        SSHTools.execute(batchDeployVO.getSlave2Node(), modifyPrioritySlave2);

        //注册系统服务
        String registerService = "bash " + watcherHome + "/bin/register_keepalived_as_system_service.sh";
        SSHTools.execute(batchDeployVO.getMasterNode(), registerService);
        SSHTools.execute(batchDeployVO.getSlave1Node(), registerService);
        SSHTools.execute(batchDeployVO.getSlave2Node(), registerService);

        //启动keepalived
        deployApi.startupService(batchDeployVO.getMasterNode(), ComponentEnum.keepalived.name());
        deployApi.startupService(batchDeployVO.getSlave1Node(), ComponentEnum.keepalived.name());
        deployApi.startupService(batchDeployVO.getSlave2Node(), ComponentEnum.keepalived.name());
    }

    private String getRemoteNetwork(SSHHost backupHost) {

        try {
            boolean isHostReachable = IpUtil.isHostReachable(backupHost.getIp(), (int) TimeUnit.SECONDS.toMillis(5));
            if (!isHostReachable) {
                throw new AppException(ErrorCodes.HOST_CONNECT_ERROR);
            }
            boolean isPortReachable = IpUtil.isPortReachable(backupHost.getIp(), backupHost.getPort());
            if (!isPortReachable) {
                throw new AppException(ErrorCodes.PORT_CONNECT_ERROR);
            }

            String findInterByIp = "ip route | grep %s | head -n 1 | awk -F '[ \\t*]' '{print $3}'";
            String network = SSHTools.execute(backupHost, String.format(findInterByIp, backupHost.getIp()));
            network = network.replace("\n", "");
            log.info("[cluster] backup node network:{}", network);
            if (StringUtils.isEmpty(network)) {
                throw new AppException(ErrorCodes.NO_NETWORK_INTERFACE);
            }
            return network;
        } catch (AppException e) {
            log.error("[cluster] query Network Info error", e);
            throw e;
        } catch (Exception e) {
            log.error("[cluster] query Network Info error", e);
            throw new AppException(ErrorCodes.WRONG_PASSWORD);
        }
    }

    @SneakyThrows
    private void mongodbCluster(BatchDeployVO batchDeployVO, String watcherHome) {

        //先清除从节点mongodb数据，并重启从节点mongodb服务
        String clearMongoData = "rm -rf " + watcherHome + "/components/mongodb/mongodb-linux-x86_64-rhel70-4.2.20-rc0/data/*";
        SSHTools.execute(batchDeployVO.getSlave1Node(), clearMongoData);
        SSHTools.execute(batchDeployVO.getSlave2Node(), clearMongoData);
        deployApi.restartService(batchDeployVO.getSlave1Node(), ComponentEnum.mongodb.name());
        deployApi.restartService(batchDeployVO.getSlave2Node(), ComponentEnum.mongodb.name());

        //搭建mongodb集群
        String cmd = "bash " + watcherHome + "/components/mongodb/cluster.sh";
        String result = SSHTools.execute(batchDeployVO.getMasterNode(), cmd);
        log.info("[cluster] mongodbCluster result is {}", result);

        String modifyMongoConfig = "sed -i 's#watcher001:27017#watcher001:27017,watcher002:27017,watcher003:27017#g' " + watcherHome + "/conf/application-prod.properties";
        SSHTools.execute(batchDeployVO.getMasterNode(), modifyMongoConfig);
        SSHTools.execute(batchDeployVO.getSlave1Node(), modifyMongoConfig);
        SSHTools.execute(batchDeployVO.getSlave2Node(), modifyMongoConfig);
    }

    @SneakyThrows
    private void zookeeperCluster(BatchDeployVO batchDeployVO, String watcherHome) {

        deployApi.shutdownService(batchDeployVO.getMasterNode(), ComponentEnum.zookeeper.name());
        deployApi.shutdownService(batchDeployVO.getSlave1Node(), ComponentEnum.zookeeper.name());
        deployApi.shutdownService(batchDeployVO.getSlave2Node(), ComponentEnum.zookeeper.name());

        //#1、修改 apache-zookeeper-3.8.0-bin/data/myid 数字 1 2 3
        String cmd2 = "echo \"2\" > " + watcherHome + "/components/zookeeper/apache-zookeeper-3.8.0-bin/data/myid";
        SSHTools.execute(batchDeployVO.getSlave1Node(), cmd2);

        String cmd3 = "echo \"3\" > " + watcherHome + "/components/zookeeper/apache-zookeeper-3.8.0-bin/data/myid";
        SSHTools.execute(batchDeployVO.getSlave2Node(), cmd3);


        //#2、修改 apache-zookeeper-3.8.0-bin/conf/zoo.cfg 添加多个server.{myid}配置,本机ip写为0.0.0.0
        String zooServer1 = "sed -i '$a server.1=watcher001:2888:3888' " + watcherHome + "/components/zookeeper/apache-zookeeper-3.8.0-bin/conf/zoo.cfg";
        String zooServer2 = "sed -i '$a server.2=watcher002:2888:3888' " + watcherHome + "/components/zookeeper/apache-zookeeper-3.8.0-bin/conf/zoo.cfg";
        String zooServer3 = "sed -i '$a server.3=watcher003:2888:3888' " + watcherHome + "/components/zookeeper/apache-zookeeper-3.8.0-bin/conf/zoo.cfg";

        SSHTools.execute(batchDeployVO.getMasterNode(), zooServer1);
        SSHTools.execute(batchDeployVO.getMasterNode(), zooServer2);
        SSHTools.execute(batchDeployVO.getMasterNode(), zooServer3);

        SSHTools.execute(batchDeployVO.getSlave1Node(), zooServer1);
        SSHTools.execute(batchDeployVO.getSlave1Node(), zooServer2);
        SSHTools.execute(batchDeployVO.getSlave1Node(), zooServer3);

        SSHTools.execute(batchDeployVO.getSlave2Node(), zooServer1);
        SSHTools.execute(batchDeployVO.getSlave2Node(), zooServer2);
        SSHTools.execute(batchDeployVO.getSlave2Node(), zooServer3);

        //#3、依次启动主动节点zookeeper
        deployApi.startupService(batchDeployVO.getMasterNode(), ComponentEnum.zookeeper.name());
        deployApi.startupService(batchDeployVO.getSlave1Node(), ComponentEnum.zookeeper.name());
        deployApi.startupService(batchDeployVO.getSlave2Node(), ComponentEnum.zookeeper.name());

        deployApi.restartService(batchDeployVO.getMasterNode(), ComponentEnum.zookeeper.name());
        deployApi.restartService(batchDeployVO.getSlave1Node(), ComponentEnum.zookeeper.name());
        deployApi.restartService(batchDeployVO.getSlave2Node(), ComponentEnum.zookeeper.name());
    }

    @SneakyThrows
    private void kafkaCluster(BatchDeployVO batchDeployVO, String watcherHome) {

        String kafkaConfigFile = watcherHome + "/components/kafka/kafka_2.12-3.0.0/config/server.properties";

        //#1、修改 kafka_2.12-3.0.0/config/server.properties broker.id数字  1 2 3
        String cmd2 = " sed -i 's#broker.id=1#broker.id=2#g' " + kafkaConfigFile;
        String cmd3 = " sed -i 's#broker.id=1#broker.id=3#g' " + kafkaConfigFile;

        SSHTools.execute(batchDeployVO.getSlave1Node(), cmd2);
        SSHTools.execute(batchDeployVO.getSlave2Node(), cmd3);

        //修改listeners配置：listeners=PLAINTEXT://watcher001:9092
        String cmdListener = " sed -i 's#watcher001#%s#g' " + kafkaConfigFile;
        String cmdListener1 = String.format(cmdListener, batchDeployVO.getMasterNode().getIp());
        SSHTools.execute(batchDeployVO.getMasterNode(), cmdListener1);

        String cmdListener2 = String.format(cmdListener, batchDeployVO.getSlave1Node().getIp());
        SSHTools.execute(batchDeployVO.getSlave1Node(), cmdListener2);

        String cmdListener3 = String.format(cmdListener, batchDeployVO.getSlave2Node().getIp());
        SSHTools.execute(batchDeployVO.getSlave2Node(), cmdListener3);

        //#2、修改 kafka_2.12-3.0.0/config/server.properties zookeeper.connect=watcher001:2181,watcher002:2181,watcher003:2181
        String zkConnect = "sed -i 's#zookeeper.connect=127.0.0.1:2181#zookeeper.connect=%s:2181,%s:2181,%s:2181#g' " + kafkaConfigFile;
        String zkConnectFormat = String.format(zkConnect, batchDeployVO.getMasterNode().getIp(), batchDeployVO.getSlave1Node().getIp(), batchDeployVO.getSlave2Node().getIp());
        SSHTools.execute(batchDeployVO.getMasterNode(), zkConnectFormat);
        SSHTools.execute(batchDeployVO.getSlave1Node(), zkConnectFormat);
        SSHTools.execute(batchDeployVO.getSlave2Node(), zkConnectFormat);

        //清理数据
        String clearData = "rm -rf " + watcherHome + "/components/kafka/kafka_2.12-3.0.0/logs/";

        //#3、依次启动主从节点kafka
        SSHTools.execute(batchDeployVO.getMasterNode(), clearData);
        deployApi.restartService(batchDeployVO.getMasterNode(), ComponentEnum.kafka.name());
        SSHTools.execute(batchDeployVO.getSlave1Node(), clearData);
        deployApi.restartService(batchDeployVO.getSlave1Node(), ComponentEnum.kafka.name());
        SSHTools.execute(batchDeployVO.getSlave2Node(), clearData);
        deployApi.restartService(batchDeployVO.getSlave2Node(), ComponentEnum.kafka.name());
    }

    private void stopThenStartAgent(BatchDeployVO batchDeployVO) {
        deployApi.shutdownService(batchDeployVO.getSlave1Node(), ComponentEnum.agent.name());
        deployApi.startupService(batchDeployVO.getSlave1Node(), ComponentEnum.agent.name());
        deployApi.shutdownService(batchDeployVO.getSlave2Node(), ComponentEnum.agent.name());
        deployApi.startupService(batchDeployVO.getSlave2Node(), ComponentEnum.agent.name());
    }
    @SneakyThrows
    public void ntpCluster(BatchDeployVO batchDeployVO,String watcherHome){
        SSHHost masterNode = batchDeployVO.getMasterNode();
        String masterNodeIp = masterNode.getIp();

        String installntp = "bash "+watcherHome+"/components/ntp/install.sh";
        String restartNtp ="service ntpd restart";
        String sedLocalIburst = "sed -i '/server 127.127.1.0 iburst /d' /etc/ntp.conf";
        log.info(sedLocalIburst);
        String sedLocalStratum = "sed -i '/127.127.1.0 stratum 10  /d' /etc/ntp.conf";
        log.info(sedLocalStratum);
        String echoIburst = "echo 'server " + masterNodeIp + " iburst' >> /etc/ntp.conf";
        log.info(echoIburst);
        String echoRestrict = "echo 'restrict " + masterNodeIp + " nomodify notrap noquery' >> /etc/ntp.conf";
        String startEnable = "systemctl enable ntpd";

        String ntpdate = " ntpdate -u "+masterNodeIp;
        String cronTwo = "echo \" */5 * * * * /user/sbin/ntpdate -u " + masterNodeIp + "  > /dev/null 2>&1 \" >> /var/spool/cron/root";

        String chmod ="chmod 777 /var/spool/cron/root";

        String restartCron ="/sbin/service crond restart";

        String disableChronyd ="systemctl disable chronyd.service";
        //执行安装
        SSHTools.execute(batchDeployVO.getMasterNode(),installntp);
        log.info(batchDeployVO.getMasterNode()+" 安装");
        //主节点服务启动
        SSHTools.execute(batchDeployVO.getMasterNode(),restartNtp);

        SSHTools.execute(batchDeployVO.getSlave1Node(), installntp);
        log.info(batchDeployVO.getSlave1Node()+" 安装");
        SSHTools.execute(batchDeployVO.getSlave2Node(), installntp);
        log.info(batchDeployVO.getSlave2Node()+" 安装");

        SSHTools.execute(batchDeployVO.getSlave1Node(), sedLocalIburst);
        log.info(sedLocalIburst);
        SSHTools.execute(batchDeployVO.getSlave2Node(), sedLocalIburst);
        log.info(sedLocalIburst);
        SSHTools.execute(batchDeployVO.getSlave1Node(), sedLocalStratum);
        log.info(sedLocalStratum);
        SSHTools.execute(batchDeployVO.getSlave2Node(), sedLocalStratum);
        log.info(sedLocalStratum);
        SSHTools.execute(batchDeployVO.getSlave1Node(), echoIburst);
        log.info(echoIburst);
        SSHTools.execute(batchDeployVO.getSlave2Node(), echoIburst);
        log.info(echoIburst);
        SSHTools.execute(batchDeployVO.getSlave1Node(), echoRestrict);
        log.info(echoRestrict);
        SSHTools.execute(batchDeployVO.getSlave2Node(), echoRestrict);
        log.info(echoRestrict);
        SSHTools.execute(batchDeployVO.getSlave1Node(), startEnable);
        log.info(startEnable);
        SSHTools.execute(batchDeployVO.getSlave2Node(), startEnable);
        log.info(startEnable);
        SSHTools.execute(batchDeployVO.getSlave1Node(), ntpdate);
        SSHTools.execute(batchDeployVO.getSlave2Node(), ntpdate);

        SSHTools.execute(batchDeployVO.getSlave1Node(), cronTwo);
        SSHTools.execute(batchDeployVO.getSlave2Node(), cronTwo);


        SSHTools.execute(batchDeployVO.getSlave1Node(), restartNtp);
        SSHTools.execute(batchDeployVO.getSlave2Node(), restartNtp);

        SSHTools.execute(batchDeployVO.getSlave1Node(), chmod);
        SSHTools.execute(batchDeployVO.getSlave2Node(), chmod);

        SSHTools.execute(batchDeployVO.getSlave1Node(),restartCron);
        SSHTools.execute(batchDeployVO.getSlave2Node(),restartCron);

        SSHTools.execute(batchDeployVO.getMasterNode(),disableChronyd);
        SSHTools.execute(batchDeployVO.getSlave1Node(),disableChronyd);
        SSHTools.execute(batchDeployVO.getSlave2Node(),disableChronyd);

    }
}
