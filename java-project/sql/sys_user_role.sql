CREATE TABLE `sys_user_role` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID（关联sys_user.id）',
  `role_id` varchar(64) NOT NULL COMMENT '角色ID（关联sys_role.id）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`tenant_id`,`user_id`,`role_id`),
  KEY `idx_user_role_user` (`user_id`),
  KEY `idx_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';