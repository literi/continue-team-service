/**
 * Token 管理器 - 实现 OAuth 2.1 + JWT 令牌管理
 */

// 令牌类型定义
interface TokenInfo {
  accessToken: string;
  refreshToken?: string;
  tokenType?: string;
  expiresAt: number; // 访问令牌过期时间戳
  refreshTokenExpiresAt?: number; // 刷新令牌过期时间戳
}

/**
 * 获取访问令牌
 */
export const getAccessToken = (): string | null => {
  const token = sessionStorage.getItem("access_token");
  if (token) {
    return token;
  }
  // 兼容旧的 token 存储方式
  return sessionStorage.getItem("token");
};

/**
 * 获取刷新令牌
 */
export const getRefreshToken = (): string | null => {
  return sessionStorage.getItem("refresh_token");
};

/**
 * 存储令牌
 */
export const storeTokens = (accessToken: string, refreshToken?: string): void => {
  // 存储访问令牌
  sessionStorage.setItem("access_token", accessToken);
  
  // 如果提供了刷新令牌，则存储它
  if (refreshToken) {
    sessionStorage.setItem("refresh_token", refreshToken);
  }
  
  // 解析访问令牌的过期时间（假设访问令牌是JWT格式）
  try {
    const tokenParts = accessToken.split('.');
    if (tokenParts.length === 3) {
      const payload = JSON.parse(atob(tokenParts[1]));
      if (payload.exp) {
        // 设置访问令牌过期时间（提前1分钟刷新以避免边缘情况）
        const expiresAt = (payload.exp - 60) * 1000; // 转换为毫秒并提前1分钟
        sessionStorage.setItem("access_token_expires_at", expiresAt.toString());
      }
    }
  } catch (e) {
    // 如果不是JWT格式，设置默认过期时间（15分钟）
    const now = Date.now();
    sessionStorage.setItem("access_token_expires_at", (now + 14 * 60 * 1000).toString()); // 14分钟
  }
  
  // 如果提供了刷新令牌，设置其过期时间（默认24小时）
  if (refreshToken) {
    const now = Date.now();
    sessionStorage.setItem("refresh_token_expires_at", (now + 24 * 60 * 60 * 1000).toString());
  }
};

/**
 * 检查访问令牌是否即将过期（在5分钟内过期）
 */
export const isAccessTokenExpiringSoon = (): boolean => {
  const expiresAtStr = sessionStorage.getItem("access_token_expires_at");
  if (!expiresAtStr) {
    return false;
  }
  
  const expiresAt = parseInt(expiresAtStr);
  const now = Date.now();
  // 检查是否在5分钟内过期
  return expiresAt < (now + 5 * 60 * 1000);
};

/**
 * 检查刷新令牌是否已过期
 */
export const isRefreshTokenExpired = (): boolean => {
  const expiresAtStr = sessionStorage.getItem("refresh_token_expires_at");
  if (!expiresAtStr) {
    return true; // 如果没有刷新令牌过期时间，则认为已过期
  }
  
  const expiresAt = parseInt(expiresAtStr);
  const now = Date.now();
  return expiresAt < now;
};

/**
 * 检查访问令牌是否已过期
 */
export const isAccessTokenExpired = (): boolean => {
  const expiresAtStr = sessionStorage.getItem("access_token_expires_at");
  if (!expiresAtStr) {
    return true; // 如果没有过期时间，则认为已过期
  }
  
  const expiresAt = parseInt(expiresAtStr);
  const now = Date.now();
  return expiresAt < now;
};

/**
 * 清除所有令牌
 */
export const clearTokens = (): void => {
  sessionStorage.removeItem("access_token");
  sessionStorage.removeItem("refresh_token");
  sessionStorage.removeItem("access_token_expires_at");
  sessionStorage.removeItem("refresh_token_expires_at");
  // 保留旧的 token 字段以确保向后兼容
  sessionStorage.removeItem("token");
};

/**
 * 刷新访问令牌
 */
export const refreshAccessToken = async (): Promise<boolean> => {
  const refreshToken = getRefreshToken();
  
  if (!refreshToken) {
    console.warn('No refresh token available');
    return false;
  }
  
  if (isRefreshTokenExpired()) {
    console.warn('Refresh token has expired');
    clearTokens();
    return false;
  }
  
  try {
    const response = await fetch('/oauth2/token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: `grant_type=refresh_token&refresh_token=${encodeURIComponent(refreshToken)}&client_id=my-client-id`
    });
    
    if (response.ok) {
      const data = await response.json();
      
      if (data.access_token) {
        const newAccessToken = `${data.token_type || 'Bearer'} ${data.access_token}`;
        storeTokens(newAccessToken, data.refresh_token || refreshToken);
        
        return true;
      } else {
        console.error('Invalid refresh token response:', data);
        return false;
      }
    } else {
      console.error('Token refresh request failed:', response.status, response.statusText);
      return false;
    }
  } catch (error) {
    console.error('Error refreshing token:', error);
    return false;
  }
};