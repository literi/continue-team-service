"""
OAuth2认证模块
"""
import httpx
import jwt
from typing import Optional, Dict, List
from fastapi import HTTPException, status
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from jwt import PyJWKClient
from loguru import logger

from app.config import settings


class OAuth2Auth:
    """OAuth2认证类"""
    
    def __init__(self):
        self.security = HTTPBearer()
        self.jwks_client: Optional[PyJWKClient] = None
        if settings.oauth2_jwks_url:
            try:
                self.jwks_client = PyJWKClient(settings.oauth2_jwks_url)
            except Exception as e:
                logger.warning(f"Failed to initialize JWKS client: {e}")
    
    async def verify_token(self, token: str) -> Dict:
        """
        验证JWT token并返回payload
        
        Args:
            token: JWT token字符串
            
        Returns:
            token的payload字典
            
        Raises:
            HTTPException: token无效时抛出401错误
        """
        try:
            # 获取签名密钥
            if not self.jwks_client:
                raise HTTPException(
                    status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                    detail="JWKS client not initialized"
                )
            
            signing_key = self.jwks_client.get_signing_key_from_jwt(token)
            
            # 验证token
            payload = jwt.decode(
                token,
                signing_key.key,
                algorithms=settings.get_oauth2_algorithms(),
                options={
                    "verify_signature": settings.jwt_verify,
                    "verify_exp": settings.jwt_verify_exp,
                }
            )
            
            return payload
            
        except jwt.ExpiredSignatureError:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token has expired"
            )
        except jwt.InvalidTokenError as e:
            logger.error(f"Invalid token: {e}")
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid token"
            )
        except Exception as e:
            logger.error(f"Token verification error: {e}")
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Token verification failed"
            )
    
    async def get_user_info(self, token: str) -> Dict:
        """
        从OAuth2服务获取用户信息
        
        Args:
            token: JWT token字符串
            
        Returns:
            用户信息字典
        """
        try:
            async with httpx.AsyncClient() as client:
                response = await client.get(
                    settings.oauth2_userinfo_url,
                    headers={"Authorization": f"Bearer {token}"},
                    timeout=5.0
                )
                response.raise_for_status()
                return response.json()
        except httpx.HTTPError as e:
            logger.error(f"Failed to get user info: {e}")
            # 如果获取用户信息失败，尝试从token中提取基本信息
            payload = await self.verify_token(token)
            return {
                "sub": payload.get("sub"),
                "name": payload.get("name"),
                "email": payload.get("email"),
            }
    
    async def get_user_roles(self, token: str) -> List[str]:
        """
        获取用户的角色列表
        
        Args:
            token: JWT token字符串
            
        Returns:
            角色ID列表
        """
        user_info = await self.get_user_info(token)
        # 从用户信息中提取角色，具体字段名需要根据OAuth2服务的实现调整
        roles = user_info.get("roles", [])
        if isinstance(roles, str):
            roles = [roles]
        return roles
    
    async def get_user_permissions(self, token: str) -> List[str]:
        """
        获取用户的权限列表
        
        Args:
            token: JWT token字符串
            
        Returns:
            权限代码列表（如：['llm:model:use', 'llm:skill:view']）
        """
        payload = await self.verify_token(token)
        # 从token的scope claim中提取权限
        scope = payload.get("scope", "")
        if isinstance(scope, str):
            return scope.split() if scope else []
        return scope if isinstance(scope, list) else []


# 全局OAuth2认证实例
oauth2_auth = OAuth2Auth()
