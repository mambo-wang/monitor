package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.dto.NetworkInfoDTO;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author z14433 on 2017/9/18.
 */
@Slf4j
public class IpUtil {

    public static int iPToInt(String ip) throws RuntimeException
    {
        ip = ip.trim();
        String regular = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        String[] ipArray = ip.split("\\.");
        if (!ip.matches(regular) || ipArray.length != 4)
        {
            throw new RuntimeException("Wrong IP.");
        }
        return Integer.parseInt(ipArray[0]) << 24 | Integer.parseInt(ipArray[1]) << 16
                | Integer.parseInt(ipArray[2]) << 8 | Integer.parseInt(ipArray[3]);
    }

    public static String intToIP(int ipNum)
    {
        return (int) ((ipNum & 0xff000000L) >> 24) + "." + (int) ((ipNum & 0xff0000L) >> 16) + "."
                + (int) ((ipNum & 0xff00L) >> 8) + "." + (int) (ipNum & 0xffL);
    }

    public static String getIpAddr(final HttpServletRequest request) throws Exception {
        if (request == null) {
            throw new Exception("getIpAddr method HttpServletRequest Object is null");
        }

        String ipString = request.getHeader("x-forward-for");
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("x-forwarded-for");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
            ipString = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ipString)) {
                ipString = "127.0.0.1";
            }
        }

        final String[] arr = ipString.split(",");
        for (final String str : arr) {
            if (!"unknown".equalsIgnoreCase(str)) {
                ipString = str;
                break;
            }
        }

        return ipString;
    }

    /**
     * 严格格式,包含%,[],/,.则报错
     * @param paramString
     * @return
     */
    public static boolean isIPv6LiteralAddressForce(String paramString) {
        if (paramString == null || paramString.contains("%") || paramString.contains("[") || paramString.contains("/") || paramString.contains(".")) {
            return false;
        }
        return textToNumericFormatV6(paramString) != null;
    }

    public static byte[] textToNumericFormatV4(String paramString)
    {
        if (paramString.length() == 0) {
            return null;
        }

        byte[] arrayOfByte = new byte[4];
        String[] arrayOfString = paramString.split("\\.", -1);
        try
        {
            long l;
            int i;
            switch (arrayOfString.length)
            {
                case 1:
                    l = Long.parseLong(arrayOfString[0]);
                    if ((l < 0L) || (l > 4294967295L))
                        return null;
                    arrayOfByte[0] = ((byte)(int)(l >> 24 & 0xFF));
                    arrayOfByte[1] = ((byte)(int)((l & 0xFFFFFF) >> 16 & 0xFF));
                    arrayOfByte[2] = ((byte)(int)((l & 0xFFFF) >> 8 & 0xFF));
                    arrayOfByte[3] = ((byte)(int)(l & 0xFF));
                    break;
                case 2:
                    l = Integer.parseInt(arrayOfString[0]);
                    if ((l < 0L) || (l > 255L))
                        return null;
                    arrayOfByte[0] = ((byte)(int)(l & 0xFF));
                    l = Integer.parseInt(arrayOfString[1]);
                    if ((l < 0L) || (l > 16777215L))
                        return null;
                    arrayOfByte[1] = ((byte)(int)(l >> 16 & 0xFF));
                    arrayOfByte[2] = ((byte)(int)((l & 0xFFFF) >> 8 & 0xFF));
                    arrayOfByte[3] = ((byte)(int)(l & 0xFF));
                    break;
                case 3:
                    for (i = 0; i < 2; i++) {
                        l = Integer.parseInt(arrayOfString[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        arrayOfByte[i] = ((byte)(int)(l & 0xFF));
                    }
                    l = Integer.parseInt(arrayOfString[2]);
                    if ((l < 0L) || (l > 65535L))
                        return null;
                    arrayOfByte[2] = ((byte)(int)(l >> 8 & 0xFF));
                    arrayOfByte[3] = ((byte)(int)(l & 0xFF));
                    break;
                case 4:
                    for (i = 0; i < 4; i++) {
                        l = Integer.parseInt(arrayOfString[i]);
                        if ((l < 0L) || (l > 255L))
                            return null;
                        arrayOfByte[i] = ((byte)(int)(l & 0xFF));
                    }
                    break;
                default:
                    return null;
            }
        } catch (NumberFormatException localNumberFormatException) {
            return null;
        }
        return arrayOfByte;
    }

    public static byte[] textToNumericFormatV6(String paramString)
    {
        if (paramString.length() < 2) {
            return null;
        }

        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte1 = new byte[16];

        int m = arrayOfChar.length;
        int n = paramString.indexOf("%");
        if (n == m - 1) {
            return null;
        }

        if (n != -1) {
            m = n;
        }

        int i = -1;
        int i1 = 0; int i2 = 0;

        if ((arrayOfChar[i1] == ':') &&
                (arrayOfChar[(++i1)] != ':'))
            return null;
        int i3 = i1;
        int j = 0;
        int k = 0;
        int i4;
        while (i1 < m) {
            char c = arrayOfChar[(i1++)];
            i4 = Character.digit(c, 16);
            if (i4 != -1) {
                k <<= 4;
                k |= i4;
                if (k > 65535)
                    return null;
                j = 1;
            }
            else if (c == ':') {
                i3 = i1;
                if (j == 0) {
                    if (i != -1)
                        return null;
                    i = i2;
                } else {
                    if (i1 == m) {
                        return null;
                    }
                    if (i2 + 2 > 16)
                        return null;
                    arrayOfByte1[(i2++)] = ((byte)(k >> 8 & 0xFF));
                    arrayOfByte1[(i2++)] = ((byte)(k & 0xFF));
                    j = 0;
                    k = 0;
                }
            }
            else if ((c == '.') && (i2 + 4 <= 16)) {
                String str = paramString.substring(i3, m);

                int i5 = 0; int i6 = 0;
                while ((i6 = str.indexOf('.', i6)) != -1) {
                    i5++;
                    i6++;
                }
                if (i5 != 3) {
                    return null;
                }
                byte[] arrayOfByte3 = textToNumericFormatV4(str);
                if (arrayOfByte3 == null) {
                    return null;
                }
                for (int i7 = 0; i7 < 4; i7++) {
                    arrayOfByte1[(i2++)] = arrayOfByte3[i7];
                }
                j = 0;
            }
            else {
                return null;
            }
        }
        if (j != 0) {
            if (i2 + 2 > 16)
                return null;
            arrayOfByte1[(i2++)] = ((byte)(k >> 8 & 0xFF));
            arrayOfByte1[(i2++)] = ((byte)(k & 0xFF));
        }

        if (i != -1) {
            i4 = i2 - i;

            if (i2 == 16)
                return null;
            for (i1 = 1; i1 <= i4; i1++) {
                arrayOfByte1[(16 - i1)] = arrayOfByte1[(i + i4 - i1)];
                arrayOfByte1[(i + i4 - i1)] = 0;
            }
            i2 = 16;
        }
        if (i2 != 16)
            return null;
        byte[] arrayOfByte2 = convertFromIPv4MappedAddress(arrayOfByte1);
        if (arrayOfByte2 != null) {
            return arrayOfByte2;
        }
        return arrayOfByte1;
    }

    public static byte[] convertFromIPv4MappedAddress(byte[] paramArrayOfByte)
    {
        if (isIPv4MappedAddress(paramArrayOfByte)) {
            byte[] arrayOfByte = new byte[4];
            System.arraycopy(paramArrayOfByte, 12, arrayOfByte, 0, 4);
            return arrayOfByte;
        }
        return null;
    }

    private static boolean isIPv4MappedAddress(byte[] paramArrayOfByte)
    {
        if (paramArrayOfByte.length < 16) {
            return false;
        }
        if ((paramArrayOfByte[0] == 0) && (paramArrayOfByte[1] == 0) && (paramArrayOfByte[2] == 0) && (paramArrayOfByte[3] == 0) && (paramArrayOfByte[4] == 0) && (paramArrayOfByte[5] == 0) && (paramArrayOfByte[6] == 0) && (paramArrayOfByte[7] == 0) && (paramArrayOfByte[8] == 0) && (paramArrayOfByte[9] == 0) && (paramArrayOfByte[10] == -1) && (paramArrayOfByte[11] == -1))
        {
            return true;
        }
        return false;
    }

    public static String queryVip() {
        String vip = null;
        try {
            vip = FuncUtil.runCommand(new String[]{"sh","-c","/opt/bin/crm status | grep webip |  awk -F \'[][ ]\' \'{print $4}\'"},Integer.MAX_VALUE);
            if (vip != null) {
                vip = vip.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (FuncUtil.isLegalIpV4(vip)) {
            return vip;
        }
        return null;
    }

    public static boolean isInOneRange(String ip1, String ip2, String mask) {
        SubnetUtils subnetUtils = new SubnetUtils(ip1, mask);
        return subnetUtils.getInfo().isInRange(ip2);
    }

    public static String numberToMask(int cidrMask) {
        long bits = 0;
        bits = 0xffffffff ^ (1 << 32 - cidrMask) - 1;
        String mask = String.format("%d.%d.%d.%d", (bits & 0x0000000000ff000000L) >> 24, (bits & 0x0000000000ff0000) >> 16, (bits & 0x0000000000ff00) >> 8, bits & 0xff);

        return mask;
    }

    public static boolean isIpReachable(String ip){
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(3000);
        } catch (IOException e) {
            return false;
        }
    }

    public static int convertPointToNumber(String mask){
        for(int i=1;i<32;i++) {
            int ip = 0xFFFFFFFF << (32 - i);
            String binaryStr = Integer.toBinaryString(ip);
            StringBuffer buffer = new StringBuffer();
            for(int j=0;j<4;j++) {
                int beginIndex = j*8;
                buffer.append(Integer.parseInt(binaryStr.substring(beginIndex, beginIndex+8), 2)).append(".");
            }
            String maskResult = buffer.substring(0,buffer.length()-1);
            if(maskResult.equalsIgnoreCase(mask)){
                return i;
            }
        }
        throw new AppException(ErrorCodes.FILE_CONVERT_FAIL);
    }

    public static boolean isPortReachable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isHostReachable(String host, Integer timeOut) {
        try {
            return InetAddress.getByName(host).isReachable(timeOut);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isIpAddress(String ip) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        if(ip.matches(regex)) {
            String[] arr = ip.split("\\.");
            for(int i=0; i<4; i++) {
                int temp = Integer.parseInt(arr[i]);
                if(temp < 0 || temp > 255) return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static String getIpByDomain(String domain) {
        String cmd = "host " + domain;
        String cmdResult = FuncUtil.runCommandThrowException(new String[]{"sh", "-c", cmd}, Integer.MAX_VALUE);
        if(StringUtils.isNotBlank(cmdResult) && cmdResult.contains("has address")) {
            String[] result = cmdResult.split(" ");
            return result[result.length - 1].replaceAll("\n","");
        }
        return "";
    }

    public static long convertIpV4ToLong(String hostIp) {
        if (null == hostIp) {
            log.error("Host IP can not be null");
            throw new NullPointerException("Host IP can not be null");
        } else {
            String[] parts = hostIp.trim().split("\\x2e");
            if (parts.length != 4) {
                log.error("Invalid ip address: {}", hostIp);
                return 0L;
            } else {
                long ip = 0L;
                String[] arr$ = parts;
                int len$ = parts.length;
                for(int i$ = 0; i$ < len$; ++i$) {
                    String part = arr$[i$];
                    try {
                        int p = Integer.parseInt(part.trim());
                        if (p < 0 || p > 255) {
                            log.error("Invalid ip address: {}", hostIp);
                            return 0L;
                        }

                        ip = ip << 8 | (long)p;
                    } catch (NumberFormatException var9) {
                        log.error("Invalid ip address: {}", hostIp, var9);
                        return 0L;
                    }
                }
                return ip;
            }
        }
    }

    public static String convertLongToIpV4(long longIp) {
        return (longIp >> 24 & 255L) + "." + (longIp >> 16 & 255L) + "." + (longIp >> 8 & 255L) + "." + (longIp & 255L);
    }

    public static List<String> calculateIp(String startIp, String endIp){
        Long startIpLong = convertIpV4ToLong(startIp);
        Long endIpLong = convertIpV4ToLong(endIp);

        List<String> ips = LongStream.rangeClosed(0, endIpLong - startIpLong).boxed()
                .map(index -> convertLongToIpV4(startIpLong + index))
                .collect(Collectors.toList());
        return ips;
    }

    @SneakyThrows
    public static List<NetworkInfoDTO> queryLocalNetworkInfo() {

        List<NetworkInfoDTO> networks = new ArrayList<>();
        Enumeration<NetworkInterface> netInterfaces;

        // 拿到所有网卡
        netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        // 遍历每个网卡，拿到ip
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = addresses.nextElement();
                if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
                    networks.add(new NetworkInfoDTO(ip.getHostAddress(), ni.getName()));
                }
            }
        }
        return networks;
    }

    public static Set<String> queryLocalIps(){
        Set<String> localIps = queryLocalNetworkInfo().stream()
                .map(NetworkInfoDTO::getIpAddr)
                .filter(ip -> !StringUtils.equals(ip, "127.0.0.1"))
                .collect(Collectors.toSet());
        return localIps;
    }

    public static Boolean pingDomain(String DomainName){
        InetAddress address = null;
        try {
            address = InetAddress.getByName("iservice.h3c.com");
            String ip = address.getHostAddress();
            return isIpReachable(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
      return false;
    }

    public static String netSegByIpAndMask(String ip,String mask){
        String[] ipArray =  ip.split("[.]");
        String[] maskArray = mask.split("[.]");

        String minIp = "";
        String maxIp = "";
        int subMask = 0;
        for(int i=0 ;i < maskArray.length ; i++){
            int maskTmp = Integer.valueOf(maskArray[i]);
            int ipTmp = Integer.valueOf(ipArray[i]);
            int mi = maskTmp&ipTmp;

            //System.out.println("==============="+maskTmp+" "+ipTmp);
            if(ipTmp == mi && maskTmp==255){
                minIp = minIp + ipTmp+"." ;
                maxIp = maxIp + ipTmp+"." ;
                subMask = subMask + 8;
            }else{
                minIp = minIp + mi+"." ;
                maxIp = maxIp + maxNetSeg(Integer.toBinaryString(maskTmp),mi)+".";
                subMask = subMask + subMaskNum(Integer.toBinaryString(maskTmp));
            }


        }

        maxIp = maxIp.substring(0,maxIp.length()-1);
        System.out.println("minip "+minIp);
        System.out.println("maxip "+maxIp);
        System.out.println("mask "+subMask);
        return minIp.substring(0,minIp.length()-1);
    }
    private static int calculate(int n) {
        if (n == 0)
            return 1;
        return 2 * calculate(n - 1);
    }

    private static Integer maxNetSeg(String s1,int mi){
        s1 = new StringBuffer(s1).reverse().toString();

        if(s1.indexOf("1")!=-1){
            int i = s1.indexOf("1");
            return mi+calculate(i);
        }else {
            return 255;
        }

    }
    private static Integer subMaskNum(String s1){
        if(s1.lastIndexOf("1")!=-1){
            int i = s1.lastIndexOf("1") + 1;
            return i;
        }
        return 0;

    }
}
