import pytest
from watcher_ai.services.document_service import DocumentProcessor
import os

def test_split_text():
    """测试文本分割"""
    # 使用较短但超过 chunk_size 的文本
    text = "A" * 600  # 600 chars, chunk_size is 500
    chunks = DocumentProcessor.split_text(text)
    assert len(chunks) >= 2
    assert all(isinstance(c, str) for c in chunks)

def test_load_document_not_found():
    """测试加载不存在的文档"""
    docs, err = DocumentProcessor.load_document("/nonexistent/path.pdf")
    assert docs == []
    assert "not exist" in err.lower() or "失败" in err

def test_supported_formats():
    """测试支持的格式"""
    supported = DocumentProcessor.get_supported_extensions()
    assert ".pdf" in supported
    assert ".md" in supported
    assert ".txt" in supported
