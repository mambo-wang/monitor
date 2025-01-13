package com.virtual.cloud.om.self.service.report;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.watcher.ComponentUsageDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatcherComponentCpuUsageCollector extends DataReportCollector {
    private final DeployApi deployApi;
    private final ParameterApi parameterApi;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DeployVO> deploys = this.deployApi.queryAll();
        return deploys.parallelStream().map(deploy -> {
            final String ip = deploy.getIp();
            DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
            vat.setTags(new StringBuilder("ip=").append(ip).append(";").toString());
            try {
                SSHHost sshHost = SSHHost.builder().ip(ip).user(deploy.getUsername()).password(SM4Utils.webDecryptText(deploy.getPassword())).port(22).build();
                // agent
                String agentPid = SSHTools.execute(sshHost, "jps | grep agent.jar").replaceAll("[^0-9]", Strings.EMPTY);
                String agentCpu = SSHTools.execute(sshHost, new StringBuilder("ps -aux | grep ").append(agentPid).append(" | grep -v grep | awk '{print $3}' | head -n 1").toString()).replace("\n", Strings.EMPTY);
                ComponentUsageDTO agent = new ComponentUsageDTO().setName("agent").setUsage(new BigDecimal(agentCpu).toPlainString());
                // zookeeper
                String zookeeperPid = SSHTools.execute(sshHost, "jps | grep QuorumPeerMain").replaceAll("[^0-9]", Strings.EMPTY);
                String zookeeperCpu = SSHTools.execute(sshHost, new StringBuilder("ps -aux | grep ").append(zookeeperPid).append(" | grep -v grep | awk '{print $3}' | head -n 1").toString()).replace("\n", Strings.EMPTY);
                ComponentUsageDTO zookeeper = new ComponentUsageDTO().setName("zookeeper").setUsage(new BigDecimal(zookeeperCpu).toPlainString());
                // kafka
                String kafkaPid = SSHTools.execute(sshHost, "jps | grep Kafka").replaceAll("[^0-9]", Strings.EMPTY);
                String kafkaCpu = SSHTools.execute(sshHost, new StringBuilder("ps -aux | grep ").append(kafkaPid).append(" | grep -v grep | awk '{print $3}' | head -n 1").toString()).replace("\n", Strings.EMPTY);
                ComponentUsageDTO kafka = new ComponentUsageDTO().setName("kafka").setUsage(new BigDecimal(kafkaCpu).toPlainString());
                // nginx
                String nginxCpu = SSHTools.execute(sshHost, new StringBuilder("ps -aux | grep ").append("'nginx: master'").append(" | grep -v grep | awk '{print $3}' | head -n 1").toString()).replace("\n", Strings.EMPTY);
                ComponentUsageDTO nginx = new ComponentUsageDTO().setName("nginx").setUsage(new BigDecimal(nginxCpu).toPlainString());
                // mongodb
                String mongodbCpu = SSHTools.execute(sshHost, new StringBuilder("ps -aux | grep ").append("mongodb").append(" | grep -v grep | awk '{print $3}' | head -n 1").toString()).replace("\n", Strings.EMPTY);
                ComponentUsageDTO mongodb = new ComponentUsageDTO().setName("mongodb").setUsage(new BigDecimal(mongodbCpu).toPlainString());
                // keepalived
                Optional<String> clusterResult = this.parameterApi.queryParameterByTypeAndName(Constant.Parameter.SYS_CONF, Constant.Parameter.NAME_CLUSTER_RESULT);
                ComponentUsageDTO keepalived = null;
                if (clusterResult.isPresent() && clusterResult.get().equals(Constant.Parameter.NAME_CLUSTER_RESULT_SUCCESS)) {
                    // mongodb
                    String keepalivedCpu = SSHTools.execute(sshHost, new StringBuilder("ps -aux | grep ").append("keepalived").append(" | grep -v grep | awk '{print $3}' | head -n 1").toString()).replace("\n", Strings.EMPTY);
                    keepalived = new ComponentUsageDTO().setName("keepalived").setUsage(new BigDecimal(keepalivedCpu).toPlainString());
                }
                vat.setValue(Lists.newArrayList(agent, zookeeper, kafka, nginx, mongodb, keepalived));
            } catch (Exception e) {
                e.printStackTrace();
                throw new AppException(ErrorCodes.WATCHER_COMPONENT_CPU_USAGE_FAIL, e.getMessage());
            }
            return vat;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.watcher_component_cpu_usage;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
