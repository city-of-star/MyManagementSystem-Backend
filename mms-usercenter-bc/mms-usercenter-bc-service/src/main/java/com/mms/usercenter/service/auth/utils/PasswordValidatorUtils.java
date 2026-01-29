package com.mms.usercenter.service.auth.utils;

import com.mms.common.core.enums.error.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 实现功能【密码校验工具类】
 * <p>
 * 用于校验密码复杂度，包括：
 * - 密码长度校验（最小8位）
 * - 字符种类校验（必须包含大小写字母、数字、特殊字符中的至少3种）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-15 09:52:26
 */
public class PasswordValidatorUtils {

    /**
     * 最小密码长度
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * 最大密码长度
     */
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * 小写字母正则
     */
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");

    /**
     * 大写字母正则
     */
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");

    /**
     * 数字正则
     */
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");

    /**
     * 特殊字符正则（包括常见特殊字符：!@#$%^&*()_+-=[]{}|;':\",./<>?~`）
     */
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;':\",./<>?~`\\\\]");

    /**
     * 字符类型检查结果
     */
    private static class CharTypeCheckResult {
        boolean hasLowercase;
        boolean hasUppercase;
        boolean hasDigit;
        boolean hasSpecialChar;
        int typeCount;

        CharTypeCheckResult(boolean hasLowercase, boolean hasUppercase, boolean hasDigit, boolean hasSpecialChar) {
            this.hasLowercase = hasLowercase;
            this.hasUppercase = hasUppercase;
            this.hasDigit = hasDigit;
            this.hasSpecialChar = hasSpecialChar;
            this.typeCount = (hasLowercase ? 1 : 0) + (hasUppercase ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecialChar ? 1 : 0);
        }
    }

    /**
     * 检查密码的字符类型
     *
     * @param password 待检查的密码
     * @return 字符类型检查结果
     */
    private static CharTypeCheckResult checkCharTypes(String password) {
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).find();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).find();
        return new CharTypeCheckResult(hasLowercase, hasUppercase, hasDigit, hasSpecialChar);
    }

    /**
     * 校验密码复杂度
     * <p>
     * 密码要求：
     * 1. 长度：8-128位
     * 2. 字符种类：必须包含大小写字母、数字、特殊字符中的至少3种
     * </p>
     *
     * @param password 待校验的密码
     * @throws BusinessException 如果密码不符合要求，抛出业务异常
     */
    public static void validate(String password) {
        if (!StringUtils.hasText(password)) {
            throw new BusinessException(ErrorCode.PWD_EMPTY);
        }

        // 校验密码长度
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PWD_INVALID,
                    String.format("密码长度至少为%d位", MIN_PASSWORD_LENGTH));
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new BusinessException(ErrorCode.PWD_INVALID,
                    String.format("密码长度不能超过%d位", MAX_PASSWORD_LENGTH));
        }

        // 检查字符种类
        CharTypeCheckResult result = checkCharTypes(password);

        // 必须包含至少3种字符类型
        if (result.typeCount < 3) {
            throw new BusinessException(ErrorCode.PWD_WEAK, "密码必须包含大小写字母、数字、特殊字符中的至少3种");
        }
    }

    /**
     * 获取密码强度提示信息
     *
     * @param password 待校验的密码
     * @return 密码强度提示信息
     */
    public static String getPasswordStrengthHint(String password) {
        if (!StringUtils.hasText(password)) {
            return "密码不能为空";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return String.format("密码长度至少为%d位", MIN_PASSWORD_LENGTH);
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            return String.format("密码长度不能超过%d位", MAX_PASSWORD_LENGTH);
        }

        CharTypeCheckResult result = checkCharTypes(password);

        if (result.typeCount < 3) {
            StringBuilder missingTypes = new StringBuilder();
            if (!result.hasLowercase) {
                missingTypes.append("小写字母、");
            }
            if (!result.hasUppercase) {
                missingTypes.append("大写字母、");
            }
            if (!result.hasDigit) {
                missingTypes.append("数字、");
            }
            if (!result.hasSpecialChar) {
                missingTypes.append("特殊字符、");
            }

            // 移除最后的顿号
            if (!missingTypes.isEmpty()) {
                missingTypes.setLength(missingTypes.length() - 1);
            }
            return String.format("密码必须包含大小写字母、数字、特殊字符中的至少3种，当前缺少：%s", missingTypes);
        }

        return "密码强度符合要求";
    }

    /**
     * 私有构造函数，防止实例化
     */
    private PasswordValidatorUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

