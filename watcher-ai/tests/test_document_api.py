import pytest
from fastapi.testclient import TestClient
from watcher_ai.main import app
import io

client = TestClient(app)

def test_upload_document():
    """测试上传文档"""
    # 先创建知识库
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    # 上传文件
    files = {"file": ("test.txt", io.BytesIO(b"Hello World"), "text/plain")}
    response = client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    assert response.status_code == 201
    data = response.json()
    assert data["file_name"] == "test.txt"
    assert data["status"] == "pending"

def test_upload_unsupported_format():
    """测试上传不支持的格式"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    files = {"file": ("test.exe", io.BytesIO(b"data"), "application/octet-stream")}
    response = client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    assert response.status_code == 400
    assert "Unsupported" in response.json()["detail"]

def test_list_documents():
    """测试列出文档"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    # 上传后列出
    files = {"file": ("doc.md", io.BytesIO(b"# Title"), "text/markdown")}
    client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    response = client.get(f"/api/knowledge/kbs/{kb_id}/documents")
    assert response.status_code == 200
    assert len(response.json()) >= 1

def test_delete_document():
    """测试删除文档"""
    kb_resp = client.post("/api/knowledge/kbs", json={"name": "测试KB"})
    kb_id = kb_resp.json()["id"]
    files = {"file": ("to_delete.txt", io.BytesIO(b"content"), "text/plain")}
    upload_resp = client.post(f"/api/knowledge/kbs/{kb_id}/documents", files=files)
    doc_id = upload_resp.json()["id"]
    response = client.delete(f"/api/knowledge/kbs/{kb_id}/documents/{doc_id}")
    assert response.status_code == 200
