"""
配置管理模块
"""
from pydantic_settings import BaseSettings
from pydantic import Field
from typing import Optional


class Settings(BaseSettings):
    """应用配置"""
    
    # 应用配置
    app_name: str = "LLM Gateway"
    app_version: str = "0.1.0"
    debug: bool = False
    
    # 服务器配置
    host: str = "0.0.0.0"
    port: int = 8000
    
    # 数据库配置
    database_url: str = "sqlite:///./data/llm_gateway.db"
    database_echo: bool = False
    
    # Redis配置（可选，用于缓存）
    redis_url: Optional[str] = None
    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_password: Optional[str] = None
    redis_db: int = 0
    
    # OAuth2配置
    oauth2_issuer_url: str = "http://localhost:8080"
    oauth2_jwks_url: str = "http://localhost:8080/oauth2/jwks"
    oauth2_userinfo_url: str = "http://localhost:8080/api/userinfo"
    oauth2_algorithms: str = Field(default="RS256", description="JWT算法列表，支持逗号分隔或JSON数组格式")
    
    def get_oauth2_algorithms(self) -> list[str]:
        """解析算法列表，支持字符串和列表格式"""
        value = self.oauth2_algorithms
        if not value:
            return ["RS256"]
        # 如果是 JSON 格式的字符串
        if value.strip().startswith("["):
            import json
            try:
                return json.loads(value)
            except json.JSONDecodeError:
                pass
        # 逗号或空格分隔的字符串
        return [alg.strip() for alg in value.replace(",", " ").split() if alg.strip()]
    
    # JWT配置
    jwt_verify: bool = True
    jwt_verify_exp: bool = True
    
    # 配置缓存
    config_cache_ttl: int = 300  # 配置缓存时间（秒）
    
    # 日志配置
    log_level: str = "INFO"
    log_file: Optional[str] = None
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False


settings = Settings()
