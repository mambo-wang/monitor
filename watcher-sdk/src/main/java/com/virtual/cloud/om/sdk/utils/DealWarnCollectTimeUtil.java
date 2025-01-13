package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.api.WarnMgrApi;
import com.virtual.cloud.om.sdk.dto.WarnDTO;
import com.virtual.cloud.om.sdk.dto.dataReport.workspace.WarnEndFromAndEndToDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * 处理告警信息采集开始结束时间
 */
@Slf4j
@Component
public class DealWarnCollectTimeUtil {

    @Resource
    private WarnMgrApi warnMgrApi;

    public  WarnEndFromAndEndToDTO dealEndFromAndEndTo(String resourceId, String type) {
        WarnDTO warn = warnMgrApi.queryWarnByIdAndType(resourceId, type);
        long endFrom = 0;
        long oneWeekMillis = 7 * 24 * 60 * 60 * 1000;
        long now = System.currentTimeMillis();
        long endTo = now + oneWeekMillis;
        Long reportTime = 0L;
        if (ObjectUtils.isEmpty(warn)) {
            //第一次查询
            endFrom = now - oneWeekMillis;
            log.info("The first collect realTimeAlarms startTime is {},The first collect realTimeAlarms startTime is {}",endFrom,endTo);
        } else {
            reportTime = warn.getReportTime();
            if (reportTime == 0) {
                //上报失败
                endFrom = now - oneWeekMillis;
            } else {
                //上一次上报成功时间是否是一周前
                if (now - reportTime > oneWeekMillis) {
                    //超过一周，endFrom为当前时间前一周
                    endFrom = now - oneWeekMillis;
                } else {
                    //endFrom为上一次采集的最新告警时间
                    endFrom = warn.getEventTime();
                }
                log.info("collect realTimeAlarms startTime is {},The first collect realTimeAlarms startTime is {}",endFrom,endTo);
            }
        }
        WarnEndFromAndEndToDTO dto = new WarnEndFromAndEndToDTO();
        dto.setEndFrom(endFrom);
        dto.setEndTo(endTo);
        dto.setReportTime(reportTime);
        return dto;
    }
}
