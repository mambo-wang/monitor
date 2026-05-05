package com.virtual.cloud.om.agent.service.datacenter;

import com.virtual.cloud.om.agent.entity.OadWatcherStatus;
import com.virtual.cloud.om.agent.entity.WebsocketSate;
import com.virtual.cloud.om.sdk.api.DataCenterApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 数据中心服务 - MySQL 单机版
 * MongoDB 版本已禁用，使用简化实现
 * @author kf9535
 */
@Service
@Slf4j
public class DataCenterService implements DataCenterApi {

    @Override
    public int getInitStep() {
        log.debug("[DataCenterService] 获取初始化步骤功能已简化");
        return 0;
    }

    /**
     * 设置当前初始化步骤 - 已禁用
     */
    public void updateStep(Integer step) {
        log.debug("[DataCenterService] 更新步骤功能已禁用");
    }

    /**
     * 查询当前初始化步骤 - 已禁用
     */
    public OadWatcherStatus findStep() {
        log.debug("[DataCenterService] 查询步骤功能已禁用");
        return OadWatcherStatus.builder().step(0).build();
    }

    /**
     * 查询当前websocket状态 - 已禁用
     */
    public WebsocketSate findWebsocketState() {
        log.debug("[DataCenterService] WebSocket状态查询功能已禁用");
        WebsocketSate websocketSate = new WebsocketSate();
        websocketSate.setState(0);
        return websocketSate;
    }
}
