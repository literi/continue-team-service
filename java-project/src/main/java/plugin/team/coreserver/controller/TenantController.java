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
import plugin.team.coreserver.domain.entity.Tenant;
import plugin.team.coreserver.service.TenantService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 租户管理控制器
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    /**
     * 分页查询租户列表
     */
    @Operation(summary = "分页查询租户")
    @GetMapping
    public ResponseEntity<Result<Page<Tenant>>> getTenants(Pageable pageable) {
        Page<Tenant> tenants = tenantService.findAll(pageable);
        return ResponseEntity.ok(Result.success(tenants));
    }

    /**
     * 根据ID获取租户详情
     */
    @Operation(summary = "获取租户详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<Tenant>> getTenantById(@PathVariable Long id) {
        Tenant tenant = tenantService.findById(id);
        return ResponseEntity.ok(Result.success(tenant));
    }

    /**
     * 创建新租户
     */
    @Operation(summary = "创建租户")
    @PostMapping
    public ResponseEntity<Result<Tenant>> createTenant(@Valid @RequestBody Tenant tenant) {
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        Tenant savedTenant = tenantService.save(tenant);
        return ResponseEntity.ok(Result.success(savedTenant));
    }

    /**
     * 更新租户信息
     */
    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Tenant>> updateTenant(@PathVariable Long id, @Valid @RequestBody Tenant tenant) {
        tenant.setId(id);
        tenant.setUpdateTime(LocalDateTime.now());
        Tenant updatedTenant = tenantService.save(tenant);
        return ResponseEntity.ok(Result.success(updatedTenant));
    }

    /**
     * 删除租户
     */
    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteTenant(@PathVariable Long id) {
        tenantService.deleteById(id);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 根据租户编码查询租户
     */
    @Operation(summary = "根据租户编码查询")
    @GetMapping("/code/{tenantCode}")
    public ResponseEntity<Result<Tenant>> getTenantByCode(@PathVariable String tenantCode) {
        Tenant tenant = tenantService.findByTenantCode(tenantCode);
        return ResponseEntity.ok(Result.success(tenant));
    }
}