package com.virtual.cloud.om.onestor.service.report.basic;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.token.onestor.OnestorRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.OnestoreUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorNodePoolBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.basic.StorNodePoolBasicReportDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.OneStorRestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OneStor节点池基本信息采集器
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class NodePoolBasicCollector extends DataReportCollector {
    private final OnestorRestConnection onestorRestConnection;
    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<StorNodePoolBasicReportDTO> infos = Lists.newArrayList();
        ObjectMapper mapper = new ObjectMapper();
        //OneStor管理节点调用接口后获取集群内的所有相关信息
        try {
            String nodePoolUrl = String.format(OnestoreUriConstants.NodePool.BASIC_INFO_NODE_POOL, onestorRestConnection.getClusterId(host, protocol, username, password, port));
            OneStorRestResult oneStorRestResult = onestorRestConnection.get(host, protocol, username, password, port, nodePoolUrl, new ParameterizedTypeReference<OneStorRestResult>(){}).getBody();
            LinkedHashMap data = (LinkedHashMap)oneStorRestResult.getData();
            List<StorNodePoolBasicDTO>  nodePoolDTOList = mapper.convertValue(data.get("nodepool_list"), new TypeReference<List<StorNodePoolBasicDTO>>(){});
            nodePoolDTOList.forEach(nodePool -> {
                StorNodePoolBasicReportDTO storNodePoolBasicReportDTO = new StorNodePoolBasicReportDTO();
                BeanUtil.copyProperties(nodePool, storNodePoolBasicReportDTO, new String[]{"is_master_subcluster", "safe_domain_flag", "maintain_mode"});
                storNodePoolBasicReportDTO.setFs_id(nodePool.getFsid());
                storNodePoolBasicReportDTO.setIs_master_subcluster(nodePool.is_master_subcluster());
                storNodePoolBasicReportDTO.setSafe_domain_flag(nodePool.getSafe_domain_flag());
                storNodePoolBasicReportDTO.setMaintain_mode(nodePool.getMaintain_mode());
                infos.add(storNodePoolBasicReportDTO);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.stor_nodepool_basic;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
