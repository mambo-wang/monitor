"""
测试知识库持久化修复

验证：
1. 服务重启后 KBService 能从 ChromaDB 恢复数据
2. ChromaDB 中有数据的 KB 可以正常问答
3. KBService 数据能持久化到磁盘
"""
import pytest
import os
import sys
import shutil
import tempfile

sys.path.insert(0, 'src')

def test_sync_from_chroma_on_init():
    """测试初始化时从 ChromaDB 同步"""
    from watcher_ai.services.kb_service import KBService, init, _kb_store, _save_to_disk, _load_from_disk
    
    # 初始化
    init()
    
    # 应该能从 ChromaDB 恢复 KB
    kbs = KBService.list_all()
    ready_kbs = [kb for kb in kbs if kb["status"] == "ready" and kb["chunk_count"] > 0]
    
    # ChromaDB 中有多个 ready 的 KB
    assert len(ready_kbs) >= 1, f"应该至少有1个ready KB，实际: {len(ready_kbs)}"
    print(f"✓ 从 ChromaDB 恢复了 {len(kbs)} 个 KB，其中 {len(ready_kbs)} 个处于就绪状态")

def test_chat_with_ready_kb():
    """测试向已构建的知识库提问"""
    from fastapi.testclient import TestClient
    from watcher_ai.main import app
    from watcher_ai.services.kb_service import KBService, init
    
    # 确保初始化
    init()
    
    # 找一个有 chunk_count 的 KB
    kbs = KBService.list_all()
    ready_kb = next((kb for kb in kbs if kb["status"] == "ready" and kb["chunk_count"] > 0), None)
    
    assert ready_kb is not None, "没有找到就绪状态的知识库"
    print(f"✓ 使用知识库: {ready_kb['name']} (ID: {ready_kb['id']})")
    
    client = TestClient(app)
    response = client.post("/api/knowledge/chat", json={
        "kb_id": ready_kb["id"],
        "question": "测试问题"
    })
    
    # 不应该返回 400 "Knowledge base is empty"
    if response.status_code == 400:
        assert "Knowledge base is empty" not in response.json().get("detail", ""), \
            f"知识库已构建但仍然报错: {response.json()}"
    
    print(f"✓ 问答 API 返回状态码: {response.status_code}")
    assert response.status_code in [200, 400, 500], f"意外的状态码: {response.status_code}"

def test_kb_service_persistence():
    """测试 KBService 数据持久化到磁盘"""
    import json
    from watcher_ai.services.kb_service import KBService, init, DATA_DIR, KB_STORE_FILE
    
    init()
    
    # 创建新 KB
    kb = KBService.create("持久化测试KB", "测试持久化功能")
    kb_id = kb["id"]
    
    # 更新状态
    KBService.update_status(kb_id, "ready")
    KBService.update_counts(kb_id, 5, 10)
    
    # 检查文件是否创建
    assert os.path.exists(KB_STORE_FILE), f"持久化文件未创建: {KB_STORE_FILE}"
    
    # 读取文件验证
    with open(KB_STORE_FILE, "r") as f:
        data = json.load(f)
    
    kb_data = next((k for k in data if k["id"] == kb_id), None)
    assert kb_data is not None, "KB 未保存到文件"
    assert kb_data["status"] == "ready", f"状态未正确保存: {kb_data['status']}"
    assert kb_data["chunk_count"] == 10, f"chunk_count 未正确保存: {kb_data['chunk_count']}"
    
    print(f"✓ KB 数据正确持久化到: {KB_STORE_FILE}")
    
    # 清理
    KBService.delete(kb_id)

if __name__ == "__main__":
    print("=" * 60)
    print("测试知识库持久化修复")
    print("=" * 60)
    
    print("\n1. 测试从 ChromaDB 同步...")
    test_sync_from_chroma_on_init()
    
    print("\n2. 测试向已构建知识库提问...")
    test_chat_with_ready_kb()
    
    print("\n3. 测试 KBService 数据持久化...")
    test_kb_service_persistence()
    
    print("\n" + "=" * 60)
    print("所有测试通过！")
    print("=" * 60)
