package com.mms.gateway;

import com.mms.common.core.constants.scan.PackageScanConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {PackageScanConstants.GATEWAY_PACKAGE_SCAN, PackageScanConstants.COMMON_PACKAGE_SCAN})
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(com.mms.gateway.GatewayApplication.class, args);
    }
}

