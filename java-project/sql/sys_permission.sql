CREATE TABLE `sys_permission` (
  `id` varchar(64) NOT NULL COMMENT '权限ID（主键）',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `perm_name` varchar(100) NOT NULL COMMENT '权限名称',
  `perm_code` varchar(100) NOT NULL COMMENT '权限编码（租户内唯一，如：sys:user:add）',
  `perm_type` tinyint(1) NOT NULL COMMENT '权限类型：1-菜单，2-按钮，3-接口',
  `resource_path` varchar(500) DEFAULT NULL COMMENT '资源路径（如：/api/user/add）',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方法（GET/POST/PUT/DELETE）',
  `parent_id` varchar(64) DEFAULT NULL COMMENT '父权限ID（用于菜单层级）',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_perm_code` (`tenant_id`,`perm_code`),
  KEY `idx_perm_tenant` (`tenant_id`),
  KEY `idx_perm_parent` (`parent_id`),
  KEY `idx_perm_type` (`perm_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';