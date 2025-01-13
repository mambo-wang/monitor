package com.virtual.cloud.om.self.service.report;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.deploy.Component;
import com.virtual.cloud.om.sdk.dto.deploy.DeployQueryVO;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.watcher.WatcherBasicInfoDTO;
import com.virtual.cloud.om.sdk.utils.SSHTools;
import com.virtual.cloud.om.sdk.utils.sm4.SM4Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("all")
public class WatcherBasicCollector extends DataReportCollector {


    private  DeployApi deployApi;
    private final ParameterApi parameterApi;
    @Autowired
    public void setDeployApi(DeployApi deployApi) {
        this.deployApi = deployApi;
    }
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        StringBuilder sbTags = new StringBuilder(tags);
        sbTags.append(tags.endsWith(";") ? Strings.EMPTY : ";");
        Optional<String> vip = parameterApi.queryParameterByTypeAndName(Constant.Parameter.SYS_CONF, Constant.Parameter.NAME_VIP);
        List<DeployQueryVO> deployQueryVOS = this.deployApi.queryStatus();
        List<WatcherBasicInfoDTO> value = deployQueryVOS.stream().map(deploy -> {
            final String ip = deploy.getIp();
            WatcherBasicInfoDTO dto = new WatcherBasicInfoDTO();
            dto.setIp(ip);
            if(deploy.getIsMaster()){
                dto.setMaster(1);
                sbTags.append("ip=").append(ip).append(";");
            }else{
                dto.setMaster(0);
            }
            dto.setVip(deploy.getVip());
            dto.setStatus(1);
            SSHHost sshHost = SSHHost.builder().ip(ip).user(deploy.getUsername()).password(SM4Utils.webDecryptText(deploy.getPassword())).port(22).build();
            try {
                // 版本
                String home = this.deployApi.queryWatcherHome(sshHost);
                String versionInfo = SSHTools.execute(sshHost, new StringBuilder("cat ").append(home).append("/bin/watcher.version").toString());
                String version = versionInfo.substring(0, versionInfo.indexOf(" "));
                dto.setVersion(version);
                deploy.getComponents().stream().filter(component -> component.getName().equals("主机") && component.getStatus().equals(Constant.Deploy.STATUS_UNKNOWN))
                        .findFirst().ifPresent(component -> {
                            dto.setStatus(0);
                        });
                deploy.getComponents().stream().filter(component -> component.getName().equals("能力中心Agent服务") && component.getStatus().equals(Constant.Deploy.STATUS_UNKNOWN))
                        .findFirst().ifPresent(component -> {
                            dto.setAgentStatus(0);
                        });
                deploy.getComponents().forEach(component -> {
                    int status = component.getStatus().equals(Constant.Deploy.STATUS_STARTUP) ? 1 : 0;
                    switch (component.getName()) {
                        case Constant.Deploy.SERVICE_NAME_AGENT: {
                            dto.setAgentStatus(status);
                            break;
                        }
                        case Constant.Deploy.SERVICE_NAME_NGINX: {
                            dto.setNginxStatus(status);
                            break;
                        }
                        case Constant.Deploy.SERVICE_NAME_KAFKA: {
                            dto.setKafkaStatus(status);
                            break;
                        }
                        case Constant.Deploy.SERVICE_NAME_MONGODB: {
                            dto.setMongodbStatus(status);
                            break;
                        }
                        case Constant.Deploy.SERVICE_NAME_ZOOKEEPER: {
                            dto.setZookeeperStatus(status);
                            break;
                        }
                    }
                });
                Optional<String> clusterResult = this.parameterApi.queryParameterByTypeAndName(Constant.Parameter.SYS_CONF, Constant.Parameter.NAME_CLUSTER_RESULT);
                if (clusterResult.isPresent() && clusterResult.get().equals(Constant.Parameter.NAME_CLUSTER_RESULT_SUCCESS)) {
                    Component keepalived = this.deployApi.queryStatus(sshHost, home, Constant.Deploy.SERVICE_NAME_KEEPALIVED);
                    dto.setKeepalivedStatus(keepalived.getStatus().equals(Constant.Deploy.STATUS_STARTUP) ? 1 : 0);
                }
            } catch (Exception e) {
                dto.setStatus(0);
                e.printStackTrace();
            }
            return dto;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        vat.setValue(value);
        vat.setTags(sbTags.toString());
        return Lists.newArrayList(vat);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.watcher_basic_info;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
