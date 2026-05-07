package com.zyd.springbootserviceseedproject.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加解密工具类
 * 用于密码管理模块的密码加解密
 *
 * @author zyd
 */
public class AesUtils
{
    /** 加密算法 */
    private static final String ALGORITHM = "AES";
    /** 加密模式和填充方式 */
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    /** 默认密钥（16字节 = 128位），生产环境应通过配置注入 */
    private static final String DEFAULT_SECRET_KEY = "Pm@ssMgr2024!#*^";
    /** 初始向量（16字节） */
    private static final String IV = "PmMgrIV2024!#*^&";

    /**
     * AES 加密
     *
     * @param plainText 明文
     * @return Base64 编码的密文
     */
    public static String encrypt(String plainText)
    {
        return encrypt(plainText, DEFAULT_SECRET_KEY);
    }

    /**
     * AES 加密（自定义密钥）
     *
     * @param plainText 明文
     * @param secretKey 密钥（16字节）
     * @return Base64 编码的密文
     */
    public static String encrypt(String plainText, String secretKey)
    {
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e)
        {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES 解密
     *
     * @param cipherText Base64 编码的密文
     * @return 明文
     */
    public static String decrypt(String cipherText)
    {
        return decrypt(cipherText, DEFAULT_SECRET_KEY);
    }

    /**
     * AES 解密（自定义密钥）
     *
     * @param cipherText Base64 编码的密文
     * @param secretKey  密钥（16字节）
     * @return 明文
     */
    public static String decrypt(String cipherText, String secretKey)
    {
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            throw new RuntimeException("AES解密失败", e);
        }
    }
}
