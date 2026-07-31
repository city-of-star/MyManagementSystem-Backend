package com.mms.base.common.backup.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 实现功能【MySQL 备份推送 Git 配置】
 * <p>
 * 配置前缀：mms.backup（建议放在 Nacos base-{profile}.yaml）
 * </p>
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Data
@Component
@ConfigurationProperties(prefix = "mms.backup")
public class MysqlBackupProperties {

    /**
     * 是否启用备份能力（关闭时任务直接跳过）
     */
    private boolean enabled = false;

    /**
     * 备份仓库 HTTPS 地址（不含 token），例如：
     * https://github.com/city-of-star/mms-side-income-data-backup.git
     */
    private String repoUrl;

    /**
     * GitHub Fine-grained PAT（Contents: Read and write）
     */
    private String githubToken;

    /**
     * 本地工作目录（临时 dump + git clone），如 /lhy/opt/mysql/backup
     */
    private String workDir = "/lhy/opt/mysql/backup";

    /**
     * 仓库内备份子目录
     */
    private String backupDir = "backups";

    /**
     * 要备份的库名；为空则从 spring.datasource.url 解析
     */
    private String database;

    /**
     * 保留最近天数
     */
    private int retainDays = 30;

    /**
     * mysqldump 可执行文件，默认从 PATH 查找
     */
    private String mysqldumpPath = "mysqldump";

    /**
     * git 可执行文件，默认从 PATH 查找
     */
    private String gitPath = "git";

    /**
     * 推送分支名
     */
    private String branch = "main";

    /**
     * git commit 作者名
     */
    private String gitUserName = "mms-backup";

    /**
     * git commit 作者邮箱
     */
    private String gitUserEmail = "mms-backup@localhost";
}
