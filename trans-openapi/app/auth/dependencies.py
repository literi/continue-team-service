"""
FastAPI依赖注入模块
"""
from typing import Optional, Dict, List
from fastapi import Depends, HTTPException, status
from fastapi.security import HTTPAuthorizationCredentials
from sqlalchemy.orm import Session

from app.auth.oauth2 import oauth2_auth
from app.database import get_db
from app.auth.permission import PermissionChecker


async def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(oauth2_auth.security)
) -> Dict:
    """
    获取当前用户信息（FastAPI依赖）
    
    Returns:
        用户信息字典，包含：sub, name, email, roles, permissions
    """
    token = credentials.credentials
    payload = await oauth2_auth.verify_token(token)
    user_info = await oauth2_auth.get_user_info(token)
    roles = await oauth2_auth.get_user_roles(token)
    permissions = await oauth2_auth.get_user_permissions(token)
    
    return {
        "sub": payload.get("sub") or user_info.get("sub"),
        "name": user_info.get("name"),
        "email": user_info.get("email"),
        "tenant_id": payload.get("tenant_id") or user_info.get("tenant_id"),
        "roles": roles,
        "permissions": permissions,
        "token": token,
    }


async def get_current_user_optional(
    credentials: Optional[HTTPAuthorizationCredentials] = Depends(oauth2_auth.security)
) -> Optional[Dict]:
    """
    获取当前用户信息（可选，用于公开接口）
    """
    if not credentials:
        return None
    try:
        return await get_current_user(credentials)
    except HTTPException:
        return None


def require_permission(permission_code: str):
    """
    权限检查装饰器工厂
    
    Args:
        permission_code: 权限代码（如：'llm:model:use'）
        
    Returns:
        依赖函数
    """
    async def permission_dependency(
        current_user: Dict = Depends(get_current_user),
        db: Session = Depends(get_db)
    ) -> Dict:
        checker = PermissionChecker(db)
        if not await checker.check_permission(current_user, permission_code):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail=f"Permission denied: {permission_code}"
            )
        return current_user
    
    return permission_dependency


def require_any_permission(*permission_codes: str):
    """
    检查是否有任一权限
    
    Args:
        *permission_codes: 权限代码列表
        
    Returns:
        依赖函数
    """
    async def permission_dependency(
        current_user: Dict = Depends(get_current_user),
        db: Session = Depends(get_db)
    ) -> Dict:
        checker = PermissionChecker(db)
        for code in permission_codes:
            if await checker.check_permission(current_user, code):
                return current_user
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Permission denied: requires one of {permission_codes}"
        )
    
    return permission_dependency


def require_all_permissions(*permission_codes: str):
    """
    检查是否有所有权限
    
    Args:
        *permission_codes: 权限代码列表
        
    Returns:
        依赖函数
    """
    async def permission_dependency(
        current_user: Dict = Depends(get_current_user),
        db: Session = Depends(get_db)
    ) -> Dict:
        checker = PermissionChecker(db)
        for code in permission_codes:
            if not await checker.check_permission(current_user, code):
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail=f"Permission denied: requires {code}"
                )
        return current_user
    
    return permission_dependency
