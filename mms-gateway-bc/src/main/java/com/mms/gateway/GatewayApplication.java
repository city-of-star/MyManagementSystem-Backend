package com.mms.gateway;

import com.mms.common.core.constants.scan.PackageScanConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthContributorAutoConfiguration;

/**
 * 网关不连库。但依赖了 mms-common-bc-core（内含 mybatis-plus-starter → JDBC），
 * 若不排除会触发 DataSource 自动配置，启动失败：Failed to determine a suitable driver class。
 */
@SpringBootApplication(
        scanBasePackages = PackageScanConstants.GATEWAY_PACKAGE_SCAN,
        exclude = {
                DataSourceAutoConfiguration.class,
                DataSourceHealthContributorAutoConfiguration.class
        }
)
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.mms.gateway.GatewayApplication.class, args);
    }
}

