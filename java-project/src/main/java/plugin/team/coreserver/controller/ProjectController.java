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
import plugin.team.coreserver.domain.entity.Project;
import plugin.team.coreserver.service.ProjectService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目管理控制器
 */
@Tag(name = "项目管理")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * 分页查询项目列表
     */
    @Operation(summary = "分页查询项目")
    @GetMapping
    public ResponseEntity<Result<Page<Project>>> getProjects(Pageable pageable) {
        Page<Project> projects = projectService.findAll(pageable);
        return ResponseEntity.ok(Result.success(projects));
    }

    /**
     * 根据ID获取项目详情
     */
    @Operation(summary = "获取项目详情")
    @GetMapping("/{id}")
    public ResponseEntity<Result<Project>> getProjectById(@PathVariable Long id) {
        Project project = projectService.findById(id);
        return ResponseEntity.ok(Result.success(project));
    }

    /**
     * 创建新项目
     */
    @Operation(summary = "创建项目")
    @PostMapping
    public ResponseEntity<Result<Project>> createProject(@Valid @RequestBody Project project) {
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        Project savedProject = projectService.save(project);
        return ResponseEntity.ok(Result.success(savedProject));
    }

    /**
     * 更新项目信息
     */
    @Operation(summary = "更新项目")
    @PutMapping("/{id}")
    public ResponseEntity<Result<Project>> updateProject(@PathVariable Long id, @Valid @RequestBody Project project) {
        project.setId(id);
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectService.save(project);
        return ResponseEntity.ok(Result.success(updatedProject));
    }

    /**
     * 删除项目
     */
    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
        return ResponseEntity.ok(Result.success(null));
    }

    /**
     * 根据租户ID查询项目列表
     */
    @Operation(summary = "根据租户查询项目")
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<Result<List<Project>>> getProjectsByTenant(@PathVariable Long tenantId) {
        List<Project> projects = projectService.findByTenantId(tenantId);
        return ResponseEntity.ok(Result.success(projects));
    }
}