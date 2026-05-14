import pytest
from watcher_ai.services.chroma_service import ChromaService

def test_create_and_get_collection():
    """测试创建和获取 collection"""
    kb_id = "test-kb-1"
    collection = ChromaService.get_or_create_collection(kb_id)
    assert collection.name == f"kb_{kb_id}"

def test_add_and_search_vectors():
    """测试添加和检索向量"""
    kb_id = "test-kb-search"
    ChromaService.delete_collection(kb_id)  # 清理
    collection = ChromaService.get_or_create_collection(kb_id)
    # 添加测试向量
    ChromaService.add_vectors(
        kb_id,
        ids=["test1", "test2"],
        documents=["Hello world", "Python programming"],
        metadatas=[{"source": "test"}, {"source": "test"}]
    )
    # 检索
    results = ChromaService.search(kb_id, query="hello", top_k=2)
    assert len(results["ids"][0]) <= 2
    # 清理
    ChromaService.delete_collection(kb_id)

def test_delete_collection():
    """测试删除 collection"""
    kb_id = "test-kb-delete"
    ChromaService.get_or_create_collection(kb_id)
    assert ChromaService.delete_collection(kb_id)
    assert not ChromaService.collection_exists(kb_id)
