#!/usr/bin/env python3
"""
检查 Git 提交改动量是否超过阈值，默认阈值为 1000 行。
如果超过阈值，分析文件并给出分批提交建议。
支持 git commit 和 git commit --dry-run 场景。
"""

import subprocess
import sys
import json
import os
from pathlib import Path
from typing import Optional


def get_git_config_threshold() -> Optional[str]:
    """从 git config 读取阈值配置"""
    result = subprocess.run(
        ["git", "config", "--local", "--get", "commitSize.threshold"],
        capture_output=True, text=True
    )
    if result.returncode == 0:
        return result.stdout.strip()
    return None


def get_threshold() -> int:
    """获取阈值优先级：环境变量 > git config > 默认值"""
    env_threshold = os.environ.get("COMMIT_SIZE_THRESHOLD", os.environ.get("THRESHOLD", ""))
    if env_threshold:
        try:
            return int(env_threshold)
        except ValueError:
            pass
    
    git_threshold = get_git_config_threshold()
    if git_threshold:
        try:
            return int(git_threshold)
        except ValueError:
            pass
    
    return 1000


DEFAULT_THRESHOLD = get_threshold()


def run_git_diff(staged: bool = True) -> str:
    """获取暂存区或工作区的 diff 统计"""
    cmd = ["git", "diff", "--numstat"]
    if staged:
        cmd.append("--cached")
    result = subprocess.run(cmd, capture_output=True, text=True)
    return result.stdout


def parse_diff_numstat(output: str) -> list[dict]:
    """解析 git diff --numstat 输出"""
    files = []
    for line in output.strip().split("\n"):
        if not line:
            continue
        parts = line.split("\t")
        if len(parts) >= 3:
            try:
                added = int(parts[0]) if parts[0] != "-" else 0
                deleted = int(parts[1]) if parts[1] != "-" else 0
                path = parts[2]
                files.append({
                    "path": path,
                    "added": added,
                    "deleted": deleted,
                    "changes": added + deleted,
                })
            except ValueError:
                continue
    return files


def group_by_package(files: list[dict]) -> dict[str, list[dict]]:
    """按包/目录对文件进行分组"""
    groups = {}
    for f in files:
        path = Path(f["path"])
        # 取前两层目录作为分组（适合 Java/TypeScript 项目）
        parts = path.parts
        if len(parts) >= 2:
            group_key = str(path.parent)
        else:
            group_key = "root"
        if group_key not in groups:
            groups[group_key] = []
        groups[group_key].append(f)
    return groups


def suggest_splits(files: list[dict], threshold: int) -> list[dict]:
    """根据改动量建议如何分批提交"""
    # 按改动量降序排列
    sorted_files = sorted(files, key=lambda x: x["changes"], reverse=True)
    splits = []
    current_group = []
    current_total = 0

    for f in sorted_files:
        # 如果单个文件就超过阈值，单独作为一组
        if f["changes"] > threshold:
            if current_group:
                splits.append({
                    "files": [x["path"] for x in current_group],
                    "total_changes": current_total,
                    "description": f"Group {len(splits)+1}: {len(current_group)} files"
                })
                current_group = []
                current_total = 0
            splits.append({
                "files": [f["path"]],
                "total_changes": f["changes"],
                "description": f"Split: {f['path']} (单个大文件，建议单独提交)"
            })
            continue

        if current_total + f["changes"] <= threshold:
            current_group.append(f)
            current_total += f["changes"]
        else:
            if current_group:
                splits.append({
                    "files": [x["path"] for x in current_group],
                    "total_changes": current_total,
                    "description": f"Group {len(splits)+1}: {len(current_group)} files"
                })
            current_group = [f]
            current_total = f["changes"]

    if current_group:
        splits.append({
            "files": [x["path"] for x in current_group],
            "total_changes": current_total,
            "description": f"Group {len(splits)+1}: {len(current_group)} files"
        })

    return splits


def format_suggestions(splits: list[dict], threshold: int) -> str:
    """格式化分批提交建议"""
    lines = ["\n📦 建议分批提交方案：\n"]
    for i, split in enumerate(splits, 1):
        status = "✅" if split["total_changes"] <= threshold else "⚠️"
        lines.append(f"{status} 第 {i} 批（{split['total_changes']} 行改动）：")
        for fp in split["files"]:
            lines.append(f"   - {fp}")
        lines.append("")
    return "\n".join(lines)


def main():
    threshold = DEFAULT_THRESHOLD
    # 可通过环境变量覆盖阈值
    env_threshold = os.environ.get("COMMIT_SIZE_THRESHOLD", "")
    if env_threshold:
        try:
            threshold = int(env_threshold)
        except ValueError:
            pass

    # 读取命令行参数
    args = sys.argv[1:]
    dry_run = "--dry-run" in args or "-n" in args
    # 忽略文件列表参数，只检查整体改动量
    # 支持 --only 参数只检查特定文件

    diff_output = run_git_diff(staged=True)
    if not diff_output.strip():
        # 没有暂存内容，尝试检查工作区
        diff_output = run_git_diff(staged=False)

    files = parse_diff_numstat(diff_output)
    total_added = sum(f["added"] for f in files)
    total_deleted = sum(f["deleted"] for f in files)
    total_changes = sum(f["changes"] for f in files)
    file_count = len(files)

    result = {
        "total_added": total_added,
        "total_deleted": total_deleted,
        "total_changes": total_changes,
        "file_count": file_count,
        "threshold": threshold,
        "exceeded": total_changes > threshold,
        "files": files,
    }

    if total_changes > threshold:
        splits = suggest_splits(files, threshold)
        result["splits"] = splits
        suggestions = format_suggestions(splits, threshold)

        print(f"""
❌ 提交被拦截！改动量超过阈值 {threshold} 行

📊 统计信息：
   - 总新增行数: +{total_added}
   - 总删除行数: -{total_deleted}
   - 总改动行数: {total_changes} 行
   - 涉及文件数: {file_count} 个

{suggestions}
💡 使用方法：
   git add <file1> <file2> ...
   git commit -m "message"
   逐批提交以满足代码审查最佳实践。
""", file=sys.stderr)

        # JSON 格式输出供程序解析
        print(f"__JSON_RESULT__:{json.dumps(result, ensure_ascii=False, indent=2)}")
        sys.exit(1)
    else:
        # 检查通过，静默退出，不输出任何信息
        sys.exit(0)


if __name__ == "__main__":
    main()
