#!/bin/bash
# GC-UI 前端版本切换脚本

set -euo pipefail

# ========== 配置区 ==========
HISTORY_DIR="./history"           # 历史版本目录
CURRENT_LINK="./current"          # 当前线上版本指向
# ===========================

echo "========== GC-UI 前端版本切换开始 =========="
echo "[INFO] 当前目录: $(pwd)"

# 1. 基本检查
if [ ! -d "$HISTORY_DIR" ]; then
  echo "[ERROR] 未找到历史版本目录: $HISTORY_DIR"
  exit 1
fi
if [ -L "$CURRENT_LINK" ]; then
  echo "[INFO] 当前 current 指向: $(readlink -f "$CURRENT_LINK" 2>/dev/null || echo "$CURRENT_LINK")"
elif [ -d "$CURRENT_LINK" ]; then
  echo "[WARN] current 是普通目录: $CURRENT_LINK（请先由发布脚本改造成软链接）"
  exit 1
else
  echo "[WARN] 当前不存在有效的 current 链接: $CURRENT_LINK"
  exit 1
fi

# 2. 选择版本
echo "[INFO] 请选择要切换到的版本（输入序号后回车，q 退出）："
mapfile -t versions < <(
  find "$HISTORY_DIR" -maxdepth 1 -mindepth 1 -type d -printf "%T@ %f\n" 2>/dev/null \
    | sort -rn | awk '{print $2}'
)
if [ "${#versions[@]}" -eq 0 ]; then
  echo "[ERROR] 未在 $HISTORY_DIR 中找到任何历史版本"
  exit 1
fi
for i in "${!versions[@]}"; do
  idx=$((i+1))
  echo "  $idx. ${versions[$i]}"
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
TARGET_DIR="${HISTORY_DIR}/${TARGET_NAME}"
if [ ! -d "$TARGET_DIR" ]; then
  echo "[ERROR] 未找到目标版本目录: $TARGET_DIR"
  exit 1
fi
if [ ! -f "${TARGET_DIR}/index.html" ]; then
  echo "[ERROR] 目标版本目录中未找到 index.html: ${TARGET_DIR}/index.html"
  exit 1
fi
ln -sfn "$TARGET_DIR" "$CURRENT_LINK"
echo "[INFO] 切换完成，current -> $TARGET_DIR"
echo "========== GC-UI 前端版本切换完成 =========="
