#!/usr/bin/env python3
"""
初始化数据库脚本
"""
import os
from app.database import engine, Base
from app.models import (
    LLMModelConfig,
    LLMModelPermission,
    LLMSkillConfig,
    LLMSkillPermission,
    LLMMCPConfig,
    LLMMCPPermission,
)


def init_database():
    """初始化数据库，创建所有表"""
    # 确保data目录存在
    os.makedirs("data", exist_ok=True)
    
    # 创建所有表
    Base.metadata.create_all(bind=engine)
    print("✅ Database initialized successfully!")
    print(f"📁 Database file: {os.path.abspath('data/llm_gateway.db')}")


if __name__ == "__main__":
    init_database()
