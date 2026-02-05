"""
模型管理API
"""
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel, ConfigDict

from app.database import get_db
from app.auth.dependencies import get_current_user, require_permission
from app.services.config_service import ConfigService
from app.auth.permission import PermissionChecker

router = APIRouter(prefix="/models", tags=["models"])


class ModelInfo(BaseModel):
    """模型信息响应"""
    model_config = ConfigDict(protected_namespaces=())
    
    id: str
    model_id: str
    model_name: str
    provider: str
    enabled: bool
    max_tokens: Optional[int] = None
    priority: Optional[int] = None


@router.get("", response_model=List[ModelInfo])
async def list_models(
    current_user: dict = Depends(require_permission("llm:model:view")),
    db: Session = Depends(get_db)
):
    """
    列出所有可用的模型
    
    需要权限：llm:model:view
    """
    tenant_id = current_user.get("tenant_id")
    if not tenant_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tenant ID not found"
        )
    
    config_service = ConfigService(db)
    models = config_service.list_model_configs(tenant_id, enabled_only=True)
    
    # 过滤用户有权限访问的模型
    checker = PermissionChecker(db)
    filtered_models = []
    for model in models:
        if await checker.check_model_access(current_user, model["model_id"]):
            filtered_models.append(model)
    
    return filtered_models


@router.get("/{model_id}")
async def get_model(
    model_id: str,
    current_user: dict = Depends(require_permission("llm:model:view")),
    db: Session = Depends(get_db)
):
    """
    获取模型详情
    
    需要权限：llm:model:view
    """
    tenant_id = current_user.get("tenant_id")
    if not tenant_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tenant ID not found"
        )
    
    # 检查访问权限
    checker = PermissionChecker(db)
    if not await checker.check_model_access(current_user, model_id):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Access denied to model: {model_id}"
        )
    
    config_service = ConfigService(db)
    model_config = config_service.get_model_config(tenant_id, model_id)
    
    if not model_config:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Model not found: {model_id}"
        )
    
    return model_config
