package com.virtual.cloud.om.service;

import com.virtual.cloud.om.sdk.constant.WarnMetricEnum;
import com.virtual.cloud.om.service.warn.WarnReportService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class WarnReportServiceTest {
    @Resource
    private DataReportCollectorOverview dataReportCollectorOverview;
    @Resource
    private WarnReportService warnReportService;

    @Test
    void report(){
        String tags = "resourceId=117";
        this.warnReportService.report(tags, WarnMetricEnum.cas_realtime_alarms);
//        this.warnReportService.report(tags,WarnMetricEnum.terminal_alarms);
//        this.warnReportService.report(tags,WarnMetricEnum.vipdesktop_alarms);
//        //cas,resourceId=128/50;
//        String castags = "resourceId=128";
//        this.warnReportService.report(castags, WarnMetricEnum.cas_realtime_alarms);
//        //uis,resourceId=170;
//        String uistags = "resourceId=170";
//        this.warnReportService.report(tags, WarnMetricEnum.uis_realtime_alarms);
    }

    public static void main(String[] args) {
        long time = System.currentTimeMillis()+120*1000;
        System.out.println(time);
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String format1 = format.format(date);
//        System.out.println("======="+format1);
        String smsTemplate = "验证码为：<VERIFYCODE>，您好<USERNAME>，您正在使用短信验证码登录，验证码的有效期至：<EXPIRETIME>。";
        String username = "zcy";
        String content = StringUtils.replace(smsTemplate, "<VERIFYCODE>", "123456");
        String s = smsTemplate.replace("<VERIFYCODE>","123456").replace("<USERNAME>",username).replace("<EXPIRETIME>",format1);
        System.out.println(s);

    }



}