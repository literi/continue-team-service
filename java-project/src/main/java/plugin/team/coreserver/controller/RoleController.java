package plugin.team.coreserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import plugin.team.coreserver.common.Result;
import plugin.team.coreserver.domain.entity.Role;
import plugin.team.coreserver.service.UserManagementService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色")
    @GetMapping
    public ResponseEntity<Result<Page<Role>>> getRoles(Pageable pageable) {
        Page<Role> roles = userManagementService.getAllRoles(pageable);
        return ResponseEntity.ok(Result.success(roles));
    }

    /**
     * 根据ID获取角色详情
     */
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<Role>> getRoleById(@PathVariable Long id) {
        Role role = userManagementService.getRoleById(id);
        return ResponseEntity.ok(Result.success(role));
    }

    /**
     * 创建新角色
     */
    @Operation(summary = "创建角色")
    @PostMapping
    public ResponseEntity<Result<Role>> createRole(@Valid @RequestBody Role role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        Role savedRole = userManagementService.createRole(role);
        return ResponseEntity.ok(Result.success(savedRole));
    }

    /**
     * 更新角色信息
     */
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Role>> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
        role.setId(id);
        role.setUpdateTime(LocalDateTime.now());
        Role updatedRole = userManagementService.updateRole(role);
        return ResponseEntity.ok(Result.success(updatedRole));
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteRole(@PathVariable Long id) {
        userManagementService.deleteRole(id);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 根据租户ID查询角色列表
     */
    @Operation(summary = "根据租户查询角色")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Result<List<Role>>> getRolesByTenant(@PathVariable Long tenantId) {
        List<Role> roles = userManagementService.getRolesByTenantId(tenantId);
        return ResponseEntity.ok(Result.success(roles));
    }

    /**
     * 为角色分配权限
     */
    @Operation(summary = "为角色分配权限")
    @PostMapping("/{roleId}/permissions/{permId}")
    public ResponseEntity<Result<Void>> assignPermissionToRole(@PathVariable Long roleId, @PathVariable Long permId, @RequestParam Long tenantId) {
        userManagementService.assignPermissionToRole(roleId, permId, tenantId);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 移除角色的权限
     */
    @Operation(summary = "移除角色权限")
    @DeleteMapping("/{roleId}/permissions/{permId}")
    public ResponseEntity<Result<Void>> removePermissionFromRole(@PathVariable Long roleId, @PathVariable Long permId) {
        userManagementService.removePermissionFromRole(roleId, permId);
        return ResponseEntity.ok(Result.success(null));
    }
}