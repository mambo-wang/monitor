//package com.virtual.cloud.om.sdk.aop;
//
//import cn.hutool.json.JSONUtil;
//import com.virtual.cloud.om.sdk.dto.dataReport.workspace.ReportDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//@Component
//@Aspect
//@Slf4j
//public class DataReportAspect {
//    @AfterReturning(value = "execution(java.util.List com.virtual.cloud.om.sdk.api.DataReportCollector.data(com.virtual.cloud.om.sdk.dto.RestHost,String))", returning = "methodResult")
//    public void afterReturning(JoinPoint joinPoint, Object methodResult) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        try {
//            List<ReportDTO> reportDTOS = JSONUtil.toList(JSONUtil.toJsonStr(methodResult), ReportDTO.class);
//            reportDTOS.forEach(dto -> {
//                Object ov = dto.getValue();
//                if(ov instanceof Collection){
//                    Collection list = (Collection) ov;
//                    list.forEach(obj->{
//                        if(obj instanceof String){
//
//                        }else if(obj instanceof Number){
//
//                        }else{
//                            Class<?> clazz = obj.getClass();
//                            Field[] fields = clazz.getDeclaredFields();
//                            Arrays.stream(fields).filter(field -> field.getType() == String.class).forEach(field -> {
//                                try {
//                                    String value = (String) field.get(obj);
//                                    if (value.equals("--")) {
//                                        field.set(obj, null);
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            });
//                        }
//                    });
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
