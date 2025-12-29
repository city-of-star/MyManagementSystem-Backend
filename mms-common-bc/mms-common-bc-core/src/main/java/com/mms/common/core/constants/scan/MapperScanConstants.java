package com.mms.common.core.constants.scan;

/**
 * 实现功能【Mapper 扫描路径常量类】
 * <p>
 * 用于统一管理各服务的 Mapper 扫描路径，避免在服务启动类中硬编码
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-06 16:52:48
 */
public class MapperScanConstants {

    /**
     * Base 服务的 Mapper 扫描路径
     */
    public static final String BASE_MAPPER_SCAN = "com.mms.base.service.**.mapper";

    /**
     * UserCenter 服务的 Mapper 扫描路径
     */
    public static final String USERCENTER_MAPPER_SCAN = "com.mms.usercenter.service.**.mapper";
}