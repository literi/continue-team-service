-- 大模型配置表
CREATE TABLE llm_model_config (
  id TEXT NOT NULL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  model_id TEXT NOT NULL,
  model_name TEXT NOT NULL,
  provider TEXT NOT NULL,
  provider_class TEXT NOT NULL,
  config_json TEXT NOT NULL,
  enabled INTEGER NOT NULL DEFAULT 1,
  max_tokens INTEGER DEFAULT 4096,
  timeout INTEGER DEFAULT 60,
  priority INTEGER DEFAULT 0,
  rate_limit INTEGER,
  created_by TEXT,
  create_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tenant_id, model_id)
);

-- 创建索引
CREATE INDEX idx_model_tenant ON llm_model_config(tenant_id);
CREATE INDEX idx_model_provider ON llm_model_config(provider);
CREATE INDEX idx_model_enabled ON llm_model_config(enabled);

-- 模型访问权限表（控制哪些角色可以使用哪些模型）
CREATE TABLE llm_model_permission (
  id TEXT NOT NULL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  model_id TEXT NOT NULL,
  role_id TEXT NOT NULL,
  create_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tenant_id, model_id, role_id),
  FOREIGN KEY (model_id) REFERENCES llm_model_config(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_model_perm_model ON llm_model_permission(model_id);
CREATE INDEX idx_model_perm_role ON llm_model_permission(role_id);

-- 创建触发器：自动更新update_time
CREATE TRIGGER update_llm_model_config_time 
AFTER UPDATE ON llm_model_config
BEGIN
  UPDATE llm_model_config SET update_time = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;
