package com.mms.common.core.constants.scan;

/**
 * 实现功能【Feign 接口扫描路径常量类】
 * <p>
 * 用于统一管理各服务的 Feign 客户端扫描路径，避免在服务启动类中硬编码
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-06 16:53:34
 */
public class FeignScanConstants {

    /**
     * Base 服务的 Feign 客户端扫描路径
     */
    public static final String BASE_FEIGN_SCAN = "com.mms.base.feign";

    /**
     * UserCenter 服务的 Feign 客户端扫描路径
     */
    public static final String USERCENTER_FEIGN_SCAN = "com.mms.usercenter.feign";
}