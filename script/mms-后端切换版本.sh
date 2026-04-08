#!/bin/bash
# MMS-BC 后端版本切换脚本

set -euo pipefail

# ========== 配置区 ==========
JAVA_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=128m -XX:MaxDirectMemorySize=64m"
HISTORY_DIR="./history"                   # 历史 JAR 目录
CURRENT_JAR="./$(basename "$(pwd)").jar"  # 当前运行的 JAR
PID_FILE="./run.pid"                      # 当前运行的 JAR 的 PID
LOG_DIR="./logs/$(basename "$(pwd)")"     # 日志目录路径
LOG_TAIL_DURATION=120                     # 日志持续查看的秒数
GRACEFUL_WAIT_SEC=3                       # 优雅停止旧进程后等待的秒数
# ============================

echo "========== MMS-BC 切换后端版本开始 =========="
echo "[INFO] 当前目录: $(pwd)"

# 1. 基本检查
if [ ! -d "$HISTORY_DIR" ]; then
  echo "[ERROR] 未找到历史版本目录: $HISTORY_DIR"
  exit 1
fi
if [ -L "$CURRENT_JAR" ]; then
  echo "[INFO] 当前 current.jar 指向: $(readlink -f "$CURRENT_JAR" 2>/dev/null || echo "$CURRENT_JAR")"
else
  echo "[WARN] 当前不存在有效的 current.jar 链接: $CURRENT_JAR"
  exit 1
fi

# 2. 选择版本
echo "[INFO] 请选择要切换到的版本（输入序号后回车，q 退出）："
mapfile -t versions < <(
  find "$HISTORY_DIR" -maxdepth 1 -mindepth 1 -type f -name "*.jar" -printf "%T@\t%f\n" 2>/dev/null \
    | sort -rn | cut -f2-
)
if [ "${#versions[@]}" -eq 0 ]; then
  echo "[ERROR] 未在 $HISTORY_DIR 中找到任何历史 JAR"
  exit 1
fi
for i in "${!versions[@]}"; do
  idx=$((i+1))
  echo "  $idx) ${versions[$i]}"
done
read -rp "请选择版本序号: " choice
if [ "$choice" = "q" ] || [ "$choice" = "Q" ]; then
  echo "[INFO] 已取消切换"
  exit 0
fi
if ! [[ "$choice" =~ ^[0-9]+$ ]]; then
  echo "[ERROR] 无效的输入: $choice"
  exit 1
fi
idx=$((choice-1))
if [ "$idx" -lt 0 ] || [ "$idx" -ge "${#versions[@]}" ]; then
  echo "[ERROR] 序号超出范围"
  exit 1
fi

# 3. 切换版本
TARGET_NAME="${versions[$idx]}"
TARGET_JAR_PATH="${HISTORY_DIR}/${TARGET_NAME}"
if [ ! -f "$TARGET_JAR_PATH" ]; then
  echo "[ERROR] 未找到目标 JAR: $TARGET_JAR_PATH"
  exit 1
fi
echo "[INFO] 目标版本 JAR: $TARGET_JAR_PATH"

# 4. 基于 PID 文件停止旧进程
echo "[INFO] 停止旧进程..."
if [ -f "$PID_FILE" ]; then
  OLD_PID="$(cat "$PID_FILE" 2>/dev/null || true)"
  if [ -n "${OLD_PID}" ]; then
    if kill -0 "${OLD_PID}" 2>/dev/null; then
      echo "[INFO] 优雅停止服务进程: ${OLD_PID}"
      kill -15 "${OLD_PID}" 2>/dev/null || true
      sleep "${GRACEFUL_WAIT_SEC}"
      if kill -0 "${OLD_PID}" 2>/dev/null; then
        echo "[WARN] 强制停止服务进程: ${OLD_PID}"
        kill -9 "${OLD_PID}" 2>/dev/null || true
      fi
    else
      echo "[WARN] PID 文件存在但进程无效: ${OLD_PID}"
      echo "[WARN] 请确认是否仍有旧进程在运行，否则新服务可能启动失败（端口被占用）"
    fi
  else
    echo "[WARN] PID 文件为空: ${PID_FILE}"
    echo "[WARN] 请确认是否仍有旧进程在运行，否则新服务可能启动失败（端口被占用）"
  fi
else
  echo "[WARN] 未找到 PID 文件: ${PID_FILE}"
  echo "[WARN] 请确认是否仍有旧进程在运行，否则新服务可能启动失败（端口被占用）"
fi

# 5. 切换 current.jar
echo "[INFO] 切换 current.jar -> ${TARGET_JAR_PATH}"
ln -sfn "${TARGET_JAR_PATH}" "${CURRENT_JAR}"

# 6. 启动新版本
echo "[INFO] 启动服务..."
nohup java ${JAVA_OPTS} -jar "${CURRENT_JAR}" > /dev/null 2>&1 &
NEW_PID=$!
echo "${NEW_PID}" > "${PID_FILE}"
sleep 1
if ! kill -0 "${NEW_PID}" 2>/dev/null; then
  echo "[ERROR] 服务启动失败，请检查日志"
  exit 1
fi
echo "[INFO] 启动完成，PID: ${NEW_PID}"

# 7. 查看日志，LOG_TAIL_DURATION 秒自动停止，Ctrl+C 仅停止日志打印
TAIL_PID=""
trap 'if [ -n "${TAIL_PID}" ]; then kill "${TAIL_PID}" 2>/dev/null || true; wait "${TAIL_PID}" 2>/dev/null || true; fi; echo ""; echo "[INFO] 已停止日志打印"; trap - INT' INT
LATEST_LOG=$(find "$LOG_DIR" -name "*.log" -type f -printf "%T@ %p\n" 2>/dev/null | sort -rn | head -1 | awk '{print $2}')
if [ -f "${LATEST_LOG}" ]; then
  echo "[INFO] 跟踪日志 ${LOG_TAIL_DURATION}s（Ctrl+C 可提前结束日志打印，不会影响服务启动）"
  tail -f "${LATEST_LOG}" &
  TAIL_PID=$!
  sleep "${LOG_TAIL_DURATION}"
  kill "${TAIL_PID}" 2>/dev/null || true
  wait "${TAIL_PID}" 2>/dev/null || true
else
  echo "[WARN] 未找到 ${LATEST_LOG}，跳过日志打印"
fi

trap - INT
echo "========== MMS-BC 切换后端版本完成 =========="
