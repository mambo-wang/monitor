import pytest
from fastapi.testclient import TestClient
from watcher_ai.main import app

client = TestClient(app)

def test_create_knowledge_base():
    """测试创建知识库"""
    response = client.post(
        "/api/knowledge/kbs",
        json={"name": "测试知识库", "description": "测试描述"}
    )
    assert response.status_code == 201
    data = response.json()["data"]
    assert "id" in data
    assert data["name"] == "测试知识库"
    assert data["status"] == "idle"

def test_list_knowledge_bases():
    """测试获取知识库列表"""
    response = client.get("/api/knowledge/kbs")
    assert response.status_code == 200
    assert isinstance(response.json()["data"], list)

def test_get_knowledge_base():
    """测试获取单个知识库"""
    # 先创建
    create_resp = client.post(
        "/api/knowledge/kbs",
        json={"name": "测试", "description": ""}
    )
    kb_id = create_resp.json()["data"]["id"]
    # 再获取
    response = client.get(f"/api/knowledge/kbs/{kb_id}")
    assert response.status_code == 200
    assert response.json()["data"]["id"] == kb_id

def test_get_nonexistent_kb():
    """测试获取不存在的知识库"""
    response = client.get("/api/knowledge/kbs/nonexistent-id")
    assert response.status_code == 404

def test_delete_knowledge_base():
    """测试删除知识库"""
    create_resp = client.post(
        "/api/knowledge/kbs",
        json={"name": "待删除", "description": ""}
    )
    kb_id = create_resp.json()["data"]["id"]
    response = client.delete(f"/api/knowledge/kbs/{kb_id}")
    assert response.status_code == 200
    # 验证已删除
    get_resp = client.get(f"/api/knowledge/kbs/{kb_id}")
    assert get_resp.status_code == 404

def test_update_knowledge_base_name():
    """测试更新知识库名称"""
    create_resp = client.post(
        "/api/knowledge/kbs",
        json={"name": "旧名称", "description": "旧描述"}
    )
    kb_id = create_resp.json()["data"]["id"]
    response = client.patch(f"/api/knowledge/kbs/{kb_id}", json={"name": "新名称"})
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["name"] == "新名称"
    assert data["description"] == "旧描述"

def test_update_knowledge_base_description():
    """测试更新知识库描述"""
    create_resp = client.post(
        "/api/knowledge/kbs",
        json={"name": "名称", "description": "旧描述"}
    )
    kb_id = create_resp.json()["data"]["id"]
    response = client.patch(f"/api/knowledge/kbs/{kb_id}", json={"description": "新描述"})
    assert response.status_code == 200
    data = response.json()["data"]
    assert data["name"] == "名称"
    assert data["description"] == "新描述"

def test_update_nonexistent_kb():
    """测试更新不存在的知识库"""
    response = client.patch("/api/knowledge/kbs/nonexistent-id", json={"name": "新名称"})
    assert response.status_code == 404