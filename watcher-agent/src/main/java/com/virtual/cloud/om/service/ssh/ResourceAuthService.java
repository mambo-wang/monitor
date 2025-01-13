package com.virtual.cloud.om.service.ssh;

import com.google.common.collect.Maps;
import com.virtual.cloud.om.entity.ResourceEntity;
import com.virtual.cloud.om.sdk.api.SshAuthAbstract;
import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author:XK
 * @Date:2022/9/13 9:49
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceAuthService {

    private final SshAuthAbstract[] authAbstracts;
    private Map<String, SshAuthAbstract> authAbstractMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {
        Stream.of(authAbstracts).forEach(sshAuthAbstract -> authAbstractMap.put(sshAuthAbstract.resourceType(), sshAuthAbstract));
    }

    public Boolean checkUserSshAuth(String username,String password, ResourceEntity resourceEntity){
        SshAuthAbstract sshAuthAbstract = authAbstractMap.get(resourceEntity.getPlatform());
        Boolean flag = sshAuthAbstract.checkUserSshAuth( resourceEntity.getIpAddress(), resourceEntity.getProtocol(), resourceEntity.getPort(),username,password);
        return flag;
    }
    public Boolean modifyUserSshAuth(Boolean flag ,String username,String password, ResourceEntity resourceEntity){
        SshAuthAbstract sshAuthAbstract = authAbstractMap.get(resourceEntity.getPlatform());
        return  sshAuthAbstract.modifySshAuth(flag, resourceEntity.getIpAddress(), resourceEntity.getProtocol(), resourceEntity.getPort(),username,password);
    }
    public Boolean getSshType(ResourceEntity resourceEntity){
        SshAuthAbstract sshAuthAbstract = authAbstractMap.get(resourceEntity.getPlatform());
        Boolean sshType = sshAuthAbstract.getSshType(resourceEntity.getIpAddress(), resourceEntity.getProtocol(), resourceEntity.getAc(), resourceEntity.getCi(), resourceEntity.getPort());
        return sshType;
    }
}
