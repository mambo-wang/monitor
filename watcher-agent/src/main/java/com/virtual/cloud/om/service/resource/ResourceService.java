package com.virtual.cloud.om.service.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.virtual.cloud.om.dto.MandatoryDTO;
import com.virtual.cloud.om.dto.ResourceDTO;
import com.virtual.cloud.om.dto.ResourcesActiveDTO;
import com.virtual.cloud.om.entity.ResourceEntity;
import com.virtual.cloud.om.entity.ResourceRemote;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService implements ResourceApi {
    private final MongoTemplate mongoTemplate;
    private final PlatformTestConnectionApi[] platformTestConnectionApis;
    private Map<ReportResourceEnum, PlatformTestConnectionApi> platformTestConnectionApiMap = Maps.newConcurrentMap();
    private final TaskMgrApi taskMgrApi;
    private final RealTimeLogApi realTimeLogApi;
    private DataCenterApi dataCenterApi;

    @Autowired
    public void setDataCenterApi(DataCenterApi dataCenterApi) {
        this.dataCenterApi = dataCenterApi;
    }

    @PostConstruct
    public void init() {
        Stream.of(platformTestConnectionApis).forEach(connectionApi -> platformTestConnectionApiMap.put(connectionApi.platform(), connectionApi));
    }



    public void resources(List<ResourceDTO> dtos) {
        // 采集端的所有资源
        List<ResourceEntity> resources = this.mongoTemplate.findAll(ResourceEntity.class);
        if (CollUtil.isEmpty(dtos)) {
            this.deleteResourceTask(resources, dtos);
            return;
        }
        final String now = DateUtil.now();

        // filter 过滤掉数据中心的脏数据
        List<ResourceDTO> collect = dtos.stream().filter(dto -> StrUtil.isNotBlank(dto.getId()) && StrUtil.isNotBlank(dto.getAc()) &&
                Objects.nonNull(dto.getPort()) && StrUtil.isNotBlank(dto.getProtocol()) &&
                StrUtil.isNotBlank(dto.getIpAddress()) && StrUtil.isNotBlank(dto.getAuthType()) &&
                Objects.nonNull(dto.getPlatform()) && Objects.nonNull(dto.getServerUsername())).collect(Collectors.toList());
        List<UpsertResourceDTO> upsertResourceList = Lists.newCopyOnWriteArrayList();
        List<ResourceDTO> saveResourceList = Lists.newCopyOnWriteArrayList();
        List<UpsertResourceDTO> upsertRemoteList = Lists.newCopyOnWriteArrayList();
        collect.forEach(dto -> {
            final String id = dto.getId();
            final String ac = dto.getAc();
            final String ci = dto.getCi();
            final String protocol = dto.getProtocol();
            final String authType = dto.getAuthType();
            final String ipAddress = dto.getIpAddress();
            final Integer port = dto.getPort();
            final ReportResourceEnum platform = dto.getPlatform();
            final String serverUsername = dto.getServerUsername();
            final String serverPassword = dto.getServerPassword();
            final Integer serverPort = Objects.isNull(dto.getServerPort()) ? 22 : dto.getServerPort();

            // 需要操作数据库的resource
            Query query = Query.query(Criteria.where("id").is(id));
            Update update = new Update()
                    // 不存在就新增，存在无操作
                    .setOnInsert("_id", id)
                    // 不存在就新增，存在就更新
                    .set("platform", platform)
                    .set("ipAddress", ipAddress)
                    .set("ac", ac)
                    .set("protocol", protocol)
                    .set("authType", authType)
                    .setOnInsert("createTime", now)
                    .set("updateTime", now)
                    .set("port", port)
                    .set("active", 1)
                    .set("serverUsername", serverUsername)
                    .set("serverPort", serverPort);
            if (StrUtil.isNotBlank(ci)) {
                update.set("ci", ci);
            } else {
                update.setOnInsert("ci", ci);
            }
            if (StrUtil.isNotBlank(serverPassword)) {
                update.set("serverPassword", serverPassword);
            } else {
                update.setOnInsert("serverPassword", serverPassword);
            }
            upsertResourceList.add(new UpsertResourceDTO(query, update));
            saveResourceList.add(dto);
            Query querySshAuthQuery = Query.query(Criteria.where("id").is(id));
            // 不存在就新增，存在无操作
            Update updateSshAuthQuery = new Update()
                    .setOnInsert("id", id)
                    // 不存在就新增，存在就更新
                    .set("resourceId", id)
                    .set("ipAddress", ipAddress)
                    .set("platform", platform)
                    .set("resourceName", platform + "_" + ipAddress)
                    .setOnInsert("remote", Constant.Websocket.SSH_REMOTE_CLOSE)
                    .setOnInsert("endTime", System.currentTimeMillis())
                    .setOnInsert("remoteType", Constant.Websocket.SSH_REMOTE_TYPE_CLOSE);
            upsertRemoteList.add(new UpsertResourceDTO(querySshAuthQuery, updateSshAuthQuery));

        });
        if (CollUtil.isNotEmpty(upsertResourceList)) {
            upsertResourceList.forEach(upsert -> this.mongoTemplate.upsert(upsert.getQuery(), upsert.getUpdate(), ResourceEntity.class));
        }
        if (CollUtil.isNotEmpty(upsertRemoteList)) {
            upsertRemoteList.forEach(upsert -> this.mongoTemplate.upsert(upsert.getQuery(), upsert.getUpdate(), ResourceRemote.class));
        }

        // 删除取消纳管的资源的定时任务
        this.deleteResourceTask(resources, saveResourceList);
        // 处理被删除资源的日志采集业务
        this.deleteResourceRealTimeLog(resources, saveResourceList);
    }

    /**
     * 纳管资源新增/修改/删除
     *
     * @param dtos 纳管资源列表
     */
    private void resourceUpsert(Integer datacenterType,String cloudToken,List<ResourceDTO> dtos, String tenantId, byte[] secretKey, String token, String ip,String portal, String operation) {
        log.info("[resource pull][data center={}] resourceUpsert start", ip);
        // 采集端的所有资源
        List<ResourceEntity> resources = this.mongoTemplate.findAll(ResourceEntity.class);
        if (CollUtil.isEmpty(dtos)) {
            this.deleteResourceTask(resources, dtos);
            log.info("[resource pull][data center={}] resource List is empty", ip);
            return;
        }
        log.info("[resource pull][data center={}] resource List size = {}", ip, dtos.size());
        final String now = DateUtil.now();


        // filter 过滤掉数据中心的脏数据
        List<ResourceDTO> collect = dtos.stream().filter(dto -> StrUtil.isNotBlank(dto.getId()) && StrUtil.isNotBlank(dto.getAc()) &&
                Objects.nonNull(dto.getPort()) && StrUtil.isNotBlank(dto.getProtocol()) &&
                StrUtil.isNotBlank(dto.getIpAddress()) && StrUtil.isNotBlank(dto.getAuthType()) &&
                Objects.nonNull(dto.getPlatform()) && Objects.nonNull(dto.getServerUsername())).collect(Collectors.toList());
        log.info("[resource pull][data center={}] resource List after filter size = {}", ip, collect.size());
        List<String> mandatoryIdList = Lists.newCopyOnWriteArrayList();
        List<UpsertResourceDTO> upsertResourceList = Lists.newCopyOnWriteArrayList();
        List<ResourceDTO> saveResourceList = Lists.newCopyOnWriteArrayList();
        List<UpsertResourceDTO> upsertRemoteList = Lists.newCopyOnWriteArrayList();
        collect.forEach(dto -> {
            final String id = dto.getId();
            final String ac = dto.getAc();
            final String ci = dto.getCi();
            final String protocol = dto.getProtocol();
            final String authType = dto.getAuthType();
            final String ipAddress = dto.getIpAddress();
            final Integer port = dto.getPort();
            final ReportResourceEnum platform = dto.getPlatform();
            final String serverUsername = dto.getServerUsername();
            final String serverPassword = dto.getServerPassword();
            final Integer serverPort = Objects.isNull(dto.getServerPort()) ? 22 : dto.getServerPort();
            Integer active = 0;
            // 密码和节点密码不为空，才去做资源连接检查
            if (StrUtil.isNotBlank(ci) && StrUtil.isNotBlank(serverPassword)) {
                ResourcesActiveDTO resourcesActive = new ResourcesActiveDTO();
                resourcesActive.setOperation(operation);
                {
                    long time = System.currentTimeMillis();
                    resourcesActive.setWatcherCode(tenantId);
                    {
                        ResourcesActiveDTO.ActiveResourceMessage activeResourceMessage = new ResourcesActiveDTO.ActiveResourceMessage();
                        activeResourceMessage.setAc(ac).setPlatform(platform).setProtocol(protocol).setPort(port)
                                .setId(id).setIpAddress(ipAddress);
                        resourcesActive.setActiveResourceMessage(activeResourceMessage);
                    }
                    String errorMsg = Strings.EMPTY;
                    PlatformTestConnectionApi testConnectionApi = this.platformTestConnectionApiMap.get(platform);
                    if (Objects.isNull(testConnectionApi)) {
                        errorMsg = "暂不支持的平台类型";
                    }
                    // 先校验平台用户名密码
                    if (StrUtil.isBlank(errorMsg)) {
                        try {
                            errorMsg = CompletableFuture.supplyAsync(() -> testConnectionApi.connection(platform.name(), ipAddress, port, ac, SM4Utils.webDecryptText(ci), protocol, authType))
                                    .get(20, TimeUnit.SECONDS);
                        } catch (TimeoutException e) {
                            errorMsg = String.format("连接资源[ip=%s]超时", ipAddress);
                        } catch (Exception e) {
                            errorMsg = e.getMessage();
                        }
                    }
                    // 再校验服务器root用户名密码
                    SSHHost sshHost = new SSHHost();
                    if (StrUtil.isBlank(errorMsg)) {
                        sshHost.setIp(ipAddress);
                        sshHost.setPassword(SM4Utils.webDecryptText(serverPassword));
                        sshHost.setUser(serverUsername);
                        sshHost.setPort(serverPort);
                        try {
                            final int timeout = 5000;
                            // 建立ssh channel
                            Session sshSession = SSHTools.createLongTimeOutSession(sshHost, timeout);
                            if (Objects.isNull(sshSession)) {
                                errorMsg = "请检查IP地址、节点用户名、节点密码和节点端口号是否正确";
                            } else {
                                //开启shell通道
                                Channel channel = sshSession.openChannel("shell");
                                if (Objects.isNull(channel)) {
                                    errorMsg = "请检查IP地址、节点用户名、节点密码和节点端口号是否正确";
                                } else {
                                    if (!channel.isConnected()) {
                                        channel.connect(timeout);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMsg = String.format("请检查IP地址、节点用户名、节点密码和节点端口号是否正确");
                        }
                    }
                    // 再校验平台类型
                    if (StrUtil.isBlank(errorMsg)) {
                        try {
                            if (!this.checkResourcePlatform(sshHost, platform)) {
                                errorMsg = "请检查平台类型是否正确";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            errorMsg = "请检查平台类型是否正确";
                        }
                    }
                    active = StrUtil.isBlank(errorMsg) ? 1 : 0;
                    resourcesActive.setConnectionStatus(active);
                    resourcesActive.setErrorMessage(errorMsg);
                    log.info("[resource pull][active resource][resourceId={},ip={}][data center={}][u/p={}/{}][s-u/s-p/s-port={}/{}/{}][time={}ms] active result={} , msg : {}", id, ipAddress, ip, ac, ci, serverUsername, serverPassword, serverPort, (System.currentTimeMillis() - time), StrUtil.isBlank(errorMsg), errorMsg);
                }
            }
            // 需要操作数据库的resource
            {
                Query query = Query.query(Criteria.where("id").is(id));
                Update update = new Update()
                        // 不存在就新增，存在无操作
                        .setOnInsert("_id", id)
                        // 不存在就新增，存在就更新
                        .set("platform", platform)
                        .set("ipAddress", ipAddress)
                        .set("ac", ac)
                        .set("protocol", protocol)
                        .set("authType", authType)
                        .setOnInsert("createTime", now)
                        .set("updateTime", now)
                        .set("port", port)
                        .set("active", active)
                        .set("serverUsername", serverUsername)
                        .set("serverPort", serverPort);
                if (StrUtil.isNotBlank(ci)) {
                    update.set("ci", ci);
                } else {
                    update.setOnInsert("ci", ci);
                }
                if (StrUtil.isNotBlank(serverPassword)) {
                    update.set("serverPassword", serverPassword);
                } else {
                    update.setOnInsert("serverPassword", serverPassword);
                }
                upsertResourceList.add(new UpsertResourceDTO(query, update));
                saveResourceList.add(dto);
                Query querySshAuthQuery = Query.query(Criteria.where("id").is(id));
                // 不存在就新增，存在无操作
                Update updateSshAuthQuery = new Update()
                        .setOnInsert("id", id)
                // 不存在就新增，存在就更新
                        .set("resourceId",id)
                        .set("ipAddress",ipAddress)
                        .set("platform",platform)
                        .set("resourceName",platform+"_"+ipAddress)
                        .setOnInsert("remote",Constant.Websocket.SSH_REMOTE_CLOSE)
                        .setOnInsert("endTime",System.currentTimeMillis())
                        .setOnInsert("remoteType",Constant.Websocket.SSH_REMOTE_TYPE_CLOSE);
                upsertRemoteList.add(new UpsertResourceDTO(querySshAuthQuery,updateSshAuthQuery));
            }
            // 需要强制上报静态数据和保留定时任务的resource
            if (active.equals(1)) {
                mandatoryIdList.add(id);
            }
        });
        if (CollUtil.isNotEmpty(upsertResourceList)) {
            upsertResourceList.forEach(upsert -> this.mongoTemplate.upsert(upsert.getQuery(), upsert.getUpdate(), ResourceEntity.class));
        }
        if (CollUtil.isNotEmpty(upsertRemoteList)){
            upsertRemoteList.forEach(upsert->this.mongoTemplate.upsert(upsert.getQuery(),upsert.getUpdate(),ResourceRemote.class));
        }
        // 删除取消纳管的资源的定时任务
        this.deleteResourceTask(resources, saveResourceList);
        // 处理被删除资源的日志采集业务
        this.deleteResourceRealTimeLog(resources, saveResourceList);
    }

    public void deleteResourceTask(List<ResourceEntity> resourcesInDb, List<ResourceDTO> resourceList) {
        List<String> deleteIds;
        if (CollUtil.isEmpty(resourceList)) {
            log.info("[resource pull] delete all resource task");
            deleteIds = resourcesInDb.stream().map(ResourceEntity::getId).collect(Collectors.toList());
        } else {
            // 当前租户的所有资源
            List<String> saveResourceIds = resourceList.stream().map(ResourceDTO::getId).collect(Collectors.toList());
            deleteIds = resourcesInDb.stream().filter(resource -> !saveResourceIds.contains(resource.getId()))
                    .map(ResourceEntity::getId).collect(Collectors.toList());
            log.info("[resource pull] delete resource [ids={}] task", JSONUtil.toJsonStr(deleteIds));
        }
        // 筛选出已经不属于当前租户的资源，删除相关定时任务
        this.taskMgrApi.deleteByResourceId(deleteIds);
        this.delResourRemote(deleteIds);
    }
    private void delResourRemote(List<String> resourceIds){
        try {
            resourceIds.stream().forEach(s->{
                Query query=new Query(Criteria.where("resourceId").is(s));
                mongoTemplate.findAllAndRemove(query,ResourceRemote.class);
            });

        } catch (Exception e) {
           log.error("delete delResourRemote fail");
        }
    }

    public void deleteResourceRealTimeLog(List<ResourceEntity> resourcesInDb, List<ResourceDTO> resourceList) {
        List<String> deleteIds;
        if (CollUtil.isEmpty(resourceList)) {
            log.info("[resource pull] delete all resource real-time-log");
            deleteIds = resourcesInDb.stream().map(ResourceEntity::getId).collect(Collectors.toList());
        } else {
            // 当前租户的所有资源
            List<String> saveResourceIds = resourceList.stream().map(ResourceDTO::getId).collect(Collectors.toList());
            deleteIds = resourcesInDb.stream().filter(resource -> !saveResourceIds.contains(resource.getId()))
                    .map(ResourceEntity::getId).collect(Collectors.toList());
            log.info("[resource pull] delete resource [ids={}] real-time-log", JSONUtil.toJsonStr(deleteIds));
        }
        // 筛选出已经不属于当前租户的资源，删除相关日志采集业务
        this.realTimeLogApi.deleteResourceRealTimeLog(deleteIds);
    }

    /**
     * 资源指定的平台类型是否符合资源ip对应的当前环境实际的平台类型
     * 判断各个平台对应的/etc/xxx.version 文件是否存在，执行find命令，如果不存在会抛出AppException异常
     * <p>
     * 先判断是否是workspace环境，如果不是则判断是否是uis环境，两者都不是的话再判断cas环境，最后只有onestor的才算onestor环境
     *
     * @param sshHost
     * @param platform
     * @throws Exception
     */
    private Boolean checkResourcePlatform(SSHHost sshHost, ReportResourceEnum platform) throws Exception {
        ReportResourceEnum type = null;
        // 当前环境etc目录下的所有文件名集合
        List<String> etcFilesNameList = Lists.newArrayList(SSHTools.execute(sshHost, "ls /etc").split("\\n"));
        if (etcFilesNameList.contains(Constant.Version.WORKSPACE_VERSION)) {
            type = ReportResourceEnum.workspace;
        } else if (etcFilesNameList.contains(Constant.Version.UIS_VERSION)) {
            type = ReportResourceEnum.uis;
        } else if (etcFilesNameList.contains(Constant.Version.CAS_VERSION)) {
            type = ReportResourceEnum.cas;
        } else if (etcFilesNameList.contains(Constant.Version.ONESTOR_VERSION)) {
            type = ReportResourceEnum.onestor;
        }
        return type == platform;
    }

    private static StringManager sm = StringManager.getManager("ErrorCode");

    @Override
    public RestHost findRestHostByResourceId(String resourceId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(resourceId));
        ResourceEntity resource = this.mongoTemplate.findOne(query, ResourceEntity.class);
        if (Objects.isNull(resource)) {
            String errInfo = sm.getString("errorCode.1401", resourceId);
            log.error(errInfo);
            throw new AppException(ErrorCodes.RESTHOST_NONE, resourceId);
        }
        RestHost restHost = BeanUtil.copyProperties(resource, RestHost.class);
        restHost.setHost(resource.getIpAddress());
        restHost.setResourceId(resource.getId());
        restHost.setUsername(resource.getAc());
        restHost.setPassword(SM4Utils.webDecryptText(resource.getCi()));
        restHost.setPlatform(resource.getPlatform());
        restHost.setServerUsername(resource.getServerUsername());
        restHost.setServerPassword(SM4Utils.webDecryptText(resource.getServerPassword()));
        return restHost;
    }

    @Override
    public List<RestHost> findAll() {
        List<ResourceEntity> resourceEntityList = this.mongoTemplate.findAll(ResourceEntity.class);
        List<RestHost> restHostList = resourceEntityList.stream().map(resourceEntity -> {
            RestHost restHost = new RestHost();
            restHost.setHost(resourceEntity.getIpAddress());
            restHost.setPort(resourceEntity.getPort());
            restHost.setProtocol(resourceEntity.getProtocol());
            restHost.setResourceId(resourceEntity.getId());
            restHost.setUsername(resourceEntity.getAc());
            restHost.setPassword(SM4Utils.webDecryptText(resourceEntity.getCi()));
            restHost.setPlatform(resourceEntity.getPlatform());
            restHost.setServerUsername(resourceEntity.getServerUsername());
            restHost.setServerPassword(SM4Utils.webDecryptText(resourceEntity.getServerPassword()));
            return restHost;
        }).collect(Collectors.toList());
        return restHostList;
    }

    @Data
    @AllArgsConstructor
    private class UpsertResourceDTO {
        private Query query;
        private Update update;
    }
}
