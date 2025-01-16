package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import com.virtual.cloud.om.sdk.dto.RpcPagingLoadResult;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.agent.service.resourceRemote.ResourceRemoteService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author:XK
 * @Date:2022/9/1 15:55
 */
@RestController
@Api(tags = "远程命令行权限")
@RequestMapping("/resourceRemote")
public class ResourceRemoteController {

    @Resource
    private ResourceRemoteService resourceRemoteService;

    private static StringManager stringManager = StringManager.getManager("SshAuth");
    @GetMapping("/selectAll")
    public RpcPagingLoadResult<ResourceRemoteDTO> selectAllResource(
                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                                             @RequestParam(value = "platform",required = false) String platform,
                                             @RequestParam(value = "resourceName",required = false) String resourceName,
                                             @RequestParam(value = "ipAddress",required = false) String ipAddress
    ){
        RpcPagingLoadResult<ResourceRemoteDTO> resourceRemoteDTORpcPagingLoadResult = resourceRemoteService.selectAllResourceInfo(page, size, platform, resourceName, ipAddress);
        return resourceRemoteDTORpcPagingLoadResult;
    }
    @PostMapping("/userAuth")
    public RpcResult<Void> userAuthResource(@RequestBody ResourceRemoteDTO remoteDTO) {

        RpcResult rpcResult = new RpcResult<>();
        try {
            resourceRemoteService.checkUserSshAuth(remoteDTO);
            rpcResult.setState(RpcResult.SUCCESS);
            return rpcResult;
        }catch (AppException ex){
            rpcResult.setState(RpcResult.FAILURE);
            rpcResult.setFailureMessage(ex.getLocalizedMessage());
            return rpcResult;
        }catch (Exception e) {
            rpcResult.setState(RpcResult.FAILURE);
            rpcResult.setFailureMessage(stringManager.getString("agent.check.user.ssh.auth",e.getLocalizedMessage()));
            return rpcResult;
        }
    }
    @PostMapping("/modify")
    public RpcResult<Void> modifyAuthResource(@RequestBody ResourceRemoteDTO remoteDTO) {
        RpcResult rpcResult = new RpcResult<>();
        try {
            resourceRemoteService.modifyResourceInfo(remoteDTO);
            rpcResult.setState(RpcResult.SUCCESS);
            rpcResult.setSuccessMessage(stringManager.getString("agent.modify.auth.success"));
            return rpcResult;
        }catch (AppException ex){
            rpcResult.setState(RpcResult.FAILURE);
            rpcResult.setFailureMessage(ex.getLocalizedMessage());
            return rpcResult;
        }catch (Exception e) {
            rpcResult.setState(RpcResult.FAILURE);
            rpcResult.setFailureMessage(stringManager.getString("agent.modify.ssh.auth",e.getLocalizedMessage()));
            return rpcResult;
        }
    }


}
