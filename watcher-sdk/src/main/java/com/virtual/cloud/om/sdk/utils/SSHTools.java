package com.virtual.cloud.om.sdk.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Created by l19767 on 2019/11/16.
 * SSH工具类
 */
@Slf4j
public class SSHTools {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private static final int DEFAULT_READ_INTERVAL = 200;

    /**
     * 将数据存储从远端服务器拷贝到本机。
     *
     * @param srcFileName  数据存储源文件名称(需要是绝对路径)。
     * @param destFileName 数据存储目的文件名称(需要是绝对路径)。
     * @param
     */
    public static String copyStorage(String srcFileName, String destFileName, SSHHost sshHost) throws Exception {
        //为文件路径中的空格前增加转义符"\"

        String srcHostIp = sshHost.getIp();
        String srcHostUserName = sshHost.getUser();
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("sshpass -p ").append(sshHost.getPassword());
        strBuffer.append(" scp -o Ciphers=arcfour128,aes128-ctr,aes192-ctr,aes256-ctr ");
        strBuffer.append(srcHostUserName).append("@");
        strBuffer.append(srcHostIp).append(":\"");
        strBuffer.append(srcFileName.replace(" ", "\\ ")).append("\" \"");
        strBuffer.append(destFileName).append("\"");
        log.info("do ssh: "+strBuffer);
        FuncUtil.runCommand(new String[]{"sh", "-c", strBuffer.toString()}, (int) TimeUnit.HOURS.toMillis(1));

        // 修改文件权限
        strBuffer.delete(0, strBuffer.length());
        strBuffer.append("chmod 644 ");
        strBuffer.append(destFileName);
        return FuncUtil.runCommand(new String[]{"sh", "-c", strBuffer.toString()}, (int) TimeUnit.HOURS.toMillis(1));
    }

    public static String copyStorageNoCiphers(String srcFileName, String destFileName, SSHHost sshHost) throws Exception {
        //为文件路径中的空格前增加转义符"\"

        String srcHostIp = sshHost.getIp();
        String srcHostUserName = sshHost.getUser();
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("sshpass -p ").append(sshHost.getPassword());
//        strBuffer.append(" scp -o Ciphers=arcfour128,aes128-ctr,aes192-ctr,aes256-ctr ");
        strBuffer.append(" scp ");
        strBuffer.append(srcHostUserName).append("@");
        strBuffer.append(srcHostIp).append(":\"");
        strBuffer.append(srcFileName.replace(" ", "\\ ")).append("\" \"");
        strBuffer.append(destFileName).append("\"");
        log.info("copyStorageNoCiphers: "+strBuffer);
        FuncUtil.runCommand(new String[]{"sh", "-c", strBuffer.toString()}, (int) TimeUnit.HOURS.toMillis(1));

        // 修改文件权限
        strBuffer.delete(0, strBuffer.length());
        strBuffer.append("chmod 644 ");
        strBuffer.append(destFileName);
        return FuncUtil.runCommand(new String[]{"sh", "-c", strBuffer.toString()}, (int) TimeUnit.HOURS.toMillis(1));
    }
    /**
     * 将数据存储拷贝到远端服务器。
     *
     * @param srcFileName 数据存储源文件名称(需要是绝对路径)。
     * @param
     */
    public static String copyToRemoteStorage(String srcFileName, String destPath, SSHHost sshHost) throws Exception {
        //为文件路径中的空格前增加转义符"\"
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("sshpass -p ")
                .append("'" + sshHost.getPassword() + "'")
                .append(" scp -P")
                .append(sshHost.getPort())
                .append(" ")
                .append(srcFileName)
                .append(" ")
                .append("root@")
                .append(sshHost.getIp())
                .append(":")
                .append(destPath);

        return FuncUtil.runCommand(new String[]{"sh", "-c", strBuffer.toString()}, (int) TimeUnit.HOURS.toMillis(1));
    }

//sshpass -p '1qaz!QAZ' scp -P22 root@10.99.224.43:/tmp/kk /tmp

    /**
     *  从从远处服务器复制文件到本地
     * @param localPath 目标路径
     * @param remmoteFilePath 远程文件
     * @param sshHost 远程服务器
     * @return
     */
    public static String copyFileFromRemote(String localPath, String remmoteFilePath, SSHHost sshHost){
        StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("sshpass -p ")
                .append("'" + sshHost.getPassword() + "'")
                .append(" scp -P")
                .append(sshHost.getPort())
                .append(" ")
                .append("root@")
                .append(sshHost.getIp())
                .append(":")
                .append(remmoteFilePath)
                .append(" ")
                .append(localPath);
        return FuncUtil.runCommand(new String[]{"sh", "-c", strBuffer.toString()}, (int) TimeUnit.HOURS.toMillis(1));
    }


    public static void rmFile(SSHHost sshHost, String volumePath) {
        Session session = null;
        try {
            session = createSession(sshHost);
            // rm -rf  /vms/isos/file
            StringBuilder cmd = new StringBuilder("rm -rf ").append(volumePath);
            log.info("rm file cmd:" + cmd);
            executeSshCmd(session, cmd.toString());
        } catch (Exception e) {
            log.error("error:" + e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * 获取远端文件大小
     *
     * @param destFileName 数据存储目的文件名称(需要是绝对路径)。
     * @param
     */
    public static String getFileSize(String destFileName, SSHHost sshHost) throws Exception {
        //为文件路径中的空格前增加转义符"\"
        String size = execute(sshHost, "stat -c \"%s\" " + destFileName);
        return size.substring(0, size.lastIndexOf("\n"));
    }


    /**
     * 发送shell命令道远程服务器执行，并收集执行结果
     *
     * @param sshHost 远程服务器的信息
     * @param cmd     shell命令
     * @return
     * @throws Exception
     */
    public static String execute(SSHHost sshHost, String cmd) throws Exception {
        if (StringUtils.isEmpty(sshHost.getPassword())) {
            executeWithoutPassword(sshHost, cmd);
        }
        Session session = null;
        try {
            session = createSession(sshHost);
            return executeSshCmd(session, cmd);
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public static String executeNoException(SSHHost sshHost, String cmd) {
        try {
            return execute(sshHost, cmd);
        } catch (Exception e) {
            log.warn("executeNoException", e);
            return "";
        }
    }


    /**
     * 发送shell命令道远程服务器执行，并收集执行结果
     *
     * @param session ssh会话
     * @param cmd     shell命令
     * @return null如果命令和会话为空
     * @throws Exception 包含jsch本身的异常，以及jsch输出的错误信息
     *                   <ul>
     *                       <li>{@link JSchException}打开通道会话失败</li>
     *                       <li>{@link IOException}会话数据读写失败/li>
     *                       <li>{@link AppException}channel的错误输出流</li>
     *                   </ul>
     */
    public static String executeSshCmd(Session session, String cmd) throws Exception {
        if (session == null || StringUtils.isEmpty(cmd)) {
            return null;
        }
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        if (channelExec == null) {
            return null;
        }
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(cmd);
        channelExec.setErrStream(errorStream);
        channelExec.connect();
        /*
         * 数据传输结果聚合，可能服务命令的回传结果大于1024缓冲区
         * 在这种情况下将所有的缓冲区拼接起来，统一返回调用者即可
         */
        StringBuilder result = new StringBuilder();
        // 远程消息应用层缓冲区
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        /*
         * 命令执行退出的状态码，不等于0代表有错误
         * 可以从errorStream中获取错误的详细信息，并输出日志和抛出异常
         */
        int exitStatus;
        String errorInfo = null;
        while (true) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                result.append(new String(buffer, 0, i, "UTF-8"));
            }
            if (channelExec.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                exitStatus = channelExec.getExitStatus();
                break;
            }
            try {
                Thread.sleep(DEFAULT_READ_INTERVAL);
            } catch (Exception exec) {
            }
        }
        if (exitStatus != 0) {
            errorInfo = errorStream.toString();
        }
        channelExec.disconnect();
        if (StringUtils.isNotEmpty(errorInfo)) {
            throw new AppException(ErrorCodes.SSH_FAIL, errorInfo);
        }
        if (exitStatus != 0) {
            throw new AppException(ErrorCodes.SSH_FAIL, exitStatus);
        }
        return result.toString();
    }

    /**
     * 发送shell命令道远程服务器执行，并收集执行结果
     *
     * @param session ssh会话
     * @param cmd     shell命令
     * @param timeout     shell命令执行超时时间
     * @return null如果命令和会话为空
     * @throws Exception 包含jsch本身的异常，以及jsch输出的错误信息
     *                   <ul>
     *                       <li>{@link JSchException}打开通道会话失败</li>
     *                       <li>{@link IOException}会话数据读写失败/li>
     *                       <li>{@link AppException}channel的错误输出流</li>
     *                   </ul>
     */
    public static String executeSshCmd(Session session, String cmd, long timeout) throws Exception {
        if (session == null || StringUtils.isEmpty(cmd)) {
            return null;
        }
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        if (channelExec == null) {
            return null;
        }
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        InputStream in = channelExec.getInputStream();
        channelExec.setCommand(cmd);
        channelExec.setErrStream(errorStream);
        channelExec.connect();
        /*
         * 数据传输结果聚合，可能服务命令的回传结果大于1024缓冲区
         * 在这种情况下将所有的缓冲区拼接起来，统一返回调用者即可
         */
        StringBuilder result = new StringBuilder();
        // 远程消息应用层缓冲区
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        /*
         * 命令执行退出的状态码，不等于0代表有错误
         * 可以从errorStream中获取错误的详细信息，并输出日志和抛出异常
         */
        int exitStatus;
        String errorInfo = null;
        long startTime = System.currentTimeMillis();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, DEFAULT_BUFFER_SIZE);
                if (i < 0) {
                    break;
                }
                result.append(new String(buffer, 0, i, "UTF-8"));
            }
            if (channelExec.isClosed()) {
                if (in.available() > 0) {
                    continue;
                }
                exitStatus = channelExec.getExitStatus();
                break;
            }
            if(System.currentTimeMillis() - startTime >= timeout){
                exitStatus = 0;
                break;
            }
            try {
                Thread.sleep(DEFAULT_READ_INTERVAL);
            } catch (Exception exec) {
            }
        }
        if (exitStatus != 0) {
            errorInfo = errorStream.toString();
        }
        channelExec.disconnect();
        if (StringUtils.isNotEmpty(errorInfo)) {
            throw new AppException(ErrorCodes.SSH_FAIL, errorInfo);
        }
        if (exitStatus != 0) {
            throw new AppException(ErrorCodes.SSH_FAIL, exitStatus);
        }
        return result.toString();
    }

    /**
     * 发送shell命令道远程服务器执行，并收集执行结果(session 免密)
     *
     * @param sshHost 远程服务器的信息
     * @param cmd     shell命令
     * @return
     * @throws Exception
     */
    public static String executeWithoutPassword(SSHHost sshHost, String cmd) throws Exception {
        Session session = null;
        try {
            session = createSessionWithoutPassword(sshHost);
            return executeSshCmd(session, cmd);
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }


    /**
     * 获取一个和远程服务器的ssh会话连接
     *
     * @param sshHost 远程服务器的信息
     * @return null 如果远程服务器信息不正确
     * @throws JSchException 无法连接到远程服务器
     */
    public static Session createSession(SSHHost sshHost) throws JSchException {
        if (!SSHHost.isLegalHost(sshHost)) {
            throw new AppException(ErrorCodes.SSH_FAIL, "主机信息不完整");
        }
        // 1、获取需要连接的ssh主机的信息
        String hostIp = sshHost.getIp();
        String user = sshHost.getUser();
        String password = sshHost.getPassword();
        int port = sshHost.getPort();

        // 2、连接到ssh的主机
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, hostIp, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();
        return session;
    }

    /**
     * 获取一个和远程服务器的ssh会话连接
     *
     * @param sshHost 远程服务器的信息
     * @return null 如果远程服务器信息不正确
     * @throws JSchException 无法连接到远程服务器
     */
    public static Session createLongTimeOutSession(SSHHost sshHost,Integer timeout) throws JSchException {
        if (!SSHHost.isLegalHost(sshHost)) {
            throw new AppException(ErrorCodes.SSH_FAIL);
        }
        // 1、获取需要连接的ssh主机的信息
        String hostIp = sshHost.getIp();
        String user = sshHost.getUser();
        String password = sshHost.getPassword();
        int port = sshHost.getPort();

        // 2、连接到ssh的主机
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, hostIp, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.setTimeout(timeout);
        session.connect(timeout);
        return session;
    }

    /**
     * 获取一个和远程服务器的ssh会话连接(免密)
     *
     * @param sshHost 远程服务器的信息
     * @return null 如果远程服务器信息不正确
     * @throws JSchException 无法连接到远程服务器
     */
    private static Session createSessionWithoutPassword(SSHHost sshHost) throws JSchException {
        if (!(((sshHost != null) && (StringUtils.isNotEmpty(sshHost.getIp()))
                && (StringUtils.isNotEmpty(sshHost.getUser()))
                && (sshHost.getPort() > 0)))) {
            return null;
        }
        // 1、获取需要连接的ssh主机的信息
        String hostIp = sshHost.getIp();
        String user = sshHost.getUser();
        int port = sshHost.getPort();

        // 2、连接到ssh的主机
        JSch jsch = new JSch();
        jsch.addIdentity("/root/.ssh/id_rsa");
        Session session = jsch.getSession(user, hostIp, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        return session;
    }

    /**
     * 检查指定映象文件是否存在。
     *
     * @param fileName 文件名称。
     */
    public static boolean isExistFile(String fileName, SSHHost sshHost) {
        try {
            String result = execute(sshHost, "ls " + fileName);
            if (StringUtils.isNotEmpty(result) && result.trim().equals(fileName)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            if (e.getMessage().contains("No such file or directory")) {
                return false;
            } else {
                log.error("check file existed failed due to {}", e.getLocalizedMessage());
                throw new AppException(ErrorCodes.NOT_FOUND);
            }
        }
    }
}

