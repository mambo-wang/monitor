package com.virtual.cloud.om.uis.service.report;

import cn.hutool.core.collection.CollUtil;
import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.api.ParameterApi;
import com.virtual.cloud.om.sdk.config.rest.uis.UisRestConnection;
import com.virtual.cloud.om.sdk.constant.*;
import com.virtual.cloud.om.sdk.constant.report.ReportDataTypeEnum;
import com.virtual.cloud.om.sdk.constant.uri.UisUriConstants;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.dataReport.DataValueAndTagsDTO;
import com.virtual.cloud.om.sdk.dto.operate.VmTaskLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 虚拟机任务操作日志上报接口
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UisVmTaskLogReportCollector extends DataReportCollector {



    private final UisRestConnection uisRestConnection;
    private final ParameterApi parameterApi;


    @Override
    public List<DataValueAndTagsDTO> collect(String platform, String host, String protocol, Integer port, String username, String password, String tags, String resourceId) {
        DataValueAndTagsDTO vat = new DataValueAndTagsDTO();
        Integer limit = 2000;
        Integer offset = 0;
        String uri = String.format(UisUriConstants.Log.OPERATION_LOGS_VM, limit, offset, OperationLogTypeEnum.vm.type);
        RpcResult<List<VmTaskLogDto>> listRpcResult = uisRestConnection.get(host, protocol, username, password, port, uri, new ParameterizedTypeReference<RpcResult<List<VmTaskLogDto>>>(){
        }).getBody();
        //根据Id去重
        List<VmTaskLogDto> dataList = listRpcResult.getData().stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(VmTaskLogDto::getId))), ArrayList::new
                )
        );
        //最大id与记录在库中的数值进行对比，如库中id值小，则说明有更新数据，则从库中id+1开始上报新的数据
        Integer lastId = dataList.get(dataList.size() - 1).getId();
        Optional<String> lastLogCollectId = parameterApi.queryParameterByTypeAndName(String.format(Constant.Parameter.Log_Collect_last_Id_Tag, resourceId), String.format(Constant.Parameter.Log_Collect_last_Id_Name, resourceId));
        parameterApi.editParamByTypeAndName(String.valueOf(lastId), String.format(Constant.Parameter.Log_Collect_last_Id_Tag, resourceId), String.format(Constant.Parameter.Log_Collect_last_Id_Name, resourceId));
        if(lastLogCollectId.isPresent()){
            dataList = dataList.stream().filter(p -> p.getId() > Integer.valueOf(lastLogCollectId.get())).collect(Collectors.toList());
        }
        if (CollUtil.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        vat.setValue(dataList);
        vat.setTimestamp(new Date().getTime());
        vat.setTags(tags);
        return Stream.of(vat).collect(Collectors.toList());
    }

    @Override
    public DataReportTypeByMetricEnum metric() {
        return DataReportTypeByMetricEnum.uis_operation_log;
    }

    @Override
    protected ReportDataTypeEnum valueType() {
        return ReportDataTypeEnum.json;
    }
}
