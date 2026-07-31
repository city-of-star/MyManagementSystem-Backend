package com.mms.base.service.system.job;

import com.mms.base.common.backup.properties.MysqlBackupProperties;
import com.mms.base.service.system.job.dto.MysqlBackupJobDto;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.job.JobHandler;
import com.mms.common.job.annotation.JobDefinition;
import com.mms.common.job.enums.JobTypeEnum;
import com.mms.common.job.utils.JobParamUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实现功能【MySQL 备份并推送到 GitHub 私有仓库】
 * <p>
 * 使用 mysqldump 导出可还原 SQL，推送到配置的备份仓，并按日期保留最近 N 天文件。
 * 兼容较旧的 git（如 CentOS 自带 1.8.x）：不使用 git -C，统一设置进程工作目录。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Slf4j
@Component
@JobDefinition(type = JobTypeEnum.MYSQL_BACKUP, paramClass = MysqlBackupJobDto.class)
public class MysqlBackupJobHandler implements JobHandler {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Pattern JDBC_URL_PATTERN = Pattern.compile(
            "^jdbc:mysql://([^:/?]+)(?::(\\d+))?/([^?]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern BACKUP_FILE_PATTERN = Pattern.compile(
            "^(.+)_(\\d{8})\\.sql$");
    private static final long PROCESS_TIMEOUT_MINUTES = 30L;

    @Resource
    private MysqlBackupProperties backupProperties;

    @Resource
    private Environment environment;

    @Override
    public String execute(String paramsJson) {
        MysqlBackupJobDto dto = JobParamUtils.parseParams(paramsJson, MysqlBackupJobDto.class);

        if (!backupProperties.isEnabled()) {
            log.info("MySQL 备份未启用（mms.backup.enabled=false），跳过执行");
            return "MySQL 备份未启用，已跳过";
        }
        validateConfig();

        int retainDays = dto.getRetainDays() != null && dto.getRetainDays() > 0
                ? dto.getRetainDays()
                : backupProperties.getRetainDays();
        if (retainDays <= 0) {
            retainDays = 30;
        }

        JdbcEndpoint endpoint = resolveJdbcEndpoint(dto.getDatabase());
        Path workDir = Paths.get(backupProperties.getWorkDir()).toAbsolutePath().normalize();
        Path dumpDir = workDir.resolve("tmp");
        Path repoDir = workDir.resolve("repo");
        Path backupDir = repoDir.resolve(backupProperties.getBackupDir());

        String day = LocalDate.now(ZONE).format(DAY_FMT);
        String fileName = endpoint.database() + "_" + day + ".sql";
        Path dumpFile = dumpDir.resolve(fileName);

        try {
            Files.createDirectories(dumpDir);
            Files.createDirectories(workDir);

            log.info("开始 MySQL 备份，database={}，host={}，port={}，file={}",
                    endpoint.database(), endpoint.host(), endpoint.port(), fileName);

            dumpDatabase(endpoint, dumpFile);
            ensureRepo(repoDir);
            Files.createDirectories(backupDir);
            Path targetFile = backupDir.resolve(fileName);
            Files.copy(dumpFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

            int deleted = pruneOldBackups(backupDir, endpoint.database(), retainDays);
            String pushResult = commitAndPush(repoDir, fileName, deleted);

            long sizeBytes = Files.size(targetFile);
            String summary = String.format(Locale.ROOT,
                    "备份成功：%s/%s，大小=%d bytes，删除过期文件=%d，%s",
                    backupProperties.getBackupDir(), fileName, sizeBytes, deleted, pushResult);
            log.info(summary);
            return summary;
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException("MySQL 备份任务失败：" + e.getMessage(), e);
        } finally {
            try {
                Files.deleteIfExists(dumpFile);
            } catch (IOException ignored) {
                // ignore
            }
        }
    }

    private void validateConfig() {
        if (!StringUtils.hasText(backupProperties.getRepoUrl())) {
            throw new ServerException("未配置 mms.backup.repo-url");
        }
        if (!StringUtils.hasText(backupProperties.getGithubToken())) {
            throw new ServerException("未配置 mms.backup.github-token");
        }
        if (!StringUtils.hasText(backupProperties.getWorkDir())) {
            throw new ServerException("未配置 mms.backup.work-dir");
        }
    }

    private JdbcEndpoint resolveJdbcEndpoint(String databaseOverride) {
        String jdbcUrl = environment.getProperty("spring.datasource.url");
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        if (!StringUtils.hasText(jdbcUrl) || !StringUtils.hasText(username)) {
            throw new ServerException("无法读取 spring.datasource 配置，请检查 Nacos mysql 配置");
        }

        Matcher matcher = JDBC_URL_PATTERN.matcher(jdbcUrl.trim());
        if (!matcher.find()) {
            throw new ServerException("无法解析 spring.datasource.url：" + maskJdbcUrl(jdbcUrl));
        }
        String host = matcher.group(1);
        String port = StringUtils.hasText(matcher.group(2)) ? matcher.group(2) : "3306";
        String database = StringUtils.hasText(databaseOverride)
                ? databaseOverride.trim()
                : (StringUtils.hasText(backupProperties.getDatabase())
                ? backupProperties.getDatabase().trim()
                : matcher.group(3));
        if (!StringUtils.hasText(database)) {
            throw new ServerException("备份库名不能为空");
        }
        return new JdbcEndpoint(host, port, database, username, password == null ? "" : password);
    }

    private void dumpDatabase(JdbcEndpoint endpoint, Path dumpFile) throws IOException, InterruptedException {
        Path defaultsFile = dumpFile.getParent().resolve(".mysqldump.cnf");
        try {
            String cnf = "[client]\nuser=" + endpoint.username() + "\npassword=" + endpoint.password() + "\n";
            Files.writeString(defaultsFile, cnf, StandardCharsets.UTF_8);
            // 尽量限制权限（Windows 上可能无效，可忽略）
            try {
                defaultsFile.toFile().setReadable(false, false);
                defaultsFile.toFile().setWritable(false, false);
                defaultsFile.toFile().setReadable(true, true);
                defaultsFile.toFile().setWritable(true, true);
            } catch (Exception ignored) {
                // ignore
            }

            List<String> cmd = new ArrayList<>();
            cmd.add(backupProperties.getMysqldumpPath());
            cmd.add("--defaults-extra-file=" + defaultsFile.toAbsolutePath());
            cmd.add("-h" + endpoint.host());
            cmd.add("-P" + endpoint.port());
            cmd.add("--single-transaction");
            cmd.add("--routines");
            cmd.add("--triggers");
            cmd.add("--events");
            cmd.add("--set-gtid-purged=OFF");
            cmd.add("--databases");
            cmd.add(endpoint.database());

            ProcessResult result = runProcess(cmd, dumpFile.getParent(), dumpFile, null);
            if (result.exitCode() != 0) {
                throw new ServerException("mysqldump 失败，exitCode=" + result.exitCode()
                        + "，stderr=" + truncate(result.stderr()));
            }
            if (!Files.exists(dumpFile) || Files.size(dumpFile) == 0) {
                throw new ServerException("mysqldump 未生成有效 SQL 文件");
            }
        } finally {
            Files.deleteIfExists(defaultsFile);
        }
    }

    private void ensureRepo(Path repoDir) throws IOException, InterruptedException {
        Path gitDir = repoDir.resolve(".git");
        String authUrl = buildAuthRepoUrl();
        if (!Files.isDirectory(gitDir)) {
            Files.createDirectories(repoDir);
            // 优先 clone（仓里已有历史时）；空仓在老 git 上常失败，再降级为 init
            ProcessResult clone = runGit(repoDir, "clone", authUrl, ".");
            if (clone.exitCode() != 0) {
                log.warn("git clone 失败，降级为 init（常见于空仓库）。stderr={}",
                        truncate(maskSecret(clone.stderr())));
                runGitOrThrow(repoDir, "init");
                runGitOrThrow(repoDir, "checkout", "-b", backupProperties.getBranch());
                runGitOrThrow(repoDir, "remote", "add", "origin", authUrl);
            }
        } else {
            // 刷新 origin，避免 token 轮换后仍用旧地址
            runGitOrThrow(repoDir, "remote", "set-url", "origin", authUrl);
            ProcessResult pull = runGit(repoDir, "pull", "--ff-only", "origin", backupProperties.getBranch());
            if (pull.exitCode() != 0) {
                // 空仓库或首次尚无分支时 pull 失败可接受
                log.warn("git pull 未成功（可能是空仓库首次备份），将继续本地提交。stderr={}",
                        truncate(maskSecret(pull.stderr())));
            }
        }
        runGitOrThrow(repoDir, "config", "user.name", backupProperties.getGitUserName());
        runGitOrThrow(repoDir, "config", "user.email", backupProperties.getGitUserEmail());
    }

    private int pruneOldBackups(Path backupDir, String database, int retainDays) throws IOException {
        LocalDate cutoff = LocalDate.now(ZONE).minusDays(retainDays);
        int deleted = 0;
        try (Stream<Path> list = Files.list(backupDir)) {
            List<Path> files = list.filter(Files::isRegularFile).collect(Collectors.toList());
            for (Path file : files) {
                String name = file.getFileName().toString();
                Matcher matcher = BACKUP_FILE_PATTERN.matcher(name);
                if (!matcher.matches()) {
                    continue;
                }
                if (!database.equals(matcher.group(1))) {
                    continue;
                }
                LocalDate fileDay;
                try {
                    fileDay = LocalDate.parse(matcher.group(2), DAY_FMT);
                } catch (DateTimeParseException e) {
                    continue;
                }
                if (fileDay.isBefore(cutoff)) {
                    Files.deleteIfExists(file);
                    deleted++;
                    log.info("删除过期备份文件：{}", name);
                }
            }
        }
        return deleted;
    }

    private String commitAndPush(Path repoDir, String fileName, int deleted) throws IOException, InterruptedException {
        runGitOrThrow(repoDir, "add", backupProperties.getBackupDir());
        ProcessResult status = runGit(repoDir, "status", "--porcelain");
        if (status.exitCode() != 0) {
            throw new ServerException("git status 失败：" + truncate(status.stderr()));
        }
        if (!StringUtils.hasText(status.stdout())) {
            return "工作区无变更，跳过 push";
        }

        String message = "backup: " + fileName + (deleted > 0 ? " (prune " + deleted + ")" : "");
        runGitOrThrow(repoDir, "commit", "-m", message);

        ProcessResult push = runGit(repoDir, "push", "-u", "origin", "HEAD:" + backupProperties.getBranch());
        if (push.exitCode() != 0) {
            throw new ServerException("git push 失败：" + truncate(maskSecret(push.stderr())));
        }
        return "已 push 到 " + backupProperties.getBranch();
    }

    private String buildAuthRepoUrl() {
        String repoUrl = backupProperties.getRepoUrl().trim();
        String token = backupProperties.getGithubToken().trim();
        if (repoUrl.startsWith("https://")) {
            // https://x-access-token:TOKEN@github.com/...
            return "https://x-access-token:" + token + "@" + repoUrl.substring("https://".length());
        }
        if (repoUrl.startsWith("http://")) {
            return "http://x-access-token:" + token + "@" + repoUrl.substring("http://".length());
        }
        throw new ServerException("mms.backup.repo-url 仅支持 http(s) 地址");
    }

    private void runGitOrThrow(Path workDir, String... args) throws IOException, InterruptedException {
        ProcessResult result = runGit(workDir, args);
        if (result.exitCode() != 0) {
            throw new ServerException("git " + String.join(" ", args) + " 失败：" + truncate(maskSecret(result.stderr())));
        }
    }

    private ProcessResult runGit(Path workDir, String... args) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add(backupProperties.getGitPath());
        for (String arg : args) {
            cmd.add(arg);
        }
        return runProcess(cmd, workDir, null, null);
    }

    /**
     * @param stdoutFile 若非空，则将进程 stdout 重定向到该文件（用于 mysqldump）
     */
    private ProcessResult runProcess(List<String> cmd, Path workDir, Path stdoutFile, Path stderrFile)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (workDir != null) {
            pb.directory(workDir.toFile());
        }
        pb.redirectErrorStream(false);
        if (stdoutFile != null) {
            pb.redirectOutput(stdoutFile.toFile());
        }
        Process process = pb.start();

        StringBuilder stderr = new StringBuilder();
        StringBuilder stdout = new StringBuilder();
        Thread errReader = new Thread(() -> readStream(process.getErrorStream(), stderr), "proc-stderr");
        Thread outReader = null;
        if (stdoutFile == null) {
            outReader = new Thread(() -> readStream(process.getInputStream(), stdout), "proc-stdout");
            outReader.start();
        }
        errReader.start();

        boolean finished = process.waitFor(PROCESS_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (!finished) {
            process.destroyForcibly();
            throw new ServerException("外部命令执行超时（>" + PROCESS_TIMEOUT_MINUTES + " 分钟）：" + cmd.get(0));
        }
        errReader.join(5000);
        if (outReader != null) {
            outReader.join(5000);
        }
        if (stderrFile != null) {
            Files.writeString(stderrFile, stderr.toString(), StandardCharsets.UTF_8);
        }
        return new ProcessResult(process.exitValue(), stdout.toString(), stderr.toString());
    }

    private void readStream(java.io.InputStream in, StringBuilder out) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() < 20000) {
                    out.append(line).append('\n');
                }
            }
        } catch (IOException e) {
            log.debug("读取进程输出失败：{}", e.getMessage());
        }
    }

    private static String truncate(String text) {
        if (text == null) {
            return "";
        }
        String t = text.trim();
        return t.length() <= 2000 ? t : t.substring(0, 2000) + "...";
    }

    private String maskSecret(String text) {
        if (text == null) {
            return "";
        }
        String masked = text;
        if (StringUtils.hasText(backupProperties.getGithubToken())) {
            masked = masked.replace(backupProperties.getGithubToken(), "***");
        }
        return masked;
    }

    private static String maskJdbcUrl(String jdbcUrl) {
        return jdbcUrl == null ? "" : jdbcUrl.replaceAll("password=[^&]*", "password=***");
    }

    private record JdbcEndpoint(String host, String port, String database, String username, String password) {
    }

    private record ProcessResult(int exitCode, String stdout, String stderr) {
    }
}
