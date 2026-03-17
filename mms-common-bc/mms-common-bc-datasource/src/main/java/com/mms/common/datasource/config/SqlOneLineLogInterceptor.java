package com.mms.common.datasource.config;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;

/**
 * 实现功能【SQL日志拦截器（一行SQL版）】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-27 11:03:16
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlOneLineLogInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SqlOneLineLogInterceptor.class);

    // 支持配置多个排除的 mapper 或方法
    private static final Set<String> EXCLUDE_STATEMENT_PREFIXES = Set.of(
            "com.mms.job.core.mapper.JobMapper"   // 整个 JobMapper 不打印
    );
    private static final Set<String> EXCLUDE_STATEMENTS = Set.of(
            "com.mms.job.core.mapper.JobMapper.selectDueJobs" // 只排除这个方法
    );

    /** ANSI颜色代码常量 */
    private static final String ANSI_RESET = "\033[0m";
    private static final String ANSI_RED = "\033[31m";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取 SQL 信息
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        String statementId = mappedStatement.getId();
        // 1. 精确方法排除
        if (EXCLUDE_STATEMENTS.contains(statementId)) {
            return invocation.proceed();
        }
        // 2. 整个 mapper 排除
        for (String prefix : EXCLUDE_STATEMENT_PREFIXES) {
            if (statementId.startsWith(prefix + ".")) {
                return invocation.proceed();
            }
        }
        
        try {
            Object result = invocation.proceed();
            long elapsed = System.currentTimeMillis() - startTime;

            Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();

            String sql = formatSqlOneLine(configuration, boundSql);
            log.info("{}耗时：{}ms {}{}", ANSI_RED, elapsed, sql, ANSI_RESET);
            return result;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;

            Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();

            String sql = formatSqlOneLine(configuration, boundSql);
            log.error("{}耗时：{}ms {} 错误：{}{}", ANSI_RED, elapsed, sql, e.getMessage(), ANSI_RESET);
            throw e;
        }
    }

    /**
     * 将参数替换到SQL中，并压缩为一行。
     */
    private String formatSqlOneLine(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql();
        if (sql == null) {
            return "";
        }
        // 保持一行：把所有空白（含换行/制表）压缩成一个空格
        sql = sql.replaceAll("[\\s]+", " ").trim();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        if (parameterMappings != null && !parameterMappings.isEmpty()) {
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                Object value;
                if (boundSql.hasAdditionalParameter(propertyName)) {
                    value = boundSql.getAdditionalParameter(propertyName);
                } else if (boundSql.getParameterObject() == null) {
                    value = null;
                } else if (typeHandlerRegistry.hasTypeHandler(boundSql.getParameterObject().getClass())) {
                    value = boundSql.getParameterObject();
                } else {
                    MetaObject metaObject = configuration.newMetaObject(boundSql.getParameterObject());
                    value = metaObject.getValue(propertyName);
                }

                sql = replacePlaceholder(sql, value);
            }
        }
        return sql;
    }

    /**
     * 替换SQL中的占位符
     */
    private String replacePlaceholder(String sql, Object propertyValue) {
        String result;
        if (propertyValue != null) {
            if (propertyValue instanceof String) {
                result = "'" + propertyValue + "'";
            } else if (propertyValue instanceof Date) {
                DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
                result = "'" + formatter.format(propertyValue) + "'";
            } else {
                result = propertyValue.toString();
            }
        } else {
            result = "null";
        }
        // 使用quoteReplacement避免值中包含$或\导致的分组越界
        return sql.replaceFirst("\\?", java.util.regex.Matcher.quoteReplacement(result));
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // no-op
    }
}

