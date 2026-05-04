#!/usr/bin/env python3
"""
团队共用 hook 安装脚本
团队成员 clone 后，运行一次即可完成配置。

用法:
    python3 .githooks/setup-hooks.py              # 交互式配置
    python3 .githooks/setup-hooks.py --all        # 启用所有功能（默认阈值 1000）
    python3 .githooks/setup-hooks.py --size 800   # 提交量检查，阈值 800
    python3 .githooks/setup-hooks.py --test       # 启用单元测试
    python3 .githooks/setup-hooks.py --merge main # 启用自动合并指定分支
    python3 .githooks/setup-hooks.py --disable   # 禁用所有 hook
    python3 .githooks/setup-hooks.py --show       # 查看当前配置
"""

import subprocess
import sys
import os
from pathlib import Path


GITHOOKS_DIR = Path(__file__).parent.resolve()
REPO_ROOT = GITHOOKS_DIR.parent


def run(cmd: list[str], **kwargs):
    return subprocess.run(cmd, **kwargs)


def is_interactive():
    return sys.stdin.isatty()


def ask(prompt: str, default: str = "") -> str:
    if is_interactive():
        try:
            val = input(prompt).strip()
            return val if val else default
        except (EOFError, KeyboardInterrupt):
            pass
    return default


def configure(enable_size: bool, threshold: str,
              enable_test: bool, enable_merge: bool, merge_branch: str):
    # 配置 core.hooksPath
    result = run(
        ["git", "config", "core.hooksPath", str(GITHOOKS_DIR)],
        cwd=REPO_ROOT,
    )
    if result.returncode != 0:
        print(f"❌ 配置 core.hooksPath 失败: {result.stderr}")
        sys.exit(1)
    print(f"✅ core.hooksPath = {GITHOOKS_DIR}")

    # 提交量检查
    if enable_size:
        try:
            int(threshold)
        except ValueError:
            threshold = "1000"
        run(["git", "config", "--local", "commitSize.threshold", threshold], cwd=REPO_ROOT)
        print(f"✅ 提交量检查 [已启用，阈值 {threshold} 行]")
    else:
        run(["git", "config", "--local", "--unset", "commitSize.threshold"], cwd=REPO_ROOT)
        print(f"⏭️  提交量检查 [跳过]")

    # 单元测试
    if enable_test:
        run(["git", "config", "--local", "autoTest.enabled", "true"], cwd=REPO_ROOT)
        print(f"✅ 单元测试 [已启用]")
    else:
        run(["git", "config", "--local", "--unset", "autoTest.enabled"], cwd=REPO_ROOT)
        print(f"⏭️  单元测试 [跳过]")

    # 自动合并
    if enable_merge:
        run(["git", "config", "--local", "autoMerge.branch", merge_branch], cwd=REPO_ROOT)
        print(f"✅ 自动合并分支 [已启用，目标: {merge_branch}]")
    else:
        run(["git", "config", "--local", "--unset", "autoMerge.branch"], cwd=REPO_ROOT)
        print(f"⏭️  自动合并分支 [跳过]")

    print(f"""
📋 当前配置：""")
    show_config()


def show_config():
    configs = {
        "commitSize.threshold": "提交量检查阈值",
        "autoTest.enabled": "单元测试",
        "autoMerge.branch": "自动合并分支",
    }
    for key, label in configs.items():
        val = run(["git", "config", "--local", "--get", key], cwd=REPO_ROOT, capture_output=True, text=True)
        if val.returncode == 0 and val.stdout.strip():
            print(f"  ✅ {label}: {val.stdout.strip()}")
        else:
            print(f"  ⬜ {label}: 未启用")


def interactive_setup():
    print("=" * 50)
    print("  Git Hooks 团队共用配置")
    print("=" * 50)

    threshold = ask("  📏 提交量检查阈值（行，默认 1000）: ", "1000")
    enable_test = ask("  🧪 是否启用单元测试? (y/N): ", "n").lower() == "y"
    enable_merge = ask("  🔄 是否启用自动合并分支? (y/N): ", "n").lower() == "y"
    merge_branch = "main"
    if enable_merge:
        merge_branch = ask("     合并到哪个分支（默认 main）: ", "main")

    try:
        int(threshold)
    except ValueError:
        threshold = "1000"

    configure(
        enable_size=True,
        threshold=threshold,
        enable_test=enable_test,
        enable_merge=enable_merge,
        merge_branch=merge_branch,
    )


def main():
    args = sys.argv[1:]

    if "--show" in args or "-l" in args:
        print("📋 当前 hook 配置：")
        show_config()
        return

    if "--disable" in args:
        for key in ["commitSize.threshold", "autoTest.enabled", "autoMerge.branch"]:
            run(["git", "config", "--local", "--unset", key], cwd=REPO_ROOT)
        print("✅ 已禁用所有 hook 配置")
        return

    enable_test = "--test" in args
    enable_merge = "--merge" in args
    enable_size = "--size" in args or "--all" in args

    threshold = "1000"
    merge_branch = "main"

    for i, arg in enumerate(args):
        if arg == "--size" and i + 1 < len(args):
            threshold = args[i + 1]
        if arg == "--merge" and i + 1 < len(args):
            merge_branch = args[i + 1]

    if not args or "--all" in args:
        interactive_setup()
    else:
        configure(
            enable_size=enable_size,
            threshold=threshold,
            enable_test=enable_test,
            enable_merge=enable_merge,
            merge_branch=merge_branch,
        )

    print(f"""
💡 管理功能：
  查看配置：python3 .githooks/setup-hooks.py --show
  禁用功能：python3 .githooks/setup-hooks.py --disable
  修改阈值：git config --local commitSize.threshold 800
  启用测试：git config --local autoTest.enabled true
""")


if __name__ == "__main__":
    main()
