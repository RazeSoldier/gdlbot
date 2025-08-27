#!/bin/sh
STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8089)
if [ "$STATUS_CODE" -eq 400 ]; then
  exit 0  # 返回 0 表示健康
else
  exit 1  # 返回非零表示不健康
fi