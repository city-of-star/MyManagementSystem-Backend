package com.mms.common.core.constants.scan;

/**
 * 实现功能【服务扫描路径常量类】
 * <p>
 * 用于统一管理各服务的包扫描路径，避免在服务启动类中硬编码
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-06 16:54:16
 */
public final class PackageScanConstants {

    /**
     * 公共模块的包扫描路径
     */
    public static final String COMMON_PACKAGE_SCAN = "com.mms.common";

    /**
     * Base 服务的包扫描路径
     */
    public static final String BASE_PACKAGE_SCAN = "com.mms.base";

    /**
     * UserCenter 服务的包扫描路径
     */
    public static final String USERCENTER_PACKAGE_SCAN = "com.mms.usercenter";

    /**
     * Gateway 服务的包扫描路径
     */
    public static final String GATEWAY_PACKAGE_SCAN = "com.mms.gateway";

    /**
     * 私有构造函数，防止实例化
     */
    private PackageScanConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}