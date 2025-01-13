package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.ResourceRemoteDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author:XK
 * @Date:2022/9/13 9:28
 */
@Slf4j
public abstract class SshAuthAbstract {

    public abstract Boolean checkUserSshAuth( String ip, String protocol, Integer port,String username, String password);

    public abstract Boolean modifySshAuth(Boolean flag,String ip,String protocol,Integer port,String username ,String password);

    public abstract Boolean getSshType(String ip,String protocol,String username,String password,Integer port);

    public abstract String resourceType();

}
