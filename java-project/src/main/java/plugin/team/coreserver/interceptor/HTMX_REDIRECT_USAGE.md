# HTMX 过期跳转实现说明

## 概述

当用户会话过期或认证失败时，系统会自动检测是否是 HTMX 请求。如果是 HTMX 请求，会返回 `HX-Redirect` 头让 HTMX 客户端自动跳转到登录页面；如果是普通请求，则使用标准 HTTP 重定向。

## 实现组件

### 1. HtmxRedirectInterceptor

`AuthenticationEntryPoint` 实现，处理 Spring Security 的认证失败情况。

**功能：**
- 检测请求头中的 `HX-Request` 字段
- 如果是 HTMX 请求，设置 `HX-Redirect` 响应头
- 如果是普通请求，使用标准 `sendRedirect`

**使用位置：**
- 在 `Oauth2SecurityConfig` 中注册为 `authenticationEntryPoint`

### 2. HtmxResponseInterceptor

`HandlerInterceptor` 实现，在请求处理前检查认证状态。

**功能：**
- 在请求到达 Controller 前检查认证状态
- 对于未认证的 HTMX 请求，提前返回 `HX-Redirect` 头
- 避免请求到达 Controller 层

**使用位置：**
- 在 `WebMvcConfig` 中注册，拦截 `/admin/**` 路径

### 3. BaseController

Controller 基类，提供辅助方法。

**提供的方法：**
- `isHtmxRequest()` - 检查是否是 HTMX 请求
- `isAuthenticated()` - 检查用户是否已认证
- `checkAuthAndRedirect()` - 检查认证并设置重定向（如果需要）

## 工作流程

```
用户请求 (HTMX)
    ↓
Spring Security 过滤器链
    ↓
认证失败？
    ↓ 是
HtmxRedirectInterceptor
    ↓
检查 HX-Request 头
    ↓
是 HTMX 请求？
    ↓ 是
设置 HX-Redirect: /login
    ↓
HTMX 客户端接收响应
    ↓
自动跳转到 /login
```

## 响应头格式

### HTMX 请求（会话过期）

```
HTTP/1.1 401 Unauthorized
HX-Redirect: /login
```

### 普通请求（会话过期）

```
HTTP/1.1 302 Found
Location: /login
```

## 前端处理

HTMX 会自动处理 `HX-Redirect` 头，无需额外 JavaScript 代码：

```html
<!-- HTMX 会自动处理重定向 -->
<button hx-get="/admin/user/list" 
        hx-target="#contentContainer">
    加载用户列表
</button>
```

当会话过期时，HTMX 会自动跳转到 `/login` 页面。

## 测试场景

### 1. 正常请求（已认证）

```bash
curl -H "HX-Request: true" http://localhost:8080/admin/user/list
# 返回正常内容
```

### 2. 会话过期（HTMX 请求）

```bash
# 清除会话后
curl -H "HX-Request: true" http://localhost:8080/admin/user/list
# 返回: HX-Redirect: /login
```

### 3. 会话过期（普通请求）

```bash
# 清除会话后
curl http://localhost:8080/admin/user/list
# 返回: Location: /login (302)
```

## 配置说明

### SecurityConfig 配置

```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint(htmxRedirectInterceptor)
)
```

### WebMvcConfig 配置

```java
registry.addInterceptor(htmxResponseInterceptor)
    .addPathPatterns("/admin/**")
    .excludePathPatterns("/login", "/doLogin", "/css/**", "/js/**");
```

## 注意事项

1. **HTMX 请求头**：HTMX 会自动在请求中添加 `HX-Request: true` 头
2. **状态码**：HTMX 请求返回 401，普通请求返回 302
3. **路径排除**：登录相关路径不会被拦截器处理
4. **静态资源**：CSS、JS 等静态资源不需要认证检查

## 扩展使用

如果需要在 Controller 中手动检查认证：

```java
@Controller
@RequestMapping("/admin/user")
public class SysUserController extends BaseController {

    @GetMapping("/list")
    public String list(HttpServletRequest request, 
                      HttpServletResponse response,
                      Model model) {
        // 手动检查认证（可选）
        if (checkAuthAndRedirect(request, response)) {
            return null;  // 已设置重定向，返回 null
        }
        
        // 正常处理逻辑
        // ...
    }
}
```

## 相关文件

- `HtmxRedirectInterceptor.java` - 认证失败处理
- `HtmxResponseInterceptor.java` - 请求拦截器
- `BaseController.java` - Controller 基类
- `Oauth2SecurityConfig.java` - 安全配置
- `WebMvcConfig.java` - Web MVC 配置
