# 系统架构详细设计

## 1. 系统架构图
(此处应插入Mermaid图表或图片，描述整体架构)

## 2. 模块交互时序图

### 2.1 MCP会话创建流程
1. Client -> Gateway: 发起创建会话请求 (携带Token)
2. Gateway -> Auth Service: 验证Token & 权限
3. Gateway -> MCP Service: 转发请求
4. MCP Service -> Tenant Service: 校验租户配额
5. MCP Service -> DB: 创建会话记录
6. MCP Service -> Client: 返回Session ID

### 2.2 Skill执行流程
1. Client -> Gateway: 请求执行Skill
2. Gateway -> Skill Service: 转发请求
3. Skill Service -> Policy Engine: 检查白名单 & 权限
4. Skill Service -> Docker Sandbox: 调度容器执行
5. Docker Sandbox -> Skill Service: 返回执行结果
6. Skill Service -> Audit Log: 记录审计日志
7. Skill Service -> Client: 返回结果

## 3. 数据库设计 (ER图摘要)

### 租户表 (tenants)
- id (PK)
- name
- status
- created_at

### 项目表 (projects)
- id (PK)
- tenant_id (FK)
- name
- config_json

### 用户表 (users)
- id (PK)
- tenant_id (FK)
- username
- role

### 会话表 (sessions)
- id (PK)
- project_id (FK)
- user_id (FK)
- context_data (JSONB)
- created_at

## 4. API接口定义
- 详见Swagger文档 (待生成)
