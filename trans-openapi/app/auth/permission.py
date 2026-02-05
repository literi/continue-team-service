"""
权限检查模块
"""
from typing import Dict, List, Optional
from sqlalchemy.orm import Session
from sqlalchemy import and_
from loguru import logger

from app.models import (
    LLMModelConfig, LLMModelPermission,
    LLMSkillConfig, LLMSkillPermission,
    LLMMCPConfig, LLMMCPPermission
)


class PermissionChecker:
    """权限检查器"""
    
    def __init__(self, db: Session):
        self.db = db
    
    async def check_permission(self, user: Dict, permission_code: str) -> bool:
        """
        检查用户是否有指定权限
        
        Args:
            user: 用户信息字典，包含roles和permissions
            permission_code: 权限代码（如：'llm:model:use'）
            
        Returns:
            是否有权限
        """
        # 管理员拥有所有权限
        if "admin" in user.get("roles", []):
            return True
        
        # 检查用户权限列表
        user_permissions = user.get("permissions", [])
        if permission_code in user_permissions:
            return True
        
        # 如果token中没有权限信息，尝试从数据库查询角色权限
        # 这里需要调用OAuth2服务查询角色对应的权限
        # 简化实现：假设权限已经在token的scope中
        return False
    
    async def check_model_access(self, user: Dict, model_id: str) -> bool:
        """
        检查用户是否可以访问指定模型
        
        Args:
            user: 用户信息字典
            model_id: 模型ID
            
        Returns:
            是否可以访问
        """
        tenant_id = user.get("tenant_id")
        if not tenant_id:
            return False
        
        # 管理员可以访问所有模型
        if "admin" in user.get("roles", []):
            return True
        
        # 检查模型权限表
        model = self.db.query(LLMModelConfig).filter(
            and_(
                LLMModelConfig.tenant_id == tenant_id,
                LLMModelConfig.model_id == model_id,
                LLMModelConfig.enabled == True
            )
        ).first()
        
        if not model:
            return False
        
        # 检查用户角色是否有权限
        user_roles = user.get("roles", [])
        if not user_roles:
            return False
        
        permission = self.db.query(LLMModelPermission).filter(
            and_(
                LLMModelPermission.tenant_id == tenant_id,
                LLMModelPermission.model_id == model.id,
                LLMModelPermission.role_id.in_(user_roles)
            )
        ).first()
        
        return permission is not None
    
    async def check_skill_access(self, user: Dict, skill_name: str) -> bool:
        """
        检查用户是否可以访问指定技能
        
        Args:
            user: 用户信息字典
            skill_name: 技能名称
            
        Returns:
            是否可以访问
        """
        tenant_id = user.get("tenant_id")
        if not tenant_id:
            return False
        
        # 管理员可以访问所有技能
        if "admin" in user.get("roles", []):
            return True
        
        # 检查技能权限表
        skill = self.db.query(LLMSkillConfig).filter(
            and_(
                LLMSkillConfig.tenant_id == tenant_id,
                LLMSkillConfig.skill_name == skill_name,
                LLMSkillConfig.enabled == True
            )
        ).first()
        
        if not skill:
            return False
        
        # 检查用户角色是否有权限
        user_roles = user.get("roles", [])
        if not user_roles:
            return False
        
        permission = self.db.query(LLMSkillPermission).filter(
            and_(
                LLMSkillPermission.tenant_id == tenant_id,
                LLMSkillPermission.skill_id == skill.id,
                LLMSkillPermission.role_id.in_(user_roles)
            )
        ).first()
        
        return permission is not None
    
    async def check_mcp_access(self, user: Dict, mcp_name: str) -> bool:
        """
        检查用户是否可以访问指定MCP服务
        
        Args:
            user: 用户信息字典
            mcp_name: MCP服务名称
            
        Returns:
            是否可以访问
        """
        tenant_id = user.get("tenant_id")
        if not tenant_id:
            return False
        
        # 管理员可以访问所有MCP服务
        if "admin" in user.get("roles", []):
            return True
        
        # 检查MCP权限表
        mcp = self.db.query(LLMMCPConfig).filter(
            and_(
                LLMMCPConfig.tenant_id == tenant_id,
                LLMMCPConfig.mcp_name == mcp_name,
                LLMMCPConfig.enabled == True
            )
        ).first()
        
        if not mcp:
            return False
        
        # 检查用户角色是否有权限
        user_roles = user.get("roles", [])
        if not user_roles:
            return False
        
        permission = self.db.query(LLMMCPPermission).filter(
            and_(
                LLMMCPPermission.tenant_id == tenant_id,
                LLMMCPPermission.mcp_id == mcp.id,
                LLMMCPPermission.role_id.in_(user_roles)
            )
        ).first()
        
        return permission is not None
