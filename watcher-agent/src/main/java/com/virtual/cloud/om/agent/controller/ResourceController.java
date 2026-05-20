package com.virtual.cloud.om.agent.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.agent.dto.ResourceDTO;
import com.virtual.cloud.om.sdk.dto.RpcListLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.entity.mysql.Resource;
import com.virtual.cloud.om.sdk.mapper.ResourceMapper;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源管理 REST 接口
 */
@RestController
@RequestMapping("/resource")
@Tag(name = "资源管理")
@Slf4j
@CrossOrigin
public class ResourceController {

    @Autowired
    private ResourceMapper resourceMapper;

    @GetMapping("/list")
    @Operation(summary = "查询资源列表")
    public RpcListLoadResult<Resource> list(
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String resourceName,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        if (platform != null && !platform.isEmpty() && !"0".equals(platform)) {
            wrapper.eq(Resource::getPlatform, platform);
        }
        if (resourceName != null && !resourceName.isEmpty()) {
            wrapper.like(Resource::getResourceName, resourceName);
        }
        if (ipAddress != null && !ipAddress.isEmpty()) {
            wrapper.eq(Resource::getIpAddress, ipAddress);
        }
        wrapper.orderByDesc(Resource::getCreateTime);
        List<Resource> records = resourceMapper.selectList(wrapper);
        return RpcListLoadResult.success(records);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "查询资源详情")
    public RpcResult<Resource> detail(@PathVariable String id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return RpcResult.fail("资源不存在");
        }
        return RpcResult.success(resource);
    }

    @PostMapping("/create")
    @Operation(summary = "创建资源")
    public RpcResult<Void> create(@RequestBody ResourceDTO dto) {
        log.info("[ResourceController] create resource: {}", JSONUtil.toJsonStr(dto));
        try {
            Resource resource = new Resource();
            resource.setId(dto.getId() != null ? dto.getId() : IdUtil.fastSimpleUUID());
            resource.setResourceName(dto.getResourceName());
            resource.setPlatform(dto.getPlatform() != null ? dto.getPlatform().toLowerCase() : null);
            resource.setIpAddress(dto.getIpAddress());
            resource.setPort(dto.getPort());
            resource.setProtocol(dto.getProtocol() != null ? dto.getProtocol() : "HTTP");
            resource.setAuthType(dto.getAuthType() != null ? dto.getAuthType() : "Digest");
            resource.setAc(dto.getAc());
            resource.setCi(SM4Utils.webEncryptText(dto.getCi()));
            resource.setServerUsername(dto.getServerUsername());
            resource.setServerPassword(SM4Utils.webEncryptText(dto.getServerPassword()));
            resource.setServerPort(dto.getServerPort() != null ? dto.getServerPort() : 22);
            resource.setActive(1);
            resource.setUsable(1);
            resource.setRemote(0);
            resource.setCreateTime(LocalDateTime.now());
            resource.setUpdateTime(LocalDateTime.now());
            resourceMapper.insert(resource);
            return RpcResult.success("资源创建成功");
        } catch (Exception e) {
            log.error("[ResourceController] create resource error", e);
            return RpcResult.fail("资源创建失败: " + e.getMessage());
        }
    }

    @PostMapping("/batchCreate")
    @Operation(summary = "批量创建资源")
    public RpcResult<Void> batchCreate(@RequestBody List<ResourceDTO> dtos) {
        log.info("[ResourceController] batch create resources, count: {}", dtos.size());
        try {
            for (ResourceDTO dto : dtos) {
                Resource resource = new Resource();
                resource.setId(dto.getId() != null ? dto.getId() : IdUtil.fastSimpleUUID());
                resource.setResourceName(dto.getResourceName());
                resource.setPlatform(dto.getPlatform() != null ? dto.getPlatform().toLowerCase() : null);
                resource.setIpAddress(dto.getIpAddress());
                resource.setPort(dto.getPort());
                resource.setProtocol(dto.getProtocol() != null ? dto.getProtocol() : "HTTP");
                resource.setAuthType(dto.getAuthType() != null ? dto.getAuthType() : "Digest");
                resource.setAc(dto.getAc());
                resource.setCi(SM4Utils.webEncryptText(dto.getCi()));
                resource.setServerUsername(dto.getServerUsername());
                resource.setServerPassword(SM4Utils.webEncryptText(dto.getServerPassword()));
                resource.setServerPort(dto.getServerPort() != null ? dto.getServerPort() : 22);
                resource.setActive(1);
                resource.setUsable(1);
                resource.setRemote(0);
                resource.setCreateTime(LocalDateTime.now());
                resource.setUpdateTime(LocalDateTime.now());

                Resource existing = resourceMapper.selectById(resource.getId());
                if (existing == null) {
                    resourceMapper.insert(resource);
                } else {
                    resourceMapper.updateById(resource);
                }
            }
            return RpcResult.success("批量创建成功");
        } catch (Exception e) {
            log.error("[ResourceController] batch create resources error", e);
            return RpcResult.fail("批量创建失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "更新资源")
    public RpcResult<Void> update(@RequestBody ResourceDTO dto) {
        log.info("[ResourceController] update resource: {}", dto.getId());
        try {
            Resource resource = resourceMapper.selectById(dto.getId());
            if (resource == null) {
                return RpcResult.fail("资源不存在");
            }
            if (dto.getResourceName() != null) {
                resource.setResourceName(dto.getResourceName());
            }
            if (dto.getPlatform() != null) {
                resource.setPlatform(dto.getPlatform().toLowerCase());
            }
            if (dto.getIpAddress() != null) {
                resource.setIpAddress(dto.getIpAddress());
            }
            if (dto.getPort() != null) {
                resource.setPort(dto.getPort());
            }
            if (dto.getProtocol() != null) {
                resource.setProtocol(dto.getProtocol());
            }
            if (dto.getAc() != null) {
                resource.setAc(dto.getAc());
            }
            if (dto.getCi() != null) {
                resource.setCi(SM4Utils.webEncryptText(dto.getCi()));
            }
            if (dto.getServerUsername() != null) {
                resource.setServerUsername(dto.getServerUsername());
            }
            if (dto.getServerPassword() != null) {
                resource.setServerPassword(SM4Utils.webEncryptText(dto.getServerPassword()));
            }
            if (dto.getServerPort() != null) {
                resource.setServerPort(dto.getServerPort());
            }
            resource.setUpdateTime(LocalDateTime.now());
            resourceMapper.updateById(resource);
            return RpcResult.success("资源更新成功");
        } catch (Exception e) {
            log.error("[ResourceController] update resource error", e);
            return RpcResult.fail("资源更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除资源")
    public RpcResult<Void> delete(@PathVariable String id) {
        log.info("[ResourceController] delete resource: {}", id);
        try {
            resourceMapper.deleteById(id);
            return RpcResult.success("资源删除成功");
        } catch (Exception e) {
            log.error("[ResourceController] delete resource error", e);
            return RpcResult.fail("资源删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/usable/{id}")
    @Operation(summary = "更新资源可用状态")
    public RpcResult<Void> updateUsable(@PathVariable String id, @RequestParam Integer usable) {
        try {
            Resource resource = resourceMapper.selectById(id);
            if (resource == null) {
                return RpcResult.fail("资源不存在");
            }
            resource.setUsable(usable);
            resource.setUpdateTime(LocalDateTime.now());
            resourceMapper.updateById(resource);
            return RpcResult.success("状态更新成功");
        } catch (Exception e) {
            log.error("[ResourceController] update usable error", e);
            return RpcResult.fail("状态更新失败: " + e.getMessage());
        }
    }

    @PutMapping("/remote/{id}")
    @Operation(summary = "更新SSH权限")
    public RpcResult<Void> updateRemote(@PathVariable String id, @RequestParam Integer remote) {
        try {
            Resource resource = resourceMapper.selectById(id);
            if (resource == null) {
                return RpcResult.fail("资源不存在");
            }
            resource.setRemote(remote);
            resource.setEndTime(remote == 0 ? LocalDateTime.now() : null);
            resource.setUpdateTime(LocalDateTime.now());
            resourceMapper.updateById(resource);
            return RpcResult.success("SSH权限更新成功");
        } catch (Exception e) {
            log.error("[ResourceController] update remote error", e);
            return RpcResult.fail("SSH权限更新失败: " + e.getMessage());
        }
    }
}
