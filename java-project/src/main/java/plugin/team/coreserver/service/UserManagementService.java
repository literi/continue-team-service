package plugin.team.coreserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plugin.team.coreserver.domain.entity.*;
import plugin.team.coreserver.domain.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserManagementService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 用户相关方法
    public User createUser(User user) {
        // 检查用户是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // 1-启用，0-禁用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        log.info("User created: {} with ID: {}", user.getUsername(), savedUser.getId());
        
        return savedUser;
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }
    
    public plugin.team.coreserver.common.dto.UserInfoDTO getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        return plugin.team.coreserver.common.dto.UserInfoDTO.builder()
                .id(user.getId())
                .tenantId(user.getTenantId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .mobile(user.getMobile())
                .email(user.getEmail())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .build();
    }
    
    public List<User> getUsersByTenantId(Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }
    
    // 角色相关方法
    public Role createRole(String roleCode, String roleName, Long tenantId, String remark) {
        // 检查角色是否已存在
        if (roleRepository.findByRoleCodeAndTenantId(roleCode, tenantId).isPresent()) {
            throw new RuntimeException("Role already exists: " + roleCode + " in tenant: " + tenantId);
        }
        
        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setTenantId(tenantId);
        role.setStatus(1); // 1-启用，0-禁用
        role.setRemark(remark);
        role.setSort(0);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        
        Role savedRole = roleRepository.save(role);
        log.info("Role created: {} with ID: {}", roleCode, savedRole.getId());
        
        return savedRole;
    }
    
    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
    }
    
    public List<Role> getRolesByTenantId(Long tenantId) {
        return roleRepository.findByTenantId(tenantId);
    }
    
    // 权限相关方法
    public Permission createPermission(String permCode, String permName, Integer permType, String resourcePath, 
                                      Long tenantId, Long parentId, String icon) {
        // 检查权限编码是否已存在
        if (permissionRepository.findByPermCode(permCode).isPresent()) {
            throw new RuntimeException("Permission already exists: " + permCode);
        }
        
        Permission permission = new Permission();
        permission.setPermCode(permCode);
        permission.setPermName(permName);
        permission.setPermType(permType);
        permission.setResourcePath(resourcePath);
        permission.setTenantId(tenantId);
        permission.setParentId(parentId);
        permission.setIcon(icon);
        permission.setStatus(1); // 1-启用，0-禁用
        permission.setSort(0);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created: {} with ID: {}", permCode, savedPermission.getId());
        
        return savedPermission;
    }
    
    public Permission getPermissionById(Long permId) {
        return permissionRepository.findById(permId)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permId));
    }
    
    public List<Permission> getPermissionsByTenantId(Long tenantId) {
        return permissionRepository.findByTenantId(tenantId);
    }
    
    // 用户-角色关联方法
    public UserRole assignRoleToUser(Long userId, Long roleId, Long tenantId) {
        // 检查是否已存在相同的用户-角色关联
        if (userRoleRepository.findByUserIdAndRoleId(userId, roleId).isPresent()) {
            throw new RuntimeException("User already assigned to role: " + roleId);
        }
        
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setTenantId(tenantId);
        userRole.setCreateTime(LocalDateTime.now());
        
        UserRole savedUserRole = userRoleRepository.save(userRole);
        log.info("Role {} assigned to user {}", roleId, userId);
        
        return savedUserRole;
    }
    
    public List<UserRole> getUserRoles(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }
    
    public void removeRoleFromUser(Long userId, Long roleId) {
        Optional<UserRole> userRoleOpt = userRoleRepository.findByUserIdAndRoleId(userId, roleId);
        if (userRoleOpt.isPresent()) {
            userRoleRepository.deleteById(userRoleOpt.get().getId());
            log.info("Role {} removed from user {}", roleId, userId);
        }
    }
    
    // 角色-权限关联方法
    public RolePermission assignPermissionToRole(Long roleId, Long permId, Long tenantId) {
        // 检查是否已存在相同的角色-权限关联
        if (rolePermissionRepository.findByRoleIdAndPermId(roleId, permId).isPresent()) {
            throw new RuntimeException("Permission already assigned to role: " + permId);
        }
        
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermId(permId);
        rolePermission.setTenantId(tenantId);
        rolePermission.setCreateTime(LocalDateTime.now());
        
        RolePermission savedRolePermission = rolePermissionRepository.save(rolePermission);
        log.info("Permission {} assigned to role {}", permId, roleId);
        
        return savedRolePermission;
    }
    
    public List<RolePermission> getRolePermissions(Long roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }
    
    public void removePermissionFromRole(Long roleId, Long permId) {
        Optional<RolePermission> rolePermissionOpt = rolePermissionRepository.findByRoleIdAndPermId(roleId, permId);
        if (rolePermissionOpt.isPresent()) {
            rolePermissionRepository.deleteById(rolePermissionOpt.get().getId());
            log.info("Permission {} removed from role {}", permId, roleId);
        }
    }
    
    // 为Controller添加的CRUD方法
    public org.springframework.data.domain.Page<User> getAllUsers(org.springframework.data.domain.Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found: " + user.getId()));
        
        // 更新用户信息（除了密码）
        existingUser.setUsername(user.getUsername());
        existingUser.setRealName(user.getRealName());
        existingUser.setMobile(user.getMobile());
        existingUser.setEmail(user.getEmail());
        existingUser.setStatus(user.getStatus());
        existingUser.setTenantId(user.getTenantId());
        existingUser.setUpdater(user.getUpdater());
        existingUser.setUpdateTime(java.time.LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User deleted: {}", id);
    }
    
    public org.springframework.data.domain.Page<Role> getAllRoles(org.springframework.data.domain.Pageable pageable) {
        return roleRepository.findAll(pageable);
    }
    
    public Role createRole(Role role) {
        role.setCreateTime(java.time.LocalDateTime.now());
        role.setUpdateTime(java.time.LocalDateTime.now());
        return roleRepository.save(role);
    }
    
    public Role updateRole(Role role) {
        Role existingRole = roleRepository.findById(role.getId())
            .orElseThrow(() -> new RuntimeException("Role not found: " + role.getId()));
        
        existingRole.setRoleCode(role.getRoleCode());
        existingRole.setRoleName(role.getRoleName());
        existingRole.setTenantId(role.getTenantId());
        existingRole.setStatus(role.getStatus());
        existingRole.setSort(role.getSort());
        existingRole.setRemark(role.getRemark());
        existingRole.setUpdater(role.getUpdater());
        existingRole.setUpdateTime(java.time.LocalDateTime.now());
        
        return roleRepository.save(existingRole);
    }
    
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
        log.info("Role deleted: {}", id);
    }
    
    public org.springframework.data.domain.Page<Permission> getAllPermissions(org.springframework.data.domain.Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }
    
    public Permission createPermission(Permission permission) {
        permission.setCreateTime(java.time.LocalDateTime.now());
        permission.setUpdateTime(java.time.LocalDateTime.now());
        return permissionRepository.save(permission);
    }
    
    public Permission updatePermission(Permission permission) {
        Permission existingPermission = permissionRepository.findById(permission.getId())
            .orElseThrow(() -> new RuntimeException("Permission not found: " + permission.getId()));
        
        existingPermission.setPermCode(permission.getPermCode());
        existingPermission.setPermName(permission.getPermName());
        existingPermission.setPermType(permission.getPermType());
        existingPermission.setResourcePath(permission.getResourcePath());
        existingPermission.setTenantId(permission.getTenantId());
        existingPermission.setParentId(permission.getParentId());
        existingPermission.setIcon(permission.getIcon());
        existingPermission.setSort(permission.getSort());
        existingPermission.setStatus(permission.getStatus());
        existingPermission.setUpdater(permission.getUpdater());
        existingPermission.setUpdateTime(java.time.LocalDateTime.now());
        
        return permissionRepository.save(existingPermission);
    }
    
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
        log.info("Permission deleted: {}", id);
    }
    
    public java.util.List<Permission> getPermissionsByRoleId(Long roleId) {
        // 通过rolePermissionRepository获取关联信息，再查询具体的权限
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRoleId(roleId);
        return rolePermissions.stream()
            .map(rp -> permissionRepository.findById(rp.getPermId()).orElse(null))
            .filter(p -> p != null)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 根据用户ID获取角色ID列表
     */
    public java.util.List<Long> getUserRoleIds(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        return userRoles.stream()
            .map(ur -> ur.getRoleId())
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 根据角色ID列表获取菜单权限
     */
    public java.util.List<java.util.Map<String, Object>> getMenusByRoleIds(java.util.List<Long> roleIds) {
        java.util.Set<Permission> menuPermissions = new java.util.HashSet<>();
        
        // 遍历角色ID，获取每个角色的权限
        for (Long roleId : roleIds) {
            List<Permission> permissions = getPermissionsByRoleId(roleId);
            // 只保留菜单类型的权限 (permType = 1)
            permissions.stream()
                .filter(p -> p.getPermType() != null && p.getPermType() == 1) // 1-菜单
                .forEach(menuPermissions::add);
        }
        
        // 将权限转换为菜单结构
        return menuPermissions.stream()
            .map(this::permissionToMenu)
            .sorted((m1, m2) -> {
                Integer sort1 = (Integer) m1.get("sort");
                Integer sort2 = (Integer) m2.get("sort");
                if (sort1 == null) sort1 = 0;
                if (sort2 == null) sort2 = 0;
                return sort1.compareTo(sort2);
            })
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 将权限对象转换为菜单结构
     */
    private java.util.Map<String, Object> permissionToMenu(Permission permission) {
        java.util.Map<String, Object> menu = new java.util.HashMap<>();
        menu.put("id", permission.getId());
        menu.put("parentId", permission.getParentId());
        menu.put("permCode", permission.getPermCode());
        menu.put("permName", permission.getPermName());
        menu.put("resourcePath", permission.getResourcePath());
        menu.put("icon", permission.getIcon());
        menu.put("sort", permission.getSort());
        menu.put("status", permission.getStatus());
        return menu;
    }
}