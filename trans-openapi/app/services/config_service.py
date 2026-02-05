"""
配置服务：从数据库读取和管理配置
"""
from typing import List, Optional, Dict
from sqlalchemy.orm import Session
from sqlalchemy import and_
import json
from loguru import logger

from app.models import (
    LLMModelConfig, LLMSkillConfig, LLMMCPConfig
)


class ConfigService:
    """配置服务"""
    
    def __init__(self, db: Session):
        self.db = db
    
    def get_model_config(self, tenant_id: str, model_id: str) -> Optional[Dict]:
        """
        获取模型配置
        
        Args:
            tenant_id: 租户ID
            model_id: 模型ID
            
        Returns:
            模型配置字典，如果不存在返回None
        """
        model = self.db.query(LLMModelConfig).filter(
            and_(
                LLMModelConfig.tenant_id == tenant_id,
                LLMModelConfig.model_id == model_id,
                LLMModelConfig.enabled == True
            )
        ).first()
        
        if not model:
            return None
        
        config = json.loads(model.config_json)
        return {
            "id": model.id,
            "model_id": model.model_id,
            "model_name": model.model_name,
            "provider": model.provider,
            "provider_class": model.provider_class,
            "config": config,
            "max_tokens": model.max_tokens,
            "timeout": model.timeout,
            "priority": model.priority,
            "rate_limit": model.rate_limit,
        }
    
    def list_model_configs(self, tenant_id: str, enabled_only: bool = True) -> List[Dict]:
        """
        列出所有模型配置
        
        Args:
            tenant_id: 租户ID
            enabled_only: 是否只返回启用的配置
            
        Returns:
            模型配置列表
        """
        query = self.db.query(LLMModelConfig).filter(
            LLMModelConfig.tenant_id == tenant_id
        )
        
        if enabled_only:
            query = query.filter(LLMModelConfig.enabled == True)
        
        models = query.order_by(LLMModelConfig.priority.desc()).all()
        
        result = []
        for model in models:
            config = json.loads(model.config_json)
            result.append({
                "id": model.id,
                "model_id": model.model_id,
                "model_name": model.model_name,
                "provider": model.provider,
                "enabled": model.enabled,
                "max_tokens": model.max_tokens,
                "priority": model.priority,
            })
        
        return result
    
    def get_skill_config(self, tenant_id: str, skill_name: str) -> Optional[Dict]:
        """
        获取技能配置
        
        Args:
            tenant_id: 租户ID
            skill_name: 技能名称
            
        Returns:
            技能配置字典，如果不存在返回None
        """
        skill = self.db.query(LLMSkillConfig).filter(
            and_(
                LLMSkillConfig.tenant_id == tenant_id,
                LLMSkillConfig.skill_name == skill_name,
                LLMSkillConfig.enabled == True
            )
        ).first()
        
        if not skill:
            return None
        
        config = json.loads(skill.config_json) if skill.config_json else {}
        return {
            "id": skill.id,
            "skill_name": skill.skill_name,
            "skill_display_name": skill.skill_display_name,
            "skill_class": skill.skill_class,
            "description": skill.description,
            "model_id": skill.model_id,
            "config": config,
        }
    
    def list_skill_configs(self, tenant_id: str, enabled_only: bool = True) -> List[Dict]:
        """
        列出所有技能配置
        
        Args:
            tenant_id: 租户ID
            enabled_only: 是否只返回启用的配置
            
        Returns:
            技能配置列表
        """
        query = self.db.query(LLMSkillConfig).filter(
            LLMSkillConfig.tenant_id == tenant_id
        )
        
        if enabled_only:
            query = query.filter(LLMSkillConfig.enabled == True)
        
        skills = query.all()
        
        result = []
        for skill in skills:
            result.append({
                "id": skill.id,
                "skill_name": skill.skill_name,
                "skill_display_name": skill.skill_display_name,
                "description": skill.description,
                "enabled": skill.enabled,
            })
        
        return result
    
    def get_mcp_config(self, tenant_id: str, mcp_name: str) -> Optional[Dict]:
        """
        获取MCP服务配置
        
        Args:
            tenant_id: 租户ID
            mcp_name: MCP服务名称
            
        Returns:
            MCP配置字典，如果不存在返回None
        """
        mcp = self.db.query(LLMMCPConfig).filter(
            and_(
                LLMMCPConfig.tenant_id == tenant_id,
                LLMMCPConfig.mcp_name == mcp_name,
                LLMMCPConfig.enabled == True
            )
        ).first()
        
        if not mcp:
            return None
        
        config = json.loads(mcp.config_json) if mcp.config_json else {}
        return {
            "id": mcp.id,
            "mcp_name": mcp.mcp_name,
            "mcp_display_name": mcp.mcp_display_name,
            "mcp_class": mcp.mcp_class,
            "description": mcp.description,
            "endpoint_url": mcp.endpoint_url,
            "config": config,
        }
    
    def list_mcp_configs(self, tenant_id: str, enabled_only: bool = True) -> List[Dict]:
        """
        列出所有MCP服务配置
        
        Args:
            tenant_id: 租户ID
            enabled_only: 是否只返回启用的配置
            
        Returns:
            MCP配置列表
        """
        query = self.db.query(LLMMCPConfig).filter(
            LLMMCPConfig.tenant_id == tenant_id
        )
        
        if enabled_only:
            query = query.filter(LLMMCPConfig.enabled == True)
        
        mcps = query.all()
        
        result = []
        for mcp in mcps:
            result.append({
                "id": mcp.id,
                "mcp_name": mcp.mcp_name,
                "mcp_display_name": mcp.mcp_display_name,
                "description": mcp.description,
                "enabled": mcp.enabled,
            })
        
        return result
