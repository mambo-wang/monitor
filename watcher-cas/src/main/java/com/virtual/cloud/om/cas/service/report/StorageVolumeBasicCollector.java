package com.virtual.cloud.om.cas.service.report;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.config.rest.cas.CasRestConnection;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.CasUriConstants;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.HostDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.StorageVolumeBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.StorageVolumeDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.utils.TagsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageVolumeBasicCollector extends DataReportCollector {
    private final CasRestConnection casRestConnection;

    @Override
    protected List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        List<DataValueAndTagsDTO> value = Lists.newCopyOnWriteArrayList();
        //查询主机列表
        String url = CasUriConstants.Host.HOST_ALL_INFO;
        List<HostDTO> hostDTOList = null;
        try {
            hostDTOList = this.casRestConnection.get(platform, host, protocol, port,
                    username, password, url, new ParameterizedTypeReference<List<HostDTO>>() {
                    });
            if (CollUtil.isEmpty(hostDTOList)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, url,e.getMessage());
        }
        hostDTOList.forEach(hostDTO -> {
            DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
            List<StorageVolumeBasicDTO> infos = Lists.newCopyOnWriteArrayList();
            Long hostId = hostDTO.getId();
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.HOST_ID, hostId.toString());
            vat.setTags(tagsTo);
            vat.setTimestamp(System.currentTimeMillis());
            String uri = String.format(CasUriConstants.Storage.STORAGE_HOST_VOLUME_ALL_BY_HOST_ID, hostId);
            List<StorageVolumeDTO> storageVolumeDTOList;
            try {
                storageVolumeDTOList = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, uri, new ParameterizedTypeReference<List<StorageVolumeDTO>>() {
                        });
                if (CollectionUtil.isEmpty(storageVolumeDTOList)) {
                    vat.setValue(infos);
                    value.add(vat);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            storageVolumeDTOList.forEach(storageVolume -> {
                StorageVolumeBasicDTO basicDTO = new StorageVolumeBasicDTO();
                basicDTO.setStoragePoolName(storageVolume.getStoragePoolName());
                basicDTO.setName(storageVolume.getName());
                basicDTO.setTotalSize(storageVolume.getSize());
                basicDTO.setAllocation(storageVolume.getAllocation());
                basicDTO.setFormat(storageVolume.getFormat());
                basicDTO.setUsers(storageVolume.getUsers());
                //basicDTO中baseFile字段cas没有返回
                basicDTO.setIsBaseFile(storageVolume.getBaseFile());
                basicDTO.setIsTempImg(storageVolume.getTempImg());
                infos.add(basicDTO);
            });
            vat.setValue(infos);
            value.add(vat);
        });
        return value;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.storage_volume_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
