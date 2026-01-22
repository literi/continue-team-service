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
import plugin.team.coreserver.domain.entity.ConfigItem;
import plugin.team.coreserver.service.ConfigService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 配置项管理控制器
 */
@Tag(name = "配置项管理")
@RestController
@RequestMapping("/api/config-items")
public class ConfigItemController {

    @Autowired
    private ConfigService configService;

    /**
     * 分页查询配置项列表
     */
    @Operation(summary = "分页查询配置项")
    @GetMapping
    public ResponseEntity<Result<Page<ConfigItem>>> getConfigItems(Pageable pageable) {
        Page<ConfigItem> configItems = configService.findAll(pageable);
        return ResponseEntity.ok(Result.success(configItems));
    }

    /**
     * 根据ID获取配置项详情
     */
    @Operation(summary = "获取配置项详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<ConfigItem>> getConfigItemById(@PathVariable Long id) {
        ConfigItem configItem = configService.findById(id);
        return ResponseEntity.ok(Result.success(configItem));
    }

    /**
     * 创建新配置项
     */
    @Operation(summary = "创建配置项")
    @PostMapping
    public ResponseEntity<Result<ConfigItem>> createConfigItem(@Valid @RequestBody ConfigItem configItem) {
        configItem.setCreatedAt(LocalDateTime.now());
        configItem.setVersion(1); // 初始版本
        ConfigItem savedConfigItem = configService.save(configItem);
        return ResponseEntity.ok(Result.success(savedConfigItem));
    }

    /**
     * 更新配置项信息
     */
    @Operation(summary = "更新配置项")
    @PutMapping("/{id}")
    public ResponseEntity<Result<ConfigItem>> updateConfigItem(@PathVariable Long id, @Valid @RequestBody ConfigItem configItem) {
        configItem.setId(id);
        configItem.setUpdatedAt(LocalDateTime.now());
        configItem.setVersion(configItem.getVersion() + 1); // 版本递增
        ConfigItem updatedConfigItem = configService.save(configItem);
        return ResponseEntity.ok(Result.success(updatedConfigItem));
    }

    /**
     * 删除配置项
     */
    @Operation(summary = "删除配置项")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteConfigItem(@PathVariable Long id) {
        configService.deleteById(id);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 根据租户ID查询配置项列表
     */
    @Operation(summary = "根据租户查询配置项")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Result<List<ConfigItem>>> getConfigItemsByTenant(@PathVariable Long tenantId) {
        List<ConfigItem> configItems = configService.findByTenantId(tenantId);
        return ResponseEntity.ok(Result.success(configItems));
    }

    /**
     * 根据项目ID查询配置项列表
     */
    @Operation(summary = "根据项目查询配置项")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Result<List<ConfigItem>>> getConfigItemsByProject(@PathVariable Long projectId) {
        List<ConfigItem> configItems = configService.findByProjectId(projectId);
        return ResponseEntity.ok(Result.success(configItems));
    }

    /**
     * 根据配置键查询配置项
     */
    @Operation(summary = "根据配置键查询配置项")
    @GetMapping("/key/{configKey}")
    public ResponseEntity<Result<ConfigItem>> getConfigItemByKey(@PathVariable String configKey) {
        ConfigItem configItem = configService.findByConfigKey(configKey);
        return ResponseEntity.ok(Result.success(configItem));
    }

    /**
     * 根据作用域查询配置项
     */
    @Operation(summary = "根据作用域查询配置项")
    @GetMapping("/scope/{scope}")
    public ResponseEntity<Result<List<ConfigItem>>> getConfigItemsByScope(@PathVariable String scope) {
        List<ConfigItem> configItems = configService.findByScope(scope);
        return ResponseEntity.ok(Result.success(configItems));
    }
}