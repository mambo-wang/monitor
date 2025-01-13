package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DomainBasicCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        List<DomainBasicDTO> infos = Lists.newArrayList();
        //查询全部虚拟机的信息
        String url = CasUriConstants.Domain.DOMAIN_QUERY_ALL;
        List<DomainDTO> domainDTOList = null;
        try {
            domainDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, url, new ParameterizedTypeReference<List<DomainDTO>>() {
                    });
            if (CollUtil.isEmpty(domainDTOList)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
        }
        domainDTOList.forEach(domainDTO -> {
            DomainBasicDTO dto = new DomainBasicDTO();
            Long domainId = domainDTO.getId();
            dto.setId(domainId);
            dto.setUuid(domainDTO.getUuid());
            dto.setClusterId(domainDTO.getClusterId());
            dto.setDomainName(domainDTO.getName());
            dto.setTitle(domainDTO.getTitle());
            dto.setDescription(domainDTO.getDescription());
            dto.setHostId(domainDTO.getHostId());
            dto.setCasToolStatus(domainDTO.getCastoolsStatus());
            dto.setSystem(domainDTO.getSystem());
            dto.setStatus(domainDTO.getStatus());
            dto.setCasToolVersion(domainDTO.getCastoolsVersion());
            dto.setUptime(domainDTO.getUptime());
            dto.setCreateDate(domainDTO.getCreateDate());
            dto.setOsVersion(domainDTO.getOsDesc());
            dto.setProtectModel(domainDTO.getProtectModel());
            dto.setCpu(domainDTO.getCpu());
            dto.setMemory(domainDTO.getMemory());
            try {
                {
                    //查osVersion、autoMigrate、enableVncProxy
                    String urlBasic = String.format(CasUriConstants.Domain.QUERY_DOMAIN_DETAIL_BY_DOMAIN_ID, domainId);
                    DomainDetailBasicDTO basicDTO = this.casRestConnection.get(platform, host, protocol, port,
                            username, password, urlBasic, new ParameterizedTypeReference<DomainDetailBasicDTO>() {
                            });
                    List<KeyValue> keyValue = basicDTO.getKeyValue();
                    Map<String, String> map = new HashMap<>();
                    keyValue.stream().forEach(s -> {
                        if (!s.getKey().equals("CAStools版本")) {
                            if (StringUtils.isEmpty(s.getValue())) {
                                s.setValue(null);
                            }
                            map.put(s.getKey(), s.getValue());
                        } else {
                            map.put(s.getKey(), dto.getCasToolVersion());
                        }
                    });
                    String autoMigrate = map.get("自动迁移");
                    if ("是".equals(autoMigrate)) {
                        autoMigrate = "1";
                    } else {
                        autoMigrate = "0";
                    }
                    dto.setAutoMigrate(Integer.parseInt(autoMigrate));
                    String enableVncProxy = map.get("启用VNC代理");
                    if ("是".equals(enableVncProxy)) {
                        enableVncProxy = "1";
                    } else {
                        enableVncProxy = "0";
                    }
                    dto.setEnableVncProxy(Integer.parseInt(enableVncProxy));
                }
                {
                    //查storage
                    String urlBasicInfo = String.format(CasUriConstants.Domain.DOMAIN_BASIC_INFO, domainId);
                    DomainDetailInfoDTO infoDTO = this.casRestConnection.get(platform, host, protocol, port,
                            username, password, urlBasicInfo, new ParameterizedTypeReference<DomainDetailInfoDTO>() {
                            });
                    if (ObjectUtils.isEmpty(infoDTO.getStorage())) {
                        dto.setStorage(Lists.newArrayList());
                    } else {
                        dto.setStorage(infoDTO.getStorage());
                    }
                    if (ObjectUtils.isEmpty(infoDTO.getNetwork())) {
                        dto.setNetwork(Lists.newArrayList());
                    } else {
                        dto.setNetwork(infoDTO.getNetwork());
                    }
                }
                {
                    //查cpuSocket、cpuCore、vncPort、displayType
                    String urlDetailInfo = String.format(CasUriConstants.Domain.QUERY_DOMAIN_DETAIL_BY_DOMAIN_ID_NEW, domainId);
                    DomainDetailDTO detailDTO = this.casRestConnection.get(platform, host, protocol, port,
                            username, password, urlDetailInfo, new ParameterizedTypeReference<DomainDetailDTO>() {
                            });

                    if (ObjectUtils.isEmpty(detailDTO)) {
                        dto.setCpuSocket(null);
                        dto.setCpuCore(null);
                        dto.setVncPort(null);
                        dto.setDisplayType("");
                    } else {
                        dto.setCpuSocket(detailDTO.getCpuSocket());
                        dto.setCpuCore(detailDTO.getCpuCore());
                        dto.setVncPort(detailDTO.getVncport());
                        dto.setDisplayType(detailDTO.getDisplayType());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            infos.add(dto);
        });
        vat.setValue(infos);
        vat.setTags(tags);
        vat.setTimestamp(System.currentTimeMillis());
        return Stream.of(vat).collect(Collectors.toList());

    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.domain_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}