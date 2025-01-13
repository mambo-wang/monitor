/*
 *------------------------------------------------------------------------------
 * Product     : iMC V300R006
 * Module Name :
 * Date Created: xxxx-xx-xx
 * Creator     : 郑雄开 z01500
 * Description :
 *
 *------------------------------------------------------------------------------
 * Modification History
 * DATE        NAME             DESCRIPTION
 *------------------------------------------------------------------------------
 * YYYY-MM-DD  zhengxiongkai    XXXX project, new code file.
 *
 * 2010-04-13  wangjialiang     添加两个判断iNode DC组件是否安装的方法.
 *                              而且使用的是平台推荐的方式.
 * 2017-09-22  huangli          增加createComprator, sortByField方法，创建基于对象单字段的比较器，支持java通用排序。
 *------------------------------------------------------------------------------
 */
package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;


/**
 * 公共函数类
 *
 * @author 郑雄开
 */
@Slf4j
public class FuncUtil {

    /**
     * 定时器1秒执行一次。
     */
    public static final int PERIOD = 1 * 1000;


    /**
     * 缺省密钥
     */
    private static final byte[] PASSWORD_KEY = "hph3c_z01500".getBytes(Charset.defaultCharset());

    /**
     * 加密工具
     */
    private static Cipher encryptDesCipher;

    /**
     * 解密工具
     */
    private static Cipher decryptDesCipher;

    private static final String ENCRYPT_AL= "DES/ECB/PKCS5Padding";

    /**
     * MAC地址模糊查询匹配正则表达式，3段式-- XXXX[-XXXX-XXXX]
     */
    public static final Pattern MAC3_LIKE = Pattern.compile("[a-fA-F0-9]{4}(-[a-fA-F0-9]{4}){0,2}");


    /**
     * MAC地址模糊查询匹配正则表达式，6段式-- XX[-XX-XX-XX-XX-XX]或XX[:XX:XX:XX:XX:XX]
     */
    public static final Pattern MAC6_LIKE = Pattern.compile("([a-fA-F0-9]{2})((-|:)[a-fA-F0-9]{2}){0,5}");

    public static final String IP_REGEX = "((?:(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d?\\d))";

    /**
     * 将指定对象的值转换为Long类型。
     *
     * @param obj 需要转换的目的对象。
     * @return Long类型对象。
     */
    public static Long getLongValue(Object obj) {
        if (obj == null) {
            return 0L;
        }
        try {
            // 如果是BigDecimal,则进行转换处理
            if (obj.getClass().equals(BigDecimal.class)) {
                // 使用Long类型进行探测
                return ((BigDecimal) obj).longValue();
            } else if (obj.getClass().equals(BigInteger.class)) {
                // 如果为BigInteger
                return ((BigInteger) obj).longValue();
            } else if (obj.getClass().equals(Double.class)) {
                // 如果为Double
                return ((Double) obj).longValue();
            } else if (obj.getClass().equals(Integer.class)) {
                // 如果为Integer
                return ((Integer) obj).longValue();
            } else if (obj.getClass().equals(Long.class)) {
                return (Long) obj;
            } else if (obj.getClass().equals(String.class)) {
                return Long.valueOf((String) obj);
            }
        } catch (Exception e) {
            log.error("getLongValue", e);
        }
        return 0L;
    }

    /**
     * 检查指定目录是否存在。
     *
     * @param directory 目录名称。
     */
    public static boolean isExistDir(String directory) {
        if (StringUtils.isEmpty(directory)) {
            return false;
        }
        String curDir = directory;
        directory = directory.trim();
        if (directory.endsWith("/")) {
            directory = directory.substring(0, directory.length() - 1);
        }
        int pos = directory.lastIndexOf("/");
        String parentDir = null;
        if (pos >= 0) {
            parentDir = directory.substring(0, pos + 1);
            curDir = directory.substring(pos + 1);
        }
        String cmdStr = " -al --time-style=+'%Y-%m-%d %H:%M' " + parentDir + " | grep ^d | awk '{print $8}'";
        String result = runCommand(new String[]{"sh", "-c", "ls " + cmdStr}, 10 * PERIOD);
        String[] dirArr = result.split("\n");
        if (dirArr != null) {
            for (String value : dirArr) {
                if (curDir.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将指定对象的值转换为Long类型。
     *
     * @param obj 需要转换的目的对象。
     * @return Long类型对象。
     */
    public static Integer getIntegerValue(Object obj) {
        if (obj == null) {
            return 0;
        }
        try {
            // 如果是BigDecimal,则进行转换处理
            if (obj.getClass().equals(BigDecimal.class)) {
                // 使用Long类型进行探测
                return ((BigDecimal) obj).intValue();
            } else if (obj.getClass().equals(BigInteger.class)) {
                // 如果为BigInteger
                return ((BigInteger) obj).intValue();
            } else if (obj.getClass().equals(Double.class)) {
                // 如果为Double
                return ((Double) obj).intValue();
            } else if (obj.getClass().equals(Integer.class)) {
                return (Integer) obj;
            } else if (obj.getClass().equals(Long.class)) {
                return ((Long) obj).intValue();
            } else if (obj.getClass().equals(String.class)) {
                String value = (String) obj;
                return StringUtils.isEmpty(value) ? Integer.valueOf(0) : Integer.valueOf(value);
            }
        } catch (Exception e) {
            log.error("getLongValue", e);
        }
        return 0;
    }

    /**
     * Convert object to xml string.
     *
     * @param obj Object with JAXB annotations.
     * @return XML string.
     */
    @SuppressWarnings("rawtypes")
    public static String convertObjectToXml(Object obj, Class cls) {
        if (obj == null) {
            log.error("Class Name:" + cls.getName());
            return null;
        }


        try (StringWriter writer = new StringWriter()) {

            getMarshaller(cls).marshal(obj, writer);
            if (log.isInfoEnabled()) {
                log.info("Convert " + obj + " to " + writer.toString());
            }
            String result = writer.toString();
            //result = ServerUtils.toUTF8String(result);
            result = new String(ServerUtils.gbkToUtf8(result),"UTF-8");
            return result;
        } catch (JAXBException e) {
            log.error(null, e);
            return null;
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    public static Marshaller getMarshaller(Class cls) throws JAXBException {
        Marshaller marshaller;
        JAXBContext context = JAXBContext.newInstance(cls);
        marshaller = context.createMarshaller();
        //设置编码方式为GBK
        //domMarshaller.setProperty(Marshaller.JAXB_ENCODING, "GBK");
        ////是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        //省略xml头信息（<?xml version
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        return marshaller;
    }

    /**
     * @param command    命令行
     * @param timeOutMax 最大的超时时长
     * @param command    命令行
     * @param evps       环境变量
     * @param dir
     * @param timeOutMax 最大的超时时长
     * @return 如果不为0，就是失败
     * @purpose 运行一个命令 。
     */
    public static String runCommand(String command, String[] evps, File dir, long timeOutMax) {
        String outString = null;
        // 创建一个进程

        Process cmProcess = null;
        StringBuilder outBuf = new StringBuilder();
        ErrorStreamHandlerThread errorHandlerThread = null;
        try {
            cmProcess = Runtime.getRuntime().exec(command, evps, dir);
        } catch (IOException e) {
            log.error("", e);
            return "";
        }
        try (BufferedReader outResult = new BufferedReader(new InputStreamReader(cmProcess.getInputStream(), "UTF-8"));) {

            errorHandlerThread = new ErrorStreamHandlerThread(cmProcess);
            errorHandlerThread.start();
            int exitValue = 1;
            long timeOut = 0;
            while (timeOut <= timeOutMax) {
                if (outResult.ready()) {
                    String strTmp = outResult.readLine();
                    while (strTmp != null && strTmp.trim().endsWith("")) {
                        outBuf.append(strTmp + "\n");
                        strTmp = outResult.readLine();
                    }
                }

                outString = outBuf.toString();
                try {
                    exitValue = cmProcess.exitValue();
                } catch (IllegalThreadStateException ex) {
                    Thread.sleep(100);
                    timeOut += 100;
                    continue;
                } catch (Exception e) {
                    timeOut = timeOutMax + 1;
                    log.error(null, e);
                }
                break;
            }

            if (outResult != null) {
                outResult.close();
            }

            try {
                if (timeOut > timeOutMax || exitValue != 0) {
                    cmProcess.destroy();
                } else {
                    cmProcess.exitValue();
                }
            } catch (Exception e) {
                log.error(null, e);
            }
        } catch (IOException e) {
            try {
                if (null != cmProcess) {
                    cmProcess.destroy();
                }
            } catch (Exception ex) {
                log.error(null, ex);
            }
        } catch (InterruptedException ie) {
            log.error(null, ie);
        } finally {
            try {
                errorHandlerThread.interrupt();
                // 等待线程停止运行
                try {
                    errorHandlerThread.join();
                } catch (InterruptedException ignore) {
                    //log.error(null, ignore);
                }
                errorHandlerThread = null;

            } catch (Exception ex3) {
            }
        }
        return outString;
    }

    /**
     * @param command    命令行
     * @param timeOutMax 最大的超时时长
     * @return 如果不为0，就是失败
     * @purpose 运行一个命令 。
     */
    public static String runCommand(String[] command, long timeOutMax) {
        String outString = "";
        try {
            outString = runCommandThrowException(command, null, null, timeOutMax);

        } catch (AppException e) {
            log.error(null, e);
        }
        return outString;
    }

    /**
     * @param command    命令行
     * @param timeOutMax 最大的超时时长
     * @param command    命令行
     * @param timeOutMax 最大的超时时长
     * @return
     * @purpose 运行一个命令 。
     */
    public static String runCommand(String[] command, String[] evps, File dir, long timeOutMax) {
        String outString = "";
        try {
            outString = runCommandThrowException(command, evps, dir, timeOutMax);
        } catch (AppException e) {
            log.info(null, e);
        }
        return outString;
    }

    public static String runCommandThrowException(String[] command, long timeOutMax) {
        return runCommandThrowException(command, null, null, timeOutMax);
    }

    /**
     * @param command    命令行
     * @param timeOutMax 最大的超时时长
     * @return 如果不为0，就是失败
     * @purpose 运行一个命令 。
     */
    public static String runCommandThrowException(String[] command, String[] evps, File dir, long timeOutMax) {
        String outString;
        // 创建一个进程

        Process cmProcess = null;
        StringBuilder outBuf = new StringBuilder();
        ErrorStreamHandlerThread errorHandlerThread = null;
        try {
            cmProcess = Runtime.getRuntime().exec(command, evps, dir);
        } catch (IOException e) {
            log.error("", e);
        }
        try (BufferedReader outResult = new BufferedReader(new InputStreamReader(cmProcess.getInputStream(),"UTF-8"))) {

            errorHandlerThread = new ErrorStreamHandlerThread(cmProcess);
            errorHandlerThread.start();
            int exitValue = 1;
            long timeOut = 0;
            while (timeOut <= timeOutMax) {
                if (outResult.ready()) {
                    String strTmp = outResult.readLine();
                    while (strTmp != null && strTmp.trim().endsWith("")) {
                        outBuf.append(strTmp + "\n");
                        strTmp = outResult.readLine();
                    }
                }

//                outString = outBuf.toString();
                try {
                    exitValue = cmProcess.exitValue();
                } catch (IllegalThreadStateException ex) {
                    Thread.sleep(100);
                    timeOut += 100;
                    continue;
                } catch (Exception e) {
                    timeOut = timeOutMax + 1;
                    log.error(null, e);
                }
                break;
            }
            outString = outBuf.toString();
            if (outResult != null) {
                outResult.close();
            }

            try {
                if (timeOut > timeOutMax || exitValue != 0) {
                    cmProcess.destroy();
                } else {
                    cmProcess.exitValue();
                }
            } catch (Exception e) {
                log.error(null, e);
            }
        } catch (IOException e) {
            try {
                if (null != cmProcess) {
                    cmProcess.destroy();
                }
            } catch (Exception ex) {
                log.error(null, ex);
            }
            throw new AppException(ErrorCodes.UNKNOWN_ERROR);
        } catch (InterruptedException ie) {
            log.error(null, ie);
            throw new AppException(ErrorCodes.UNKNOWN_ERROR);
        } finally {
            try {
                errorHandlerThread.interrupt();
                // 等待线程停止运行
                try {
                    errorHandlerThread.join();
                } catch (InterruptedException ignore) {
                    //log.error(null, ignore);
                }
                errorHandlerThread = null;

            } catch (Exception ex3) {
            }
        }
        return outString;
    }


    public static String runCommandWithThrowException(String[] command, long timeOutMax) {
        return runCommandWithThrowException(command, null, null, timeOutMax);
    }

    public static String runCommandWithThrowException(String[] command, String[] evps, File dir, long timeOutMax) {
        String outString;
        // 创建一个进程

        Process cmProcess = null;
        StringBuilder outBuf = new StringBuilder();
        ErrorStreamHandlerThread errorHandlerThread = null;
        try {
            cmProcess = Runtime.getRuntime().exec(command, evps, dir);
        } catch (IOException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        }
        try (BufferedReader outResult = new BufferedReader(new InputStreamReader(cmProcess.getInputStream(),"UTF-8"))) {
            errorHandlerThread = new ErrorStreamHandlerThread(cmProcess);
            errorHandlerThread.start();
            int exitValue = 1;
            long timeOut = 0;
            while (timeOut <= timeOutMax) {
                if (outResult.ready()) {
                    String strTmp = outResult.readLine();
                    while (strTmp != null && strTmp.trim().endsWith("")) {
                        outBuf.append(strTmp + "\n");
                        strTmp = outResult.readLine();
                    }
                }
                if(StringUtils.isNotBlank(errorHandlerThread.getStdErrorMessage())) {
                    return errorHandlerThread.getStdErrorMessage();
                }
                try {
                    exitValue = cmProcess.exitValue();
                    if (exitValue == 0) {
                        return "";
                    } else {
                        Thread.sleep(100);
                        timeOut += 100;
                        continue;
                    }
                } catch (IllegalThreadStateException ex) {
                    Thread.sleep(100);
                    timeOut += 100;
                    continue;
                } catch (Exception e) {
                    timeOut = timeOutMax + 1;
                    log.error(null, e);
                }
                break;
            }
            outString = outBuf.toString();
            if (outResult != null) {
                outResult.close();
            }

            try {
                if (timeOut > timeOutMax || exitValue != 0) {
                    cmProcess.destroy();
                } else {
                    cmProcess.exitValue();
                }
            } catch (Exception e) {
                log.error(null, e);
            }
        } catch (IOException e) {
            try {
                if (null != cmProcess) {
                    cmProcess.destroy();
                }
            } catch (Exception ex) {
                log.error(null, ex);
            }
            throw new AppException(ErrorCodes.UNKNOWN_ERROR);
        } catch (InterruptedException ie) {
            log.error(null, ie);
            throw new AppException(ErrorCodes.UNKNOWN_ERROR);
        } finally {
            try {
                errorHandlerThread.interrupt();
                // 等待线程停止运行
//                try {
//                    errorHandlerThread.join();
//                } catch (InterruptedException ignore) {
//                    //log.error(null, ignore);
//                }
                errorHandlerThread = null;

            } catch (Exception ex3) {
            }
        }
        return outString;
    }

    /**
     * 对字符串数据进行解密操作。
     *
     * @param cryptoText 文本密文
     * @return 文本明文
     */
    public synchronized static String decryptData(String cryptoText) {
        if (cryptoText == null) {
            return null;
        }
        // 如果DES解密工具没有创建，则创建之
        if (decryptDesCipher == null) {
            try {
                DESKeySpec desKeySpec = new DESKeySpec(PASSWORD_KEY);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
                SecretKey desSecretKey = factory.generateSecret(desKeySpec);
                decryptDesCipher = Cipher.getInstance(ENCRYPT_AL);
                decryptDesCipher.init(Cipher.DECRYPT_MODE, desSecretKey);
            } catch (Exception t) {
                log.error("",t);
                return null;
            }
        }

        // 对密文进行Base64解码
        byte[] decodedText = Base64.getDecoder().decode(cryptoText.getBytes(Charset.defaultCharset()));
        if (decodedText == null) {
            return null;
        }

        // 进行数据解密操作
        byte[] plainText;
        try {
            plainText = decryptDesCipher.doFinal(decodedText);
        } catch (Exception e) {
            return null;
        }
        try {
            // 返回字符串
            if (plainText != null) {
                return new String(plainText,"UTF-8");
            }
        } catch(Exception e) {
            return null;
        }
        return null;
    }


    /**
     * 对字符串进行加密，主要用于在保存用户密码时，进行加密操作。
     *
     * @param plainText 文本明文
     * @return 文本密文
     */
    public synchronized static String encryptData(String plainText) {
        // 如果DES加密工具没有创建，则创建之

        if (encryptDesCipher == null) {
            try {
                DESKeySpec desKeySpec = new DESKeySpec(PASSWORD_KEY);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
                SecretKey desSecretKey = factory.generateSecret(desKeySpec);
                encryptDesCipher = Cipher.getInstance(ENCRYPT_AL);
                encryptDesCipher.init(Cipher.ENCRYPT_MODE, desSecretKey);
            } catch (Exception e) {
                log.error(null, e);
                return null;
            }
        }

        // 执行加密操作
        byte[] cryptoText = null;
        try {
            cryptoText = encryptDesCipher.doFinal(plainText.getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
        try {
            // 执行Base64编码
            if (cryptoText != null) {
                return new String(Base64.getEncoder().encode(cryptoText),"UTF-8");
            }
        } catch(Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 将xml格式内容转换为相应的对象。
     *
     * @param file, cls xml格式内容。
     * @return Object with JAXB annotations。
     */
    @SuppressWarnings("rawtypes")
    public static Object convertXmlToObject(File file, Class cls) {
        if (file == null) {
            return null;
        }
        try {
            Object obj = getUnmarshaller(cls).unmarshal(file);
            return obj;
        } catch (JAXBException e) {
            log.error(null, e);
            return null;
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    /**
     * Return the JAXB Unmarshaller for configuratiion.
     *
     * @return the JAXB Unmarshaller for configuratiion.
     * @throws JAXBException JAXB 转换错误。
     */
    @SuppressWarnings("rawtypes")
    public static Unmarshaller getUnmarshaller(Class cls) throws JAXBException {
        Unmarshaller unmarshaller;
        JAXBContext context = JAXBContext.newInstance(cls);
        unmarshaller = context.createUnmarshaller();
        return unmarshaller;
    }

    /**
     * 获取当前目录的剩余可用空间大小(kb)
     *
     * @param path 目录绝对路径
     * @return kb
     */
    public static long getDirAvailableSize(String path) {
        long availableSize = 0L;
        if (StringUtils.isEmpty(path)) {
            return availableSize;
        }
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return availableSize;
        }
        String cmdStr = "df -k " + path + " | sed '1d' | awk '{print $4}'";
        String size = runCommand(new String[]{"sh", "-c", cmdStr}, 10 * PERIOD);
        if (size == null || size.equals("")) {
            return availableSize;
        }
        if (size.lastIndexOf("\n") != -1) {
            size = size.substring(0, size.lastIndexOf("\n"));
        }
        if (size.matches("\\d+"))
            availableSize = Long.parseLong(size);
        return availableSize;
    }

    /**
     * 将IP转换成LONG
     *
     * @param strIP
     * @return
     */
    public static long ipToLong(String strIP) {
        long[] ip = new long[4];
        int position1 = strIP.indexOf(".");
        int position2 = strIP.indexOf(".", position1 + 1);
        int position3 = strIP.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(strIP.substring(0, position1));
        ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIP.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    /**
     * 将long转换成IP
     *
     * @param longIP
     * @return
     */
    public static String longToIP(long longIP) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(longIP >>> 24));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((longIP & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(longIP & 0x000000FF));
        return sb.toString();
    }


    /**
     * 获取指定文件的MD5摘要。
     *
     * @param file 文件绝对路径名。
     * @return MD5摘要。
     */
    public static String getFileMD5(String file) {
        String result = "";
        String cmd = "md5sum " + file + " | awk '{print $1}'";
        String resultValue = runCommand(new String[]{"sh", "-c", cmd}, 24 * 60 * 60 * PERIOD);
        if (StringUtils.isNotEmpty(resultValue)) {
            String[] rowValue = resultValue.trim().split("\n");
            if (rowValue.length > 0) {
                result = rowValue[0].trim();
            }
        }
        return result;
    }

    public static String convertTimer (String time) {
        if (time == null) {
            return null;
        } else {
            List<String> resultList = new ArrayList<String>();
            String[] timeSplit = time.split(",");
            if (timeSplit != null && timeSplit.length > 0) {
                for (String timer : timeSplit) {
                    int point = Integer.parseInt(timer);
                    if (point < 10) {
                        timer = "0" + timer;
                    }
                    if (resultList.size() > 0) {
                        int listLastOne = Integer.parseInt(resultList.get(resultList.size() - 1).substring(0, 2));
                        if (point - listLastOne == 1) {
                            resultList.set(resultList.size() - 1, timer + ":59");
                        } else {
                            resultList.add(",");
                            resultList.add(timer + ":00");
                            resultList.add("~");
                            resultList.add(timer + ":59");
                        }
                    } else {
                        resultList.add(timer + ":00");
                        resultList.add("~");
                        resultList.add(timer + ":59");
                    }
                }
                if (resultList != null && resultList.size() > 0) {
                    String result = "";
                    for (String timer : resultList) {
                        result += timer;
                    }
                    return result;
                }
            }
        }
        return time;
    }

    /**
     * ipv6地址转成BigInteger
     * @param ipv6
     * @return
     */
    public static BigInteger ipv6toInt(String ipv6) {
        int compressIndex = ipv6.indexOf("::");
        //1::2:0:1 --> 1 , 2:0:1
        if (compressIndex != -1) {
            String part1s = ipv6.substring(0, compressIndex);
            String part2s = ipv6.substring(compressIndex + 1);
            BigInteger part1 = ipv6toInt(part1s);
            BigInteger part2 = ipv6toInt(part2s);
            int part1hasDot = 0;
            char ch[] = part1s.toCharArray();
            for (char c : ch) {
                if (c == ':') {
                    part1hasDot++;
                }
            }
            return part1.shiftLeft(16 * (7 - part1hasDot )).add(part2);
        }
        String[] str = ipv6.split(":");
        BigInteger big = BigInteger.ZERO;
        for (int i = 0; i < str.length; i++) {
            if (str[i].isEmpty()) {
                str[i] = "0";
            }
            big = big.add(BigInteger.valueOf(Long.valueOf(str[i], 16)).shiftLeft(16 * (str.length - i - 1)));
        }
        return big;
    }

    /**
     * 校验IPv4地址串是否合法。
     *
     * @param ipAddress IP地址。
     * @return true 合法。
     */
    public static boolean isLegalIpV4(String ipAddress) {
        if (StringUtils.isEmpty(ipAddress)) {
            return false;
        }
        if (ipAddress.matches(IP_REGEX)) {
            return true;
        }
        return false;
    }
}


