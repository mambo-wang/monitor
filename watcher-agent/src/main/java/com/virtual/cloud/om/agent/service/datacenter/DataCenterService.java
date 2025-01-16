package com.virtual.cloud.om.agent.service.datacenter;

import cn.hutool.core.util.StrUtil;
import com.virtual.cloud.om.agent.entity.OadWatcherStatus;
import com.virtual.cloud.om.agent.entity.WebsocketSate;
import com.virtual.cloud.om.sdk.api.DataCenterApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.utils.StringManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author kf9535
 * @version 1.0
 * @date 2022/5/10 14:26
 */
@Service
@Slf4j
public class DataCenterService implements DataCenterApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    private StringManager sm = StringManager.getManager("Common");

    @Override
    public int getInitStep() {
        return findStep().getStep();
    }


    /**
     * 设置当前初始化步骤
     */
    public void updateStep(Integer step) {
        OadWatcherStatus oadWatcherStatus = this.findStep();
        oadWatcherStatus.setStep(step);
        mongoTemplate.save(oadWatcherStatus);
    }

    /**
     * 查询当前初始化步骤
     */
    public OadWatcherStatus findStep() {
        Query query = new Query();
        List<OadWatcherStatus> oadWatcherStatuses = mongoTemplate.find(query, OadWatcherStatus.class);
        if (oadWatcherStatuses.isEmpty()) {
            return OadWatcherStatus.builder().step(0).build();
        }
        return oadWatcherStatuses.get(0);
    }

    /**
     * 查询当前websocket状态
     */
    public WebsocketSate findWebsocketState() {
        WebsocketSate websocketSate = new WebsocketSate();
        WebsocketSate wsk = mongoTemplate.findOne(new Query(), WebsocketSate.class);
        if (Objects.nonNull(wsk) && !Objects.equals(wsk.getState(), Constant.Websocket.connect_success)) {
            if (StrUtil.isNotBlank(wsk.getMessage())) {
                wsk.setMessage(wsk.getMessage() + sm.getString("websocket.error"));
            } else {
                wsk.setMessage(sm.getString("websocket.conn.error"));
            }
            websocketSate = wsk;
        } else {
            websocketSate.setState(Constant.Websocket.not_lose_connection);
        }
        return websocketSate;
    }

}
