package com.mms.gateway.service;

import com.mms.common.security.properties.GatewaySignatureProperties;
import com.mms.common.security.utils.GatewaySignatureUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【网关签名服务】
 * <p>
 * 网关使用RSA私钥对用户信息进行数字签名
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 11:42:40
 */
@Slf4j
@Service
public class GatewaySignatureService {

    @Resource
    private GatewaySignatureProperties signatureProperties;

    /**
     * 生成网关签名
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param tokenJti Token标识
     * @return 签名值和时间戳的数组 [signature, timestamp]
     */
    public String[] generateSignature(String userId, String username, String tokenJti) {
        long timestamp = System.currentTimeMillis();
        String signature = GatewaySignatureUtils.sign(
                userId, username, tokenJti, timestamp, signatureProperties.getPrivateKey());
        // 签名生成成功，日志由调用方记录，避免重复日志
        return new String[]{signature, String.valueOf(timestamp)};
    }
}


