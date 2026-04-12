#!/bin/bash
# GC-UI 前端发布脚本

set -euo pipefail

# ========== 配置区 ==========
#APP_NAME="$(basename "$(pwd)")"                # 当前目录名（也是上传的构建产物的名称）
APP_NAME="dist"                             # 当前目录名（也是上传的构建产物的名称）
UPLOAD_DIR="./${APP_NAME}"                     # 用户上传的新构建产物相对路径
ZIP_FILE="./${APP_NAME}.zip"                   # 用户上传的压缩包路径
HISTORY_DIR="./history"                        # 历史版本目录
CURRENT_LINK="./current"                       # Nginx root 指向这里
TMP_EXTRACT_DIR="./.__extract_${APP_NAME}_$$"  # 解压临时目录
MAX_KEEP_HISTORY=5                             # 保留的历史版本数量（含当前）
# ===========================

echo "========== GC-UI 前端发布开始 =========="
echo "[INFO] 当前目录: $(pwd)"

# 1. 基本检查
if [ ! -d "$UPLOAD_DIR" ]; then
  if [ ! -f "$ZIP_FILE" ]; then
    echo "[ERROR] 未找到新上传的 $APP_NAME 目录: $UPLOAD_DIR"
    echo "        也未找到压缩包: ${APP_NAME}.zip"
    exit 1
  fi

  echo "[INFO] 检测到压缩包，开始解压: $ZIP_FILE"
  rm -rf "$TMP_EXTRACT_DIR"
  mkdir -p "$TMP_EXTRACT_DIR"

  unzip -q "$ZIP_FILE" -d "$TMP_EXTRACT_DIR"

  if [ -d "${TMP_EXTRACT_DIR}/${APP_NAME}" ]; then
    mv "${TMP_EXTRACT_DIR}/${APP_NAME}" "$UPLOAD_DIR"
  elif [ -f "${TMP_EXTRACT_DIR}/index.html" ]; then
    mkdir -p "$UPLOAD_DIR"
    shopt -s dotglob nullglob
    mv "${TMP_EXTRACT_DIR}"/* "$UPLOAD_DIR"/
    shopt -u dotglob nullglob
  else
    echo "[ERROR] 解压后未找到有效构建目录: ${TMP_EXTRACT_DIR}/${APP_NAME}"
    rm -rf "$TMP_EXTRACT_DIR"
    exit 1
  fi

  rm -rf "$TMP_EXTRACT_DIR"
  rm -f "$ZIP_FILE"
  echo "[INFO] 解压完成并已删除压缩包: $ZIP_FILE"
fi

mkdir -p "$HISTORY_DIR"

# 2. 最小健康检查（必须有 index.html）
if [ ! -f "${UPLOAD_DIR}/index.html" ]; then
  echo "[ERROR] 未找到 index.html: ${UPLOAD_DIR}/index.html"
  echo "        请检查构建产物是否正确。"
  exit 1
fi

# 3. 为本次发布生成版本目录名，并移动构建产物进去
ts="$(date +%Y%m%d_%H%M%S)"  
NEW_RELEASE_DIR="${HISTORY_DIR}/${APP_NAME}_${ts}"  
echo "[INFO] 新版本目录: $NEW_RELEASE_DIR"
mv "$UPLOAD_DIR" "$NEW_RELEASE_DIR"

# 4 继承上一版本的配置文件（config/index.js）  
OLD_RELEASE_DIR="$(readlink -f "$CURRENT_LINK" 2>/dev/null || echo "")"
if [ -n "$OLD_RELEASE_DIR" ]; then
  OLD_CONFIG="${OLD_RELEASE_DIR}/config/index.js"
  if [ -f "$OLD_CONFIG" ]; then
    echo "[INFO] 复制上一版本的 config/index.js 到新版本"  
    mkdir -p "${NEW_RELEASE_DIR}/config"
    cp -f "$OLD_CONFIG" "${NEW_RELEASE_DIR}/config/index.js"  
  else
    echo "[WARN] 上一版本中未找到 config/index.js，跳过复制"  
    fi
else  
  echo "[WARN] 未检测到当前线上版本（current 未指向有效目录），跳过配置继承"  
fi

# 5. 使用符号链接原子切换 current
if [ -d "$CURRENT_LINK" ] && [ ! -L "$CURRENT_LINK" ]; then
  echo "[WARN] 检测到 current 是普通目录，先删除以便创建符号链接: $CURRENT_LINK"
  rm -rf "$CURRENT_LINK"
fi
echo "[INFO] 切换 current -> ${NEW_RELEASE_DIR}"
ln -sfn "$NEW_RELEASE_DIR" "$CURRENT_LINK"

# 6. 清理历史版本：只保留最新 MAX_KEEP_HISTORY 个
echo "[INFO] 清理历史版本，保留最新 ${MAX_KEEP_HISTORY} 个"

mapfile -t history < <(
  find "$HISTORY_DIR" -maxdepth 1 -mindepth 1 -type d -name "${APP_NAME}_*" \
    -printf "%T@ %p\n" 2>/dev/null | sort -nr | awk '{print $2}'
)

count="${#history[@]}"
echo "[INFO] 当前版本数量: $count"

if [ "$count" -gt "$MAX_KEEP_HISTORY" ]; then
  for ((i=MAX_KEEP_HISTORY; i<count; i++)); do
    old_dir="${history[$i]}"
    [ -d "$old_dir" ] || continue

    # 避免误删 current 正在指向的目录（正常不会发生，但额外保护）
    if [ "$(readlink -f "$CURRENT_LINK" 2>/dev/null || echo "")" = "$(readlink -f "$old_dir" 2>/dev/null || echo "")" ]; then
      echo "[WARN] 跳过删除当前正在使用的版本: $old_dir"
      continue
    fi

    echo "[INFO] 删除旧版本目录: $old_dir"
    rm -rf "$old_dir"
  done
else
  echo "[INFO] 历史版本数量在保留范围内，无需清理"
fi

echo "========== GC-UI 前端发布完成 =========="
