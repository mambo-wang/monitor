package com.virtual.cloud.om.service;

import com.virtual.cloud.om.sdk.api.DataReportCollector;
import com.virtual.cloud.om.sdk.constant.DataReportTypeByMetricEnum;
import com.virtual.cloud.om.sdk.constant.report.ReportMetricEnum;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @Author: w22798
 * @Date: 2022/4/23 16:20
 */
@Component
public class DataReportCollectorOverview implements ApplicationRunner {

    @Resource
    private DataReportCollector[] dataReportCollectors;

    private Map<DataReportTypeByMetricEnum, DataReportCollector> collectApiMap = new ConcurrentHashMap();



    @Override
    public void run(ApplicationArguments args) throws Exception {
        Stream.of(dataReportCollectors).forEach(collectApi -> collectApiMap.put(collectApi.metric(), collectApi));
    }

    /**
     * 获取数据上报收集器
     * @param metric
     * @return
     */
    public DataReportCollector getCollector(ReportMetricEnum metric){
        return Optional.ofNullable(collectApiMap.get(metric)).orElseThrow(() -> new AppException(ErrorCodes.NOT_FOUND));
    }
}
