package com.virtual.cloud.om.sdk.dto;

import com.virtual.cloud.om.sdk.dto.deploy.DeployQueryVO;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * ssh远程主机信息
 * Created by m15730 on 2018/2/6.
 * @author m15730
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("SSH远程主机连接信息")
public class SSHHost {

    /** 远程主机的IP地址 */
    private String ip;

    /** 远程主机的用户 */
    private String user;

    /** 远程主机的密码 */
    private String password;

    /** 远程主机的端口号 */
    private Integer port;

    /** 主机名 */
    private String hostName;

    /** 主机id */
    private Long hostId;

    /** 资源id */
    private String resourceId;

    /** 资源类型 */
    private String platform;

    /**
     * 判读一个host是否为空
     * @param sshHost 远程host的信息
     * @return true，false
     */
    public static boolean isLegalHost(SSHHost sshHost){
        return ((sshHost != null) && (StringUtils.isNotEmpty(sshHost.getIp()))
                && (StringUtils.isNotEmpty(sshHost.getUser()))
                && (StringUtils.isNotEmpty(sshHost.getPassword()))
                && (sshHost.getPort() > 0));
    }

    public static SSHHost newInstance(DeployVO deployVO) {
        return SSHHost.builder()
                .ip(deployVO.getIp())
                .password(SM4Utils.webDecryptText(deployVO.getPassword()))
                .user(deployVO.getUsername())
                .port(22)
                .build();
    }

    public static SSHHost newInstance(DeployQueryVO deployQueryVO) {
        return SSHHost.builder()
                .ip(deployQueryVO.getIp())
                .password(SM4Utils.webDecryptText(deployQueryVO.getPassword()))
                .user(deployQueryVO.getUsername())
                .port(22)
                .build();
    }

    public static SSHHost newInstance(String ip, String username, String password) {
        return SSHHost.builder()
                .ip(ip)
                .password(password)
                .user(username)
                .port(22)
                .build();
    }

    public static SSHHost newInstance(String ip, String username, String password, Long hostId, String hostName, String resourceId, String platform) {
        return SSHHost.builder()
                .ip(ip)
                .password(password)
                .user(username)
                .port(22)
                .hostId(hostId)
                .hostName(hostName)
                .resourceId(resourceId)
                .platform(platform)
                .build();
    }
}
