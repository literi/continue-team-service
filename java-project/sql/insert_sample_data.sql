-- 插入示例数据，确保数据流程完整可用（SQLite版本）
-- 插入顺序遵循外键依赖关系
-- 时间固定为2026-01-27 12:00:00

-- 1. 插入租户
INSERT INTO `sys_tenant` (`id`, `tenant_name`, `tenant_code`, `contact_person`, `contact_phone`, `status`, `expire_time`, `create_time`, `update_time`) VALUES
('tenant_001', '示例租户', 'example_tenant', '张三', '13800138000', 1, '2026-12-31 23:59:59', '2026-01-27 12:00:00', '2026-01-27 12:00:00');

-- 2. 插入用户
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `nickname`, `phone`, `email`, `status`, `last_login_time`, `create_time`, `update_time`) VALUES
('user_001', 'tenant_001', 'admin', '$2a$10$pmGQiKYZP.IMsPJkTdimjeOf81har2Fj2fQz59u6Ry3.KOAJ53uP.', '管理员', '13800138000', 'admin@example.com', 1, NULL, '2026-01-27 12:00:00', '2026-01-27 12:00:00');

-- 3. 插入角色
INSERT INTO `sys_role` (`id`, `tenant_id`, `role_name`, `role_code`, `description`, `status`, `create_time`, `update_time`) VALUES
('role_001', 'tenant_001', '管理员', 'admin', '系统管理员角色', 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00'),
('role_002', 'tenant_001', '普通用户', 'user', '普通用户角色', 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00');

-- 4. 插入权限
INSERT INTO `sys_permission` (`id`, `tenant_id`, `perm_name`, `perm_code`, `perm_type`, `resource_path`, `method`, `parent_id`, `sort`, `status`, `create_time`, `update_time`) VALUES
('perm_001', 'tenant_001', '用户管理', 'sys:user', 1, '/api/user', NULL, NULL, 1, 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00'),
('perm_002', 'tenant_001', '添加用户', 'sys:user:add', 2, '/api/user/add', 'POST', 'perm_001', 1, 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00'),
('perm_003', 'tenant_001', '编辑用户', 'sys:user:edit', 2, '/api/user/edit', 'PUT', 'perm_001', 2, 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00'),
('perm_004', 'tenant_001', '角色管理', 'sys:role', 1, '/api/role', NULL, NULL, 2, 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00'),
('perm_005', 'tenant_001', '添加角色', 'sys:role:add', 2, '/api/role/add', 'POST', 'perm_004', 1, 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00');

-- 5. 插入用户角色关联
INSERT INTO `sys_user_role` (`id`, `tenant_id`, `user_id`, `role_id`, `create_time`) VALUES
('ur_001', 'tenant_001', 'user_001', 'role_001', '2026-01-27 12:00:00');

-- 6. 插入角色权限关联
INSERT INTO `sys_role_permission` (`id`, `tenant_id`, `role_id`, `permission_id`, `create_time`) VALUES
('rp_001', 'tenant_001', 'role_001', 'perm_001', '2026-01-27 12:00:00'),
('rp_002', 'tenant_001', 'role_001', 'perm_002', '2026-01-27 12:00:00'),
('rp_003', 'tenant_001', 'role_001', 'perm_003', '2026-01-27 12:00:00'),
('rp_004', 'tenant_001', 'role_001', 'perm_004', '2026-01-27 12:00:00'),
('rp_005', 'tenant_001', 'role_001', 'perm_005', '2026-01-27 12:00:00');

-- 7. 插入OAuth2注册客户端
INSERT INTO `oauth2_registered_client` (`id`, `tenant_id`, `client_id`, `client_id_issued_at`, `client_secret`, `client_secret_expires_at`, `client_name`, `client_authentication_methods`, `authorization_grant_types`, `redirect_uris`, `post_logout_redirect_uris`, `scopes`, `client_settings`, `token_settings`, `status`, `create_time`, `update_time`) VALUES
('client_001', 'tenant_001', 'example_client', '2026-01-27 12:00:00', '$2a$10$example.client.secret', NULL, '示例客户端', 'client_secret_basic,client_secret_post', 'authorization_code,refresh_token', 'http://localhost:8080/login/oauth2/code/example', 'http://localhost:8080/', 'read,write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}', 1, '2026-01-27 12:00:00', '2026-01-27 12:00:00');

-- 8. 插入OAuth2授权记录
INSERT INTO `oauth2_authorization` (`id`, `tenant_id`, `registered_client_id`, `principal_name`, `authorization_grant_type`, `authorized_scopes`, `attributes`, `state`, `authorization_code_value`, `authorization_code_issued_at`, `authorization_code_expires_at`, `authorization_code_metadata`, `access_token_value`, `access_token_issued_at`, `access_token_expires_at`, `access_token_metadata`, `access_token_type`, `access_token_scopes`, `refresh_token_value`, `refresh_token_issued_at`, `refresh_token_expires_at`, `refresh_token_metadata`, `oidc_id_token_value`, `oidc_id_token_issued_at`, `oidc_id_token_expires_at`, `oidc_id_token_metadata`, `oidc_id_token_claims`, `user_code_value`, `user_code_issued_at`, `user_code_expires_at`, `user_code_metadata`, `device_code_value`, `device_code_issued_at`, `device_code_expires_at`, `device_code_metadata`) VALUES
('auth_001', 'tenant_001', 'client_001', 'admin', 'authorization_code', 'read,write', '{"@class":"java.util.Collections$UnmodifiableMap","java.security.Principal":{"@class":"org.springframework.security.core.userdetails.User","username":"admin","password":"[PROTECTED]","enabled":true,"accountNonExpired":true,"credentialsNonExpired":true,"accountNonLocked":true,"authorities":[]},"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest":{"@class":"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest","authorizationUri":{"@class":"java.net.URI","value":"http://localhost:8080/oauth2/authorize"},"authorizationGrantType":{"@class":"org.springframework.security.oauth2.core.AuthorizationGrantType","value":"authorization_code"},"responseType":{"@class":"org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames$ResponseType","value":"code"},"clientId":"example_client","redirectUri":{"@class":"java.net.URI","value":"http://localhost:8080/login/oauth2/code/example"},"scopes":["read","write"],"state":"xyz123","additionalParameters":{"@class":"java.util.Collections$UnmodifiableMap"}}}', 'xyz123', 'example_auth_code', '2026-01-27 12:00:00', '2026-01-27 12:05:00', '{"@class":"java.util.Collections$UnmodifiableMap"}', 'example_access_token', '2026-01-27 12:00:00', '2026-01-27 12:05:00', '{"@class":"java.util.Collections$UnmodifiableMap"}', 'Bearer', 'read,write', 'example_refresh_token', '2026-01-27 12:00:00', '2026-01-27 13:00:00', '{"@class":"java.util.Collections$UnmodifiableMap"}', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- 9. 插入OAuth2授权码
INSERT INTO `oauth2_authorization_code` (`id`, `tenant_id`, `code`, `client_id`, `principal_name`, `authorization_scopes`, `authorized_scopes`, `attributes`, `state`, `created_at`, `expires_at`, `consumed_at`) VALUES
('code_001', 'tenant_001', 'example_auth_code', 'example_client', 'admin', 'read,write', 'read,write', '{"@class":"java.util.Collections$UnmodifiableMap","java.security.Principal":{"@class":"org.springframework.security.core.userdetails.User","username":"admin","password":"[PROTECTED]","enabled":true,"accountNonExpired":true,"credentialsNonExpired":true,"accountNonLocked":true,"authorities":[]},"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest":{"@class":"org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest","authorizationUri":{"@class":"java.net.URI","value":"http://localhost:8080/oauth2/authorize"},"authorizationGrantType":{"@class":"org.springframework.security.oauth2.core.AuthorizationGrantType","value":"authorization_code"},"responseType":{"@class":"org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames$ResponseType","value":"code"},"clientId":"example_client","redirectUri":{"@class":"java.net.URI","value":"http://localhost:8080/login/oauth2/code/example"},"scopes":["read","write"],"state":"xyz123","additionalParameters":{"@class":"java.util.Collections$UnmodifiableMap"}}}', 'xyz123', '2026-01-27 12:00:00', '2026-01-27 12:05:00', NULL);