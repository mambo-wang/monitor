#!/usr/bin/env python3
import os
import re
import json
from pathlib import Path
from collections import defaultdict

JAVA_EXTENSIONS = {'.java'}

METHOD_DEF_PATTERN = re.compile(
    r'(?:public|protected|private)\s+(?:static\s+)?(?:final\s+)?(?:[\w<>[\],\s]+?)\s+(\w+)\s*\([^)]*\)\s*(?:throws[\s\S]*?)?\{',
    re.MULTILINE
)

CLASS_DEF_PATTERN = re.compile(
    r'(?:public|protected|private)?\s*(?:abstract\s+)?(?:final\s+)?class\s+(\w+)',
    re.MULTILINE
)

METHOD_CALL_PATTERN = re.compile(
    r'(?:^|[^\w])(\w+)\s*\([^)]*\)\s*;',
    re.MULTILINE
)

def find_java_files(root_path):
    java_files = []
    for dirpath, _, filenames in os.walk(root_path):
        if 'test' in dirpath.split(os.sep):
            continue
        for f in filenames:
            if Path(f).suffix in JAVA_EXTENSIONS:
                java_files.append(os.path.join(dirpath, f))
    return java_files

def extract_class_name(content, filepath):
    match = CLASS_DEF_PATTERN.search(content)
    if match:
        return match.group(1)
    return Path(filepath).stem

def extract_methods(content, class_name):
    methods = []
    for match in METHOD_DEF_PATTERN.finditer(content):
        method_name = match.group(1)
        full_signature = match.group(0)
        if method_name not in ['if', 'while', 'for', 'switch', 'catch', 'try', 'else', 'do', 'return']:
            methods.append({
                'name': method_name,
                'class': class_name,
                'signature': full_signature[:80]
            })
    return methods

def extract_method_calls(content):
    calls = set()
    for match in METHOD_CALL_PATTERN.finditer(content):
        calls.add(match.group(1))
    return calls

def main():
    root = Path('/Users/kirito/repos/ShowTime')
    java_files = find_java_files(root)

    all_methods = []
    method_locations = defaultdict(list)
    method_calls = defaultdict(set)

    for jf in java_files:
        try:
            content = Path(jf).read_text(encoding='utf-8', errors='ignore')
        except Exception as e:
            continue

        class_name = extract_class_name(content, jf)
        methods = extract_methods(content, class_name)

        for m in methods:
            key = f"{m['class']}.{m['name']}"
            m['file'] = str(jf)
            all_methods.append(m)
            method_locations[key].append(m)

        calls = extract_method_calls(content)
        for c in calls:
            method_calls[c].add(str(jf))

    defined_methods = set(m['name'] for m in all_methods)

    never_called = []
    for m in all_methods:
        key = f"{m['class']}.{m['name']}"
        if m['name'] not in method_calls or not method_calls[m['name']]:
            if not m['name'].startswith('get') and not m['name'].startswith('set'):
                if m['name'] not in ['main', 'toString', 'equals', 'hashCode', 'compareTo']:
                    never_called.append(m)

    print(f"Total Java files scanned: {len(java_files)}")
    print(f"Total methods defined: {len(all_methods)}")
    print(f"Methods potentially never called: {len(never_called)}")
    print()
    print("=" * 80)
    print("POTENTIALLY DEAD METHODS (never called from scanned files):")
    print("=" * 80)

    by_class = defaultdict(list)
    for m in never_called:
        by_class[m['class']].append(m)

    for cls, methods in sorted(by_class.items()):
        print(f"\n{cls}:")
        for m in methods:
            print(f"  - {m['name']}() @ {Path(m['file']).name}")

if __name__ == '__main__':
    main()