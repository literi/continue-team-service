CREATE TABLE `tenant` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '租户ID（主键）',
  `tenant_code` varchar(64) NOT NULL COMMENT '租户唯一编码（如企业简称）',
  `tenant_name` varchar(128) NOT NULL COMMENT '租户名称（如企业全称）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `expire_time` datetime DEFAULT NULL COMMENT '租户过期时间',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_code` (`tenant_code`) COMMENT '租户编码唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `tenant_id` bigint NOT NULL COMMENT '租户ID（关联tenant.id）',
  `username` varchar(64) NOT NULL COMMENT '用户名（租户内唯一）',
  `password` varchar(128) NOT NULL COMMENT '密码（加密存储，如BCrypt）',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_username` (`tenant_id`,`username`) COMMENT '租户内用户名唯一',
  KEY `idx_tenant_id` (`tenant_id`) COMMENT '租户ID索引，提升查询效率'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID（主键）',
  `tenant_id` bigint NOT NULL COMMENT '租户ID（关联tenant.id）',
  `role_code` varchar(64) NOT NULL COMMENT '角色编码（租户内唯一）',
  `role_name` varchar(128) NOT NULL COMMENT '角色名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `sort` int DEFAULT 0 COMMENT '排序值（用于前端展示）',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_role_code` (`tenant_id`,`role_code`) COMMENT '租户内角色编码唯一',
  KEY `idx_tenant_id` (`tenant_id`) COMMENT '租户ID索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID（主键）',
  `tenant_id` bigint DEFAULT 0 COMMENT '租户ID：0-系统公共权限，>0-租户独有权限',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父权限ID：0-顶级权限',
  `perm_code` varchar(128) NOT NULL COMMENT '权限编码（全局唯一，如：sys:user:add）',
  `perm_name` varchar(128) NOT NULL COMMENT '权限名称（如：用户新增）',
  `perm_type` tinyint NOT NULL COMMENT '权限类型：1-菜单，2-按钮/接口，3-数据权限',
  `resource_path` varchar(256) DEFAULT NULL COMMENT '资源路径（如菜单URL、接口路径）',
  `icon` varchar(128) DEFAULT NULL COMMENT '菜单图标（仅菜单类型）',
  `sort` int DEFAULT 0 COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`) COMMENT '权限编码全局唯一',
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（关联sys_user.id）',
  `role_id` bigint NOT NULL COMMENT '角色ID（关联sys_role.id）',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_user_role` (`tenant_id`,`user_id`,`role_id`) COMMENT '租户内用户-角色唯一',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID（关联sys_role.id）',
  `perm_id` bigint NOT NULL COMMENT '权限ID（关联sys_permission.id）',
  `creator` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_role_perm` (`tenant_id`,`role_id`,`perm_id`) COMMENT '租户内角色-权限唯一',
  KEY `idx_role_id` (`role_id`),
  KEY `idx_perm_id` (`perm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';


-- 租户1：企业A（租户ID=1）
INSERT INTO tenant (tenant_code, tenant_name, status, expire_time, remark)
VALUES ('company_a', 'A科技有限公司', 1, '2028-12-31 23:59:59', '测试租户A，主营电商系统');

-- 租户2：企业B（租户ID=2）
INSERT INTO tenant (tenant_code, tenant_name, status, expire_time, remark)
VALUES ('company_b', 'B物流有限公司', 1, '2028-12-31 23:59:59', '测试租户B，主营物流管理');

-- 租户1用户：admin（超级管理员，密码加密后为123456，实际应使用BCrypt加密）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', '13800138000', 'admin@companya.com', 1);

-- 租户1用户：user1（普通运营）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
VALUES (1, 'user1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', '13800138001', 'user1@companya.com', 1);

-- 租户2用户：admin（超级管理员）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
VALUES (2, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', '13900139000', 'admin@companyb.com', 1);

-- 租户2用户：user1（普通物流专员）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
VALUES (2, 'user1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', '13900139001', 'user1@companyb.com', 1);


-- 租户1角色：超级管理员（role_code=admin）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
VALUES (1, 'admin', '超级管理员', 1, 1, '租户1最高权限角色，拥有所有权限');

-- 租户1角色：运营专员（role_code=operator）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
VALUES (1, 'operator', '运营专员', 1, 2, '租户1普通运营角色，仅拥有基础操作权限');

-- 租户2角色：超级管理员（role_code=admin）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
VALUES (2, 'admin', '超级管理员', 1, 1, '租户2最高权限角色，拥有所有权限');

-- 租户2角色：物流专员（role_code=logistics）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
VALUES (2, 'logistics', '物流专员', 1, 2, '租户2物流操作角色，仅拥有物流相关权限');


-- 系统公共权限（tenant_id=0）
-- 基础菜单：系统管理
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (0, 0, 'sys:manage', '系统管理', 1, '/sys/manage', 1, 1);

-- 系统公共权限：用户管理菜单
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (0, 1, 'sys:user', '用户管理', 1, '/sys/user', 2, 1);

-- 系统公共权限：用户新增按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (0, 2, 'sys:user:add', '用户新增', 2, '/api/sys/user/add', 1, 1);

-- 系统公共权限：用户编辑按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (0, 2, 'sys:user:edit', '用户编辑', 2, '/api/sys/user/edit', 2, 1);

-- 系统公共权限：用户删除按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (0, 2, 'sys:user:delete', '用户删除', 2, '/api/sys/user/delete', 3, 1);

-- 租户1独有权限：商品管理菜单
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (1, 0, 'goods:manage', '商品管理', 1, '/goods/manage', 3, 1);

-- 租户1独有权限：商品上架按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (1, 6, 'goods:manage:online', '商品上架', 2, '/api/goods/online', 1, 1);

-- 租户2独有权限：物流订单管理菜单
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (2, 0, 'logistics:order', '物流订单管理', 1, '/logistics/order', 3, 1);

-- 租户2独有权限：物流订单分配按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
VALUES (2, 8, 'logistics:order:assign', '订单分配', 2, '/api/logistics/order/assign', 1, 1);

-- 租户1：admin用户绑定超级管理员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id) VALUES (1, 1, 1);

-- 租户1：user1用户绑定运营专员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id) VALUES (1, 2, 2);

-- 租户2：admin用户绑定超级管理员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id) VALUES (2, 3, 3);

-- 租户2：user1用户绑定物流专员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id) VALUES (2, 4, 4);



-- 租户1超级管理员：拥有所有系统公共权限+租户1独有权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id)
VALUES (1, 1, 1), (1, 1, 2), (1, 1, 3), (1, 1, 4), (1, 1, 5), (1, 1, 6), (1, 1, 7);

-- 租户1运营专员：仅拥有用户查看（隐式，无删除）+商品上架权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id)
VALUES (1, 2, 2), (1, 2, 3), (1, 2, 4), (1, 2, 6), (1, 2, 7);

-- 租户2超级管理员：拥有所有系统公共权限+租户2独有权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id)
VALUES (2, 3, 1), (2, 3, 2), (2, 3, 3), (2, 3, 4), (2, 3, 5), (2, 3, 8), (2, 3, 9);

-- 租户2物流专员：仅拥有用户查看+物流订单分配权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id)
VALUES (2, 4, 2), (2, 4, 3), (2, 4, 4), (2, 4, 8), (2, 4, 9);