package com.mms.usercenter.server;

import com.mms.common.core.constants.scan.FeignScanConstants;
import com.mms.common.core.constants.scan.MapperScanConstants;
import com.mms.common.core.constants.scan.PackageScanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@MapperScan(MapperScanConstants.USERCENTER_MAPPER_SCAN)
@EnableFeignClients(basePackages = {FeignScanConstants.BASE_FEIGN_SCAN})
@SpringBootApplication(scanBasePackages = {PackageScanConstants.USERCENTER_PACKAGE_SCAN, PackageScanConstants.COMMON_PACKAGE_SCAN})
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }
}