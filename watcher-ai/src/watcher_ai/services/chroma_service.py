import chromadb
import os
import requests
from typing import List, Dict, Optional
from concurrent.futures import ThreadPoolExecutor, as_completed

CHROMA_DB_PATH = os.path.join(os.path.dirname(__file__), "../../chroma_db")
OLLAMA_EMBED_URL = os.getenv("OLLAMA_EMBED_URL", "http://localhost:11434/api/embeddings")
OLLAMA_EMBED_MODEL = os.getenv("OLLAMA_EMBED_MODEL", "bge-m3")

chroma_client = chromadb.PersistentClient(path=CHROMA_DB_PATH)

class ChromaService:
    @staticmethod
    def get_embedding(text: str) -> List[float]:
        """调用 Ollama Embedding API 获取向量"""
        resp = requests.post(
            OLLAMA_EMBED_URL,
            json={"model": OLLAMA_EMBED_MODEL, "prompt": text},
            timeout=120
        )
        resp.raise_for_status()
        return resp.json()["embedding"]

    @staticmethod
    def get_embeddings_batch(texts: List[str], max_workers: int = 5) -> List[List[float]]:
        """并行获取多个文本的 embedding"""
        embeddings = [None] * len(texts)
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            future_to_idx = {executor.submit(ChromaService.get_embedding, text): i for i, text in enumerate(texts)}
            for future in as_completed(future_to_idx):
                idx = future_to_idx[future]
                try:
                    embeddings[idx] = future.result()
                except Exception as e:
                    print(f"Embedding error at {idx}: {e}")
                    embeddings[idx] = [0.0] * 1024  # fallback
        return embeddings

    @staticmethod
    def get_or_create_collection(kb_id: str):
        """获取或创建 collection"""
        collection_name = f"kb_{kb_id}"
        return chroma_client.get_or_create_collection(
            name=collection_name,
            metadata={"kb_id": kb_id}
        )

    @staticmethod
    def collection_exists(kb_id: str) -> bool:
        """检查 collection 是否存在"""
        collection_name = f"kb_{kb_id}"
        try:
            chroma_client.get_collection(collection_name)
            return True
        except:
            return False

    @staticmethod
    def delete_collection(kb_id: str) -> bool:
        """删除 collection"""
        collection_name = f"kb_{kb_id}"
        try:
            chroma_client.delete_collection(collection_name)
            return True
        except:
            return False

    @staticmethod
    def add_vectors(kb_id: str, ids: List[str], documents: List[str], metadatas: List[dict]):
        """添加向量到 collection"""
        collection = ChromaService.get_or_create_collection(kb_id)
        print(f"[ChromaService] Getting embeddings for {len(documents)} documents...")
        embeddings = ChromaService.get_embeddings_batch(documents, max_workers=5)
        print(f"[ChromaService] Got {len(embeddings)} embeddings, adding to collection...")
        collection.add(ids=ids, embeddings=embeddings, documents=documents, metadatas=metadatas)
        print(f"[ChromaService] Done adding vectors")

    @staticmethod
    def search(kb_id: str, query: str, top_k: int = 3) -> Dict:
        """检索向量"""
        collection_name = f"kb_{kb_id}"
        try:
            collection = chroma_client.get_collection(collection_name)
        except:
            return {"ids": [[]], "documents": [[]], "metadatas": [[]], "distances": [[]]}
        query_embedding = ChromaService.get_embedding(query)
        return collection.query(query_embeddings=[query_embedding], n_results=top_k)

    @staticmethod
    def get_count(kb_id: str) -> int:
        """获取 collection 中的向量数量"""
        collection_name = f"kb_{kb_id}"
        try:
            collection = chroma_client.get_collection(collection_name)
            return collection.count()
        except:
            return 0

    @staticmethod
    def count_by_file(kb_id: str, file_name: str) -> int:
        """查询指定文件在 ChromaDB 中的块数量"""
        collection_name = f"kb_{kb_id}"
        try:
            collection = chroma_client.get_collection(collection_name)
            results = collection.get(include=["metadatas"])
            if not results or not results.get("metadatas"):
                return 0
            count = 0
            for meta in results["metadatas"]:
                if meta and meta.get("file_name") == file_name:
                    count += 1
            return count
        except:
            return 0
