package com.virtual.cloud.om.onestor.service.clusterBasic;

import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorCluserIdDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.StorClusterPgDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author:XK
 * @Date:2022/8/24 10:19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StorClusterPgCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Resource
    private StorUtils storUtils;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        StorCluserIdDTO clusterId = storUtils.getClusterId(platform, host, protocol, port, username, password, tags, resourceId);
        if (Objects.isNull(clusterId.getId())){
            log.info("ONESTOR StorCluserIdDTO is empty host : {}",host);
            return Collections.emptyList();
        }
        String url = String.format(OnestoreUriConstants.Cluster.STOR_CLUSTER_MONITOC, clusterId.getId());
        OneStorRestResult oneStorRestResult = this.onestorRestConnection.get(host, protocol, username, password, port, url, new ParameterizedTypeReference<OneStorRestResult>() {
        }).getBody();
        if (Objects.isNull(oneStorRestResult)){
            return Collections.emptyList();
        }

        LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
        LinkedHashMap pg = (LinkedHashMap) data.get("pg");
        String pg_warn = ((LinkedHashMap) pg.get("warn")).get("count").toString();
        String pg_critical = ((LinkedHashMap) pg.get("critical")).get("count").toString();
        String pg_ok = ((LinkedHashMap) pg.get("ok")).get("count").toString();
//        String pg_warn = pg.get("warn").toString();
//        String pg_critical = pg.get("critical").toString();
//        String pg_ok = pg.get("ok").toString();

        Integer pg_pgNums = (Integer)pg.get("pgNums");
        StorClusterPgDTO storClusterPgDTO=new StorClusterPgDTO();
        storClusterPgDTO.setFs_id(clusterId.getId());
        storClusterPgDTO.setPg_warn(pg_warn);
        storClusterPgDTO.setPg_critical(pg_critical);
        storClusterPgDTO.setPg_ok(pg_ok);
        storClusterPgDTO.setPg_num(pg_pgNums);
        DataValueAndTagsDTO dataValueAndTagsDTO=new DataValueAndTagsDTO();
        dataValueAndTagsDTO.setTags(tags);
        dataValueAndTagsDTO.setTimestamp(System.currentTimeMillis());
        dataValueAndTagsDTO.setValue(Lists.newArrayList(storClusterPgDTO));
        log.info("stor_cluster_pg : {}",Lists.newArrayList(dataValueAndTagsDTO));
        return Lists.newArrayList(dataValueAndTagsDTO);
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_cluster_pg;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
