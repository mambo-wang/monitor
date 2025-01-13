package com.virtual.cloud.om.onestor.service.ssh;

import com.virtual.cloud.om.sdk.api.SshAuthAbstract;
import com.virtual.cloud.om.sdk.constant.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author:XK
 * @Date:2023/2/1 14:25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OnestorSSHService extends SshAuthAbstract {
    @Override
    public Boolean checkUserSshAuth(String ip, String protocol, Integer port, String username, String password) {
        return true;
    }

    @Override
    public Boolean modifySshAuth(Boolean flag, String ip, String protocol, Integer port, String username, String password) {
        return true;
    }

    @Override
    public Boolean getSshType(String ip, String protocol, String username, String password, Integer port) {
        return true;
    }

    @Override
    public String resourceType() {
        return Constant.RESOURCE_ONESTOR;
    }
}
