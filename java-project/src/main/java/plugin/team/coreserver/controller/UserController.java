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
import plugin.team.coreserver.domain.entity.User;
import plugin.team.coreserver.service.UserManagementService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户")
    @GetMapping
    public ResponseEntity<Result<Page<User>>> getUsers(Pageable pageable) {
        Page<User> users = userManagementService.getAllUsers(pageable);
        return ResponseEntity.ok(Result.success(users));
    }

    /**
     * 根据ID获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<User>> getUserById(@PathVariable Long id) {
        User user = userManagementService.getUserById(id);
        return ResponseEntity.ok(Result.success(user));
    }

    /**
     * 创建新用户
     */
    @Operation(summary = "创建用户")
    @PostMapping
    public ResponseEntity<Result<User>> createUser(@Valid @RequestBody User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        User savedUser = userManagementService.createUser(user);
        return ResponseEntity.ok(Result.success(savedUser));
    }

    /**
     * 更新用户信息
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public ResponseEntity<Result<User>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        User updatedUser = userManagementService.updateUser(user);
        return ResponseEntity.ok(Result.success(updatedUser));
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 根据租户ID查询用户列表
     */
    @Operation(summary = "根据租户查询用户")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Result<List<User>>> getUsersByTenant(@PathVariable Long tenantId) {
        List<User> users = userManagementService.getUsersByTenantId(tenantId);
        return ResponseEntity.ok(Result.success(users));
    }

    /**
     * 为用户分配角色
     */
    @Operation(summary = "为用户分配角色")
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Result<Void>> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId, @RequestParam Long tenantId) {
        userManagementService.assignRoleToUser(userId, roleId, tenantId);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 移除用户的角色
     */
    @Operation(summary = "移除用户角色")
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Result<Void>> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userManagementService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(Result.success(null));
    }
}