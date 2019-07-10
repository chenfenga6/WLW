package com.Fate_Project.AES;

/**
 * AES 128bit 加密解密工具类
 * @author dufy
 */

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    //密钥（16进制，和前台保持一致，或者是作为参数直接传过来也可以）
    private static final String defaultKey = "ChEnFeNgCjtZLfym";
    //算法PKCS5Padding
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
    //测试数据
    private static final String testContent = " afasdfadfadfasdfadfa";

    //自测
    public static void main(String[] args) throws Exception {
        System.out.println("加密前：" + testContent);

        String encrypt = encrypt(testContent);
        System.out.println("加密后：" + encrypt+"长度："+encrypt.length());

        System.out.println("PD= "+encrypt.endsWith("=="));

        String decrypt = decrypt(encrypt);
        System.out.println("解密后：" + decrypt);

        System.out.println("PD= "+decrypt.endsWith("=="));
    }



    //AES加密--为base 64 code
    public static String encrypt(String content) throws Exception {
        return base64Encode(aesEncryptToBytes(content));
    }

    //AES解密--并解密base 64 code
    public static String decrypt(String encryptStr) throws Exception {
        encryptStr.replaceAll("#","/");
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr));
    }


    //base 64 encode编码
    private static String base64Encode(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    //base 64 decode解码---》因为传过来的值是通过base64编码而后再进行aes加密出来的，所以解密之前先进行base64解码
    private static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }


    //AES加密
    private static byte[] aesEncryptToBytes(String content) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(defaultKey.getBytes(), "AES"));
        return cipher.doFinal(content.getBytes("utf-8"));
    }

    //AES解密
    private static String aesDecryptByBytes(byte[] encryptBytes) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(defaultKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes,"utf-8");
    }

}
