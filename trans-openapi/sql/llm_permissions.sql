-- 网关权限定义（需要在OAuth2服务的sys_permission表中插入）
-- 这些权限码用于控制用户对不同资源的访问

-- 模型相关权限
INSERT INTO `sys_permission` (`id`, `tenant_id`, `perm_name`, `perm_code`, `perm_type`, `resource_path`, `method`, `parent_id`, `sort`, `status`) VALUES
('llm_perm_001', 'default', '查看模型列表', 'llm:model:view', 3, '/api/v1/models', 'GET', NULL, 1, 1),
('llm_perm_002', 'default', '使用模型', 'llm:model:use', 3, '/api/v1/chat/completions', 'POST', NULL, 2, 1),
('llm_perm_003', 'default', '管理模型配置', 'llm:model:manage', 3, '/api/v1/models', 'POST,PUT,DELETE', NULL, 3, 1),

-- Skill相关权限
('llm_perm_004', 'default', '查看技能列表', 'llm:skill:view', 3, '/api/v1/skills', 'GET', NULL, 4, 1),
('llm_perm_005', 'default', '使用技能', 'llm:skill:use', 3, '/api/v1/skills/*/execute', 'POST', NULL, 5, 1),
('llm_perm_006', 'default', '管理技能配置', 'llm:skill:manage', 3, '/api/v1/skills', 'POST,PUT,DELETE', NULL, 6, 1),

-- MCP相关权限
('llm_perm_007', 'default', '查看MCP服务列表', 'llm:mcp:view', 3, '/api/v1/mcp/services', 'GET', NULL, 7, 1),
('llm_perm_008', 'default', '使用MCP服务', 'llm:mcp:use', 3, '/api/v1/mcp/*/call', 'POST', NULL, 8, 1),
('llm_perm_009', 'default', '管理MCP配置', 'llm:mcp:manage', 3, '/api/v1/mcp', 'POST,PUT,DELETE', NULL, 9, 1);

-- 注意：tenant_id需要根据实际租户ID修改，perm_code是权限标识符，用于代码中检查权限
