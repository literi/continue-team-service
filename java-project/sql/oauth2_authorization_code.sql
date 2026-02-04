CREATE TABLE `oauth2_authorization_code` (
  `id` varchar(100) NOT NULL COMMENT '主键ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `code` varchar(100) NOT NULL COMMENT '授权码（唯一）',
  `client_id` varchar(100) NOT NULL COMMENT '客户端ID（关联oauth2_registered_client.client_id）',
  `principal_name` varchar(200) NOT NULL COMMENT '用户标识（sys_user.username）',
  `authorization_scopes` varchar(1000) DEFAULT NULL COMMENT '授权范围',
  `authorized_scopes` varchar(1000) DEFAULT NULL COMMENT '已授权范围',
  `attributes` varchar(4000) DEFAULT NULL COMMENT '附加属性（JSON）',
  `state` varchar(500) DEFAULT NULL COMMENT '状态值',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expires_at` timestamp NOT NULL COMMENT '过期时间',
  `consumed_at` timestamp NULL DEFAULT NULL COMMENT '消费时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_authorization_code` (`code`),
  KEY `idx_auth_code_tenant` (`tenant_id`),
  KEY `idx_auth_code_client` (`client_id`),
  KEY `idx_auth_code_principal` (`principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2授权码表';