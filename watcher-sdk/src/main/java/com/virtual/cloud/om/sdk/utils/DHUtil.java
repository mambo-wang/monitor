package com.virtual.cloud.om.sdk.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DHUtil {
    /**
     * 使用DH方式加解密的前提条件：否则报错 java.security.NoSuchAlgorithmException: Unsupported secret key algorithm: AES
     *  1.当前开发环境中的运行的java程序, 在jre中配置缺省VM变量 -Djdk.crypto.KeyAgreement.legacyKDF=true
     *      A.点击窗口，选择首选项  B.点击installed JREs, 选择JRE配置，然后编辑  C.在缺省VM参数：-Djdk.crypto.KeyAgreement.legacyKDF=true
     *  2.可运行jar包，则需要在运行时采用命令提示符运行，在运行时添加VM参数，运行命令为：java -jar -Djdk.crypto.KeyAgreement.legacyKDF=true jarPackName.jar
     *      A.编辑可执行文件，配置VM参数：-Djdk.crypto.KeyAgreement.legacyKDF=true
     */

    /**
     * 本地密钥算法，即对称加密密钥算法  可选DES、DESede或者AES
     */
    private static final String SELECT_ALGORITHM = "AES";
    /**
     * 默认的加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /**
     * 密钥长度
     */
    private static final int KEY_SIZE = 512;
    //公钥
    private static final String PUBLIC_KEY = "DHPublicKey";
    //私钥
    private static final String PRIVATE_KEY = "DHPrivateKey";

    /**
     * 初始化甲方密钥
     *
     * @return Map 甲方密钥Map
     * @throws Exception
     */
    public static Map<String, Object> initKey() {
        try {
            //实例化密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            //初始化密钥对生成器
            keyPairGenerator.initialize(KEY_SIZE);
            //生成密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            //甲方公钥
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
            //甲方私钥
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
            //将密钥对存储在Map中
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化乙方密钥
     *
     * @param key 甲方公钥
     * @return Map 乙方密钥Map
     * @throws Exception
     */
    public static Map<String, Object> initKey(byte[] key) {
        try {
            //解析甲方公钥
            //转换公钥材料
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            //由甲方公钥构建乙方密钥
            DHParameterSpec dhParameterSpec = ((DHPublicKey) pubKey).getParams();
            //实例化密钥对生成器
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            //初始化密钥对生成器
            keyPairGenerator.initialize(dhParameterSpec);
            //产生密钥对
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            //乙方公钥
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
            //乙方私约
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
            //将密钥对存储在Map中
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final byte[] key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(SELECT_ALGORITHM);
            // 加密过程需手动指定随机数的生成规则，同理在解密过程中也需手动指定随机数的生成规则
            // 否则再linux环境中会导致同样的加密内容每次加密结果不一致
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key);
            //AES 要求密钥长度为 128
            kg.init(128, random);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), SELECT_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            byte[] byteContent = data;
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));// 初始化为加密模式的密码器
            byte[] result = cipher.doFinal(byteContent);// 加密
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            //执行操作
            byte[] result = cipher.doFinal(data);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 构建密钥
     *
     * @param publicKey  公钥
     * @param privateKey 私钥
     * @return byte[] 本地密钥
     * @throws Exception
     */
    public static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) {
        try {
            //实例化密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance("DH");
            //初始化公钥
            //密钥材料转换
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            //产生公钥
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            //初始化私钥
            //密钥材料转换
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            //产生私钥
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            //实例化
            KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
            //初始化
            keyAgree.init(priKey);
            keyAgree.doPhase(pubKey, true);
            //生成本地密钥
            SecretKey secretKey = keyAgree.generateSecret(SELECT_ALGORITHM);
            return secretKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得私钥
     *
     * @param keyMap 密钥Map
     * @return byte[] 私钥
     * @throws Exception
     */
    public static byte[] getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥
     *
     * @param keyMap 密钥Map
     * @return byte[] 公钥
     * @throws Exception
     */
    public static byte[] getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }


    public static void main(String[] args) {
        //甲方公钥
        byte[] publicKeyA;
        //甲方私钥
        byte[] privateKeyA;
        //甲方本地密钥
        byte[] localKeyA;
        //乙方公钥
        byte[] publicKeyB;
        //乙方私钥
        byte[] privateKeyB;
        //乙方本地密钥
        byte[] localKeyB;

        //初始化密钥
        //生成甲方密钥对
        Map<String, Object> keyMapA = DHUtil.initKey();
        publicKeyA = DHUtil.getPublicKey(keyMapA);
        privateKeyA = DHUtil.getPrivateKey(keyMapA);
        System.out.println("甲方公钥:\n" + Base64.getEncoder().encodeToString(publicKeyA));
        System.out.println("甲方私钥:\n" + Base64.getEncoder().encodeToString(privateKeyA));
        //由甲方公钥产生乙方本地密钥对
        Map<String, Object> keyMapB = DHUtil.initKey(publicKeyA);
        publicKeyB = DHUtil.getPublicKey(keyMapB);
        privateKeyB = DHUtil.getPrivateKey(keyMapB);
        System.out.println("乙方公钥:\n" + Base64.getEncoder().encodeToString(publicKeyB));
        System.out.println("乙方私钥:\n" + Base64.getEncoder().encodeToString(privateKeyB));
        localKeyA = DHUtil.getSecretKey(publicKeyB, privateKeyA);
        System.out.println("甲方本地密钥:\n" + Base64.getEncoder().encodeToString(localKeyA));
        localKeyB = DHUtil.getSecretKey(publicKeyA, privateKeyB);
        System.out.println("乙方本地密钥:\n" + Base64.getEncoder().encodeToString(localKeyB));

        System.out.println();
        System.out.println("===甲方向乙方发送加密数据===");
        String msgA2B = "求知若饥，虚心若愚。";
        System.out.println("原文:\n" + msgA2B);
        System.out.println("---使用甲方本地密钥对数据进行加密---");
        //使用甲方本地密钥对数据加密
        byte[] encodeMsgA2B = DHUtil.encrypt(msgA2B.getBytes(), localKeyA);
        System.out.println("加密:\n" + Base64.getEncoder().encodeToString(encodeMsgA2B));
        System.out.println("---使用乙方本地密钥对数据库进行解密---");
        //使用乙方本地密钥对数据进行解密
        byte[] msgB2A = DHUtil.decrypt(encodeMsgA2B, localKeyB);
        String output1 = new String(msgB2A);
        System.out.println("解密:\n" + output1);
        System.out.println("/~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~..~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~.~/");
        System.out.println("===乙方向甲方发送加密数据===");
        String input2 = "好好学习，天天向上。";
        System.out.println("原文:\n" + input2);
        System.out.println("---使用乙方本地密钥对数据进行加密---");
        //使用乙方本地密钥对数据进行加密
        byte[] encode2 = DHUtil.encrypt(input2.getBytes(), localKeyB);
        System.out.println("加密:\n" + Base64.getEncoder().encodeToString(encode2));
        System.out.println("---使用甲方本地密钥对数据进行解密---");
        //使用甲方本地密钥对数据进行解密
        byte[] decode2 = DHUtil.decrypt(encode2, localKeyA);
        String output2 = new String(decode2);
        System.out.println("解密:\n" + output2);
//
//
//        String encryptText = RSAUtils.encrypt(msgA2B, privateKeyA);
//        System.out.println("加密后的字符串为:" + encryptText);
//        String messageDe = RSAUtils.decrypt(msgA2B, publicKeyA);
//        System.out.println("解密后的字符串为:" + messageDe);


    }
}
