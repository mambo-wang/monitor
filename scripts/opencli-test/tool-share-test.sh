#!/bin/bash
#
# 工具分享页面 OpenCLI 自动化测试脚本
# 用法: ./tool-share-test.sh [test-type]
# 测试类型: all, folders, buttons, navigation
#

set -e

BASE_URL="http://localhost:9090"
API_URL="http://localhost:8888/watcher"
TEST_SESSION="tool-share-test-$(date +%s)"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查服务是否运行
check_services() {
    log_info "检查服务状态..."

    if ! lsof -i :9090 | grep -q LISTEN; then
        log_error "前端服务 (localhost:9090) 未运行"
        exit 1
    fi
    log_info "前端服务 ✓"

    if ! lsof -i :8888 | grep -q LISTEN; then
        log_error "后端服务 (localhost:8888) 未运行"
        exit 1
    fi
    log_info "后端服务 ✓"
}

# 登录获取 token
get_token() {
    log_info "获取登录 Token..."
    RESPONSE=$(curl -s -X POST "$API_URL/user/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"iesB4yJHVdE1R3mP4yT6LA=="}')

    TOKEN=$(echo $RESPONSE | grep -o '"data":"[^"]*"' | cut -d'"' -f4)

    if [ -z "$TOKEN" ]; then
        log_error "获取 Token 失败"
        exit 1
    fi
    log_info "Token 获取成功 ✓"
}

# 接口自动化测试
test_api() {
    log_info "=== 接口自动化测试 ==="

    # 1. 获取文件夹列表
    log_info "测试 1: 获取文件夹列表"
    FOLDERS=$(curl -s -X GET "$API_URL/api/tool-share/folders" \
        -H "Authorization: Bearer $TOKEN")
    echo "$FOLDERS" | jq '.' 2>/dev/null || echo "$FOLDERS"

    FOLDER_COUNT=$(echo "$FOLDERS" | jq '.data | length' 2>/dev/null || echo "0")
    log_info "文件夹数量: $FOLDER_COUNT"

    # 2. 创建新文件夹
    log_info "测试 2: 创建新文件夹"
    CREATE_RESULT=$(curl -s -X POST "$API_URL/api/tool-share/folder" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"name":"自动化测试文件夹"}')
    echo "$CREATE_RESULT" | jq '.' 2>/dev/null || echo "$CREATE_RESULT"

    # 3. 获取文件列表
    log_info "测试 3: 获取文件列表"
    FILES=$(curl -s -X GET "$API_URL/api/tool-share/files?folderId=24" \
        -H "Authorization: Bearer $TOKEN")
    FILE_COUNT=$(echo "$FILES" | jq '.data | length' 2>/dev/null || echo "0")
    log_info "文件数量: $FILE_COUNT"

    log_info "=== 接口测试完成 ==="
}

# 浏览器自动化测试
test_browser() {
    TEST_TYPE=${1:-all}
    log_info "=== 浏览器自动化测试 (类型: $TEST_TYPE) ==="

    # 使用 opencli adapter
    log_info "运行 opencli showtime tool-share-test --test $TEST_TYPE"
    opencli showtime tool-share-test --test "$TEST_TYPE" -f json

    log_info "=== 浏览器测试完成 ==="
}

# 清理测试数据
cleanup() {
    log_info "清理测试数据..."
    # 删除创建的测试文件夹
    FOLDERS=$(curl -s -X GET "$API_URL/api/tool-share/folders" \
        -H "Authorization: Bearer $TOKEN")
    echo "$FOLDERS" | jq -r '.data[] | select(.name | contains("自动化测试") or contains("OpenCLI") or contains("测试")) | .id' 2>/dev/null | while read id; do
        if [ -n "$id" ]; then
            log_info "删除文件夹 ID: $id"
            curl -s -X DELETE "$API_URL/api/tool-share/folder/$id" \
                -H "Authorization: Bearer $TOKEN" | jq '.' 2>/dev/null || true
        fi
    done
}

# 显示帮助
show_help() {
    echo "工具分享页面 OpenCLI 自动化测试脚本"
    echo ""
    echo "用法: $0 [命令] [选项]"
    echo ""
    echo "命令:"
    echo "  api         运行接口自动化测试"
    echo "  browser     运行浏览器自动化测试"
    echo "  all         运行全部测试 (默认)"
    echo "  cleanup     清理测试数据"
    echo "  help        显示帮助信息"
    echo ""
    echo "选项:"
    echo "  --test TYPE 指定浏览器测试类型 (all, folders, buttons, navigation)"
    echo ""
    echo "示例:"
    echo "  $0 all                    # 运行全部测试"
    echo "  $0 api                    # 仅运行接口测试"
    echo "  $0 browser --test buttons # 仅测试按钮"
}

# 主逻辑
main() {
    COMMAND=${1:-all}

    case $COMMAND in
        api)
            check_services
            get_token
            test_api
            ;;
        browser)
            check_services
            TEST_TYPE=${2:-all}
            if [ "$TEST_TYPE" = "--test" ]; then
                TEST_TYPE=${3:-all}
            fi
            test_browser "$TEST_TYPE"
            ;;
        all)
            check_services
            get_token
            test_api
            echo ""
            test_browser "$2"
            ;;
        cleanup)
            check_services
            get_token
            cleanup
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "未知命令: $COMMAND"
            show_help
            exit 1
            ;;
    esac
}

main "$@"