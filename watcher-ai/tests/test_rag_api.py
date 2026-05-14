import pytest
from fastapi.testclient import TestClient
from watcher_ai.main import app
import io

client = TestClient(app)

def test_chat_with_empty_kb():
    """测试向空知识库提问"""
    # 创建 KB
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "Chat测试"})
    kb_id = kb_resp.json()["id"]
    # 提问
    response = client.post("/api/knowledge/chat", json={
        "kb_id": kb_id,
        "question": "什么是云桌面？"
    })
    assert response.status_code == 400

def test_search_kb():
    """测试检索知识库"""
    # 创建 KB 并上传文档
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "Search测试"})
    kb_id = kb_resp.json()["id"]
    files = {"file": ("test.txt", io.BytesIO(b"Hello World. This is a test document."), "text/plain")}
    client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    # 构建
    client.post(f"/api/knowledge/kbs/{kb_id}/build")
    # 检索
    response = client.post(f"/api/knowledge/kbs/{kb_id}/search", json={
        "query": "hello",
        "top_k": 3
    })
    assert response.status_code == 200
