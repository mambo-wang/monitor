package com.virtual.cloud.om.sdk.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public final class DateTimeTool {

    /**
     * 完整时间格式yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter FORMATTER_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_NUMBER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter FORMATTER_YM = DateTimeFormatter.ofPattern("yyyy_MM");

    /**
     * 定义规范的UTC时间格式
     * <br>
     * 格式效果：2017-10-11T15:49:42.342+08:00
     */
    public static final String DATE_FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /**
     * 定义页面默认显示用的时间格式
     * <br>
     * 格式效果：2017-02-23 08:04:02
     */
    public static final String DATE_FORMAT_DEFAULT_VIEW = "yyyy-MM-dd HH:mm:ss";
    /**
     * 格式化日期时间
     * @param time      long类型日期
     * @param formatter 时间格式
     * @return 格式化后日期时间
     */
    public static String formatDate(Long time, DateTimeFormatter formatter) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC+08:00"));
        return localDateTime.format(formatter);
    }

    public static String formatDate(Date date) {
        if(Objects.isNull(date)){
            return null;
        }
        return  formatDate(date.getTime(), FORMATTER_FULL);
    }

    /**
     * 将日期时间格式化成yyyy-MM-dd HH:mm:ss格式
     * @param datetime long类型日期时间
     * @return 格式化后日期时间
     */
    public static String formatFullDateTime(Long datetime) {
        return formatDate(datetime, FORMATTER_FULL);
    }

    public static String formatFullDateTime(LocalDateTime time) {
        return  FORMATTER_FULL.format(time);
    }

    public static String formatYearMonth(Date date) {
        return  formatDate(date.getTime(), FORMATTER_YM);
    }


    /**
     * 获得指定日期的前一天
     * @param specifiedDay
     * @param format eq:"yyyy-MM-dd"
     * @return
     */
    public static String getSpecifiedDayBefore(String specifiedDay,String format){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-1);

        String dayBefore=new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的前n天
     * @param specifiedDay
     * @param format eq:"yyyy-MM-dd"
     * @return
     */
    public static String getSpecifiedDayBeforeSomeDay(String specifiedDay,String format,int n){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-n);

        String dayBefore=new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的后一天
     * @param specifiedDay
     * @param format eq:"yyyy-MM-dd"
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay,String format){
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day+1);

        String dayAfter=new SimpleDateFormat(format).format(c.getTime());
        return dayAfter;
    }


    /**
     * 2020-02-10T22:40:29.714+08:00 转 date
     * @param time
     * @return
     */
    public static Date GMTToDate(String time) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return format.parse(time);
    }

    /**
     * 2020-02-10T22:40:29.714+08:00 转 yyyy-MM-dd string
     * @param time
     * @return
     */
    public static String GMTToString(String time) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date  date = df.parse(time);
        SimpleDateFormat df1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        Date date1 =  df1.parse(date.toString());
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df2.format(date1);
    }

    /**
     *
     * @param time :2021-01-02 10:10:10
     * @return
     * @throws ParseException
     */
    public static Long stringToLong(String time) throws ParseException {
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(time).getTime();

    }

    public static void main(String[] args) throws ParseException {
        System.out.println(formatYearMonth(new Date()));
    }

}
