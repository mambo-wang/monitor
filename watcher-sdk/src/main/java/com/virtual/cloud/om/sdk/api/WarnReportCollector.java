package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.dto.RestHost;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WarnReportCollector {

    private static final Logger log = LoggerFactory.getLogger(WarnReportCollector.class);

    /**
     * @param goal 目标id 如 hostId
     * @param tags
     * @return
     */
    public List<String> getId(String goal, String tags) {
        List<String> idList = new ArrayList<>();
        String[] ids = tags.split(";");
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].contains(goal)) {
                String replace = ids[i].replace(goal + "=", "");
                String[] goalIds = replace.split(",");
                List<String> goalIdList = Arrays.asList(goalIds);
                idList.addAll(goalIdList);
            }
        }
        return idList;
    }


    /**
     * 获取数据上报接口报文的data字段值
     *
     * @return
     */
    public List<WarnDataDTO> data(RestHost restHost, String tags) {
        long time = System.currentTimeMillis();
        final String host = restHost.getHost();
        log.info("[warn collect][{}] start now", host);
        final String protocol = restHost.getProtocol();
        final Integer port = restHost.getPort();
        final String username = restHost.getUsername();
        final String password = restHost.getPassword();
        final String resourceId = restHost.getResourceId();
        final String platform = restHost.getPlatform();
        List<WarnDataDTO> vats = collect(platform, host, protocol, port, username, password, tags, resourceId);
        log.info("[warn collect][{}] end , time is {} ms", host, System.currentTimeMillis() - time);
        return vats;
    }

    /**
     * 获取data的值
     *
     *
     * @param platform
     * @param host
     * @param protocol
     * @param port
     * @param username
     * @param password
     * @param resourceId
     * @return
     */
    protected abstract List<WarnDataDTO> collect(String platform,String host, String protocol, Integer port, String username, String password, String tags, String resourceId);

    /**
     * 获取告警信息上报的策略名称
     *
     * @return
     */
    public abstract WarnMetricEnum metric();

}
