import os
import requests
from typing import List, Dict

OLLAMA_EMBED_URL = os.getenv("OLLAMA_EMBED_URL", "http://localhost:11434/api/embeddings")
OLLAMA_EMBED_MODEL = os.getenv("OLLAMA_EMBED_MODEL", "bge-m3")
MINIMAX_API_KEY = os.getenv("MINIMAX_API_KEY", "sk-cp-B7wLOi1u7D10BybqBfS50vmPufJ_e88g4arwKLkuDnIH6WpO4MElIO-MCgvf1hOZgyErLd-iYNCNpJqYIoaRdmNL40o_3tXBDK4iKNgZoPr1fQ7W8R7H5WI")
MINIMAX_BASE_URL = "https://api.minimax.chat/v1"
MINIMAX_MODEL = "MiniMax-M2.7"

class LLMService:
    @staticmethod
    def get_embedding(text: str) -> List[float]:
        """获取文本向量"""
        try:
            resp = requests.post(
                OLLAMA_EMBED_URL,
                json={"model": OLLAMA_EMBED_MODEL, "prompt": text},
                timeout=30
            )
            resp.raise_for_status()
            return resp.json()["embedding"]
        except Exception as e:
            print(f"Embedding error: {e}")
            # 返回零向量作为降级
            return [0.0] * 1024

    @staticmethod
    def chat(question: str, context: str = "") -> str:
        """调用 LLM 生成回答"""
        if not MINIMAX_API_KEY:
            return "LLM not configured. Please set MINIMAX_API_KEY."
        
        system_prompt = """你是一个智能助手，基于给定的上下文回答用户问题。
如果上下文中没有相关信息，请如实告知用户。"""

        user_prompt = f"上下文：\n{context}\n\n问题：{question}\n\n请基于上下文回答问题。"
        
        try:
            resp = requests.post(
                f"{MINIMAX_BASE_URL}/chat/completions",
                headers={
                    "Authorization": f"Bearer {MINIMAX_API_KEY}",
                    "Content-Type": "application/json"
                },
                json={
                    "model": MINIMAX_MODEL,
                    "messages": [
                        {"role": "system", "content": system_prompt},
                        {"role": "user", "content": user_prompt}
                    ]
                },
                timeout=60
            )
            resp.raise_for_status()
            return resp.json()["choices"][0]["message"]["content"]
        except Exception as e:
            return f"LLM调用失败: {str(e)}"
