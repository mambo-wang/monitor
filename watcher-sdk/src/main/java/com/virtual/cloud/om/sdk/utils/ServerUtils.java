package com.virtual.cloud.om.sdk.utils;

import com.virtual.cloud.om.sdk.exception.AppException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.Base64;
import org.apache.http.util.TextUtils;
import org.springframework.http.HttpHeaders;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * 服务器端工具集。 st
 *
 */
public final class ServerUtils {

    /** 日志记录实体。 */
    private static Log log = LogFactory.getLog(ServerUtils.class);

    // ------------------------------------------------------------------ 加密和解密

    /** 加解密使用的密钥。 */
    private static final byte[] SECRET_KEY = "li_01010".getBytes(Charset.defaultCharset());
    /** 加密使用的 DES 密码。 */
    private static Cipher encryptDesCipher = null;

    /** 解密使用的 DES 密码。 */
    private static Cipher decryptDesCipher = null;

    private static final String ENCRYPT_AL = "DES/ECB/PKCS5Padding";

    /** Like 查询条件自动扩展的匹配模式。 */
    public enum AutoMatchMode {
        /** 精确匹配。 */
        Exact,
        /** 前面可以有任意字符。 */
        Start,
        /** 后面可以有任意字符。 */
        End,
        /** 前后均可以有任意字符。 */
        Anywhere
    }

    static {
        try {
            DESKeySpec desKeySpec = new DESKeySpec(SECRET_KEY);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey desSecretKey = factory.generateSecret(desKeySpec);
            decryptDesCipher = Cipher.getInstance(ENCRYPT_AL);
            decryptDesCipher.init(Cipher.DECRYPT_MODE, desSecretKey);
        } catch (Exception e) {
            log.error(null, e);
        }

        try {
            DESKeySpec desKeySpec = new DESKeySpec(SECRET_KEY);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey desSecretKey = factory.generateSecret(desKeySpec);
            encryptDesCipher = Cipher.getInstance(ENCRYPT_AL);
            encryptDesCipher.init(Cipher.ENCRYPT_MODE, desSecretKey);
        } catch (Exception e) {
            log.error(null, e);
        }
    }

    /**
     * 将给定的字符串转换为UTF编码数组。主要用于将汉字转换为UTF-8编码。
     *
     * @param chenese 输入字符串。
     * @return the byte[] 经UTF编码后的数组
     */
    public static byte[] gbkToUtf8(String chenese) {
        // Step 1: 得到GBK编码下的字符数组，一个中文字符对应这里的一个c
        char c[] = chenese.toCharArray();
        // Step 2: UTF-8使用3个字节存放一个中文字符，所以长度必须为字符的3 倍
        byte[] fullByte = new byte[3 * c.length];
        int pos = 0;
        // Step 3: 循环将字符的GBK编码转换成UTF-8编码
        for (int i = 0; i < c.length; i++) {
            // Step 3-1：将字符的ASCII编码转换成2进制值
            int m = (int)c[i];
            if (m >= 0 && m <= 255) {
                fullByte[pos] = (byte)m;
                pos++;
                continue;
            }
            String word = Integer.toBinaryString(m);

            // Step 3-2：将2进制值补足16位(2个字节的长度)
            StringBuffer sb = new StringBuffer();
            int len = 16 - word.length();
            for (int j = 0; j < len; j++) {
                sb.append("0");
            }
            // Step 3-3：得到该字符最终的2进制GBK编码
            // 形似：1000 0010 0111 1010
            sb.append(word);
            // Step 3-4：最关键的步骤，根据UTF-8的汉字编码规则，首字节
            // 以1110开头，次字节以10开头，第3字节以10开头。在原始的2进制
            // 字符串中插入标志位。最终的长度从16--->16+3+2+2=24。
            sb.insert(0, "1110");
            sb.insert(8, "10");
            sb.insert(16, "10");

            // Step 3-5：将新的字符串进行分段截取，截为3个字节
            String s1 = sb.substring(0, 8);
            String s2 = sb.substring(8, 16);
            String s3 = sb.substring(16);

            // Step 3-6：最后的步骤，把代表3个字节的字符串按2进制的方式
            // 进行转换，变成2进制的整数，再转换成16进制值
            byte b0 = Integer.valueOf(s1, 2).byteValue();
            byte b1 = Integer.valueOf(s2, 2).byteValue();
            byte b2 = Integer.valueOf(s3, 2).byteValue();

            // Step 3-7：把转换后的3个字节按顺序存放到字节数组的对应位置
            byte[] bf = new byte[3];
            bf[0] = b0;
            bf[1] = b1;
            bf[2] = b2;

            fullByte[pos] = bf[0];
            fullByte[pos + 1] = bf[1];
            fullByte[pos + 2] = bf[2];
            pos += 3;
            // Step 3-8：返回继续解析下一个中文字符
        }
        byte[] result = new byte[pos];
        System.arraycopy(fullByte, 0, result, 0, pos);
        return result;
    }

    /**
     * 对特定文本进行解密。文本编码使用当前缺省编码。
     *
     * @param cryptoText 解密前的密文。
     * @return 解密后的明文。
     */
    public static String decryptText(String cryptoText) {
        return decryptText(cryptoText, Charset.defaultCharset().name());
    }

    /**
     * 对特定文本进行解密。
     *
     * @param cryptoText 解密前的密文。
     * @param charsetName 文本使用的字符编码名。
     * @return 解密后的明文。
     */
    public static String decryptText(String cryptoText, String charsetName) {
        try {
            byte[] bytes = Base64.decodeBase64(cryptoText.getBytes(charsetName));
            if (bytes != null) {
                bytes = decrypt(bytes);
                if (bytes != null) {
                    return new String(bytes, charsetName);
                }
            }
        } catch (Exception e) {
            log.error(null, e);
        }
        return null;
    }

    /**
     * 使用 DES 算法，对特定二进制字符进行解密。
     *
     * @param cryptoData 解密前的密文。
     * @return 解密后的明文。
     */
    public static byte[] decrypt(byte[] cryptoData) {
        try {
            return decryptDesCipher.doFinal(cryptoData);
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    /**
     * 对特定文本进行加密。文本编码使用当前缺省编码。
     *
     * @param plainText 加密前的明文。
     * @return 加密后的密文。
     */
    public static String encryptText(String plainText) {
        return encryptText(plainText, Charset.defaultCharset().name());
    }

    /**
     * 对特定文本进行加密。
     *
     * @param plainText 加密前的明文。
     * @param charsetName 文本使用的字符编码名。
     * @return 加密后的密文。
     */
    public static String encryptText(String plainText, String charsetName) {
        try {
            byte[] bytes = encrypt(plainText.getBytes(charsetName));
            if (bytes != null) {
                return new String(Base64.encodeBase64(bytes), charsetName);
            }
        } catch (Exception e) {
            log.error(null, e);
        }
        return null;
    }

    /**
     * 使用 DES 算法，对特定二进制字符进行加密。
     *
     * @param plainData 加密前的明文。
     * @return 加密后的密文。
     */
    public static byte[] encrypt(byte[] plainData) {
        try {
            return encryptDesCipher.doFinal(plainData);
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    /**
     * 删除本地目录或者文件
     * @param path
     */
    public static void deleteLocalFile(String path){
        ProcessBuilder pb = new ProcessBuilder("rm","-rf", path);
        pb.redirectErrorStream(true);
        try {
            pb.start();
        } catch (IOException e) {
            log.error(null, e);
        }
    }

    public static String encode(String str) {
        try {
            String value = null;
            Locale locale = Locale.getDefault();
            if ("zh".equals(locale.getLanguage())) {
                value = new String(str.getBytes("GBK"), "ISO-8859-1");
            } else {
                value = new String(str.getBytes(Charset.defaultCharset()), "ISO-8859-1");
            }
            return value;
        } catch (UnsupportedEncodingException e) {
            log.error(null, e);
            return str;
        }
    }

    /**
     * 相当于调用 transformLike(condition, AutoMatchMode.Anywhere)。
     *
     * @param condition Like 查询条件，仅当此参数为 CharSequence，才有效。
     * @return 转义后的查询条件。
     */
    public static Object transformLike(Object condition) {
        return transformLike(condition, AutoMatchMode.Anywhere);
    }

    /**
     * 对 Like 查询条件进行转义处理，对查询条件中出现的 % 和 _ 进行转义，而使用更为流行的 * 和 ? 代替。
     * <p>处理规则如下：
     * <ul>
     * <li>将 SQL 通配符 % 和 _ 直接转义，前面增加转义符 \；</li>
     * <li>对前面没有转义字符 \ 的 * 和 ? 分别转换为 SQL 中的通配符 % 和 _；</li>
     * <li>若查询中出现带转义符的 \* 和 \?，将转义符删除，只保留 * 和 ?；</li>
     * <li>若条件中没有出现通配符 * 和 ?，则根据 matchMode 参数，有选择的增加通配符。</li>
     * </ul>
     *
     * @param condition Like 查询条件，仅当此参数为 CharSequence，才有效。
     * @param matchMode 当查询条件中没有通配符时，增加自动通配符的方式。
     * @return 转义后的查询条件。
     */
    public static Object transformLike(Object condition, AutoMatchMode matchMode) {
        // 参数检查
        if (condition == null) {
            return null;
        } else if (!(condition instanceof CharSequence)) {
            return condition;
        }
        CharSequence cs = (CharSequence) condition;
        // 准备
        StringBuilder sb = new StringBuilder();
        char lastChar = 0;
        boolean hasWildcard = false;
        // 逐个字符进行检查
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (c == '%' || c == '_') {
                if (lastChar != '\\') {
                    sb.append('\\').append(c);
                    lastChar = c;
                    continue;
                }
            } else if (c == '*' || c == '?') {
                if (lastChar != '\\') {
                    sb.append(c == '*' ?  '%' : '_');
                    lastChar = c;
                    hasWildcard = true;
                    continue;
                } else {
                    sb.deleteCharAt(sb.length() - 1);
                    sb.append(c);
                    lastChar = c;
                    continue;
                }
            }
            sb.append(c);
            lastChar = c;
        }

        // 自动扩展通配符
        if (!hasWildcard) {
            if (matchMode == AutoMatchMode.Start) {
                return "%" + sb.toString();
            } else if (matchMode == AutoMatchMode.End) {
                return sb.toString() + "%";
            } else if (matchMode == AutoMatchMode.Anywhere) {
                return "%" + sb.toString() + "%";
            }
        }
        return sb.toString();
    }

    /**
     * 处理表示层 {@link AppException} 的日志记录方式。
     * 对关键的系统错误，以 WARN 级别记录日志；否则以 DEBUG 级别记录日志。
     * @param log 用于记录日志的对象。
     * @param ae {@link AppException} 错误。
     */
    public static void logAppException(Log log, AppException ae) {
        if (ae.getErrorCode() <= 20) {
            log.warn("Application Exception", ae);
        } else if (log.isDebugEnabled()) {
            log.debug("Application Exception", ae);
        } else {
            log.error("Application Exception", ae);
        }
    }

    /**
     * 特殊字符转义，用于前台不便于转义的情况
     * @param str
     * @return 转义后的字符串
     */
    public static String escapeXml(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '&') {
                sb.append("&amp;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '"') {
                sb.append("&#034;");
            } else if (c == '\'') {
                sb.append("&#039;");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 生成统一认证模块re接口token header
     * */
    public static HttpHeaders formatAuthorizationHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        //Bearer+空格+统一认证模块管理员Token
        String token = (String) WebUtils.request().getAttribute("AC_TOKEN");
        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
        return httpHeaders;
    }

    /**
     * 生成统一认证模块re接口token header
     * */
    public static HttpHeaders formatAuthorizationHeader(String token) {
        //token有值时使用传递的token
        if (TextUtils.isEmpty(token)){
            token = (String) WebUtils.request().getAttribute("AC_TOKEN");
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return httpHeaders;
    }
}
