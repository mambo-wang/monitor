package com.virtual.cloud.om.agent.service.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virtual.cloud.om.agent.dto.ResourceDTO;
import com.virtual.cloud.om.sdk.api.*;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.ReportResourceEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.entity.mysql.ResourceEntity;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.mapper.ResourceEntityMapper;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 资源服务 - MySQL 单机版
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService implements ResourceApi {
    
    private final ResourceEntityMapper resourceEntityMapper;
    private final PlatformTestConnectionApi[] platformTestConnectionApis;
    private Map<ReportResourceEnum, PlatformTestConnectionApi> platformTestConnectionApiMap = Maps.newConcurrentMap();
    private final TaskMgrApi taskMgrApi;
    private final com.virtual.cloud.om.sdk.mapper.ResourceMapper resourceMapper;

    @PostConstruct
    public void init() {
        Stream.of(platformTestConnectionApis).forEach(connectionApi -> platformTestConnectionApiMap.put(connectionApi.platform(), connectionApi));
    }

    /**
     * 资源同步 - 从 CollectController 调用
     */
    public void syncResources(List<ResourceDTO> dtos) {
        log.info("[ResourceService] sync resources, count: {}", dtos.size());
        for (ResourceDTO dto : dtos) {
            try {
                com.virtual.cloud.om.sdk.entity.mysql.Resource resource = new com.virtual.cloud.om.sdk.entity.mysql.Resource();
                resource.setId(dto.getId() != null ? dto.getId() : cn.hutool.core.util.IdUtil.fastSimpleUUID());
                resource.setResourceName(dto.getResourceName());
                resource.setPlatform(dto.getPlatform() != null ? dto.getPlatform().toLowerCase() : null);
                resource.setIpAddress(dto.getIpAddress());
                resource.setPort(dto.getPort());
                resource.setProtocol(dto.getProtocol());
                resource.setAuthType(dto.getAuthType());
                resource.setAc(dto.getAc());
                resource.setCi(dto.getCi());
                resource.setServerUsername(dto.getServerUsername());
                resource.setServerPassword(dto.getServerPassword());
                resource.setServerPort(dto.getServerPort() != null ? dto.getServerPort() : 22);
                resource.setActive(1);
                resource.setUsable(1);
                resource.setCreateTime(java.time.LocalDateTime.now());
                resource.setUpdateTime(java.time.LocalDateTime.now());

                com.virtual.cloud.om.sdk.entity.mysql.Resource existing = resourceMapper.selectById(resource.getId());
                if (existing == null) {
                    resourceMapper.insert(resource);
                } else {
                    resourceMapper.updateById(resource);
                }
            } catch (Exception e) {
                log.error("[ResourceService] sync resource error, id: {}", dto.getId(), e);
            }
        }
    }

    /**
     * 资源同步
     */
    public void resources(List<ResourceDTO> dtos) {
        List<ResourceEntity> resources = resourceEntityMapper.selectList(null);
        if (CollUtil.isEmpty(dtos)) {
            this.deleteResourceTask(resources, dtos);
            return;
        }
        
        final String now = DateUtil.now();
        
        List<ResourceDTO> validDtos = dtos.stream()
            .filter(dto -> StrUtil.isNotBlank(dto.getId()) && StrUtil.isNotBlank(dto.getAc()) &&
                    Objects.nonNull(dto.getPort()) && StrUtil.isNotBlank(dto.getProtocol()) &&
                    StrUtil.isNotBlank(dto.getIpAddress()) && StrUtil.isNotBlank(dto.getAuthType()) &&
                    Objects.nonNull(dto.getPlatform()) && Objects.nonNull(dto.getServerUsername()))
            .collect(Collectors.toList());
        
        for (ResourceDTO dto : validDtos) {
            ResourceEntity entity = new ResourceEntity();
            entity.setId(dto.getId());
            entity.setPlatform(dto.getPlatform());
            entity.setIpAddress(dto.getIpAddress());
            entity.setAc(dto.getAc());
            entity.setCi(dto.getCi());
            entity.setProtocol(dto.getProtocol());
            entity.setAuthType(dto.getAuthType());
            entity.setPort(dto.getPort());
            entity.setActive(1);
            entity.setServerUsername(dto.getServerUsername());
            entity.setServerPort(Objects.isNull(dto.getServerPort()) ? 22 : dto.getServerPort());
            entity.setServerPassword(dto.getServerPassword());
            entity.setUpdateTime(now);
            
            ResourceEntity existing = resourceEntityMapper.selectById(dto.getId());
            if (existing == null) {
                entity.setCreateTime(now);
                resourceEntityMapper.insert(entity);
            } else {
                resourceEntityMapper.updateById(entity);
            }
        }
        
        this.deleteResourceTask(resources, validDtos);
        this.deleteResourceRealTimeLog(resources, validDtos);
    }

    public void deleteResourceTask(List<ResourceEntity> resourcesInDb, List<ResourceDTO> resourceList) {
        List<String> deleteIds;
        if (CollUtil.isEmpty(resourceList)) {
            log.info("[resource pull] delete all resource task");
            deleteIds = resourcesInDb.stream().map(ResourceEntity::getId).collect(Collectors.toList());
        } else {
            List<String> saveResourceIds = resourceList.stream().map(ResourceDTO::getId).collect(Collectors.toList());
            deleteIds = resourcesInDb.stream()
                .filter(resource -> !saveResourceIds.contains(resource.getId()))
                .map(ResourceEntity::getId)
                .collect(Collectors.toList());
            log.info("[resource pull] delete resource [ids={}] task", JSONUtil.toJsonStr(deleteIds));
        }
        this.taskMgrApi.deleteByResourceId(deleteIds);
    }

    public void deleteResourceRealTimeLog(List<ResourceEntity> resourcesInDb, List<ResourceDTO> resourceList) {
        log.info("[resource pull] real-time-log feature disabled in MySQL standalone mode");
    }

    private Boolean checkResourcePlatform(SSHHost sshHost, ReportResourceEnum platform) throws Exception {
        ReportResourceEnum type = null;
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
        // 查询 Resource 表（前端创建的资源和查询都使用此表）
        com.virtual.cloud.om.sdk.entity.mysql.Resource resource = resourceMapper.selectById(resourceId);
        if (Objects.isNull(resource)) {
            String errInfo = sm.getString("errorCode.1401", resourceId);
            log.error(errInfo);
            throw new AppException(ErrorCodes.RESTHOST_NONE, resourceId);
        }
        RestHost restHost = new RestHost();
        restHost.setHost(resource.getIpAddress());
        restHost.setResourceId(resource.getId());
        restHost.setPort(resource.getPort());
        restHost.setProtocol(resource.getProtocol() != null ? resource.getProtocol() : "HTTP");
        restHost.setUsername(resource.getAc());
        restHost.setPassword(SM4Utils.webDecryptText(resource.getCi()));
        restHost.setPlatform(resource.getPlatform());
        restHost.setServerUsername(resource.getServerUsername());
        restHost.setServerPassword(SM4Utils.webDecryptText(resource.getServerPassword()));
        return restHost;
    }

    @Override
    public List<RestHost> findAll() {
        List<ResourceEntity> resourceEntityList = resourceEntityMapper.selectList(null);
        return resourceEntityList.stream().map(resourceEntity -> {
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
    }
}
