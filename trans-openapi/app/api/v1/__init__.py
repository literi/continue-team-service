"""
API v1 路由
"""
from fastapi import APIRouter

from app.api.v1 import models, skills, mcp

api_router = APIRouter(prefix="/v1")

api_router.include_router(models.router)
api_router.include_router(skills.router)
api_router.include_router(mcp.router)
