-- Skill技能配置表
CREATE TABLE llm_skill_config (
  id TEXT NOT NULL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  skill_name TEXT NOT NULL,
  skill_display_name TEXT NOT NULL,
  skill_class TEXT NOT NULL,
  description TEXT,
  model_id TEXT,
  config_json TEXT,
  enabled INTEGER NOT NULL DEFAULT 1,
  created_by TEXT,
  create_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tenant_id, skill_name)
);

-- 创建索引
CREATE INDEX idx_skill_tenant ON llm_skill_config(tenant_id);
CREATE INDEX idx_skill_enabled ON llm_skill_config(enabled);
CREATE INDEX idx_skill_model ON llm_skill_config(model_id);

-- Skill访问权限表（控制哪些角色可以使用哪些技能）
CREATE TABLE llm_skill_permission (
  id TEXT NOT NULL PRIMARY KEY,
  tenant_id TEXT NOT NULL,
  skill_id TEXT NOT NULL,
  role_id TEXT NOT NULL,
  create_time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(tenant_id, skill_id, role_id),
  FOREIGN KEY (skill_id) REFERENCES llm_skill_config(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_skill_perm_skill ON llm_skill_permission(skill_id);
CREATE INDEX idx_skill_perm_role ON llm_skill_permission(role_id);

-- 创建触发器：自动更新update_time
CREATE TRIGGER update_llm_skill_config_time 
AFTER UPDATE ON llm_skill_config
BEGIN
  UPDATE llm_skill_config SET update_time = CURRENT_TIMESTAMP WHERE id = NEW.id;
END;
