package com.virtual.cloud.om.self.service.warn;

import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.WarnReportCollector;
import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.sdk.constant.warn.WarnConstant;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
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
public class WatcherMemUsageWarnCollector extends WarnReportCollector {
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
                // 内存利用率
                String[] memArr = SSHTools.execute(sshHost, "free -m | grep Mem | awk '{print $2,$3}'").replace("\n", Strings.EMPTY).split(" ");
                log.info("节点IP为:{},内存利用率处理前为:{}", ip, memArr);
                BigDecimal totalSize = new BigDecimal(memArr[0]);
                BigDecimal usedSize = new BigDecimal(memArr[1]);
                BigDecimal divide = usedSize.divide(totalSize, 4, BigDecimal.ROUND_UP);
                BigDecimal hundred = new BigDecimal("100");
                BigDecimal multiply = divide.multiply(hundred);
                Double memRateDouble = Double.parseDouble(multiply.toPlainString());
                String memRate = memRateDouble + "%";
                log.info("节点IP为:{},内存利用率为:{}", ip, memRate);
                if (memRateDouble >= WarnConstant.WatcherWarn.WARN_BASIC_COUNT && memRateDouble < WarnConstant.WatcherWarn.WARN_URGENT_COUNT) {
                    dto.setType(WarnConstant.WatcherWarn.WATCHER_WARN_TYPE);
                    dto.setSrc("能力中心Agent（"+ip+"）");
                    dto.setName(WarnConstant.eventName.MEM_USAGE_NAME);
                    dto.setObjectType(WarnConstant.ObjectType.WATCHER_OBJECTTYPE);
                    dto.setMessage("能力中心Agent内存利用率达到或超过90%");
                    dto.setStartsAt(System.currentTimeMillis());
                    dto.setEndsAt(System.currentTimeMillis());
                    dto.setLevel(WarnConstant.WatcherWarn.WARN_LEVEL_SERIOUS);
                    dto.setCount(WarnConstant.count.WARN_COUNT);
                    dto.setWatcherIp(ip);
                    value.add(dto);
                } else if (memRateDouble.equals(WarnConstant.WatcherWarn.WARN_URGENT_COUNT)){
                    dto.setType(WarnConstant.WatcherWarn.WATCHER_WARN_TYPE);
                    dto.setSrc("能力中心Agent（"+ip+"）");
                    dto.setName(WarnConstant.eventName.MEM_USAGE_NAME);
                    dto.setObjectType(WarnConstant.ObjectType.WATCHER_OBJECTTYPE);
                    dto.setMessage("能力中心Agent内存利用率为100%");
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
        log.info("[watcher mem usage value={}]", value);
        return value;

    }

    @Override
    public WarnMetricEnum metric() {
        return WarnMetricEnum.watcher_mem_warn;
    }

}
