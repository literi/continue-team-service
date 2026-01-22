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
import plugin.team.coreserver.domain.entity.Permission;
import plugin.team.coreserver.service.UserManagementService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限管理控制器
 */
@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * 分页查询权限列表
     */
    @Operation(summary = "分页查询权限")
    @GetMapping
    public ResponseEntity<Result<Page<Permission>>> getPermissions(Pageable pageable) {
        Page<Permission> permissions = userManagementService.getAllPermissions(pageable);
        return ResponseEntity.ok(Result.success(permissions));
    }

    /**
     * 根据ID获取权限详情
     */
    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<Permission>> getPermissionById(@PathVariable Long id) {
        Permission permission = userManagementService.getPermissionById(id);
        return ResponseEntity.ok(Result.success(permission));
    }

    /**
     * 创建新权限
     */
    @Operation(summary = "创建权限")
    @PostMapping
    public ResponseEntity<Result<Permission>> createPermission(@Valid @RequestBody Permission permission) {
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        Permission savedPermission = userManagementService.createPermission(permission);
        return ResponseEntity.ok(Result.success(savedPermission));
    }

    /**
     * 更新权限信息
     */
    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Permission>> updatePermission(@PathVariable Long id, @Valid @RequestBody Permission permission) {
        permission.setId(id);
        permission.setUpdateTime(LocalDateTime.now());
        Permission updatedPermission = userManagementService.updatePermission(permission);
        return ResponseEntity.ok(Result.success(updatedPermission));
    }

    /**
     * 删除权限
     */
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deletePermission(@PathVariable Long id) {
        userManagementService.deletePermission(id);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 根据租户ID查询权限列表
     */
    @Operation(summary = "根据租户查询权限")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Result<List<Permission>>> getPermissionsByTenant(@PathVariable Long tenantId) {
        List<Permission> permissions = userManagementService.getPermissionsByTenantId(tenantId);
        return ResponseEntity.ok(Result.success(permissions));
    }

    /**
     * 根据角色ID查询权限列表
     */
    @Operation(summary = "根据角色查询权限")
    @GetMapping("/role/{roleId}")
    public ResponseEntity<Result<List<Permission>>> getPermissionsByRole(@PathVariable Long roleId) {
        List<Permission> permissions = userManagementService.getPermissionsByRoleId(roleId);
        return ResponseEntity.ok(Result.success(permissions));
    }
}