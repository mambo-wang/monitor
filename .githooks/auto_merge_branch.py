#!/usr/bin/env python3
"""
auto-merge-branch hook
每次 git commit 后自动将指定分支合并到当前分支。
支持 git commit, git merge, git pull 后的自动合并。

配置方式（在 .git/config 或全局 ~/.gitconfig 中添加）：
    [autoMerge]
        branch = main  # 指定要合并的分支

或在项目根目录添加 .auto-merge-branch 文件，每行一个分支名。
"""

import os
import sys
import subprocess
import re
from pathlib import Path


def run(cmd: list[str], cwd: str = None) -> subprocess.CompletedProcess:
    return subprocess.run(cmd, capture_output=True, text=True, cwd=cwd)


def get_current_branch() -> str:
    result = run(["git", "rev-parse", "--abbrev-ref", "HEAD"])
    if result.returncode != 0:
        return ""
    return result.stdout.strip()


def get_auto_merge_branches(repo_root: str) -> list[str]:
    """获取需要自动合并的分支列表"""
    branches = []

    # 1. 读取 .auto-merge-branch 文件
    marker_file = Path(repo_root) / ".auto-merge-branch"
    if marker_file.exists():
        for line in marker_file.read_text().strip().split("\n"):
            line = line.strip()
            if line and not line.startswith("#"):
                branches.append(line)

    # 2. 读取 git config
    for scope in ["local", "global"]:
        if scope == "local":
            result = run(["git", "config", "--local", "autoMerge.branch"])
        else:
            result = run(["git", "config", "--global", "autoMerge.branch"])
        if result.returncode == 0 and result.stdout.strip():
            branch = result.stdout.strip()
            if branch not in branches:
                branches.append(branch)

    # 去重
    return list(dict.fromkeys(branches))


def is_merge_in_progress() -> bool:
    """检查是否存在 merge 冲突需要解决"""
    merge_head = Path(".git") / "MERGE_HEAD"
    return merge_head.exists()


def has_stashed_changes() -> bool:
    """检查是否有未提交的暂存更改"""
    result = run(["git", "status", "--porcelain"])
    return bool(result.stdout.strip())


def merge_branch(branch: str) -> bool:
    """尝试将指定分支合并到当前分支"""
    current = get_current_branch()
    if not current:
        return False

    if branch == current:
        print(f"⚠️  跳过自合并: {branch} == {current}", file=sys.stderr)
        return False

    # 检查分支是否存在
    result = run(["git", "rev-parse", "--verify", branch])
    if result.returncode != 0:
        print(f"❌ 分支不存在: {branch}", file=sys.stderr)
        return False

    # 获取该分支的最新 commit hash
    result = run(["git", "rev-parse", branch])
    if result.returncode != 0:
        return False
    branch_hash = result.stdout.strip()

    # 检查当前分支是否已经包含该 commit
    result = run(["git", "merge-base", "--is-ancestor", branch_hash, "HEAD"])
    if result.returncode == 0:
        print(f"✅ {branch} 已是当前分支 {current} 的祖先，跳过合并", file=sys.stderr)
        return False

    print(f"\n🔄 正在合并分支 {branch} 到 {current}...", file=sys.stderr)

    # 先 fetch 确保最新
    print(f"   📥 fetch {branch}...", file=sys.stderr)
    run(["git", "fetch", "origin", f"{branch}:{branch}"])

    # 执行合并
    result = run(["git", "merge", branch, "--no-edit"])

    if result.returncode != 0:
        print(f"\n❌ 合并冲突！请解决冲突后手动 commit", file=sys.stderr)
        print(f"   冲突文件：", file=sys.stderr)
        for line in result.stdout.split("\n"):
            if line.startswith("CONFLICT"):
                print(f"   - {line}", file=sys.stderr)
        print(f"\n💡 解决冲突后运行以下命令继续：", file=sys.stderr)
        print(f"   git add <resolved-files>", file=sys.stderr)
        print(f"   git commit -m 'Merge branch {branch}'", file=sys.stderr)
        return False

    print(f"✅ 成功合并 {branch} 到 {current}", file=sys.stderr)
    return True


def main():
    # 确保在 git 仓库中
    result = run(["git", "rev-parse", "--is-inside-work-tree"])
    if result.returncode != 0 or result.stdout.strip() != "true":
        sys.exit(0)

    repo_root = run(["git", "rev-parse", "--show-toplevel"]).stdout.strip()
    branches = get_auto_merge_branches(repo_root)

    if not branches:
        # 无配置，不执行
        sys.exit(0)

    # 检查是否有未解决的 merge
    if is_merge_in_progress():
        print(f"⚠️  检测到 merge 正在进行中，跳过自动合并", file=sys.stderr)
        sys.exit(0)

    # 检查是否有未提交的更改
    if has_stashed_changes():
        print(f"⚠️  检测到未提交的更改，跳过自动合并", file=sys.stderr)
        sys.exit(0)

    current = get_current_branch()
    print(f"\n========== auto-merge-branch ==========", file=sys.stderr)
    print(f"   当前分支: {current}", file=sys.stderr)
    print(f"   目标分支: {', '.join(branches)}", file=sys.stderr)
    print(f"========================================\n", file=sys.stderr)

    merged_any = False
    for branch in branches:
        if merge_branch(branch):
            merged_any = True

    if merged_any:
        # 合并后自动推送
        for branch in branches:
            result = run(["git", "push", "origin", current])
            if result.returncode == 0:
                print(f"✅ 已推送到 origin/{current}", file=sys.stderr)
            else:
                print(f"⚠️  推送失败: {result.stderr}", file=sys.stderr)

    sys.exit(0)


if __name__ == "__main__":
    main()
