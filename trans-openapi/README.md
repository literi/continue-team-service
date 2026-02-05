# 可扩展的团队级大模型服务网关

基于 Python 和 FastAPI 实现的可扩展大模型服务网关，支持多模型提供者、Skill 技能、MCP 服务，并集成 OAuth2 认证和基于角色的权限控制。

## 功能特性

- ✅ **多模型支持**：支持 OpenAI、智谱、通义、本地模型等多种大模型
- ✅ **统一接口**：兼容 OpenAI Chat Completions API 格式
- ✅ **Skill 技能**：支持业务技能扩展（数据提取、文档总结、代码生成等）
- ✅ **MCP 服务**：支持 MCP（Model Context Protocol）服务扩展
- ✅ **OAuth2 认证**：集成现有 OAuth2 服务进行用户认证
- ✅ **角色权限**：基于角色的访问控制（RBAC），细粒度权限管理
- ✅ **数据库存储**：所有配置存储在数据库中，支持动态更新
- ✅ **多租户支持**：支持多租户隔离

## 项目结构

```
trans-openapi/
├── app/
│   ├── __init__.py
│   ├── main.py                 # FastAPI应用入口
│   ├── config.py               # 配置管理
│   ├── database.py             # 数据库连接
│   ├── models.py               # 数据库模型
│   ├── auth/
│   │   ├── oauth2.py           # OAuth2认证
│   │   ├── permission.py      # 权限检查
│   │   └── dependencies.py    # FastAPI依赖注入
│   ├── services/
│   │   └── config_service.py  # 配置服务
│   └── api/
│       └── v1/
│           ├── models.py       # 模型管理API
│           ├── skills.py       # Skill管理API
│           └── mcp.py          # MCP管理API
├── sql/                        # 数据库表结构（SQLite）
│   ├── llm_model_config.sql
│   ├── llm_skill_config.sql
│   ├── llm_mcp_config.sql
│   └── llm_permissions.sql
├── data/                       # SQLite数据库文件目录
├── requirements.txt
├── .env.example
├── init_db.py                  # 数据库初始化脚本
└── README.md
```

## 快速开始

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

**注意**：如果遇到 `ModuleNotFoundError: No module named '_sqlite3'` 错误，项目已包含 `pysqlite3` 作为 SQLite 的替代方案，安装依赖后会自动使用。

### 2. 配置环境变量

复制 `.env.example` 为 `.env` 并修改配置：

```bash
cp .env.example .env
```

主要配置项：

- `DATABASE_URL`: 数据库连接字符串（默认：`sqlite:///./data/llm_gateway.db`）
- `OAUTH2_ISSUER_URL`: OAuth2 服务地址（默认：http://localhost:8080）
- `OAUTH2_JWKS_URL`: JWKS 端点地址
- `OAUTH2_USERINFO_URL`: 用户信息端点地址

**注意**：SQLite 数据库文件会存储在 `data/llm_gateway.db`，确保 `data` 目录存在且有写权限。

### 3. 初始化数据库

#### 3.1 创建数据库目录

```bash
mkdir -p data
```

#### 3.2 初始化 SQLite 数据库

**方法一：使用 Python 脚本（推荐）**

```bash
python init_db.py
```

**方法二：使用 SQLite 命令行工具**

```bash
sqlite3 data/llm_gateway.db < sql/llm_model_config.sql
sqlite3 data/llm_gateway.db < sql/llm_skill_config.sql
sqlite3 data/llm_gateway.db < sql/llm_mcp_config.sql
```

#### 3.3 在 OAuth2 服务中定义权限

在 OAuth2 服务的数据库中执行权限定义（如果 OAuth2 服务使用 MySQL）：

```bash
mysql -u user -p oauth2_database < sql/llm_permissions.sql
```

**注意**：权限定义需要在 OAuth2 服务的数据库中执行，因为权限表（sys_permission）在 OAuth2 服务中。

### 4. 运行服务

```bash
python -m app.main
```

或使用 uvicorn：

```bash
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

## API 文档

启动服务后，访问以下地址查看 API 文档：

- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## 权限说明

### 权限代码

- `llm:model:view` - 查看模型列表
- `llm:model:use` - 使用模型
- `llm:model:manage` - 管理模型配置
- `llm:skill:view` - 查看技能列表
- `llm:skill:use` - 使用技能
- `llm:skill:manage` - 管理技能配置
- `llm:mcp:view` - 查看 MCP 服务列表
- `llm:mcp:use` - 使用 MCP 服务
- `llm:mcp:manage` - 管理 MCP 配置

### 角色定义

- **admin**: 管理员，拥有所有权限
- **developer**: 开发者，可以使用模型和技能，管理自己创建的配置
- **user**: 普通用户，只能使用已授权的模型和技能
- **viewer**: 只读用户，只能查看配置

## 使用示例

### 1. 获取模型列表

```bash
curl -X GET "http://localhost:8000/api/v1/models" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 2. 使用模型（Chat Completions）

```bash
curl -X POST "http://localhost:8000/api/v1/chat/completions" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "gpt-4",
    "messages": [
      {"role": "user", "content": "Hello!"}
    ]
  }'
```

### 3. 执行 Skill

```bash
curl -X POST "http://localhost:8000/api/v1/skills/data_extraction/execute" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "input": "提取以下文本中的关键信息...",
    "params": {}
  }'
```

## 扩展开发

### 添加新模型提供者

1. 在 `app/providers/` 创建新的提供者类，实现 `BaseModelProvider` 接口
2. 在数据库中插入模型配置记录
3. 无需修改核心代码

### 添加新 Skill

1. 在 `app/skills/` 创建新的 Skill 类，实现 `BaseSkill` 接口
2. 在数据库中插入 Skill 配置记录
3. 配置 Skill 的访问权限

### 添加新 MCP 服务

1. 在 `app/mcp/` 创建新的 MCP 服务类，实现 `BaseMCPService` 接口
2. 在数据库中插入 MCP 配置记录
3. 配置 MCP 服务的访问权限

## 注意事项

1. **OAuth2 服务**：确保 OAuth2 服务（端口 8080）正常运行，并且 JWT token 中包含用户角色和权限信息
2. **数据库连接**：SQLite 数据库文件存储在 `data/llm_gateway.db`，确保目录存在且有写权限。如需使用其他数据库，修改 `DATABASE_URL` 配置即可
3. **权限配置**：在 OAuth2 服务中为用户分配角色，并在网关数据库中配置资源访问权限
4. **敏感信息**：模型配置中的 API Key 等敏感信息存储在数据库中，注意数据库文件安全（SQLite 文件权限）
5. **SQLite 限制**：SQLite 适合开发和中小规模部署，生产环境如需高并发可考虑使用 PostgreSQL 或 MySQL

## 许可证

MIT License
