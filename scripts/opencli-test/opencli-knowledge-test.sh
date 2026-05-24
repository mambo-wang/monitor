#!/bin/bash
# OpenCLI 知识库自动化测试脚本
# 用法: bash tests/opencli-knowledge-test.sh

set -e

SESSION="kb-test-$(date +%s)"
BASE_URL="http://localhost:9090"
API_URL="http://localhost:8000"

echo "=== 知识库自动化测试 ==="
echo "Session: $SESSION"

# 1. 打开知识库页面
echo ""
echo "[1/10] 打开知识库页面..."
opencli browser "$SESSION" open "$BASE_URL/#/knowledge/index"
opencli browser "$SESSION" wait time 3

# 2. 点击知识库菜单
echo ""
echo "[2/10] 点击知识库菜单..."
opencli browser "$SESSION" state 2>&1 | grep -q "知识库" && echo "  ✓ 页面加载成功"

# 3. 点击新建知识库按钮
echo ""
echo "[3/10] 点击新建知识库按钮..."
opencli browser "$SESSION" click --role button --name "新建知识库"
opencli browser "$SESSION" wait time 2

# 4. 验证创建对话框打开
echo ""
echo "[4/10] 验证对话框..."
opencli browser "$SESSION" find --css "[aria-label='新建知识库']" 2>&1 | grep -q "新建知识库" && echo "  ✓ 创建对话框已打开"

# 5. 填写知识库名称
echo ""
echo "[5/10] 填写表单..."
opencli browser "$SESSION" type "[aria-label='新建知识库'] input" "OpenCLI测试知识库"
opencli browser "$SESSION" wait time 1

# 6. 点击创建按钮
echo ""
echo "[6/10] 点击创建按钮..."
opencli browser "$SESSION" click --role button --name "创建"
opencli browser "$SESSION" wait time 3

# 7. 验证知识库已创建
echo ""
echo "[7/10] 验证知识库已创建..."
opencli browser "$SESSION" state 2>&1 | grep -q "OpenCLI测试知识库" && echo "  ✓ 知识库创建成功"

# 8. 找到编辑按钮并点击（表格第一行的第4个按钮，ref 146）
echo ""
echo "[8/10] 点击第一行的编辑按钮..."
opencli browser "$SESSION" click 146
opencli browser "$SESSION" wait time 2

# 9. 验证编辑对话框打开
echo ""
echo "[9/10] 验证编辑对话框..."
opencli browser "$SESSION" find --css "[aria-label='编辑知识库']" 2>&1 | grep -q "编辑知识库" && echo "  ✓ 编辑对话框已打开"

# 10. 修改名称并保存
echo ""
echo "[10/10] 修改名称并保存..."
opencli browser "$SESSION" type "[aria-label='编辑知识库'] input" "OpenCLI测试知识库_已编辑"
opencli browser "$SESSION" click --role button --name "保存"
opencli browser "$SESSION" wait time 3

echo ""
echo "=== 测试完成 ==="

# API 测试
echo ""
echo "=== API 接口测试 ==="

# 创建知识库
echo ""
echo "[API] 创建知识库..."
KB_RESPONSE=$(curl -s -X POST "$API_URL/api/knowledge/kbs" \
  -H "Content-Type: application/json" \
  -d '{"name":"API测试KB","description":"API测试描述"}')
echo "$KB_RESPONSE" | python3 -m json.tool

KB_ID=$(echo "$KB_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['id'])")
echo "KB ID: $KB_ID"

# 更新知识库
echo ""
echo "[API] 更新知识库..."
curl -s -X PATCH "$API_URL/api/knowledge/kbs/$KB_ID" \
  -H "Content-Type: application/json" \
  -d '{"name":"API测试KB_已编辑","description":"描述已更新"}' | python3 -m json.tool

# 获取文档列表
echo ""
echo "[API] 获取文档列表..."
curl -s "$API_URL/api/knowledge/kbs/04b88c92-5256-4a6b-8d64-f50f0694bc04/documents" | python3 -m json.tool

# 删除测试知识库
echo ""
echo "[API] 删除测试知识库..."
curl -s -X DELETE "$API_URL/api/knowledge/kbs/$KB_ID" | python3 -m json.tool

# 关闭浏览器会话
echo ""
echo "=== 关闭浏览器会话 ==="
opencli browser "$SESSION" close

echo ""
echo "=== 全部测试完成 ==="