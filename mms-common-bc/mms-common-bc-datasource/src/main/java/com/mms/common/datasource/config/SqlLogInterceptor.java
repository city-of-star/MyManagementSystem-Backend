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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * 实现功能【自定义格式化SQL打印日志】
 * <p>
 * 拦截MyBatis SQL执行，自定义日志输出格式
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-07 10:37:27
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class SqlLogInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SqlLogInterceptor.class);
    
    /**
     * ANSI颜色代码常量 - XML风格配色
     */
    private static final String ANSI_RESET = "\033[0m";        // 重置颜色
    private static final String ANSI_RED = "\033[31m";         // 红色（用于错误信息）
    private static final String ANSI_YELLOW = "\033[33m";      // 黄色（用于表名/字段名，类似XML属性名）
    private static final String ANSI_BLUE = "\033[34m";        // 蓝色（用于SQL关键字，类似XML标签）
    private static final String ANSI_CYAN = "\033[36m";        // 青色（用于耗时信息）
    private static final String ANSI_BRIGHT_BLUE = "\033[94m"; // 亮蓝色（用于JOIN等）
    private static final String ANSI_BRIGHT_GREEN = "\033[92m"; // 亮绿色（用于字符串值，类似XML属性值）
    private static final String ANSI_BRIGHT_YELLOW = "\033[93m"; // 亮黄色（用于数字值，类似XML属性值）
    private static final String ANSI_GRAY = "\033[90m";        // 灰色（用于运算符等次要信息）

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            // 执行SQL
            Object result = invocation.proceed();
            long endTime = System.currentTimeMillis();
            long elapsed = endTime - startTime;

            // 获取SQL信息
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();

            // 格式化SQL
            String sql = formatSql(configuration, boundSql);
            
            // 自定义日志输出，使用彩色SQL日志（sql已经在formatSql中进行了格式化和颜色化）
            String methodName = mappedStatement.getId();
            log.info("\n\n{}执行SQL - 耗时: {} ms{}\n{}方法: {}{}\n{}{}\n",
                    ANSI_CYAN, elapsed, ANSI_RESET, ANSI_YELLOW, methodName, ANSI_RESET, sql, ANSI_RESET);

            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long elapsed = endTime - startTime;
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs().length > 1 ? invocation.getArgs()[1] : null;
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = formatSql(configuration, boundSql);
            log.error("\n\n{}执行SQL失败 - 耗时: {} ms{}\n{}方法: {}{}\n{}{}\n{}错误: {}{}\n",
                    ANSI_RED, elapsed, ANSI_RESET, ANSI_YELLOW, mappedStatement.getId(), ANSI_RESET, 
                    sql, ANSI_RESET, ANSI_RED, e.getMessage(), ANSI_RESET);
            throw e;
        }
    }

    /**
     * 格式化SQL，将参数替换到SQL中
     */
    private String formatSql(Configuration configuration, BoundSql boundSql) {
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
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
        // 先美化SQL格式（添加换行和缩进）
        sql = beautifySql(sql);
        // 再添加颜色（在格式化之后，这样颜色代码不会影响格式化）
        return colorizeSql(sql);
    }

    /**
     * 为SQL添加颜色，使用XML风格的配色方案
     * XML配色映射：
     * - SQL关键字（SELECT, FROM等）-> 蓝色（类似XML标签）
     * - 表名/字段名 -> 黄色（类似XML属性名）
     * - 值（字符串、数字）-> 绿色（类似XML属性值）
     * - 运算符 -> 灰色或默认色
     */
    private String colorizeSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        
        // 1. 先处理字符串值（单引号内容）- 使用亮绿色（类似XML属性值）
        sql = sql.replaceAll("('([^']*)')", ANSI_BRIGHT_GREEN + "$1" + ANSI_RESET);
        
        // 2. 处理数字值 - 使用亮黄色（类似XML属性值）
        sql = sql.replaceAll("\\b(\\d+)\\b", ANSI_BRIGHT_YELLOW + "$1" + ANSI_RESET);
        
        // 3. SQL主要关键字 - 使用蓝色（类似XML标签）
        sql = sql.replaceAll("(?i)\\b(SELECT|FROM|WHERE|ORDER BY|GROUP BY|HAVING|INSERT INTO|UPDATE|DELETE FROM|SET|VALUES)\\b", 
                ANSI_BLUE + "$1" + ANSI_RESET);
        
        // 4. JOIN相关关键字 - 使用亮蓝色（类似XML标签）
        sql = sql.replaceAll("(?i)\\b(INNER JOIN|LEFT JOIN|RIGHT JOIN|FULL JOIN|LEFT OUTER JOIN|RIGHT OUTER JOIN|ON)\\b", 
                ANSI_BRIGHT_BLUE + "$1" + ANSI_RESET);
        
        // 5. 逻辑运算符 - 使用灰色（次要信息）
        sql = sql.replaceAll("(?i)\\b(AND|OR|NOT|IN|EXISTS|LIKE|BETWEEN)\\b", 
                ANSI_GRAY + "$1" + ANSI_RESET);
        
        // 6. 比较运算符 - 使用灰色
        sql = sql.replaceAll("(?i)\\b(=|!=|<>|<|>|<=|>=)\\b", 
                ANSI_GRAY + "$1" + ANSI_RESET);
        
        // 7. 排序关键字 - 使用灰色
        sql = sql.replaceAll("(?i)\\b(ASC|DESC)\\b", 
                ANSI_GRAY + "$1" + ANSI_RESET);
        
        // 8. 聚合函数 - 使用蓝色（函数名类似标签）
        sql = sql.replaceAll("(?i)\\b(COUNT|SUM|AVG|MAX|MIN|DISTINCT)\\b", 
                ANSI_BLUE + "$1" + ANSI_RESET);
        
        // 9. 处理表名和字段名 - 使用黄色（类似XML属性名）
        // 注意：这个需要在关键字之后处理，避免关键字被误匹配
        // 仅突出表名和WHERE/ON中的字段，SELECT列表不再上色以减少干扰
        // FROM/JOIN后面的表名
        sql = sql.replaceAll("(?i)(FROM|JOIN)\\s+([a-z_][a-z0-9_]*)", "$1 " + ANSI_YELLOW + "$2" + ANSI_RESET);
        // WHERE/AND/OR/ON 条件中的字段名
        sql = sql.replaceAll("(?i)(WHERE|AND|OR|ON)\\s+([a-z_][a-z0-9_]*)\\s*[=<>!]", "$1 " + ANSI_YELLOW + "$2" + ANSI_RESET);
        
        return sql;
    }

    /**
     * 美化SQL格式，添加换行和缩进
     */
    private String beautifySql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        
        // 先统一处理空格
        sql = sql.trim().replaceAll("\\s+", " ");
        
        // 使用正则表达式进行格式化
        // 1. 在主要关键字前添加换行
        sql = sql.replaceAll("(?i)\\s+(FROM)\\s+", "\nFROM ");
        sql = sql.replaceAll("(?i)\\s+(WHERE)\\s+", "\nWHERE ");
        sql = sql.replaceAll("(?i)\\s+(ORDER BY)\\s+", "\nORDER BY ");
        sql = sql.replaceAll("(?i)\\s+(GROUP BY)\\s+", "\nGROUP BY ");
        sql = sql.replaceAll("(?i)\\s+(HAVING)\\s+", "\nHAVING ");
        sql = sql.replaceAll("(?i)\\s+(SET)\\s+", "\nSET ");
        sql = sql.replaceAll("(?i)\\s+(VALUES)\\s+", "\nVALUES ");
        
        // 2. 处理JOIN
        sql = sql.replaceAll("(?i)\\s+(INNER JOIN|LEFT JOIN|RIGHT JOIN|FULL JOIN|LEFT OUTER JOIN|RIGHT OUTER JOIN)\\s+", "\n$1 ");
        sql = sql.replaceAll("(?i)\\s+(ON)\\s+", "\n  ON ");
        
        // 3. 处理AND/OR，添加换行和缩进
        sql = sql.replaceAll("(?i)\\s+(AND)\\s+", "\n  AND ");
        sql = sql.replaceAll("(?i)\\s+(OR)\\s+", "\n  OR ");
        
        // 4. 处理ORDER BY和GROUP BY - 添加换行和缩进
        sql = sql.replaceAll("(?i)(ORDER BY|GROUP BY)\\s+", "$1\n  ");
        // 处理ORDER BY/GROUP BY中的逗号
        sql = sql.replaceAll("(?i)(ORDER BY|GROUP BY)\\s+([^,\\n]+),", "$1\n  $2,\n  ");
        
        // 5. 处理SET中的逗号
        sql = sql.replaceAll("(?i)(SET)\\s+([^,\\n]+),", "$1\n  $2,\n  ");
        
        // 注意：SELECT字段列表保持在一行，不进行换行处理
        
        // 7. 清理多余的空行
        sql = sql.replaceAll("\n\\s*\n", "\n");
        
        return sql.trim();
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
        // 可以在这里设置一些属性
    }
}

