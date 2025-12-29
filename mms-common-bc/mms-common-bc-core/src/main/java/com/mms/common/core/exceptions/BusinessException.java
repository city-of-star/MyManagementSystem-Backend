package com.mms.common.core.exceptions;

import com.mms.common.core.enums.error.ErrorCode;
import lombok.Getter;
import java.io.Serial;

/**
 * 实现功能【业务异常类】
 * <p>
 * 支持三种异常方式：
 * 1. 规范方式：使用预定义的 ErrorCode 枚举
 * 2. 灵活方式：使用自定义错误消息
 * 3. 灵活方式：使用预定义的 ErrorCode 枚举编码和自定义错误消息
 * </p>
 *
 * @author li.hongyu
 * @date 2025-10-28 20:21:27
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 9196224339724626039L;

    /**
     * 错误码枚举（规范方式）
     */
    private final ErrorCode errorCode;
    
    /**
     * 自定义错误消息（灵活方式）
     */
    private final String customMessage;
    
    /**
     * 是否为自定义消息模式
     */
    private final boolean isCustomMessage;

    /**
     * 构造方法1：使用预定义的ErrorCode枚举（规范方式）
     * 
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
        this.isCustomMessage = false;
    }

    /**
     * 构造方法2：使用自定义错误消息（灵活方式）
     * 
     * @param customMessage 自定义错误消息
     */
    public BusinessException(String customMessage) {
        super(customMessage);
        this.errorCode = ErrorCode.INVALID_OPERATION; // 使用默认错误码
        this.customMessage = customMessage;
        this.isCustomMessage = true;
    }

    /**
     * 构造方法3：使用自定义错误消息来覆盖预定义的ErrorCode枚举中的消息（灵活方式）
     *
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
        this.isCustomMessage = true;
    }

    /**
     * 获取最终的错误消息
     * 
     * @return 错误消息
     */
    public String getMessage() {
        return isCustomMessage ? customMessage : errorCode.getMessage();
    }

    /**
     * 获取最终的错误码
     * 
     * @return 错误码
     */
    public int getCode() {
        return errorCode.getCode();
    }
}
