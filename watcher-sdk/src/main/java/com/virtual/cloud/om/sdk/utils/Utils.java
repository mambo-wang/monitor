package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static final ZoneId BEIJING_ZONE = ZoneId.of("UTC+08:00");

    /**
     * 完整时间格式yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter FORMATTER_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATTER_FULL_NUMBER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static final String DATE_FORMAT_DEFAULT_VIEW = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DATE_VIEW = "yyyy-MM-dd";

    /**
     * 用来计算MD5摘要的hex数组
     */
    private static char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 逗号分隔符
     */
    private static final char SEP_COMMA = ',';


    private Utils() {
    }

    public static void checkResult(String url, StateResult stateResult) {
        if (Objects.isNull(stateResult)) {
            log.error("[rpc-error]remote call error, response body is empty");
            throw new AppException(ErrorCodes.HTTP_RESPONSE_ERROR, url);
        }
        if(stateResult.getErrorCode()==ErrorCodes.report_data_error_need_report_again){
            throw new AppException(ErrorCodes.report_data_error_need_report_again);
        }
        if (!stateResult.isSuccess()) {
            log.error("[rpc-error]remote call error, msg is {}", stateResult.getFailureMessage());
            throw new AppException(ErrorCodes.REST_FAIL, url, stateResult.getFailureMessage());
        }
    }

    public static void checkResult(String ip, String url, StateResult stateResult) {
        if (Objects.isNull(stateResult)) {
            log.error("[rpc-error]remote call error, response body is empty");
            throw new AppException(ErrorCodes.HTTP_RESPONSE_ERROR_DETAIL, ip, url);
        }
        if (!stateResult.isSuccess()) {
            log.error("[rpc-error]remote call error, msg is {}", ip + ":" + stateResult.getFailureMessage());
            throw new AppException(ErrorCodes.REST_FAIL, url, "IP:" + ip + ",URI:" + url + "-" + stateResult.getFailureMessage());
        }
    }


    /**
     * 将一个对象转成String，如果对象为null，返回空字符串。目前仅用于将ASN中的byte[]转成String，以后会扩展其他类型
     *
     * @param srcObj 原始对象
     * @return 对象的String类型
     */
    public static String toString(Object srcObj) {
        if (srcObj == null) {
            return "";
        }
        if (srcObj instanceof byte[]) {
            return new String((byte[]) srcObj, Charset.forName("UTF-8"));
        }
        return String.valueOf(srcObj);
    }

    /**
     * 导出文件时对属性字符串中的双引号和逗号进行处理
     *
     * @param value
     * @return
     */
    public static String addQuotationForExport(String value) {
        if (value == null) {
            return value;
        }
        boolean quoteFlag = false;//标记是否添加过双引号
        //20161214 若发现有逗号 需前后加引号 否则会出现串列的情况
        if (value.contains("\"")) {
            //若发现有双引号 需要将字符串中的一个双引号替换为两个 并且需前后加双引号,否则可能导致原本字符串中的两个双引号变成一个
            value = value.replaceAll("\"", "\"\"");
            value = "\"" + value + "\"";
            quoteFlag = true;
        }
        if (value.contains(",") && !quoteFlag) {
            //若发现有逗号 需前后加引号
            value = "\"" + value + "\"";
        }

        return value;
    }

    /**
     * 导入文件时对属性字符串中的双引号进行处理
     *
     * @param value
     * @return
     */
    public static String delQuotationForImport(String value) {
        if (StringUtils.length(value) < 2) {
            return value;
        }
        if (value.indexOf("\"") == 0) {
            //去掉开始的双引号
            value = value.substring(1, value.length());
        }
        if (value.lastIndexOf("\"") == value.length() - 1) {
            //去掉结尾的双引号
            value = value.substring(0, value.length() - 1);
        }
        //把字符串中的两个双引号替换成一个
        value = value.replaceAll("\"\"", "\"");
        return value;
    }

    /**
     * long类型转换为Localdate日期类型
     *
     * @param time 时间
     * @return
     */
    public static LocalDate toLocalDate(Long time) {
        return toLocalDateTime(time).toLocalDate();
    }

    /**
     * long类型时间转换为LocalDateTime类型
     *
     * @param time
     * @return
     */
    public static LocalDateTime toLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), BEIJING_ZONE);
    }

    /**
     * 将日期时间格式化成yyyy-MM-dd HH:mm:ss格式
     *
     * @param datetime long类型日期时间
     * @return 格式化后日期时间
     */
    public static String formatFullDateTime(Long datetime) {
        return toLocalDateTime(datetime).format(FORMATTER_FULL);
    }

    @SneakyThrows
    public static Long formatStringToLong(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse(dateTime);
        Long timestamp = date.getTime();

        return timestamp;
    }

    /**
     * 将日期时间格式化成yyyy-MM-dd HH:mm:ss格式
     *
     * @param datetime long类型日期时间
     * @return 格式化后日期时间
     */
    public static String formatDay(Long datetime) {
        return toLocalDateTime(datetime).format(FORMATTER_DAY);
    }

    /**
     * 将string类型的时间转化为Date类型
     *
     * @param strTime   时间
     * @param strFormat 时间格式，可以为："yyyy-MM-dd HH:mm:ss"或者"yyyy-MM-dd"
     * @return
     */
    public static Date stringToDate(String strTime, String strFormat) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(strFormat);
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            log.warn("Failed to get date from string,ParseException.", e);
        }
        return date;
    }

    public static String formatFullNumber(Long datetime) {
        return toLocalDateTime(datetime).format(FORMATTER_FULL_NUMBER);
    }


    /**
     * 将数组对象以逗号拼接成字符串
     *
     * @param objects 目标数组
     * @return 按逗号拼接的字符串
     */
    public static String join(List<String> objects) {
        return StringUtils.join(objects, SEP_COMMA);
    }

    public static String joinObject(Object... objects) {
        return StringUtils.join(objects, SEP_COMMA);
    }


    /**
     * 数据库模糊查询拼接，按任意匹配方式，前后追加%。
     *
     * @param val 查询条件的值
     * @return 任意匹配值：%val%
     */
    public static String like(String val) {
        return val == null ? "%" : "%" + val + "%";
    }

    /**
     * 获取本地IP列表（针对多网卡情况）
     *
     * @return
     */
    public static List<String> getLocalIPList() {
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
                        ip = inetAddress.getHostAddress();
                        // 过滤掉127.0.0.1
                        // docker容器和宿主机组成一个独立的局域网，默认宿主机的IP为172.17.0.1,对应主机的网络名称为docker0，暂时直接过滤掉
                        if (!Objects.equals("127.0.0.1", ip) && !Objects.equals("172.17.0.1", ip)) {
                            ipList.add(ip);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            log.warn("get local ip list fail.", e);
        }
        return ipList;
    }

    public static String getVswitch0Ip() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                if (networkInterface.getName().equals("vswitch0")) {
                    inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        inetAddress = inetAddresses.nextElement();
                        if (inetAddress != null && inetAddress instanceof Inet4Address) {
                            // 一般一个网络接口只有一个IP，暂时只取第一个
                            ip = inetAddress.getHostAddress();
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            log.warn("get local ip list fail.", e);
        }
        log.info("vswitch0 ip: {}.", ip);
        return ip;
    }

    /**
     * 计算文件的MD5摘要信息
     *
     * @param file 文件对象
     * @return 大写的MD5摘要
     */
    public static String getFileMD5(File file) {
        try (FileInputStream is = new FileInputStream(file)) {
            FileChannel channel = is.getChannel();
            MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());

            // 获取MD5对象
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(byteBuffer);
            byte[] data = digest.digest();
            StringBuilder buffer = new StringBuilder(2 * data.length);

            for (byte dt : data) {
                char c0 = hexDigits[(dt & 0xf0) >> 4];
                char c1 = hexDigits[dt & 0xf];
                buffer.append(c0).append(c1);
            }

            // 返回大写形式的MD5值
            return StringUtils.upperCase(buffer.toString());
        } catch (NoSuchAlgorithmException | IOException e) {
            log.warn("get file MD5 failed.", e);
            return null;
        }
    }

    public static List<String> parseTimeFormats(String reTime) {
        String[] reTimes = reTime.split(",");

        List<String> timeFormats = new ArrayList<>();
        for (String time : reTimes) {

            /**
             * 半小时计算
             * */
            String[] hourMin = time.split("\\.");
            if (hourMin.length == 1) {

                if (hourMin[0].length() == 1) {
                    timeFormats.add("0" + hourMin[0] + ":00:00");
                } else {
                    timeFormats.add(hourMin[0] + ":00:00");
                }
            } else {

                int min = Integer.parseInt(hourMin[1]) * 6;

                if (hourMin[0].length() == 1) {
                    timeFormats.add("0" + hourMin[0] + ":" + min + ":00");
                } else {
                    timeFormats.add(hourMin[0] + ":" + min + ":00");
                }

            }
        }

        return timeFormats;
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
