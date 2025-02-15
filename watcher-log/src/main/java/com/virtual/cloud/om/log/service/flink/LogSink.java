package com.virtual.cloud.om.log.service.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

@Slf4j
public class LogSink extends RichSinkFunction<IpAndCount> {

    @Override
    public void invoke(IpAndCount value, Context context) throws Exception {

        //业务处理，入库
        log.info("receive an sink msg {}", value);
    }
}
