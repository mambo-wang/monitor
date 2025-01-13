package com.virtual.cloud.om.sdk.utils.uis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

@Slf4j
public class WebDecryptUtils {

    private static Cipher encryptDesCipher;

    private static Cipher decryptDesCipher;

    private static final byte[] PASSWORD_KEY = "hph3c_z01500".getBytes();

    public synchronized static String encryptData(String plainText) {
        // 如果DES加密工具没有创建，则创建之
        if (encryptDesCipher == null) {
            try {
                DESKeySpec desKeySpec = new DESKeySpec(PASSWORD_KEY);
                SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
                SecretKey desSecretKey = factory.generateSecret(desKeySpec);
                encryptDesCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                encryptDesCipher.init(Cipher.ENCRYPT_MODE, desSecretKey);
            } catch (Exception e) {
                log.error(null, e);
                return null;
            }
        }

        // 执行加密操作
        byte[] cryptoText = null;
        try {
            cryptoText = encryptDesCipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }

        // 执行Base64编码
        if (cryptoText != null) {
            return new String(Base64.encodeBase64(cryptoText));
        }

        return null;
    }

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
                decryptDesCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                decryptDesCipher.init(Cipher.DECRYPT_MODE, desSecretKey);
            } catch (Throwable t) {
                return null;
            }
        }

        // 对密文进行Base64解码
        byte[] decodedText = Base64.decodeBase64(cryptoText.getBytes());
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

        // 返回字符串
        if (plainText != null) {
            return new String(plainText);
        }

        return null;
    }

}
