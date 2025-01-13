package com.virtual.cloud.om.cas.service.severPerformanceMonitor;

import cn.hutool.core.collection.CollectionUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.IoWriteAndReadDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.Rates;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.TrendRatesDTO;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author:XK
 * @Date:2022/5/24 15:30
 */

/**
 * 主机io吞吐
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiskThroughputCollector extends DataReportCollector {

    private final CasRestConnection casRestConnection;
    @Resource
    private FindAllIds findAllIds;


    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> finalResult = null;
        if (tags.contains(Constant.Tags.HOST_IDS)) {

            finalResult = this.getHostiskThroughput(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else if (tags.contains(Constant.Tags.DOMAIN_IDS)) {
            finalResult = this.getDomainDiskThroughput(platform, Collections.emptyList(), host, protocol, port, username, password, tags, resourceId);
        } else {
            finalResult = this.getAll(platform, host, protocol, port, username, password, tags, resourceId);
        }
        log.debug("[disk_throughput]==================>>采集完成：size=" + finalResult.size());
        return finalResult;
    }


    private List<DataValueAndTagsDTO> getHostiskThroughput(String platform, List<String> hostIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds;
        if (CollectionUtil.isEmpty(hostIdList)) {
            hostIds = getId(Constant.Tags.HOST_IDS, tags);
        } else {
            hostIds = hostIdList;
        }
        if (CollectionUtil.isEmpty(hostIds)) {
            return Collections.emptyList();
        }
        if (hostIds.contains(Constant.Tags.HOST_IDS_ALL_VALUE)){
            hostIds=this.findAllIds.getHostIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOList = hostIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Host.QUERY_HOST_IO_IOPS, s);
            List<TrendRatesDTO> trendRatesDTOS = null;
            try {
                trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                        });
            } catch (Exception e) {
                log.error("cas disk_throughput is fail : " + e);
            }
            if (CollectionUtil.isEmpty(trendRatesDTOS)) {
                return null;
            }
            List<DataValueAndTagsDTO> oneDiskValue = null;
            try {
                oneDiskValue = this.getOneDiskValue(trendRatesDTOS, resourceId, Constant.Tags.HOST_ID, s);
            } catch (Exception e) {
                log.error("cas [disk_throughput] fail resourceId:{},domainId:{} ", resourceId, s);
            }
            return oneDiskValue;
        }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        return dataValueAndTagsDTOList;
    }

    private List<DataValueAndTagsDTO> getDomainDiskThroughput(String platform, List<String> domainIdList, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> domainIds;
        if (CollectionUtil.isEmpty(domainIdList)) {
            domainIds = getId(Constant.Tags.DOMAIN_IDS, tags);
        } else {
            domainIds = domainIdList;
        }
        if (CollectionUtil.isEmpty(domainIds)) {
            return Collections.emptyList();
        }
        if (domainIds.contains(Constant.Tags.DOMAIN_IDS_ALL_VALUE)){
            domainIds=this.findAllIds.getDomainIds(platform,host,protocol,port,username,password,tags,resourceId);
        }
        List<DataValueAndTagsDTO> dataValueAndTagsDTOList = domainIds.parallelStream().map(s -> {
            String url = String.format(CasUriConstants.Domain.QUERY_DOMAIN_DISK_THROUGHPUT, s);
            List<TrendRatesDTO> trendRatesDTOS = null;
            try {
                trendRatesDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<TrendRatesDTO>>() {
                        });
            } catch (Exception e) {
                log.error("cas disk_throughput is fail : " + e);
            }
            if (CollectionUtil.isEmpty(trendRatesDTOS)) {
                return null;
            }
            List<DataValueAndTagsDTO> oneDiskValue = null;
            try {
                oneDiskValue = this.getOneDiskValue(trendRatesDTOS, resourceId, Constant.Tags.DOMAIN_ID, s);
            } catch (Exception e) {
                log.error("cas [disk_rate] fail resourceId:{},domainId:{} ", resourceId, s);
            }
            return oneDiskValue;
        }).filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());
        return dataValueAndTagsDTOList;
    }

    private List<DataValueAndTagsDTO> getOneDiskValue(List<TrendRatesDTO> trendRatesDTOS, String resourceId, String type, String id) {
        List<TrendRatesDTO> readList = trendRatesDTOS.stream().filter(t -> t.getName().endsWith("读")).collect(Collectors.toList());
        List<TrendRatesDTO> writeList = trendRatesDTOS.stream().filter(t -> t.getName().endsWith("写")).collect(Collectors.toList());
        Map<String, List<Rates>> readMap = readList.stream().collect(Collectors.toMap(TrendRatesDTO::getName, TrendRatesDTO::getRates));
        List<DataValueAndTagsDTO> resuleOne = writeList.stream().map(w -> {
            List<Rates> read = readMap.get(w.getName().replace("-写", "-读"));
            Rates readRates = read.stream().sorted(Comparator.comparing(Rates::getTime).reversed()).findFirst().get();
            Map<String, String> writeRateMap = w.getRates().stream().collect(Collectors.toMap(Rates::getTime, Rates::getRate));
            String writeRates = writeRateMap.get(readRates.getTime());
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            IoWriteAndReadDTO ioWriteAndReadDTO = new IoWriteAndReadDTO();
            ioWriteAndReadDTO.setRead(Double.valueOf(readRates.getRate()));
            ioWriteAndReadDTO.setWrite(Double.valueOf(writeRates));
            dataValueAndTagsDTO.setValue(ioWriteAndReadDTO);
            String dev = "dev=" + w.getName().replace("-写", "");
            String tagsTo = TagsUtil.buildTags(resourceId, type, id, dev);
            dataValueAndTagsDTO.setTags(tagsTo);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(readRates.getTime()));
            return dataValueAndTagsDTO;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return resuleOne;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.disk_throughput;
    }


    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }


    public List<DataValueAndTagsDTO> dealData(List<Rates> read, Map<String, String> writeMap, String resourceId, String s) {
        List<DataValueAndTagsDTO> dataValueAndTagsDTOS = read.stream().map(r -> {
            DataValueAndTagsDTO dataValueAndTagsDTO = new DataValueAndTagsDTO();
            IoWriteAndReadDTO writeAndReadDTO = new IoWriteAndReadDTO();
            writeAndReadDTO.setRead(Double.valueOf(r.getRate()));
            writeAndReadDTO.setWrite(Double.valueOf(writeMap.get(r.getTime())));
            dataValueAndTagsDTO.setValue(writeAndReadDTO);
            String tagsTO = TagsUtil.buildTags(resourceId, Constant.Tags.HOST_ID, s);
            dataValueAndTagsDTO.setTags(tagsTO);
            dataValueAndTagsDTO.setTimestamp(Long.valueOf(r.getTime()));
            return dataValueAndTagsDTO;
        }).collect(Collectors.toList());
        return dataValueAndTagsDTOS;
    }

    private List<DataValueAndTagsDTO> getAll(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<String> hostIds = findAllIds.getHostIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<String> domainIds = findAllIds.getDomainIds(platform, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> hostIoIops = this.getHostiskThroughput(platform, hostIds, host, protocol, port, username, password, tags, resourceId);
        List<DataValueAndTagsDTO> domainIoIops = this.getDomainDiskThroughput(platform, domainIds, host, protocol, port, username, password, tags, resourceId);
        hostIoIops.addAll(domainIoIops);
        return hostIoIops;

    }
}
