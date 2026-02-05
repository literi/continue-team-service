"""
Skill管理API
"""
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel

from app.database import get_db
from app.auth.dependencies import require_permission
from app.services.config_service import ConfigService
from app.auth.permission import PermissionChecker

router = APIRouter(prefix="/skills", tags=["skills"])


class SkillInfo(BaseModel):
    """Skill信息响应"""
    id: str
    skill_name: str
    skill_display_name: str
    description: Optional[str] = None
    enabled: bool


class SkillExecuteRequest(BaseModel):
    """Skill执行请求"""
    input: str
    params: dict = {}


@router.get("", response_model=List[SkillInfo])
async def list_skills(
    current_user: dict = Depends(require_permission("llm:skill:view")),
    db: Session = Depends(get_db)
):
    """
    列出所有可用的技能
    
    需要权限：llm:skill:view
    """
    tenant_id = current_user.get("tenant_id")
    if not tenant_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tenant ID not found"
        )
    
    config_service = ConfigService(db)
    skills = config_service.list_skill_configs(tenant_id, enabled_only=True)
    
    # 过滤用户有权限访问的技能
    checker = PermissionChecker(db)
    filtered_skills = []
    for skill in skills:
        if await checker.check_skill_access(current_user, skill["skill_name"]):
            filtered_skills.append(skill)
    
    return filtered_skills


@router.post("/{skill_name}/execute")
async def execute_skill(
    skill_name: str,
    request: SkillExecuteRequest,
    current_user: dict = Depends(require_permission("llm:skill:use")),
    db: Session = Depends(get_db)
):
    """
    执行技能
    
    需要权限：llm:skill:use
    """
    tenant_id = current_user.get("tenant_id")
    if not tenant_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tenant ID not found"
        )
    
    # 检查访问权限
    checker = PermissionChecker(db)
    if not await checker.check_skill_access(current_user, skill_name):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Access denied to skill: {skill_name}"
        )
    
    config_service = ConfigService(db)
    skill_config = config_service.get_skill_config(tenant_id, skill_name)
    
    if not skill_config:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Skill not found: {skill_name}"
        )
    
    # TODO: 实现技能执行逻辑
    # 1. 动态加载技能类
    # 2. 实例化技能
    # 3. 调用执行方法
    
    return {
        "skill_name": skill_name,
        "result": "Skill execution not implemented yet",
        "input": request.input,
        "params": request.params,
    }
