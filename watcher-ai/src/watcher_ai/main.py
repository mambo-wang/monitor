from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
from watcher_ai.api.knowledge import router as knowledge_router
from watcher_ai.api.chat_history import router as chat_history_router
from watcher_ai.api.mcp_router import router as mcp_router
from watcher_ai.services.kb_service import init as kb_init

@asynccontextmanager
async def lifespan(app: FastAPI):
    # 启动时初始化 KBService
    kb_init()
    yield

app = FastAPI(title="ShowTime RAG API", lifespan=lifespan)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 路由
app.include_router(knowledge_router)
app.include_router(chat_history_router)
app.include_router(mcp_router)

@app.get("/")
def root():
    return {"message": "ShowTime RAG API"}

@app.get("/health")
def health():
    return {"status": "ok"}
