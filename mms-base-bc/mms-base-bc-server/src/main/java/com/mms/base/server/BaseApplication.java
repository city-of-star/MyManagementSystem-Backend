package com.mms.base.server;

import com.mms.common.core.constants.scan.FeignScanConstants;
import com.mms.common.core.constants.scan.MapperScanConstants;
import com.mms.common.core.constants.scan.PackageScanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@MapperScan(MapperScanConstants.BASE_MAPPER_SCAN)
@EnableFeignClients(basePackages = {FeignScanConstants.USERCENTER_FEIGN_SCAN})
@EnableScheduling
@SpringBootApplication(scanBasePackages = {PackageScanConstants.BASE_PACKAGE_SCAN, PackageScanConstants.COMMON_PACKAGE_SCAN})
public class BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }
}