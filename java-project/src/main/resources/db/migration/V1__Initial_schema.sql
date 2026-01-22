-- 创建租户表
CREATE TABLE IF NOT EXISTS tenant (
  id BIGSERIAL PRIMARY KEY,
  tenant_code VARCHAR(64) NOT NULL UNIQUE,
  tenant_name VARCHAR(128) NOT NULL,
  status INTEGER NOT NULL DEFAULT 1,
  expire_time TIMESTAMP DEFAULT NULL,
  creator VARCHAR(64) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT NULL,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(512) DEFAULT NULL
);

-- 添加注释（可选）
COMMENT ON TABLE tenant IS '租户表';
COMMENT ON COLUMN tenant.tenant_code IS '租户唯一编码（如企业简称）';
COMMENT ON COLUMN tenant.tenant_name IS '租户名称（如企业全称）';
COMMENT ON COLUMN tenant.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN tenant.expire_time IS '租户过期时间';
COMMENT ON COLUMN tenant.creator IS '创建人';
COMMENT ON COLUMN tenant.create_time IS '创建时间';
COMMENT ON COLUMN tenant.updater IS '更新人';
COMMENT ON COLUMN tenant.update_time IS '更新时间';
COMMENT ON COLUMN tenant.remark IS '备注';

-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  real_name VARCHAR(64) DEFAULT NULL,
  mobile VARCHAR(20) DEFAULT NULL,
  email VARCHAR(128) DEFAULT NULL,
  status INTEGER NOT NULL DEFAULT 1,
  creator VARCHAR(64) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT NULL,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

-- 添加注释
COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.tenant_id IS '租户ID（关联tenant.id）';
COMMENT ON COLUMN sys_user.username IS '用户名（租户内唯一）';
COMMENT ON COLUMN sys_user.password IS '密码（加密存储，如BCrypt）';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.mobile IS '手机号';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN sys_user.creator IS '创建人';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.updater IS '更新人';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';

-- 创建角色表
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  role_code VARCHAR(64) NOT NULL,
  role_name VARCHAR(128) NOT NULL,
  status INTEGER NOT NULL DEFAULT 1,
  sort INTEGER DEFAULT 0,
  creator VARCHAR(64) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT NULL,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(512) DEFAULT NULL,
  FOREIGN KEY (tenant_id) REFERENCES tenant(id),
  UNIQUE (tenant_id, role_code)
);

-- 添加注释
COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.tenant_id IS '租户ID（关联tenant.id）';
COMMENT ON COLUMN sys_role.role_code IS '角色编码（租户内唯一）';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN sys_role.sort IS '排序值（用于前端展示）';
COMMENT ON COLUMN sys_role.creator IS '创建人';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.updater IS '更新人';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.remark IS '备注';

-- 创建权限表
CREATE TABLE IF NOT EXISTS sys_permission (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT DEFAULT 0,
  parent_id BIGINT NOT NULL DEFAULT 0,
  perm_code VARCHAR(128) NOT NULL UNIQUE,
  perm_name VARCHAR(128) NOT NULL,
  perm_type INTEGER NOT NULL,
  resource_path VARCHAR(256) DEFAULT NULL,
  icon VARCHAR(128) DEFAULT NULL,
  sort INTEGER DEFAULT 0,
  status INTEGER NOT NULL DEFAULT 1,
  creator VARCHAR(64) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT NULL,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释
COMMENT ON TABLE sys_permission IS '权限表';
COMMENT ON COLUMN sys_permission.tenant_id IS '租户ID：0-系统公共权限，>0-租户独有权限';
COMMENT ON COLUMN sys_permission.parent_id IS '父权限ID：0-顶级权限';
COMMENT ON COLUMN sys_permission.perm_code IS '权限编码（全局唯一，如：sys:user:add）';
COMMENT ON COLUMN sys_permission.perm_name IS '权限名称（如：用户新增）';
COMMENT ON COLUMN sys_permission.perm_type IS '权限类型：1-菜单，2-按钮/接口，3-数据权限';
COMMENT ON COLUMN sys_permission.resource_path IS '资源路径（如菜单URL、接口路径）';
COMMENT ON COLUMN sys_permission.icon IS '菜单图标（仅菜单类型）';
COMMENT ON COLUMN sys_permission.sort IS '排序值';
COMMENT ON COLUMN sys_permission.status IS '状态：1-启用，0-禁用';
COMMENT ON COLUMN sys_permission.creator IS '创建人';
COMMENT ON COLUMN sys_permission.create_time IS '创建时间';
COMMENT ON COLUMN sys_permission.updater IS '更新人';
COMMENT ON COLUMN sys_permission.update_time IS '更新时间';

-- 创建用户-角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  creator VARCHAR(64) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenant(id),
  FOREIGN KEY (user_id) REFERENCES sys_user(id),
  FOREIGN KEY (role_id) REFERENCES sys_role(id),
  UNIQUE (tenant_id, user_id, role_id)
);

-- 添加注释
COMMENT ON TABLE sys_user_role IS '用户-角色关联表';
COMMENT ON COLUMN sys_user_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID（关联sys_user.id）';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID（关联sys_role.id）';
COMMENT ON COLUMN sys_user_role.creator IS '创建人';
COMMENT ON COLUMN sys_user_role.create_time IS '创建时间';

-- 创建角色-权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  perm_id BIGINT NOT NULL,
  creator VARCHAR(64) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenant(id),
  FOREIGN KEY (role_id) REFERENCES sys_role(id),
  FOREIGN KEY (perm_id) REFERENCES sys_permission(id),
  UNIQUE (tenant_id, role_id, perm_id)
);

-- 添加注释
COMMENT ON TABLE sys_role_permission IS '角色-权限关联表';
COMMENT ON COLUMN sys_role_permission.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role_permission.role_id IS '角色ID（关联sys_role.id）';
COMMENT ON COLUMN sys_role_permission.perm_id IS '权限ID（关联sys_permission.id）';
COMMENT ON COLUMN sys_role_permission.creator IS '创建人';
COMMENT ON COLUMN sys_role_permission.create_time IS '创建时间';

-- 创建项目表
CREATE TABLE IF NOT EXISTS projects (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  status VARCHAR(50) NOT NULL,
  config_json JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

-- 添加注释
COMMENT ON TABLE projects IS '项目表';
COMMENT ON COLUMN projects.tenant_id IS '租户ID（关联tenant.id）';
COMMENT ON COLUMN projects.name IS '项目名称';
COMMENT ON COLUMN projects.description IS '项目描述';
COMMENT ON COLUMN projects.status IS '项目状态（ACTIVE, ARCHIVED等）';
COMMENT ON COLUMN projects.config_json IS '项目配置JSON';
COMMENT ON COLUMN projects.created_at IS '创建时间';
COMMENT ON COLUMN projects.updated_at IS '更新时间';

-- 创建配置项表
CREATE TABLE IF NOT EXISTS config_items (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  project_id BIGINT,
  user_id BIGINT,
  config_key VARCHAR(255) NOT NULL,
  config_value TEXT NOT NULL,
  scope VARCHAR(50) NOT NULL,
  version INTEGER NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  created_by VARCHAR(64),
  description TEXT,
  FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

-- 添加注释
COMMENT ON TABLE config_items IS '配置项表';
COMMENT ON COLUMN config_items.tenant_id IS '租户ID（关联tenant.id）';
COMMENT ON COLUMN config_items.project_id IS '项目ID（关联projects.id，可选）';
COMMENT ON COLUMN config_items.user_id IS '用户ID（关联sys_user.id，可选）';
COMMENT ON COLUMN config_items.config_key IS '配置键名';
COMMENT ON COLUMN config_items.config_value IS '配置键值';
COMMENT ON COLUMN config_items.scope IS '作用域（TENANT, PROJECT, USER）';
COMMENT ON COLUMN config_items.version IS '配置版本';
COMMENT ON COLUMN config_items.status IS '配置状态（ACTIVE, ARCHIVED等）';
COMMENT ON COLUMN config_items.created_at IS '创建时间';
COMMENT ON COLUMN config_items.updated_at IS '更新时间';
COMMENT ON COLUMN config_items.created_by IS '创建者';
COMMENT ON COLUMN config_items.description IS '配置描述';

-- 插入初始数据
-- 租户1：企业A（租户ID=1）
INSERT INTO tenant (tenant_code, tenant_name, status, expire_time, remark)
SELECT 'company_a', 'A科技有限公司', 1, '2028-12-31 23:59:59', '测试租户A，主营电商系统'
WHERE NOT EXISTS (SELECT 1 FROM tenant WHERE tenant_code = 'company_a');

-- 租户2：企业B（租户ID=2）
INSERT INTO tenant (tenant_code, tenant_name, status, expire_time, remark)
SELECT 'company_b', 'B物流有限公司', 1, '2028-12-31 23:59:59', '测试租户B，主营物流管理'
WHERE NOT EXISTS (SELECT 1 FROM tenant WHERE tenant_code = 'company_b');

-- 租户1用户：admin（超级管理员，密码加密后为123456，实际应使用BCrypt加密）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
SELECT 1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', '13800138000', 'admin@companya.com', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin' AND tenant_id = 1);

-- 租户1用户：user1（普通运营）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
SELECT 1, 'user1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', '13800138001', 'user1@companya.com', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'user1' AND tenant_id = 1);

-- 租户2用户：admin（超级管理员）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
SELECT 2, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', '13900139000', 'admin@companyb.com', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'admin' AND tenant_id = 2);

-- 租户2用户：user1（普通物流专员）
INSERT INTO sys_user (tenant_id, username, password, real_name, mobile, email, status)
SELECT 2, 'user1', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', '13900139001', 'user1@companyb.com', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE username = 'user1' AND tenant_id = 2);

-- 租户1角色：超级管理员（role_code=admin）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
SELECT 1, 'admin', '超级管理员', 1, 1, '租户1最高权限角色，拥有所有权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'admin' AND tenant_id = 1);

-- 租户1角色：运营专员（role_code=operator）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
SELECT 1, 'operator', '运营专员', 1, 2, '租户1普通运营角色，仅拥有基础操作权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'operator' AND tenant_id = 1);

-- 租户2角色：超级管理员（role_code=admin）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
SELECT 2, 'admin', '超级管理员', 1, 1, '租户2最高权限角色，拥有所有权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'admin' AND tenant_id = 2);

-- 租户2角色：物流专员（role_code=logistics）
INSERT INTO sys_role (tenant_id, role_code, role_name, status, sort, remark)
SELECT 2, 'logistics', '物流专员', 1, 2, '租户2物流操作角色，仅拥有物流相关权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'logistics' AND tenant_id = 2);

-- 系统公共权限（tenant_id=0）
-- 基础菜单：系统管理
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 0, 0, 'sys:manage', '系统管理', 1, '/sys/manage', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:manage');

-- 系统公共权限：用户管理菜单
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 0, COALESCE((SELECT id FROM sys_permission WHERE perm_code = 'sys:manage'), 0), 'sys:user', '用户管理', 1, '/sys/user', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:user');

-- 系统公共权限：用户新增按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 0, COALESCE((SELECT id FROM sys_permission WHERE perm_code = 'sys:user'), 0), 'sys:user:add', '用户新增', 2, '/api/sys/user/add', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:user:add');

-- 系统公共权限：用户编辑按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 0, COALESCE((SELECT id FROM sys_permission WHERE perm_code = 'sys:user'), 0), 'sys:user:edit', '用户编辑', 2, '/api/sys/user/edit', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:user:edit');

-- 系统公共权限：用户删除按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 0, COALESCE((SELECT id FROM sys_permission WHERE perm_code = 'sys:user'), 0), 'sys:user:delete', '用户删除', 2, '/api/sys/user/delete', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'sys:user:delete');

-- 租户1独有权限：商品管理菜单
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 1, 0, 'goods:manage', '商品管理', 1, '/goods/manage', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'goods:manage' AND tenant_id = 1);

-- 租户1独有权限：商品上架按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 1, COALESCE((SELECT id FROM sys_permission WHERE perm_code = 'goods:manage' AND tenant_id = 1), 0), 'goods:manage:online', '商品上架', 2, '/api/goods/online', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'goods:manage:online' AND tenant_id = 1);

-- 租户2独有权限：物流订单管理菜单
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 2, 0, 'logistics:order', '物流订单管理', 1, '/logistics/order', 3, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'logistics:order' AND tenant_id = 2);

-- 租户2独有权限：物流订单分配按钮
INSERT INTO sys_permission (tenant_id, parent_id, perm_code, perm_name, perm_type, resource_path, sort, status)
SELECT 2, COALESCE((SELECT id FROM sys_permission WHERE perm_code = 'logistics:order' AND tenant_id = 2), 0), 'logistics:order:assign', '订单分配', 2, '/api/logistics/order/assign', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE perm_code = 'logistics:order:assign' AND tenant_id = 2);

-- 租户1：admin用户绑定超级管理员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id, creator)
SELECT 1, u.id, r.id, 'system'
FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND u.tenant_id = 1
  AND r.role_code = 'admin' AND r.tenant_id = 1
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- 租户1：user1用户绑定运营专员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id, creator)
SELECT 1, u.id, r.id, 'system'
FROM sys_user u, sys_role r
WHERE u.username = 'user1' AND u.tenant_id = 1
  AND r.role_code = 'operator' AND r.tenant_id = 1
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- 租户2：admin用户绑定超级管理员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id, creator)
SELECT 2, u.id, r.id, 'system'
FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND u.tenant_id = 2
  AND r.role_code = 'admin' AND r.tenant_id = 2
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- 租户2：user1用户绑定物流专员角色
INSERT INTO sys_user_role (tenant_id, user_id, role_id, creator)
SELECT 2, u.id, r.id, 'system'
FROM sys_user u, sys_role r
WHERE u.username = 'user1' AND u.tenant_id = 2
  AND r.role_code = 'logistics' AND r.tenant_id = 2
  AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- 租户1超级管理员：拥有所有系统公共权限+租户1独有权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id, creator)
SELECT 1, r.id, p.id, 'system'
FROM sys_role r, sys_permission p
WHERE r.role_code = 'admin' AND r.tenant_id = 1
  AND p.perm_code IN ('sys:manage', 'sys:user', 'sys:user:add', 'sys:user:edit', 'sys:user:delete', 'goods:manage', 'goods:manage:online')
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id);

-- 租户1运营专员：仅拥有用户查看（隐式，无删除）+商品上架权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id, creator)
SELECT 1, r.id, p.id, 'system'
FROM sys_role r, sys_permission p
WHERE r.role_code = 'operator' AND r.tenant_id = 1
  AND p.perm_code IN ('sys:user', 'sys:user:add', 'sys:user:edit', 'goods:manage', 'goods:manage:online')
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id);

-- 租户2超级管理员：拥有所有系统公共权限+租户2独有权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id, creator)
SELECT 2, r.id, p.id, 'system'
FROM sys_role r, sys_permission p
WHERE r.role_code = 'admin' AND r.tenant_id = 2
  AND p.perm_code IN ('sys:manage', 'sys:user', 'sys:user:add', 'sys:user:edit', 'sys:user:delete', 'logistics:order', 'logistics:order:assign')
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id);

-- 租户2物流专员：仅拥有用户查看+物流订单分配权限
INSERT INTO sys_role_permission (tenant_id, role_id, perm_id, creator)
SELECT 2, r.id, p.id, 'system'
FROM sys_role r, sys_permission p
WHERE r.role_code = 'logistics' AND r.tenant_id = 2
  AND p.perm_code IN ('sys:user', 'sys:user:add', 'sys:user:edit', 'logistics:order', 'logistics:order:assign')
  AND NOT EXISTS (SELECT 1 FROM sys_role_permission rp WHERE rp.role_id = r.id AND rp.perm_id = p.id);