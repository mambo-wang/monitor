package com.virtual.cloud.om.onestor.service.report.monitor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.virtual.cloud.om.sdk.constant.onestor.report.OneStorNodePoolMonitorTargetEnum;
import com.virtual.cloud.om.sdk.dto.dataReport.onestor.monitor.OneStorMonitorDTO;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OneStor监控数据收集器公用方法
 * */
public interface IOnestorMonitorCollector {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //处理从OneStor获取到的监控数据
    default double getData(Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap, String targetValue, String ...names){
        String formatTarget = String.format(targetValue, names);
        OneStorMonitorDTO oneStorMonitorDTO = oneStorMonitorDTOMap.get(formatTarget);
        if(ObjectUtil.isEmpty(oneStorMonitorDTO) || oneStorMonitorDTO.getDatapoints().size() < 1){
            return 0;
        }
        List<List> dataPoints = oneStorMonitorDTO.getDatapoints();
        List dataInfo = dataPoints.get(dataPoints.size() - 1);
        Double value = (Double)dataInfo.get(0);
        return value;
    }
    //获取从OneStor获取到的监控数据的时间
    default Date getTime(Map<String, OneStorMonitorDTO> oneStorMonitorDTOMap){
        List<OneStorMonitorDTO> oneStorMonitorDTOList = oneStorMonitorDTOMap.values().stream().sorted(
                Comparator.comparing(OneStorMonitorDTO::getDataPointsLength).reversed()
        ).collect(Collectors.toList());
        if(oneStorMonitorDTOList.size() < 1){
            return null;
        }
        List<List> dataPoints = oneStorMonitorDTOList.get(0).getDatapoints();
        if (CollectionUtil.isNotEmpty(dataPoints)){
            List dataInfo = dataPoints.get(dataPoints.size() - 1);
            Long timeValue = Long.valueOf(dataInfo.get(1).toString());
            return new Date(timeValue * 1000);
        }
       return new Date();

    }
    //处理OneStor接口查询所需的target字段
    default String appendTarget(String targetValue, String ...names){
        String formatTarget = String.format(targetValue, names);
        return String.format("&target=%s", formatTarget);
    }
}
