#!/usr/bin/env python3
"""
检查 commit message 格式，必须以 story 或 bugfix 开头。
"""

import sys
import re


COMMIT_MSG_FILE = sys.argv[1] if len(sys.argv) > 1 else ".git/COMMIT_EDITMSG"

# 支持的前缀（不区分大小写）
ALLOWED_PREFIXES = ["story", "bugfix"]

# 匹配模式
PATTERN = re.compile(
    r"^\s*(" + "|".join(ALLOWED_PREFIXES) + r")[\s:]", re.IGNORECASE
)


def main():
    try:
        with open(COMMIT_MSG_FILE, "r") as f:
            first_line = f.readline().strip()
    except (FileNotFoundError, IOError):
        print(f"⚠️  未找到提交信息文件: {COMMIT_MSG_FILE}", file=sys.stderr)
        sys.exit(0)

    if not first_line:
        print(f"❌ 提交信息不能为空", file=sys.stderr)
        sys.exit(1)

    if not PATTERN.match(first_line):
        allowed = ", ".join(f"'{p}'" for p in ALLOWED_PREFIXES)
        print(f"""
❌ 提交信息格式不规范！

  提交信息必须以以下前缀开头：{allowed}
  例如：
    story: 添加用户登录功能
    story: [ABC-123] 重构订单模块
    bugfix: 修复登录页面闪退问题
    bugfix: [ABC-456] 修复支付金额计算错误

  你的提交信息：
    {first_line}
""", file=sys.stderr)
        sys.exit(1)

    print(f"✅ 提交信息格式检查通过", file=sys.stderr)
    sys.exit(0)


if __name__ == "__main__":
    main()
