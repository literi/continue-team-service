"""
数据库模型定义
"""
from sqlalchemy import Column, String, Text, Integer, Boolean, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from app.database import Base


class LLMModelConfig(Base):
    """大模型配置表"""
    __tablename__ = "llm_model_config"
    
    id = Column(String(64), primary_key=True, comment="配置ID")
    tenant_id = Column(String(64), nullable=False, index=True, comment="租户ID")
    model_id = Column(String(100), nullable=False, comment="模型标识")
    model_name = Column(String(100), nullable=False, comment="模型显示名称")
    provider = Column(String(50), nullable=False, index=True, comment="提供者类型")
    provider_class = Column(String(200), nullable=False, comment="提供者实现类路径")
    config_json = Column(Text, nullable=False, comment="配置JSON")
    enabled = Column(Boolean, default=True, nullable=False, index=True, comment="是否启用")
    max_tokens = Column(Integer, default=4096, comment="最大token数")
    timeout = Column(Integer, default=60, comment="超时时间（秒）")
    priority = Column(Integer, default=0, comment="优先级")
    rate_limit = Column(Integer, comment="速率限制")
    created_by = Column(String(64), comment="创建人ID")
    create_time = Column(DateTime, default=datetime.now, nullable=False, comment="创建时间")
    update_time = Column(DateTime, default=datetime.now, onupdate=datetime.now, nullable=False, comment="更新时间")
    
    # 关系
    permissions = relationship("LLMModelPermission", back_populates="model", cascade="all, delete-orphan")


class LLMModelPermission(Base):
    """模型访问权限表"""
    __tablename__ = "llm_model_permission"
    
    id = Column(String(64), primary_key=True, comment="主键ID")
    tenant_id = Column(String(64), nullable=False, comment="租户ID")
    model_id = Column(String(64), ForeignKey("llm_model_config.id"), nullable=False, index=True, comment="模型配置ID")
    role_id = Column(String(64), nullable=False, index=True, comment="角色ID")
    create_time = Column(DateTime, default=datetime.now, nullable=False, comment="创建时间")
    
    # 关系
    model = relationship("LLMModelConfig", back_populates="permissions")


class LLMSkillConfig(Base):
    """Skill技能配置表"""
    __tablename__ = "llm_skill_config"
    
    id = Column(String(64), primary_key=True, comment="配置ID")
    tenant_id = Column(String(64), nullable=False, index=True, comment="租户ID")
    skill_name = Column(String(100), nullable=False, comment="技能名称")
    skill_display_name = Column(String(100), nullable=False, comment="技能显示名称")
    skill_class = Column(String(200), nullable=False, comment="技能实现类路径")
    description = Column(Text, comment="技能描述")
    model_id = Column(String(64), index=True, comment="默认使用的模型ID")
    config_json = Column(Text, comment="技能配置JSON")
    enabled = Column(Boolean, default=True, nullable=False, index=True, comment="是否启用")
    created_by = Column(String(64), comment="创建人ID")
    create_time = Column(DateTime, default=datetime.now, nullable=False, comment="创建时间")
    update_time = Column(DateTime, default=datetime.now, onupdate=datetime.now, nullable=False, comment="更新时间")
    
    # 关系
    permissions = relationship("LLMSkillPermission", back_populates="skill", cascade="all, delete-orphan")


class LLMSkillPermission(Base):
    """Skill访问权限表"""
    __tablename__ = "llm_skill_permission"
    
    id = Column(String(64), primary_key=True, comment="主键ID")
    tenant_id = Column(String(64), nullable=False, comment="租户ID")
    skill_id = Column(String(64), ForeignKey("llm_skill_config.id"), nullable=False, index=True, comment="技能配置ID")
    role_id = Column(String(64), nullable=False, index=True, comment="角色ID")
    create_time = Column(DateTime, default=datetime.now, nullable=False, comment="创建时间")
    
    # 关系
    skill = relationship("LLMSkillConfig", back_populates="permissions")


class LLMMCPConfig(Base):
    """MCP服务配置表"""
    __tablename__ = "llm_mcp_config"
    
    id = Column(String(64), primary_key=True, comment="配置ID")
    tenant_id = Column(String(64), nullable=False, index=True, comment="租户ID")
    mcp_name = Column(String(100), nullable=False, comment="MCP服务名称")
    mcp_display_name = Column(String(100), nullable=False, comment="MCP服务显示名称")
    mcp_class = Column(String(200), nullable=False, comment="MCP服务实现类路径")
    description = Column(Text, comment="服务描述")
    endpoint_url = Column(String(500), comment="MCP服务端点URL")
    config_json = Column(Text, comment="MCP配置JSON")
    enabled = Column(Boolean, default=True, nullable=False, index=True, comment="是否启用")
    created_by = Column(String(64), comment="创建人ID")
    create_time = Column(DateTime, default=datetime.now, nullable=False, comment="创建时间")
    update_time = Column(DateTime, default=datetime.now, onupdate=datetime.now, nullable=False, comment="更新时间")
    
    # 关系
    permissions = relationship("LLMMCPPermission", back_populates="mcp", cascade="all, delete-orphan")


class LLMMCPPermission(Base):
    """MCP访问权限表"""
    __tablename__ = "llm_mcp_permission"
    
    id = Column(String(64), primary_key=True, comment="主键ID")
    tenant_id = Column(String(64), nullable=False, comment="租户ID")
    mcp_id = Column(String(64), ForeignKey("llm_mcp_config.id"), nullable=False, index=True, comment="MCP配置ID")
    role_id = Column(String(64), nullable=False, index=True, comment="角色ID")
    create_time = Column(DateTime, default=datetime.now, nullable=False, comment="创建时间")
    
    # 关系
    mcp = relationship("LLMMCPConfig", back_populates="permissions")
