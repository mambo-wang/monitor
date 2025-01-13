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
import com.virtual.cloud.om.sdk.dto.dataReport.cas.ClusterDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.ShareFileBasicDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.cas.ShareFileDTO;
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
public class ShareFileBasicCollector extends DataReportCollector {
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
            if (CollUtil.isEmpty(clusterDTOS)) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("cas rest fail: " + e);
            throw new AppException(ErrorCodes.RESOURCE_EXCEPTION_REASION, CasUriConstants.Cluster.CLUSTER_QUERY_ALL,e.getMessage());
        }
        clusterDTOS.forEach(clusterDTO -> {
            List<ShareFileBasicDTO> infos = Lists.newCopyOnWriteArrayList();
            DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
            Long clusterId = clusterDTO.getId();
            String url = String.format(CasUriConstants.Storage.STORAGE_FS_BY_CLUSTER_ID, clusterId);
            String tagsTo = TagsUtil.buildTags(resourceId, Constant.Tags.CLUSTER_ID, clusterId.toString());
            vat.setTags(tagsTo);
            vat.setTimestamp(System.currentTimeMillis());
            try {
                List<ShareFileDTO> shareFileDTOS = this.casRestConnection.get(platform, host, protocol, port,
                        username, password, url, new ParameterizedTypeReference<List<ShareFileDTO>>() {
                        });
                if (CollUtil.isEmpty(shareFileDTOS)) {
                    vat.setValue(infos);
                    value.add(vat);
                    return;
                }
                shareFileDTOS.forEach(shareFile -> {
                    ShareFileBasicDTO basicDTO = new ShareFileBasicDTO();
                    basicDTO.setId(shareFile.getId());
                    basicDTO.setName(shareFile.getName());
                    basicDTO.setTitle(shareFile.getName());
                    basicDTO.setType(shareFile.getTypeStr());
                    basicDTO.setPath(shareFile.getPath());
                    basicDTO.setTotalSize(shareFile.getMaxSize());
                    basicDTO.setAllocation(shareFile.getAllocation());
                    basicDTO.setFreeSize(shareFile.getRemainSize());
                    infos.add(basicDTO);
                });
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            vat.setValue(infos);
            value.add(vat);
        });
        return value;
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.share_file_basic;
    }

    @Override
    public ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
