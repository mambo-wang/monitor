package com.virtual.cloud.om.sdk.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Created by x19765 on 2022/7/15.
 */
public class TimeUtils {

    private static final Logger log = LoggerFactory.getLogger(TimeUtils.class);

    private static String DATE_REGEX = "((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]).([0-9]{3})";

    private static SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static SimpleDateFormat sdf_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static Long convertToTimestamp(String datestr) {
        try {
            datestr = StringUtils.trimToEmpty(datestr);
            boolean matches =  Pattern.matches(DATE_REGEX, datestr);
            if(matches){
                return sdf_2.parse(datestr).getTime();
            }
            return sdf_1.parse(datestr).getTime();
        } catch(Exception e) {
            log.warn("convertToTimestamp error:{}",e.getMessage());
        }
        return null;
    }
}
