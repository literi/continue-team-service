"""
数据库连接和会话管理
"""
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker, Session
from contextlib import contextmanager
from typing import Generator
import os
import sys

from app.config import settings

# SQLite 模块回退：如果系统 sqlite3 不可用，使用 pysqlite3
if settings.database_url.startswith("sqlite"):
    try:
        import sqlite3
    except ImportError:
        # 如果系统 sqlite3 不可用，尝试使用 pysqlite3
        try:
            import pysqlite3 as sqlite3
            # 将 pysqlite3 注册为 sqlite3 的替代
            sys.modules['sqlite3'] = sqlite3
        except ImportError:
            raise ImportError(
                "SQLite support not available. Please install pysqlite3: "
                "pip install pysqlite3"
            )

# SQLite特定配置
connect_args = {}
if settings.database_url.startswith("sqlite"):
    # SQLite需要check_same_thread=False用于多线程
    connect_args = {"check_same_thread": False}
    # 确保数据库目录存在
    db_path = settings.database_url.replace("sqlite:///", "")
    if db_path != ":memory:":
        db_dir = os.path.dirname(db_path)
        if db_dir and not os.path.exists(db_dir):
            os.makedirs(db_dir, exist_ok=True)

# 创建数据库引擎
engine_kwargs = {
    "url": settings.database_url,
    "echo": settings.database_echo,
    "connect_args": connect_args,
}

# MySQL特定配置（如果不是SQLite）
if not settings.database_url.startswith("sqlite"):
    engine_kwargs.update({
        "pool_pre_ping": True,
        "pool_recycle": 3600,
    })

engine = create_engine(**engine_kwargs)

# 创建会话工厂
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# 声明基类
Base = declarative_base()


def get_db() -> Generator[Session, None, None]:
    """
    获取数据库会话（用于FastAPI依赖注入）
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


@contextmanager
def get_db_context() -> Generator[Session, None, None]:
    """
    获取数据库会话（用于上下文管理器）
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
