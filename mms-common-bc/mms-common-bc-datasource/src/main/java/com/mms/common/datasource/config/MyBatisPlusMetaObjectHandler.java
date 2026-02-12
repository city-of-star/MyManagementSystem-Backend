package com.mms.common.datasource.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.mms.common.webmvc.utils.UserContextUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 实现功能【MyBatis-Plus 自动填充处理器】
 * <p>
 * 自动填充 create_time、update_time、create_by、update_by 等字段
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-10 10:24:56
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
        this.fillStrategy(metaObject, "createTime", LocalDateTime.now());
        // 填充更新时间
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
        // 填充创建人ID
        Long userId = UserContextUtils.getUserId();
        if (userId != null) {
            this.fillStrategy(metaObject, "createBy", userId);
        }
        // 填充更新人ID
        if (userId != null) {
            this.fillStrategy(metaObject, "updateBy", userId);
        }
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间
        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now());
        // 填充更新人ID
        Long userId = UserContextUtils.getUserId();
        if (userId != null) {
            this.fillStrategy(metaObject, "updateBy", userId);
        }
    }
}

