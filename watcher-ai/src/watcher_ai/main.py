from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from watcher_ai.api.knowledge import router as knowledge_router

app = FastAPI(title="ShowTime RAG API")

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

@app.get("/")
def root():
    return {"message": "ShowTime RAG API"}

@app.get("/health")
def health():
    return {"status": "ok"}
