package com.mms.job.server;


import com.mms.common.core.constants.scan.MapperScanConstants;
import com.mms.common.core.constants.scan.PackageScanConstants;
import com.mms.common.core.constants.scan.FeignScanConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 实现功能【Job 调度中心服务启动类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 17:37:57
 */
@EnableScheduling
@EnableDiscoveryClient
@MapperScan(MapperScanConstants.JOB_MAPPER_SCAN)
@EnableFeignClients(basePackages = FeignScanConstants.USERCENTER_FEIGN_SCAN)
@SpringBootApplication(scanBasePackages = PackageScanConstants.JOB_PACKAGE_SCAN)
public class JobApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}