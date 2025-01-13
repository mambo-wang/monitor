package com.virtual.cloud.om.config;

import com.virtual.cloud.om.sdk.config.kafka.KafkaCallback;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.OperationLogVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * aop采集日志(接口请求参数，接口调用时间)
 *
 * @author wangbao6
 * @date 2019/7/31 17:50
 */
@Aspect
@Component
@Slf4j
public class LogAop {

    @Autowired
    private KafkaProducer<String, String> kafkaProducer;

    /**
     * 拦截所有web端访问的controller方法
     */
    @Pointcut("execution(* com.virtual.cloud.om.controller.*.*(..)) ")
    public void controllerMethodPointcut() {

    }


    @AfterThrowing(pointcut = "controllerMethodPointcut()", throwing= "error")
    public void controller(JoinPoint point, Throwable error) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String info = String.format("\n =======> request uri: %s", request.getRequestURI());
        log.error("{}, ====> error msg: {}", info, error.getMessage());
        String log = OperationLogVO.failure(OperationLogVO.MODULE_AGENT, request.getRequestURI() + " " + point.getSignature().toString(), error.toString(), "system");
        ProducerRecord<String, String> record = new ProducerRecord<>(Constant.KAFKA_TOPIC_LOG, log);
        kafkaProducer.send(record, new KafkaCallback());
    }

    @AfterReturning(pointcut="controllerMethodPointcut()", returning="retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){
        if(jp.getSignature().toString().contains("DeployController.queryLocalIps()")){
            return;
        }
        log.info("[afterReturningAdvice] Method Signature: {}", jp.getSignature());
        log.info("[afterReturningAdvice] Returning: {}", retVal.toString() );
        String log = OperationLogVO.success(OperationLogVO.MODULE_AGENT, jp.getSignature().toString(), retVal.toString(),"admin");
        ProducerRecord<String, String> record = new ProducerRecord<>(Constant.KAFKA_TOPIC_LOG, log);
        kafkaProducer.send(record, new KafkaCallback());
    }


}
