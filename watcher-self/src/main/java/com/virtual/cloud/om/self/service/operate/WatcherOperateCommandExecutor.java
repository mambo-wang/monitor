package com.virtual.cloud.om.self.service.operate;

import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.OperateCommandApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.WebsocketPushTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.ObjectTypeEnum;
import com.virtual.cloud.om.sdk.constant.operate.OperateTypeEnum;
import com.virtual.cloud.om.sdk.dto.deploy.ComponentManage;
import com.virtual.cloud.om.sdk.dto.deploy.DeployQueryVO;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.operate.OperateQueryDTO;
import com.virtual.cloud.om.sdk.dto.operate.OperateResultDTO;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("all")
public class WatcherOperateCommandExecutor extends OperateCommandApi {
    private final DeployApi deployApi;

    @Override
    public void execute(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, OperateTypeEnum type, OperateResultDTO.DataDTO data) {
        final String uuid = data.getUuid();
        final ObjectTypeEnum objectType = this.type();
        ComponentTypeEnum componentType = ComponentTypeEnum.getByVal(object.getComponentType());
        String watcherIp = object.getWatcherIp();
        OperateResultDTO.DataDTO.ResultObject ro = new OperateResultDTO.DataDTO.ResultObject();
        data.setWatcherIp(watcherIp);
        ro.setComponentType(componentType.val);
        data.setObject(ro);
        data.setResult(OperateResultDTO.DataDTO.ResultEnum.success.val);
        List<DeployVO> deploys = this.deployApi.queryAll();
        deploys.stream().filter(d -> d.getIp().equals(watcherIp)).findFirst().ifPresent(d -> {
            SSHHost sshHost = SSHHost.builder().user(d.getUsername()).password(SM4Utils.webDecryptText(d.getPassword())).port(22).ip(d.getIp()).build();
            ComponentManage cm = new ComponentManage();
            cm.setOperate(type.name());
            cm.setIp(d.getIp());
            switch (componentType) {
                case node: {
                    cm.setName("主机");
                    break;
                }
                default: {
                    cm.setName(componentType.name());
                }
            }
            this.deployApi.componentsManage(cm);
            List<DeployQueryVO> deployQueryVOS = this.deployApi.queryStatus();
            deployQueryVOS.stream().filter(dq -> dq.getIp().equals(d.getIp())).findFirst().ifPresent(dq -> {
                dq.getComponents().stream().filter(c -> {
                    switch (componentType) {
                        case node: {
                            return c.getName().equals("主机");
                        }
                        default: {
                            return c.getName().equals(componentType.name());
                        }
                    }
                }).findFirst().ifPresent(c -> {
                    ro.setComponentStatus(c.getStatus().equals(Constant.Deploy.STATUS_STARTUP) ? 1 : 0);
                });
            });
        });
    }

    @Override
    public Object executeRefresh(String platform, String host, Integer port, String protocol, String username, String password, OperateQueryDTO.TargetObject object, String resourceId, String uuid) {
        return null;
    }

    @Override
    public ObjectTypeEnum type() {
        return ObjectTypeEnum.watcher;
    }

    @Override
    public WebsocketPushTypeEnum refreshStatusType() {
        return null;
    }

    @AllArgsConstructor
    enum ComponentTypeEnum {
        node(1),
        agent(2),
        nginx(3),
        kafka(4),
        mongodb(5),
        zookeeper(6),
        keepalived(7),
        ;

        public final Integer val;

        public static ComponentTypeEnum getByVal(Integer val) {
            Optional<ComponentTypeEnum> first = Arrays.stream(values()).filter(e -> e.val.equals(val)).findFirst();
            return first.isPresent() ? first.get() : null;
        }
    }
}
