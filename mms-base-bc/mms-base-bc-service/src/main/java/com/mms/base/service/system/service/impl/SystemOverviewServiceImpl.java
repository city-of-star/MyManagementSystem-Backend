package com.mms.base.service.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mms.base.common.system.entity.ConfigEntity;
import com.mms.base.common.system.vo.SystemOverviewVo;
import com.mms.base.service.system.mapper.ConfigMapper;
import com.mms.base.service.system.service.SystemOverviewService;
import com.mms.common.security.constants.JwtConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * 实现功能【系统运行总览服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-29 17:02:33
 */
@Slf4j
@Service
public class SystemOverviewServiceImpl implements SystemOverviewService {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.cloud.nacos.server-addr:unknown}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.config.namespace:${spring.cloud.nacos.discovery.namespace:unknown}}")
    private String nacosNamespace;

    @Value("${spring.cloud.nacos.config.group:${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}}")
    private String nacosGroup;

    @Override
    public SystemOverviewVo getOverview() {
        SystemOverviewVo vo = new SystemOverviewVo();

        // 1. MySQL 健康检查（简单统计一次配置表数量）
        boolean mysqlOk = false;
        long configTotal = 0L;
        try {
            LambdaQueryWrapper<ConfigEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ConfigEntity::getDeleted, 0);
            configTotal = configMapper.selectCount(wrapper);
            mysqlOk = true;
        } catch (Exception e) {
            log.error("数据库健康检查失败：{}", e.getMessage(), e);
            mysqlOk = false;
        }

        // 2. Redis 健康检查 & 在线用户数量统计
        boolean redisOk = false;
        long onlineUsers = 0L;
        try {
            // ping 一次 Redis
            String pong = redisTemplate.execute((RedisCallback<String>) connection -> {
                try {
                    return connection.ping();
                } catch (Exception e) {
                    return null;
                }
            });
            redisOk = "PONG".equalsIgnoreCase(pong);

            // 基于 Refresh Token Key 统计在线用户数（key 前缀：mms:auth:refresh:）
            String prefix = JwtConstants.CacheKeys.REFRESH_TOKEN_PREFIX;
            onlineUsers = countKeysWithPrefix(prefix);
        } catch (Exception e) {
            log.error("Redis 健康检查或在线用户统计失败：{}", e.getMessage(), e);
            redisOk = false;
        }

        // 3. JVM 运行时 + 内存信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemoryMb = runtime.totalMemory() / (1024 * 1024);
        long usedMemoryMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        double usagePercent = totalMemoryMb > 0
                ? (usedMemoryMb * 100.0 / totalMemoryMb)
                : 0.0;

        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMs = mxBean.getUptime();

        vo.setMysqlOk(mysqlOk);
        vo.setDbOk(mysqlOk);
        vo.setRedisOk(redisOk);
        vo.setOnlineUsers(onlineUsers);
        vo.setConfigTotal(configTotal);
        vo.setJvmMemoryTotalMb(totalMemoryMb);
        vo.setJvmMemoryUsedMb(usedMemoryMb);
        vo.setJvmMemoryUsagePercent(Math.round(usagePercent * 10.0) / 10.0); // 保留 1 位小数
        vo.setUptime(formatUptime(uptimeMs));
        vo.setNacosServerAddr(nacosServerAddr);
        vo.setNacosNamespace(nacosNamespace);
        vo.setNacosGroup(nacosGroup);

        // 4. 计算整体状态
        String status;
        if (!mysqlOk) {
            status = "DOWN";
        } else if (usagePercent >= 85.0) {
            status = "DEGRADED";
        } else {
            status = "UP";
        }
        vo.setStatus(status);

        return vo;
    }

    /**
     * 统计指定前缀的 Key 数量（使用 SCAN，避免全量 KEYS 阻塞）
     */
    private long countKeysWithPrefix(String prefix) {
        if (prefix == null) {
            return 0L;
        }
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> matchKeys = new HashSet<>();
            ScanOptions options = ScanOptions.scanOptions()
                    .match(prefix + "*")
                    .count(1000)
                    .build();
            try (var cursor = connection.scan(options)) {
                cursor.forEachRemaining(item -> matchKeys.add(new String(item, StandardCharsets.UTF_8)));
            }
            return matchKeys;
        });
        return keys != null ? keys.size() : 0L;
    }

    /**
     * 将毫秒级运行时间格式化为“X天X小时X分”
     */
    private String formatUptime(long uptimeMs) {
        long totalSeconds = uptimeMs / 1000;
        long days = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0) {
            sb.append(hours).append("小时");
        }
        if (minutes > 0 || sb.length() == 0) {
            sb.append(minutes).append("分");
        }
        return sb.toString();
    }
}

