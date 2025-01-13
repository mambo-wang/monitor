package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.*;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShareFileHostBasicCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> value = Lists.newCopyOnWriteArrayList();
        //查询全部集群信息
        List<ClusterDTO> clusterDTOS = null;
        try {
            clusterDTOS = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, CasUriConstants.Cluster.CLUSTER_QUERY_ALL, new ParameterizedTypeReference<List<ClusterDTO>>() {
                    });
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, CasUriConstants.Cluster.CLUSTER_QUERY_ALL,e.getMessage());
        }
        if (CollUtil.isEmpty(clusterDTOS)) {
            return Collections.emptyList();
        }
        clusterDTOS.forEach(clusterDTO -> {
            List<ShareFileHostBasicDTO> infos = Lists.newCopyOnWriteArrayList();
            DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
            //查询指定集群中的共享文件系统信息
            Long clusterId = clusterDTO.getId();
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.CLUSTER_ID, clusterId.toString());
            vat.setTags(tagsTo);
            vat.setTimestamp(System.currentTimeMillis());
            String url = String.format(CasUriConstants.Storage.STORAGE_FS_BY_CLUSTER_ID, clusterId);
            List<ShareFileDTO> shareFileDTOS = null;
            try {
                shareFileDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<ShareFileDTO>>() {
                        });
                if (CollectionUtils.isEmpty(shareFileDTOS)) {
                    vat.setValue(infos);
                    value.add(vat);
                    return;
                }
            } catch (Exception e) {
                log.error("cas rest fail: " + e);
                return;
            }
            shareFileDTOS.forEach(shareFile -> {
                long fsId = shareFile.getId();
                List<ShareFileHostDTO> fileHostDTOS;
                try {
                    //通过共享文件id和集群id查询对应的主机id
                    String uri = String.format(CasUriConstants.Storage.STORAGE_FS_HOST_BY_CLUSTER_ID_AND_FS_ID, clusterId, fsId);
                    fileHostDTOS = this.casRestConnection.get(platform, host, protocol, port,
                            username, password, uri, new ParameterizedTypeReference<List<ShareFileHostDTO>>() {
                            });
                    if (CollectionUtils.isEmpty(fileHostDTOS)) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                fileHostDTOS.forEach(fileHost -> {
                    long hostId = fileHost.getId();
                    String urlHost = CasUriConstants.Host.HOST_ALL_INFO;
                    try {
                        //查询所有主机
                        List<HostDTO> hostDTOList = this.casRestConnection.get(platform, host, protocol, port,
                                username, password, urlHost, new ParameterizedTypeReference<List<HostDTO>>() {
                                });
                        //获取所有使用共享存储主机的id
                        hostDTOList.stream().filter(hostDTO -> hostDTO.getId() == hostId).forEach(hostDTO -> {
                            String urlStorage = String.format(CasUriConstants.Storage.STORAGE_POOL_IN_HOST_URL, hostId);
                            List<StoragePoolDTO> storagePoolDTOList = this.casRestConnection.get(platform, host, protocol, port,
                                    username, password, urlStorage, new ParameterizedTypeReference<List<StoragePoolDTO>>() {
                                    });
                            storagePoolDTOList.forEach(storage -> {
                                String type = storage.getType();
                                ShareFileHostBasicDTO dto = new ShareFileHostBasicDTO();
                                //共享存储的主机池
                                if ("fs".equals(type) && shareFile.getName().equals(storage.getName())) {
                                    dto.setId(hostId);
                                    dto.setFsName(shareFile.getName());
                                    dto.setInitiatorName(hostDTO.getIscsiNodeName());
                                    dto.setHostIp(hostDTO.getIp());
                                    dto.setHostName(hostDTO.getName());
                                    dto.setHostStatus(hostDTO.getStatus());
                                    dto.setPoolstatus(storage.getStatus());
                                    infos.add(dto);
                                }
                            });
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                });
            });
            vat.setValue(infos);
            value.add(vat);
        });
        return value;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.share_file_host_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
