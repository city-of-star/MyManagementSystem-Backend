#!/bin/bash
# MMS-BC 后端服务日志查看脚本

set -euo pipefail

# ========== 配置区 ==========
LOG_DIR="./logs/$(basename "$(pwd)")"  # 日志目录路径
# ============================

# 1. 基本检查
if [ ! -d "$LOG_DIR" ]; then
echo "[ERROR] 日志目录不存在: $LOG_DIR"
exit 1
fi

# 2. 找到该模块最新的 .log 文件
LATEST_LOG=$(find "$LOG_DIR" -name "*.log" -type f -printf "%T@ %p\n" 2>/dev/null | sort -rn | head -1 | awk '{print $2}')
if [ -z "$LATEST_LOG" ] || [ ! -f "$LATEST_LOG" ]; then
echo "[ERROR] 未在 $LOG_DIR 中找到任何 .log 文件"
exit 1
fi
echo "[INFO] 正在跟踪日志文件: $LATEST_LOG（按 Ctrl+C 退出）"

# 3. 持续输出最新日志，直到手动 Ctrl+C
tail -f "$LATEST_LOG"
