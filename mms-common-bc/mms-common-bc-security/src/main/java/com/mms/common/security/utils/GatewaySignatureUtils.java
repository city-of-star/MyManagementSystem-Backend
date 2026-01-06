package com.mms.common.security.utils;

import com.mms.common.core.exceptions.ServerException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 实现功能【网关签名工具类】
 * <p>
 * 网关使用RSA私钥对用户信息进行数字签名，下游服务使用公钥验证签名
 * 签名内容：userId|username|tokenJti|timestamp
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 11:42:40
 */
@Slf4j
public class GatewaySignatureUtils {

    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String DELIMITER = "|";

    /**
     * 使用RSA私钥对用户信息进行签名
     *
     * @param userId    用户ID
     * @param username  用户名
     * @param tokenJti  Token标识
     * @param timestamp 时间戳（毫秒）
     * @param privateKeyStr RSA私钥（Base64编码的PKCS#8格式）
     * @return Base64编码的签名值
     */
    public static String sign(String userId, String username, String tokenJti, long timestamp, String privateKeyStr) {
        try {
            // 组装待签名内容
            String content = buildSignContent(userId, username, tokenJti, timestamp);

            // 解析私钥
            PrivateKey privateKey = parsePrivateKey(privateKeyStr);

            // 创建签名对象
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));

            // 生成签名
            byte[] signBytes = signature.sign();

            // Base64编码返回
            return Base64.getEncoder().encodeToString(signBytes);
        } catch (Exception e) {
            log.error("网关签名生成失败: userId={}, username={}, tokenJti={}, error={}", 
                    userId, username, tokenJti, e.getMessage(), e);
            throw new ServerException("网关签名失败", e);
        }
    }

    /**
     * 使用RSA公钥验证签名
     *
     * @param userId      用户ID
     * @param username    用户名
     * @param tokenJti    Token标识
     * @param timestamp   时间戳（毫秒）
     * @param signatureStr Base64编码的签名值
     * @param publicKeyStr RSA公钥（Base64编码的X.509格式）
     * @return 验证通过返回true，否则返回false
     */
    public static boolean verify(String userId, String username, String tokenJti, long timestamp,
                                 String signatureStr, String publicKeyStr) {
        try {
            // 组装待验证内容（与签名时一致）
            String content = buildSignContent(userId, username, tokenJti, timestamp);

            // 解析公钥
            PublicKey publicKey = parsePublicKey(publicKeyStr);

            // Base64解码签名
            byte[] signBytes = Base64.getDecoder().decode(signatureStr);

            // 创建签名验证对象
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));

            // 验证签名
            boolean isValid = signature.verify(signBytes);
            if (!isValid) {
                log.warn("网关签名验证失败: userId={}, username={}, tokenJti={}, reason=签名不匹配",
                        userId, username, tokenJti);
            }
            return isValid;
        } catch (Exception e) {
            log.warn("网关签名验证异常: userId={}, username={}, tokenJti={}, error={}", 
                    userId, username, tokenJti, e.getMessage());
            return false;
        }
    }

    /**
     * 组装待签名内容
     * 格式：userId|username|tokenJti|timestamp
     */
    private static String buildSignContent(String userId, String username, String tokenJti, long timestamp) {
        return userId + DELIMITER + username + DELIMITER + tokenJti + DELIMITER + timestamp;
    }

    /**
     * 解析RSA私钥（PKCS#8格式，Base64编码）
     */
    private static PrivateKey parsePrivateKey(String privateKeyStr) throws Exception {
        try {
            // 移除可能的PEM格式标记
            String keyContent = privateKeyStr
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("解析RSA私钥失败", e);
            throw new ServerException("解析RSA私钥失败", e);
        }
    }

    /**
     * 解析RSA公钥（X.509格式，Base64编码）
     */
    private static PublicKey parsePublicKey(String publicKeyStr) throws Exception {
        try {
            // 移除可能的PEM格式标记
            String keyContent = publicKeyStr
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                    .replace("-----END RSA PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("解析RSA公钥失败", e);
            throw new ServerException("解析RSA公钥失败", e);
        }
    }
}


