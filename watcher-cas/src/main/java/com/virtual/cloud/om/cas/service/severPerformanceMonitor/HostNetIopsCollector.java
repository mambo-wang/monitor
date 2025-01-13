package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.*;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/5/24 15:46
 */

/**
 * 服务器下性能监控 网络吞吐量
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HostNetIopsCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.HOST_IDS)) {
            finalResult = this.getHostNetIops(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainNetIops(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAll(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[net_throughput]==================>>采集完成：size=" + finalResult.size());
        return finalResult;

    }

    private List<DataValueAndTagsDTO> getHostNetIops(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds;
        if (CollectionUtil.isEmpty(hostIdList)) {
            hostIds = getId(Constant.Tags.HOST_IDS, tags);
        } else {
            hostIds = hostIdList;
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = new CopyOnWriteArrayList<>();
        if (CollectionUtil.isEmpty(hostIds)) {
            return Collections.emptyList();
        }
        if (hostIds.contains(Constant.Tags.HOST_IDS_ALL_VALUE)){
            hostIds=this.findAllIds.getHostIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        hostIds.parallelStream().forEach(s -> {
            String url = String.format(CasUriConstants.Host.QUERY_HOST_NET_IOPS, s);
            try {
                List<TrendRatesDTO> trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                        });
                if (CollectionUtil.isNotEmpty(trendRatesDTOS)) {
                    List<DataValueAndTagsDTO> dataValueAndTagsDTOSResult = this.addData(trendRatesDTOS, resourceId, Constant.Tags.HOST_ID, s);
                    dataValueAndTagsDTOS.addAll(dataValueAndTagsDTOSResult);
                }
            } catch (Exception e) {
                log.error("cas net_throughput is fail : " + e);
            }
        });
        return dataValueAndTagsDTOS.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<DataValueAndTagsDTO> getDomainNetIops(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds;
        if (CollectionUtil.isEmpty(domainIdList)) {
            domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        } else {
            domainIds = domainIdList;
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = new CopyOnWriteArrayList<>();
        if (CollectionUtil.isEmpty(domainIds)) {
            return Collections.emptyList();
        }
        if (domainIds.contains(Constant.Tags.DOMAIN_IDS_ALL_VALUE)){
            domainIds=this.findAllIds.getDomainIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        domainIds.parallelStream().forEach(s -> {
            String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_NET_IOPS, s);
            try {
                List<PerfDataDTO> perfDataDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<PerfDataDTO>>() {
                        });
                if (CollectionUtil.isNotEmpty(perfDataDTOS)) {
                    List<DataValueAndTagsDTO> dataValueAndTagsDTOSResult = this.addDataDmoain(perfDataDTOS, resourceId, Constant.Tags.DOMAIN_ID, s);
                    dataValueAndTagsDTOS.addAll(dataValueAndTagsDTOSResult);
                }
            } catch (Exception e) {
                log.error("cas net_throughput is fail : " + e);
            }
        });
        return dataValueAndTagsDTOS;
    }

    private List<DataValueAndTagsDTO> addData( List<TrendRatesDTO> trendRatesDTOS, String resourceId, String type, String id) {
        List<TrendRatesDTO> receiveTrendRatesDTOList = trendRatesDTOS.stream().filter(h -> h.getName().endsWith("接收")).collect(Collectors.toList());
        List<TrendRatesDTO> sendTrendRatesDTOList = trendRatesDTOS.stream().filter(h -> h.getName().endsWith("发送")).collect(Collectors.toList());
        Map<String, List<Rates>> receiveDevMap = receiveTrendRatesDTOList.stream().collect(Collectors.toMap(TrendRatesDTO::getValue, TrendRatesDTO::getRates));
        List<DataValueAndTagsDTO> DataValueAndTagsDTOList = sendTrendRatesDTOList.parallelStream().map(a -> {
            List<Rates> sendRates = a.getRates();
            List<Rates> receiveRates = receiveDevMap.get(a.getValue());
            Map<String, String> receiveRateMap = receiveRates.stream().collect(Collectors.toMap(Rates::getTime, Rates::getRate));
            Rates sendRate = sendRates.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            String receiveRate = receiveRateMap.get(sendRate.getTime());
            SendAndReceiveDTO sendAndReceiveDTO = new SendAndReceiveDTO();
            sendAndReceiveDTO.setSend(Double.valueOf(sendRate.getRate())).setReceive(Double.valueOf(receiveRate));
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(sendAndReceiveDTO);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(sendRate.getTime()));
            String dev = "dev=" + a.getValue();
            String tagsTo = TagsUtil.buildTags(resourceId, type, id, dev);
            dataValueAndTagsDTO.setTags(tagsTo);
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return DataValueAndTagsDTOList;
    }

    private List<DataValueAndTagsDTO> addDataDmoain( List<PerfDataDTO> perfDataDTOS, String resourceId, String type, String id) {
        List<PerfDataDTO> receiveTrendRatesDTOList = perfDataDTOS.stream().filter(h -> h.getName().endsWith("接收")).collect(Collectors.toList());
        List<PerfDataDTO> sendTrendRatesDTOList = perfDataDTOS.stream().filter(h -> h.getName().endsWith("发送")).collect(Collectors.toList());
        Map<String, PerfDataDTO> receiveDevMap = receiveTrendRatesDTOList.stream().collect(Collectors.toMap(PerfDataDTO::getName, perfDataDTO -> perfDataDTO));
        List<DataValueAndTagsDTO> dataList = sendTrendRatesDTOList.parallelStream().map(a -> {
            PerfDataDTO receivePerfDTO = receiveDevMap.get(a.getName().replace("发送", "接收"));
            Map<String, String> receiveTimeAndRateMap = receivePerfDTO.getList().stream().collect(Collectors.toMap(RateListDTO::getTime, RateListDTO::getRate));
            RateListDTO sendRate = a.getList().stream().sorted(Comparator.comparing(RateListDTO::getTime).reversed()).findFirst().get();
            SendAndReceiveDTO sendAndReceiveDTO = new SendAndReceiveDTO();
            sendAndReceiveDTO.setSend(Double.valueOf(sendRate.getRate())).setReceive(Double.valueOf(receiveTimeAndRateMap.get(sendRate.getTime())));
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            dataValueAndTagsDTO.setValue(sendAndReceiveDTO);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(sendRate.getTime()));
            String dev = "dev=" + a.getName().replace("-发送", "");
            String tagsTo = TagsUtil.buildTags(resourceId, type, id, dev);
            dataValueAndTagsDTO.setTags(tagsTo);
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return dataList;
    }

    private List<DataValueAndTagsDTO> getAll(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<String> domainIds = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> hostIoIops = this.getHostNetIops(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> domainIoIops = this.getDomainNetIops(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        hostIoIops.addAll(domainIoIops);
        return hostIoIops;

    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.net_throughput;
    }


    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }

}
