package com.virtual.cloud.om.sdk.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ObjectMapUtil {


    //跳过serialVersionUID
    public static Map<String, String> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, String> map = new HashMap<String, String>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if(Objects.equals(fieldName, "serialVersionUID") || Objects.equals(fieldName, "sdf") || Objects.isNull(field.get(obj))){
                continue;
            }
            String value = field.get(obj).toString();
            map.put(fieldName, value);
        }
        return map;
    }

    public void setFieldValue(Map<String, String> maps, Object obj){
        if (maps != null){
            Iterator keys = maps.keySet().iterator();
            while(keys.hasNext()){
                String key = (String)keys.next();
                Object value = maps.get(key);
                try{
                    Field field = obj.getClass().getDeclaredField(key);
                    field.setAccessible(true);
                    field.set(obj, field.getType().getConstructor(field.getType()).newInstance(value));
                }catch(Exception e){
//                    log.error(e.getLocalizedMessage());
                }
            }
        }
    }
}
