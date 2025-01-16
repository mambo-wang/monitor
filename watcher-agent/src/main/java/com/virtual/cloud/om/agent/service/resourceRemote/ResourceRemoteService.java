package com.virtual.cloud.om.agent.service.resourceRemote;

import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import com.virtual.cloud.om.agent.entity.ResourceEntity;
import com.virtual.cloud.om.agent.entity.ResourceRemote;
import com.virtual.cloud.om.agent.entity.ResourceRemoteOamServer;
import com.virtual.cloud.om.sdk.dto.RpcPagingLoadResult;

import java.util.List;

/**
 * @author:XK
 * @Date:2022/9/2 11:12
 */
public interface ResourceRemoteService {

    RpcPagingLoadResult<ResourceRemoteDTO> selectAllResourceInfo(Integer page,Integer size,String platform, String resoureName,String ipAddress);


    void modifyResourceInfo(ResourceRemoteDTO remoteDTO);

    void checkUserSshAuth(ResourceRemoteDTO remoteDTO);

    void  modifyOamServer(List<ResourceRemoteOamServer> oamResourceServer, ResourceRemote resourceRemote);

    void closeSsh(ResourceRemoteDTO resourceRemoteDTO);
}
