#!/bin/bash

# コピー先ディレクトリを設定
DEST_DIR="../src/main/resources/public"

echo "Safely copying .output/public/* to $DEST_DIR"

# コピー先ディレクトリを作成
mkdir -p "$DEST_DIR"

# コピー先の中身のみを削除（ディレクトリ自体は保持）
rm -rf "$DEST_DIR"/*

# ファイルをコピー
cp -r .output/public/* "$DEST_DIR/"

echo "Safe copy completed successfully!"