package com.virtual.cloud.om.self.service.warn;

import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnDataDTO;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatcherCpuUsageWarnCollector extends WarnReportCollector {
    private final DeployApi deployApi;

    @Override
    protected List<WarnDataDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<WarnDataDTO> value = new CopyOnWriteArrayList<>();
        List<DeployVO> deploys = this.deployApi.queryAll();
        deploys.stream().forEach(deploy -> {
            WarnDataDTO dto = new WarnDataDTO();
            String ip = deploy.getIp();
            try {
                SSHHost sshHost = SSHHost.builder().ip(ip).user(deploy.getUsername()).password(SM4Utils.webDecryptText(deploy.getPassword())).port(22).build();
                // cpu利用率
                String execute = SSHTools.execute(sshHost,
                                new StringBuilder("export TERM=xterm").append(" ; ")
                                        .append("top -b -n 1 | grep '%Cpu' | head -n 1 | awk '{print $8}'").toString())
                        .replace("\n", Strings.EMPTY);
                log.info("节点IP为:{},cpu利用率为:{}", ip, execute);
                String s = new BigDecimal(100).subtract(new BigDecimal(execute)).toPlainString();
                log.info("======={} ", s);
                BigDecimal totalSize = new BigDecimal(execute);
                BigDecimal ONE = new BigDecimal("100");
                BigDecimal subtract = ONE.subtract(totalSize);
                Double cpuRate = Double.parseDouble(subtract.toPlainString());
                if (cpuRate >= WarnConstant.WatcherWarn.WARN_BASIC_COUNT && cpuRate < WarnConstant.WatcherWarn.WARN_URGENT_COUNT) {
                    dto.setType(WarnConstant.WatcherWarn.WATCHER_WARN_TYPE);
                    dto.setSrc("能力中心Agent（"+ip+"）");
                    dto.setName(WarnConstant.eventName.CPU_USAGE_NAME);
                    dto.setObjectType(WarnConstant.ObjectType.WATCHER_OBJECTTYPE);
                    dto.setMessage("能力中心Agent CPU利用率达到或超过90%");
                    dto.setStartsAt(System.currentTimeMillis());
                    dto.setEndsAt(System.currentTimeMillis());
                    dto.setLevel(WarnConstant.WatcherWarn.WARN_LEVEL_SERIOUS);
                    dto.setCount(WarnConstant.count.WARN_COUNT);
                    dto.setWatcherIp(ip);
                    value.add(dto);
                }else if (cpuRate.equals(WarnConstant.WatcherWarn.WARN_URGENT_COUNT)){
                    dto.setType(WarnConstant.WatcherWarn.WATCHER_WARN_TYPE);
                    dto.setSrc("能力中心Agent（"+ip+"）");
                    dto.setName(WarnConstant.eventName.CPU_USAGE_NAME);
                    dto.setObjectType(WarnConstant.ObjectType.WATCHER_OBJECTTYPE);
                    dto.setMessage("能力中心Agent CPU利用率为100%");
                    dto.setStartsAt(System.currentTimeMillis());
                    dto.setEndsAt(System.currentTimeMillis());
                    dto.setLevel(WarnConstant.WatcherWarn.WARN_LEVEL_URGENT);
                    dto.setCount(WarnConstant.count.WARN_COUNT);
                    dto.setWatcherIp(ip);
                    value.add(dto);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
        log.info("[watcher cpu usage value={}]", value);
        return value;
    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.watcher_cpu_warn;
    }

}
