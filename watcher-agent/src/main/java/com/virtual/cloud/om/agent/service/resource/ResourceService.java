package com.virtual.cloud.om.agent.service.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virtual.cloud.om.agent.dto.ResourceDTO;
import com.virtual.cloud.om.agent.entity.ResourceEntity;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        });
        if (CollUtil.isNotEmpty(upsertResourceList)) {
            upsertResourceList.forEach(upsert -> this.mongoTemplate.upsert(upsert.getQuery(), upsert.getUpdate(), ResourceEntity.class));
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
        //todo wb
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
