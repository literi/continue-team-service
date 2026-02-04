CREATE TABLE `sys_role_permission` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `role_id` varchar(64) NOT NULL COMMENT '角色ID（关联sys_role.id）',
  `permission_id` varchar(64) NOT NULL COMMENT '权限ID（关联sys_permission.id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`tenant_id`,`role_id`,`permission_id`),
  KEY `idx_role_perm_role` (`role_id`),
  KEY `idx_role_perm_perm` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';