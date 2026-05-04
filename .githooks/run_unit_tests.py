#!/usr/bin/env python3
"""
run-unit-tests hook
每次 git commit 前自动运行单元测试，测试不通过则阻止提交。
支持 Maven (mvn), Gradle, npm test 等构建工具。

配置方式：
- 在项目根目录创建 .run-tests 文件，指定测试命令
- 或使用 git config autoTest.command
- 或自动检测 pom.xml/build.gradle/package.json
"""

import os
import sys
import subprocess
import re
from pathlib import Path


# 默认测试命令（按项目类型自动检测）
AUTO_DETECT_COMMANDS = [
    ("pom.xml", ["mvn", "test", "-q"]),
    ("build.gradle", ["gradle", "test", "--quiet"]),
    ("build.gradle.kts", ["gradle", "test", "--quiet"]),
    ("pom.xml", ["mvn", "test", "-q"]),
    ("package.json", ["npm", "test", "--", "--silent"]),
    ("Cargo.toml", ["cargo", "test", "--quiet"]),
    ("go.mod", ["go", "test", "./..."]),
    ("pyproject.toml", ["python", "-m", "pytest", "-q"]),
    ("requirements.txt", ["python", "-m", "pytest", "-q"]),
]


def run(cmd: list[str], cwd: str = None, timeout: int = 300) -> subprocess.CompletedProcess:
    """执行命令，超时控制"""
    import signal

    class TimeoutError(Exception):
        pass

    def timeout_handler(signum, frame):
        raise TimeoutError(f"命令执行超时（{timeout}秒）")

    try:
        signal.signal(signal.SIGALRM, timeout_handler)
        signal.alarm(timeout)
        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            cwd=cwd,
            shell=False,
        )
        signal.alarm(0)
        return result
    except TimeoutError:
        print(f"⏰ 命令执行超时（{timeout}秒），终止提交", file=sys.stderr)
        sys.exit(1)


def get_repo_root() -> str:
    result = subprocess.run(
        ["git", "rev-parse", "--show-toplevel"], capture_output=True, text=True
    )
    if result.returncode == 0:
        return result.stdout.strip()
    return ""


def get_test_command(repo_root: str) -> list[str]:
    """获取测试命令"""
    # 1. 读取 .run-tests 文件
    test_file = Path(repo_root) / ".run-tests"
    if test_file.exists():
        cmd = test_file.read_text().strip()
        if cmd and not cmd.startswith("#"):
            return cmd.split()

    # 2. 读取 git config
    for scope in ["local", "global"]:
        if scope == "local":
            result = subprocess.run(
                ["git", "config", "--local", "autoTest.command"], capture_output=True, text=True
            )
        else:
            result = subprocess.run(
                ["git", "config", "--global", "autoTest.command"], capture_output=True, text=True
            )
        if result.returncode == 0 and result.stdout.strip():
            cmd = result.stdout.strip()
            if cmd:
                return cmd.split()

    # 3. 自动检测项目类型
    for filename, cmd in AUTO_DETECT_COMMANDS:
        if Path(repo_root, filename).exists():
            # 对于 Maven，检查是否有 test 阶段
            if filename == "pom.xml":
                return cmd
            elif filename == "package.json":
                return ["npm", "test"]
            elif filename == "build.gradle" or filename == "build.gradle.kts":
                return ["gradle", "test"]
            elif filename == "Cargo.toml":
                return ["cargo", "test"]
            elif filename == "go.mod":
                return ["go", "test", "./..."]
            elif filename == "pyproject.toml":
                return ["python", "-m", "pytest"]
            elif filename == "requirements.txt":
                return ["python", "-m", "pytest"]

    return []


def parse_test_results(output: str, cmd: list[str]) -> dict:
    """解析测试输出，返回统计信息"""
    result = {
        "passed": 0,
        "failed": 0,
        "skipped": 0,
        "errors": 0,
        "total": 0,
        "duration": "",
    }

    # Maven: Tests run: 5, Failures: 1, Errors: 0, Skipped: 0
    mvn_match = re.findall(
        r"Tests run:\s*(\d+),\s*Failures:\s*(\d+),\s*Errors:\s*(\d+),\s*Skipped:\s*(\d+)",
        output,
    )
    if mvn_match:
        for run_count, failures, errors, skipped in mvn_match:
            result["total"] += int(run_count)
            result["failed"] += int(failures)
            result["errors"] += int(errors)
            result["skipped"] += int(skipped)
            result["passed"] += int(run_count) - int(failures) - int(errors)
        return result

    # Gradle: > Task :app:test PASSED
    gradle_matches = re.findall(r"(\w+) PASSED", output)
    if gradle_matches:
        result["passed"] = len(gradle_matches)
        result["total"] = len(gradle_matches)
        return result

    # npm test: Tests: 2 passed, 1 failed
    npm_match = re.search(r"Tests:\s*(?:(\d+)\s+passed)?,?\s*(?:(\d+)\s+failed)?", output)
    if npm_match:
        passed = npm_match.group(1)
        failed = npm_match.group(2)
        if passed:
            result["passed"] = int(passed)
        if failed:
            result["failed"] = int(failed)
        result["total"] = result["passed"] + result["failed"]
        return result

    # pytest: 5 passed, 1 failed in 0.50s
    pytest_match = re.search(
        r"(\d+)\s+passed(?:,?\s*(\d+)\s+failed)?(?:,?\s*(\d+)\s+skipped)?", output
    )
    if pytest_match:
        result["passed"] = int(pytest_match.group(1))
        if pytest_match.group(2):
            result["failed"] = int(pytest_match.group(2))
        if pytest_match.group(3):
            result["skipped"] = int(pytest_match.group(3))
        duration = re.search(r"in\s+([\d.]+s)", output)
        if duration:
            result["duration"] = duration.group(1)
        result["total"] = result["passed"] + result["failed"]
        return result

    # go test: ok  	./...	5.123s   /   FAIL
    go_ok = re.findall(r"^\s*ok\s+", output, re.MULTILINE)
    go_fail = re.findall(r"^\s*FAIL\s+", output, re.MULTILINE)
    if go_ok or go_fail:
        result["passed"] = len(go_ok)
        result["failed"] = len(go_fail)
        result["total"] = len(go_ok) + len(go_fail)
        return result

    return result


def run_tests(cmd: list[str], repo_root: str) -> tuple[int, dict]:
    """运行单元测试"""
    print(f"\n========== 单元测试 ==========", file=sys.stderr)
    print(f"   命令: {' '.join(cmd)}", file=sys.stderr)
    print(f"==============================\n", file=sys.stderr)

    result = run(cmd, cwd=repo_root)

    stats = parse_test_results(result.stdout + result.stderr, cmd)
    return result.returncode, stats


def main():
    # 确保在 git 仓库中
    result = subprocess.run(
        ["git", "rev-parse", "--is-inside-work-tree"], capture_output=True, text=True
    )
    if result.returncode != 0 or result.stdout.strip() != "true":
        sys.exit(0)

    repo_root = get_repo_root()
    test_cmd = get_test_command(repo_root)

    if not test_cmd:
        print(
            "⚠️  未找到测试命令，跳过测试。\n"
            "   请创建 .run-tests 文件或使用 git config autoTest.command 配置。",
            file=sys.stderr,
        )
        sys.exit(0)

    returncode, stats = run_tests(test_cmd, repo_root)

    # 格式化输出
    duration_str = f" ({stats['duration']})" if stats["duration"] else ""
    if returncode == 0:
        if stats["total"] > 0:
            print(
                f"\n✅ 单元测试全部通过！{stats['passed']}/{stats['total']} 测试用例{duration_str}",
                file=sys.stderr,
            )
        else:
            print(f"\n✅ 单元测试通过（无测试用例）", file=sys.stderr)
        sys.exit(0)
    else:
        print(
            f"\n❌ 单元测试失败！阻止提交。\n"
            f"   通过: {stats['passed']}  "
            f"失败: {stats['failed']}  "
            f"跳过: {stats['skipped']}  "
            f"总计: {stats['total']}{duration_str}",
            file=sys.stderr,
        )
        print(
            f"\n💡 修复测试后重新提交：\n   {' '.join(test_cmd)}",
            file=sys.stderr,
        )
        sys.exit(1)


if __name__ == "__main__":
    main()
