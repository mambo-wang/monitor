package com.virtual.cloud.om.self.service.report;

import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.api.DeployApi;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.deploy.DeployVO;
import com.virtual.cloud.om.sdk.dto.SSHHost;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WatcherMemUsageCollector extends DataReportCollector {
    private final DeployApi deployApi;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DeployVO> deploys = this.deployApi.queryAll();
        return deploys.parallelStream().map(deploy -> {
            final String ip = deploy.getIp();
            DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
            vat.setTags(new StringBuilder("ip=").append(ip).append(";").toString());
            try {
                SSHHost sshHost = SSHHost.builder().ip(ip).user(deploy.getUsername()).password(SM4Utils.webDecryptText(deploy.getPassword())).port(22).build();
                // 内存利用率
                String[] memArr = SSHTools.execute(sshHost, "free -m | grep Mem | awk '{print $2,$3}'").replace("\n", Strings.EMPTY).split(" ");
                vat.setValue(new BigDecimal(100).multiply(new BigDecimal(memArr[1]).divide(new BigDecimal(memArr[0]), 2, BigDecimal.ROUND_HALF_UP)).toPlainString());
            } catch (Exception e) {
                e.printStackTrace();
                throw new AppException(ErrorCodes.WATCHER_MEM_USAGE_FAIL, e.getMessage());
            }
            return vat;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.watcher_mem_usage;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.gauge;
    }
}
