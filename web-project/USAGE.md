# OAuth 2.1 + PKCE + JWT 使用文档

## 系统概述

本系统实现了现代化的OAuth 2.1认证授权框架，结合PKCE（Proof Key for Code Exchange）增强公共客户端安全性，并使用JWT（JSON Web Token）作为访问令牌。

## 核心特性

- **OAuth 2.1 授权码流程**：支持标准的授权码授权流程
- **PKCE 支持**：防止中间人攻击，适用于公共客户端（如浏览器应用）
- **JWT 令牌**：包含租户ID、用户ID、权限范围等信息
- **多租户架构**：支持多租户场景下的权限隔离
- **访问令牌 + 刷新令牌**：短期访问令牌（15分钟）+ 长期刷新令牌（24小时）
- **令牌自动刷新**：前端实现智能令牌刷新机制，提升用户体验
- **安全退出**：支持令牌撤销和会话清理

## API 端点

### 1. OAuth 2.1 端点

#### 客户端注册
```
POST /oauth2/register
```

参数：
- `clientId` - 客户端ID
- `clientName` - 客户端名称
- `clientSecret` - 客户端密钥
- `grantTypes` - 授权类型数组（如 `["authorization_code", "refresh_token"]`）
- `redirectUris` - 重定向URI数组
- `scopes` - 权限范围数组（如 `["read", "write"]`）

#### 授权码获取
```
GET /oauth2/authorize
```

参数：
- `response_type` - 必须为 `code`
- `client_id` - 客户端ID
- `redirect_uri` - 重定向URI
- `state` - 状态参数（推荐使用）
- `scope` - 权限范围
- `code_challenge` - PKCE挑战码（公共客户端必需）
- `code_challenge_method` - PKCE方法（S256或plain）

#### 令牌获取
```
POST /oauth2/token
```

参数（授权码模式）：
- `grant_type` - `authorization_code`
- `code` - 授权码
- `redirect_uri` - 重定向URI
- `client_id` - 客户端ID
- `client_secret` - 客户端密钥（私密客户端）或留空（公共客户端）
- `code_verifier` - PKCE验证器（公共客户端必需）

参数（刷新令牌模式）：
- `grant_type` - `refresh_token`
- `refresh_token` - 刷新令牌
- `client_id` - 客户端ID

#### 令牌撤销
```
POST /oauth2/revoke
```

参数：
- `token` - 要撤销的令牌
- `token_type_hint` - 令牌类型提示（可选）

### 2. 传统认证端点

#### 用户登录
```
POST /api/v1/auth/login
```

请求体：
```json
{
  "username": "用户名",
  "password": "密码",
  "captcha": "验证码",
  "uuid": "验证码UUID"
}
```

#### 获取验证码
```
GET /api/v1/auth/captcha
```

#### 获取当前用户信息
```
GET /api/v1/auth/me
```

需要在请求头中包含：
```
Authorization: Bearer {access_token}
```

## 使用示例

### 1. 公共客户端（如浏览器应用）使用PKCE流程

#### 步骤1：生成PKCE参数
```javascript
// 生成code_verifier（推荐43-128个字符）
const codeVerifier = generateRandomString(64);

// 生成code_challenge（SHA256哈希）
const encoder = new TextEncoder();
const data = encoder.encode(codeVerifier);
const hashed = await crypto.subtle.digest('SHA-256', data);
const codeChallenge = base64URLEncode(hashed);
```

#### 步骤2：发起授权请求
```javascript
const authUrl = `/oauth2/authorize?response_type=code&client_id=my-client-id&redirect_uri=http://localhost:3000/callback&state=random-state&scope=read+write&code_challenge=${codeChallenge}&code_challenge_method=S256`;

// 重定向到授权服务器
window.location.href = authUrl;
```

#### 步骤3：获取令牌
```javascript
// 用户授权后，将重定向到回调地址，带有code参数
const urlParams = new URLSearchParams(window.location.search);
const code = urlParams.get('code');

// 使用code换取令牌
const response = await fetch('/oauth2/token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: `grant_type=authorization_code&code=${code}&redirect_uri=http://localhost:3000/callback&client_id=my-client-id&code_verifier=${codeVerifier}`
});

const tokens = await response.json();
```

### 2. 私密客户端（如后端服务）使用传统流程

#### 直接获取令牌
```javascript
const response = await fetch('/oauth2/token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
    'Authorization': 'Basic ' + btoa(clientId + ':' + clientSecret)
  },
  body: 'grant_type=client_credentials&scope=read+write'
});
```

## JWT 令牌结构

访问令牌（JWT）包含以下声明：

- `userId`: 用户ID
- `sub`: 用户主体（用户名）
- `tenantId`: 租户ID
- `role`: 用户角色
- `scope`: 权限范围（空格分隔的字符串）
- `exp`: 过期时间戳
- `iat`: 签发时间戳

## 注意事项

### 1. 安全注意事项

- **PKCE 必要性**：公共客户端（如浏览器应用）必须使用PKCE来防止授权码拦截攻击
- **密钥安全**：私密客户端的client_secret必须安全存储，不能暴露给前端
- **HTTPS 强制**：生产环境中必须使用HTTPS来保护令牌传输
- **令牌存储**：访问令牌应存储在内存中，避免持久化；刷新令牌可安全存储
- **令牌撤销**：及时撤销不再需要的令牌以减少安全风险

### 2. 配置注意事项

- **令牌有效期**：访问令牌默认15分钟，刷新令牌默认24小时，可根据需要调整
- **客户端注册**：所有客户端必须预先在系统中注册
- **重定向URI**：必须准确配置，防止开放重定向漏洞
- **权限范围**：合理定义和使用权限范围，遵循最小权限原则

### 3. 性能和维护

- **令牌刷新**：客户端应实现自动令牌刷新逻辑，在访问令牌即将过期前使用刷新令牌获取新令牌
- **缓存策略**：合理缓存令牌和用户信息以提高性能
- **监控告警**：监控认证失败率、令牌刷新频率等指标
- **定期轮换**：定期轮换JWT密钥以增强安全性

### 4. 故障排除

- **401 错误**：令牌无效或已过期，需要重新认证或刷新令牌
- **403 错误**：令牌有效但权限不足，检查权限范围
- **授权服务器错误**：检查客户端配置、重定向URI和PKCE参数
- **令牌解析失败**：检查JWT密钥配置和算法兼容性

## 配置文件说明

### application.properties 相关配置

```properties
# OAuth2 配置
oauth2.access-token-expiration=900  # 访问令牌有效期（秒，默认15分钟）
oauth2.refresh-token-expiration=86400  # 刷新令牌有效期（秒，默认24小时）

# 安全配置白名单
security.permit-all-paths[0]=/api/v1/auth/login
security.permit-all-paths[1]=/api/v1/auth/captcha
security.permit-all-paths[2]=/api/v1/auth/register
security.permit-all-paths[3]=/oauth2/**
security.permit-all-paths[4]=/swagger-ui/**
security.permit-all-paths[5]=/v3/api-docs/**
security.permit-all-paths[6]=/swagger-resources/**
security.permit-all-paths[7]=/webjars/**
```

## 数据库表结构

系统包含以下OAuth 2.1相关表：

- `oauth2_registered_client`：注册客户端信息
- `oauth2_authorization`：授权信息（授权码、访问令牌、刷新令牌等）

## 常见问题

1. **为什么需要PKCE？**
   PKCE（Proof Key for Code Exchange）是为了防止授权码拦截攻击而设计的，特别适用于公共客户端（如单页应用、移动应用）。

2. **访问令牌和刷新令牌的区别？**
   访问令牌用于访问受保护资源，有效期较短（15分钟）；刷新令牌用于获取新的访问令牌，有效期较长（24小时）。

3. **如何实现自动令牌刷新？**
   客户端应在访问令牌即将过期前使用刷新令牌获取新令牌，建议在原令牌剩余1-2分钟时提前刷新。

4. **如何处理多租户权限？**
   JWT令牌中包含tenantId，服务端可根据此字段实现租户隔离和权限控制。