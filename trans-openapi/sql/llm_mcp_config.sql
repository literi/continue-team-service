-- MCP服务配置表
CREATE TABLE llm_mcp_config (
  id TEXT NOT NULL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  mcp_name TEXT NOT NULL,
  mcp_display_name TEXT NOT NULL,
  mcp_class TEXT NOT NULL,
  description TEXT,
  endpoint_url TEXT,
  config_json TEXT,
  enabled INTEGER NOT NULL DEFAULT 1,
  created_by TEXT,
  create_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tenant_id, mcp_name)
);

-- 创建索引
CREATE INDEX idx_mcp_tenant ON llm_mcp_config(tenant_id);
CREATE INDEX idx_mcp_enabled ON llm_mcp_config(enabled);

-- MCP访问权限表（控制哪些角色可以使用哪些MCP服务）
CREATE TABLE llm_mcp_permission (
  id TEXT NOT NULL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  mcp_id TEXT NOT NULL,
  role_id TEXT NOT NULL,
  create_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tenant_id, mcp_id, role_id),
  FOREIGN KEY (mcp_id) REFERENCES llm_mcp_config(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_mcp_perm_mcp ON llm_mcp_permission(mcp_id);
CREATE INDEX idx_mcp_perm_role ON llm_mcp_permission(role_id);

-- 创建触发器：自动更新update_time
CREATE TRIGGER update_llm_mcp_config_time 
AFTER UPDATE ON llm_mcp_config
BEGIN
  UPDATE llm_mcp_config SET update_time = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;
