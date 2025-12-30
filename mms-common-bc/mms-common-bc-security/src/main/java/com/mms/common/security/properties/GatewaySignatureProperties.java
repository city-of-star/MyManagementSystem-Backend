package com.mms.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【网关签名配置属性】
 * <p>
 * 网关使用RSA私钥签名，下游服务使用RSA公钥验证
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-30 11:42:40
 */
@Data
@ConfigurationProperties(prefix = "gateway.signature")
public class GatewaySignatureProperties {

    /**
     * RSA私钥（Base64编码的PKCS#8格式）
     * 仅网关持有，用于签名
     */
    private String privateKey;

    /**
     * RSA公钥（Base64编码的X.509格式）
     * 各下游服务持有，用于验证签名
     */
    private String publicKey;

    /**
     * 签名时间戳有效期（毫秒）
     * 默认5分钟，用于防止重放攻击
     */
    private Long timestampValidity = 300000L;
}


