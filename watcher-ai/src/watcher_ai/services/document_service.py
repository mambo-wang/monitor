import os
from typing import Tuple, List

CHUNK_SIZE = 300
CHUNK_OVERLAP = 30

class DocumentProcessor:
    SUPPORTED_EXTENSIONS = {".pdf", ".md", ".txt"}

    @staticmethod
    def get_supported_extensions() -> set:
        return DocumentProcessor.SUPPORTED_EXTENSIONS

    @staticmethod
    def load_document(file_path: str) -> Tuple[List[dict], str]:
        """加载文档，返回文档内容列表"""
        if not os.path.exists(file_path):
            return [], f"File not exist: {file_path}"
        ext = os.path.splitext(file_path)[1].lower()
        try:
            if ext == ".pdf":
                import pdfplumber
                pages = []
                with pdfplumber.open(file_path) as pdf:
                    for i, page in enumerate(pdf.pages):
                        text = page.extract_text()
                        if text:
                            pages.append({"content": text, "page": i + 1})
                if not pages:
                    return [], "Failed to extract text from PDF"
                return pages, None
            elif ext == ".md":
                with open(file_path, "r", encoding="utf-8") as f:
                    text = f.read()
                return [{"content": text, "page": 1}], None
            elif ext == ".txt":
                with open(file_path, "r", encoding="utf-8") as f:
                    text = f.read()
                return [{"content": text, "page": 1}], None
            else:
                return [], f"Unsupported format: {ext}"
        except Exception as e:
            return [], f"Load failed: {str(e)}"

    @staticmethod
    def split_text(text: str) -> List[str]:
        """分割文本为 chunks - 简单实现"""
        if len(text) <= CHUNK_SIZE:
            return [text] if text else []
        
        chunks = []
        start = 0
        while start < len(text):
            end = min(start + CHUNK_SIZE, len(text))
            chunk = text[start:end]
            chunks.append(chunk)
            if end >= len(text):
                break
            start = end - CHUNK_OVERLAP
            if start >= end:
                start = end
        return chunks

    @staticmethod
    def process_document(file_path: str) -> Tuple[List[dict], str]:
        """处理文档，返回 chunks 列表"""
        docs, err = DocumentProcessor.load_document(file_path)
        if err:
            return [], err
        if not docs:
            return [], "Empty document"
        full_text = "\n".join(doc["content"] for doc in docs)
        chunks = DocumentProcessor.split_text(full_text)
        return [{"content": c, "chunk_index": i} for i, c in enumerate(chunks)], None
