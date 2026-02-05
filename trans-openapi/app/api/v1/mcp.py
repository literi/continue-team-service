"""
MCP服务管理API
"""
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel

from app.database import get_db
from app.auth.dependencies import require_permission
from app.services.config_service import ConfigService
from app.auth.permission import PermissionChecker

router = APIRouter(prefix="/mcp", tags=["mcp"])


class MCPInfo(BaseModel):
    """MCP服务信息响应"""
    id: str
    mcp_name: str
    mcp_display_name: str
    description: Optional[str] = None
    enabled: bool


class MCPCallRequest(BaseModel):
    """MCP服务调用请求"""
    method: str
    params: dict = {}


@router.get("/services", response_model=List[MCPInfo])
async def list_mcp_services(
    current_user: dict = Depends(require_permission("llm:mcp:view")),
    db: Session = Depends(get_db)
):
    """
    列出所有可用的MCP服务
    
    需要权限：llm:mcp:view
    """
    tenant_id = current_user.get("tenant_id")
    if not tenant_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tenant ID not found"
        )
    
    config_service = ConfigService(db)
    mcps = config_service.list_mcp_configs(tenant_id, enabled_only=True)
    
    # 过滤用户有权限访问的MCP服务
    checker = PermissionChecker(db)
    filtered_mcps = []
    for mcp in mcps:
        if await checker.check_mcp_access(current_user, mcp["mcp_name"]):
            filtered_mcps.append(mcp)
    
    return filtered_mcps


@router.post("/{mcp_name}/call")
async def call_mcp_service(
    mcp_name: str,
    request: MCPCallRequest,
    current_user: dict = Depends(require_permission("llm:mcp:use")),
    db: Session = Depends(get_db)
):
    """
    调用MCP服务
    
    需要权限：llm:mcp:use
    """
    tenant_id = current_user.get("tenant_id")
    if not tenant_id:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Tenant ID not found"
        )
    
    # 检查访问权限
    checker = PermissionChecker(db)
    if not await checker.check_mcp_access(current_user, mcp_name):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail=f"Access denied to MCP service: {mcp_name}"
        )
    
    config_service = ConfigService(db)
    mcp_config = config_service.get_mcp_config(tenant_id, mcp_name)
    
    if not mcp_config:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"MCP service not found: {mcp_name}"
        )
    
    # TODO: 实现MCP服务调用逻辑
    # 1. 动态加载MCP服务类
    # 2. 实例化MCP服务
    # 3. 调用指定方法
    
    return {
        "mcp_name": mcp_name,
        "method": request.method,
        "result": "MCP service call not implemented yet",
        "params": request.params,
    }
