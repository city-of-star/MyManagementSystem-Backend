#!/bin/bash
# MMS-BC 后端服务重启脚本

set -euo pipefail

# ========== 配置区 ==========
JAVA_OPTS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=128m -XX:MaxDirectMemorySize=64m"
HISTORY_DIR="./history"                   # 历史 JAR 目录
CURRENT_JAR="./$(basename "$(pwd)").jar"  # 当前运行的 JAR
PID_FILE="./run.pid"                      # 当前运行的 JAR 的 PID
LOG_DIR="./logs/$(basename "$(pwd)")"     # 日志目录路径
MAX_KEEP_JARS=5                           # 最多保留的历史 JAR 数量（按时间最新优先）
LOG_TAIL_DURATION=120                     # 日志持续查看的秒数
GRACEFUL_WAIT_SEC=3                       # 优雅停止旧进程后等待的秒数，再考虑强制 kill
# ============================

echo "========== MMS-BC 重启服务开始 =========="
echo "[INFO] 当前目录: $(pwd)"

mkdir -p "$HISTORY_DIR"

# 1. 基于 PID 文件停止旧进程
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

# 2. 查找用户最新上传的 JAR（排除 current.jar）
LATEST_JAR_PATH="$(find . -maxdepth 1 -type f -name "*.jar" ! -name "current.jar" -printf "%T@ %p\n" 2>/dev/null | sort -rn | head -1 | awk '{print $2}')"
LATEST_JAR="$(basename "${LATEST_JAR_PATH}" 2>/dev/null || true)"
if [ -z "${LATEST_JAR}" ] || [ ! -f "${LATEST_JAR}" ]; then
  echo "[ERROR] 当前目录未找到可发布 JAR"
  exit 1
fi

# 3. 归档 JAR
TIMESTAMP="$(date '+%Y_%m_%d-%H_%M_%S')"
BASE_NAME="${LATEST_JAR%.jar}"
TARGET_JAR="${BASE_NAME}_${TIMESTAMP}.jar"
TARGET_JAR_PATH="${HISTORY_DIR}/${TARGET_JAR}"
echo "[INFO] 归档: ${LATEST_JAR} -> ${TARGET_JAR_PATH}"
mv -f -- "${LATEST_JAR}" "${TARGET_JAR_PATH}"

# 4. 切换 current.jar 到新版本
ln -sfn "${TARGET_JAR_PATH}" "${CURRENT_JAR}"

# 5. 清理超出数量的历史包（按时间保留最新 MAX_KEEP_JARS 个）
echo "[INFO] 清理历史 JAR，最多保留 ${MAX_KEEP_JARS} 个"
find "$HISTORY_DIR" -maxdepth 1 -type f -name "*.jar" -printf "%T@ %p\n" 2>/dev/null | sort -rn | awk '{print $2}' \
  | tail -n +$((MAX_KEEP_JARS + 1)) | while read -r OLD_JAR; do
      if [ -n "${OLD_JAR}" ] && [ "${OLD_JAR}" != "${TARGET_JAR_PATH}" ]; then
        echo "[INFO] 删除: ${OLD_JAR}"
        rm -f "${OLD_JAR}"
      fi
    done

# 6. 启动服务
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
echo "========== MMS-BC 重启服务完成 =========="
