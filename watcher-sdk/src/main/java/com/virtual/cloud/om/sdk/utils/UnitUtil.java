package com.virtual.cloud.om.sdk.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;

/**
 * GB,MB等单位转换，默认MB
 */
@Slf4j
public class UnitUtil {


    public static Long getMemory(String memory) {
        Double result = null;
        if (memory.contains("TB")) {
            String tb = memory.replace("TB", "");
            result = Double.parseDouble(tb) * 1024L * 1024;
            return result.longValue();
        }
        if (memory.contains("GB")) {
            String gb = memory.replace("GB", "");
            result = Double.parseDouble(gb) * 1024;
            System.out.println(result + "==========");
            return result.longValue();
        }
        if (memory.contains("MB")) {
            String mb = memory.replace("MB", "");
            result = Double.parseDouble(mb);
            return result.longValue();
        }
        if (memory.contains("KB")) {
            String kb = memory.replace("KB", "");
            result = Double.parseDouble(kb) / 1024;
            return result.longValue();
        }
        String b = memory.replace("B", "");
        result = Double.parseDouble(b) / 1024 / 1024;
        return result.longValue();

    }
}
