package com.virtual.cloud.om.sdk.dto;

import com.virtual.cloud.om.sdk.dto.deploy.DeployQueryVO;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang3.StringUtils;

/**
 * ssh远程主机信息
 * Created by m15730 on 2018/2/6.
 * @author m15730
 */
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

    public SSHHost() {
    }

    public SSHHost(String ip, String user, String password, Integer port, String hostName, Long hostId, String resourceId, String platform) {
        this.ip = ip;
        this.user = user;
        this.password = password;
        this.port = port;
        this.hostName = hostName;
        this.hostId = hostId;
        this.resourceId = resourceId;
        this.platform = platform;
    }

    // Getters and Setters
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public Long getHostId() { return hostId; }
    public void setHostId(Long hostId) { this.hostId = hostId; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

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
        SSHHost host = new SSHHost();
        host.setIp(deployVO.getIp());
        host.setPassword(SM4Utils.webDecryptText(deployVO.getPassword()));
        host.setUser(deployVO.getUsername());
        host.setPort(22);
        return host;
    }

    public static SSHHost newInstance(DeployQueryVO deployQueryVO) {
        SSHHost host = new SSHHost();
        host.setIp(deployQueryVO.getIp());
        host.setPassword(SM4Utils.webDecryptText(deployQueryVO.getPassword()));
        host.setUser(deployQueryVO.getUsername());
        host.setPort(22);
        return host;
    }

    public static SSHHost newInstance(String ip, String username, String password) {
        SSHHost host = new SSHHost();
        host.setIp(ip);
        host.setPassword(password);
        host.setUser(username);
        host.setPort(22);
        return host;
    }

    public static SSHHost newInstance(String ip, String username, String password, Long hostId, String hostName, String resourceId, String platform) {
        SSHHost host = new SSHHost();
        host.setIp(ip);
        host.setPassword(password);
        host.setUser(username);
        host.setPort(22);
        host.setHostId(hostId);
        host.setHostName(hostName);
        host.setResourceId(resourceId);
        host.setPlatform(platform);
        return host;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String ip;
        private String user;
        private String password;
        private Integer port = 22;
        private String hostName;
        private Long hostId;
        private String resourceId;
        private String platform;

        public Builder ip(String ip) { this.ip = ip; return this; }
        public Builder user(String user) { this.user = user; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder port(Integer port) { this.port = port; return this; }
        public Builder hostName(String hostName) { this.hostName = hostName; return this; }
        public Builder hostId(Long hostId) { this.hostId = hostId; return this; }
        public Builder resourceId(String resourceId) { this.resourceId = resourceId; return this; }
        public Builder platform(String platform) { this.platform = platform; return this; }

        public SSHHost build() {
            SSHHost host = new SSHHost();
            host.setIp(this.ip);
            host.setUser(this.user);
            host.setPassword(this.password);
            host.setPort(this.port);
            host.setHostName(this.hostName);
            host.setHostId(this.hostId);
            host.setResourceId(this.resourceId);
            host.setPlatform(this.platform);
            return host;
        }
    }
}
