import pytest
from fastapi.testclient import TestClient
from watcher_ai.main import app
import io

client = TestClient(app)

def test_build_kb_with_documents():
    """测试构建有文档的知识库"""
    # 创建 KB
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "Build测试"})
    kb_id = kb_resp.json()["id"]
    # 上传文档
    files = {"file": ("test.txt", io.BytesIO(b"Hello World. This is a test."), "text/plain")}
    client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    # 构建
    response = client.post(f"/api/knowledge/kbs/{kb_id}/build")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] in ["building", "ready"]

def test_build_empty_kb():
    """测试构建空知识库"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "空KB"})
    kb_id = kb_resp.json()["id"]
    response = client.post(f"/api/knowledge/kbs/{kb_id}/build")
    assert response.status_code == 400
    assert "empty" in response.json()["detail"].lower() or "no documents" in response.json()["detail"].lower()

def test_get_kb_stats():
    """测试获取知识库统计"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "Stats测试"})
    kb_id = kb_resp.json()["id"]
    response = client.get(f"/api/knowledge/kbs/{kb_id}/stats")
    assert response.status_code == 200
    assert "chunk_count" in response.json()
