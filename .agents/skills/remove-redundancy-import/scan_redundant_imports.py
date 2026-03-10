#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
冗余 import 扫描脚本
按 reference.md 规则扫描项目内 Java 文件，输出待移除的冗余 import 列表。
用法：在项目根目录执行 python scan_redundant_imports.py
"""
import os
import re
import sys


def get_simple_name(imp: str) -> str | None:
    """从 import 行提取简单名（用于类体搜索）"""
    m = re.match(r'import\s+static\s+[\w.]+\.(\w+)\s*;', imp)
    if m:
        return m.group(1)
    m = re.match(r'import\s+(?:static\s+)?([\w.]+)\s*;', imp)
    if m:
        return m.group(1).split('.')[-1]
    return None


def get_import_full(imp: str) -> str:
    """提取 import 的完整限定名"""
    m = re.match(r'import\s+(?:static\s+)?([\w.]+)\s*;', imp)
    return m.group(1).strip() if m else ''


def get_import_package(imp: str) -> str:
    """提取 import 所在包（用于同包冗余判断）"""
    m = re.match(r'import\s+(?:static\s+)?([\w.]+)\s*;', imp)
    if m:
        parts = m.group(1).split('.')
        return '.'.join(parts[:-1]) if len(parts) > 1 else ''
    return ''


def find_class_body_start(content: str) -> int:
    """找到类体起始位置（最后一个 import 之后）"""
    last = 0
    for m in re.finditer(r'import\s+(?:static\s+)?[\w.]+\s*;', content):
        last = m.end()
    return last


def main() -> None:
    root = sys.argv[1] if len(sys.argv) > 1 else '.'
    skip_dirs = {'target', 'build', '.git', 'node_modules'}

    results = []
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in skip_dirs]
        for f in filenames:
            if not f.endswith('.java'):
                continue
            path = os.path.join(dirpath, f).replace('\\', '/')
            try:
                with open(path, 'r', encoding='utf-8', errors='ignore') as fp:
                    content = fp.read()
            except OSError:
                continue

            pkg_match = re.search(r'package\s+([\w.]+)\s*;', content)
            file_pkg = pkg_match.group(1) if pkg_match else ''

            imports = re.findall(r'import\s+(?:static\s+)?[\w.]+\s*;', content)
            imports = [i for i in imports if '*;' not in i and '.*' not in i]

            body_start = find_class_body_start(content)
            body = content[body_start:]

            redundant = []
            for imp in imports:
                simple = get_simple_name(imp)
                if not simple:
                    continue
                imp_full = get_import_full(imp)
                imp_pkg = get_import_package(imp)
                if imp_pkg and imp_pkg == file_pkg:
                    redundant.append(imp_full)
                    continue
                if not re.search(r'\b' + re.escape(simple) + r'\b', body):
                    redundant.append(imp_full)

            if redundant:
                results.append((path, redundant))

    for path, red in results:
        print(f"{path} | {'; '.join(red)} | {len(red)}")
    print("TOTAL_FILES:" + str(len(results)))
    print("TOTAL_IMPORTS:" + str(sum(len(r[1]) for r in results)))


if __name__ == '__main__':
    main()
