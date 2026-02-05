# OAuth2 服务使用指南

## 概述

本项目实现了完整的 OAuth2 授权服务器和资源服务器，支持 OAuth2 2.0 和 OpenID Connect 1.0 协议。

## 架构说明

### 1. 授权服务器（Authorization Server）
- **端点**: `/oauth2/authorize`, `/oauth2/token`, `/oauth2/jwks` 等
- **功能**: 处理客户端注册、用户授权、令牌颁发
- **配置类**: `OAuth2AuthorizationServerConfig`

### 2. 资源服务器（Resource Server）
- **端点**: `/api/**`
- **功能**: 验证访问令牌，保护 API 资源
- **配置类**: `OAuth2ResourceServerConfig`

### 3. 数据存储
- **客户端注册**: `oauth2_registered_client` 表
- **授权记录**: `oauth2_authorization` 表
- **授权码**: `oauth2_authorization_code` 表
- **授权同意**: `oauth2_authorization_consent` 表

## OAuth2 端点

### 授权端点
```
GET /oauth2/authorize?client_id={clientId}&response_type=code&redirect_uri={redirectUri}&scope={scopes}&state={state}
```

### 令牌端点
```
POST /oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code
&code={authorization_code}
&redirect_uri={redirect_uri}
&client_id={client_id}
&client_secret={client_secret}
```

### JWK Set 端点
```
GET /oauth2/jwks
```

### 用户信息端点（OIDC）
```
GET /userinfo
Authorization: Bearer {access_token}
```

## 客户端管理

### 创建客户端

1. 访问管理后台：`/index#/admin/oauth2/client/list`
2. 点击"新增客户端"
3. 填写客户端信息：
   - **客户端ID**: 唯一标识（如：`my-client`）
   - **客户端密钥**: 可选（如：`secret`）
   - **客户端名称**: 显示名称
   - **客户端认证方法**: `client_secret_basic` 或 `client_secret_post`
   - **授权类型**: `authorization_code`, `refresh_token` 等
   - **重定向URI**: 回调地址（如：`http://localhost:3000/callback`）
   - **作用域**: `openid`, `profile`, `email` 等

### 客户端设置示例

**客户端认证方法**:
```
client_secret_basic
```

**授权类型**:
```
authorization_code,refresh_token
```

**重定向URI**:
```
http://localhost:3000/callback,http://localhost:3000/logout
```

**作用域**:
```
openid,profile,email
```

**客户端设置（JSON）**:
```json
{}
```

**令牌设置（JSON）**:
```json
{
  "accessTokenTimeToLive": 3600,
  "refreshTokenTimeToLive": 86400
}
```

## OAuth2 授权流程

### 授权码流程（Authorization Code Flow）

1. **用户授权请求**
   ```
   GET /oauth2/authorize?client_id=my-client&response_type=code&redirect_uri=http://localhost:3000/callback&scope=openid profile&state=xyz
   ```

2. **用户登录并授权**
   - 如果未登录，重定向到 `/login`
   - 登录后显示授权同意页面
   - 用户同意后，重定向到客户端回调地址，带上授权码

3. **交换访问令牌**
   ```
   POST /oauth2/token
   grant_type=authorization_code
   &code={authorization_code}
   &redirect_uri=http://localhost:3000/callback
   &client_id=my-client
   &client_secret=secret
   ```

4. **响应**
   ```json
   {
     "access_token": "...",
     "token_type": "Bearer",
     "expires_in": 3600,
     "refresh_token": "...",
     "scope": "openid profile"
   }
   ```

### 刷新令牌流程

```
POST /oauth2/token
grant_type=refresh_token
&refresh_token={refresh_token}
&client_id=my-client
&client_secret=secret
```

## 资源服务器使用

### 保护 API 端点

在需要保护的 Controller 方法上，资源服务器会自动验证 JWT 令牌：

```java
@RestController
@RequestMapping("/api")
public class ApiController {
    
    @GetMapping("/userinfo")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        // jwt.getClaim("sub") - 用户标识
        // jwt.getClaim("scope") - 作用域
        return Map.of(
            "sub", jwt.getClaim("sub"),
            "name", jwt.getClaim("name"),
            "email", jwt.getClaim("email")
        );
    }
}
```

### 访问受保护的 API

```
GET /api/userinfo
Authorization: Bearer {access_token}
```

## 配置说明

### 授权服务器设置

在 `OAuth2AuthorizationServerConfig` 中配置：
- **发行者**: `http://localhost:8080`
- **端点路径**: `/oauth2/authorize`, `/oauth2/token` 等
- **JWK Set**: 自动生成 RSA 密钥对

### 安全过滤器链顺序

1. **Order(1)**: OAuth2 授权服务器（`OAuth2AuthorizationServerConfig`）
2. **Order(2)**: 应用安全配置（`Oauth2SecurityConfig`）
3. **默认**: 资源服务器（`OAuth2ResourceServerConfig`）

## 数据库表结构

### oauth2_registered_client
存储客户端注册信息

### oauth2_authorization
存储授权记录和令牌

### oauth2_authorization_code
存储授权码（可选，也可存储在 oauth2_authorization 中）

### oauth2_authorization_consent
存储用户授权同意记录

## 测试示例

### 1. 创建测试客户端

通过管理后台创建客户端：
- 客户端ID: `test-client`
- 客户端密钥: `test-secret`
- 授权类型: `authorization_code,refresh_token`
- 重定向URI: `http://localhost:3000/callback`
- 作用域: `openid,profile,email`

### 2. 获取授权码

```
http://localhost:8080/oauth2/authorize?client_id=test-client&response_type=code&redirect_uri=http://localhost:3000/callback&scope=openid profile&state=xyz
```

### 3. 交换访问令牌

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "code={authorization_code}" \
  -d "redirect_uri=http://localhost:3000/callback" \
  -d "client_id=test-client" \
  -d "client_secret=test-secret"
```

### 4. 访问受保护的资源

```bash
curl http://localhost:8080/api/userinfo \
  -H "Authorization: Bearer {access_token}"
```

## 注意事项

1. **生产环境**: 
   - 使用固定的 RSA 密钥对（不要每次启动生成新的）
   - 配置 HTTPS
   - 设置合适的令牌过期时间

2. **客户端密钥**: 
   - 生产环境应使用强密钥
   - 建议使用 BCrypt 加密存储

3. **重定向URI**: 
   - 必须与注册时的一致
   - 建议使用 HTTPS

4. **作用域**: 
   - `openid` - OpenID Connect 必需
   - `profile` - 用户基本信息
   - `email` - 用户邮箱

## 相关文件

- `OAuth2AuthorizationServerConfig.java` - 授权服务器配置
- `OAuth2ResourceServerConfig.java` - 资源服务器配置
- `JpaRegisteredClientRepository.java` - 客户端仓库实现
- `JpaOAuth2AuthorizationService.java` - 授权服务实现
- `JpaOAuth2AuthorizationConsentService.java` - 授权同意服务实现
- `Oauth2ClientController.java` - 客户端管理控制器
- `oauth2-client-list.html` - 客户端列表页面
- `oauth2-client-form.html` - 客户端表单页面
