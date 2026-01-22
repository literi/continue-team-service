package plugin.team.coreserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import plugin.team.coreserver.domain.entity.Tenant;
import plugin.team.coreserver.domain.entity.Project;
import plugin.team.coreserver.domain.entity.User;
import plugin.team.coreserver.domain.repository.TenantRepository;
import plugin.team.coreserver.domain.repository.ProjectRepository;
import plugin.team.coreserver.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TenantService {
    
    @Autowired
    private TenantRepository tenantRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Tenant createTenant(@NonNull String tenantCode, @NonNull String tenantName) {
        Tenant tenant = new Tenant();
        tenant.setTenantCode(tenantCode);
        tenant.setTenantName(tenantName);
        tenant.setStatus(1); // 1-启用，0-禁用
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        
        Tenant saved = tenantRepository.save(tenant);
        Long tenantId = saved.getId();
        if (tenantId != null) {
            log.info("Tenant created: {} (id: {})", tenantName, tenantId);
        } else {
            log.warn("Tenant created but ID is null: {}", tenantName);
        }
        return saved;
    }
    
    public Tenant getTenantById(@NonNull Long tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found: " + tenantId));
    }
    
    public Optional<Tenant> getTenantByCode(@NonNull String tenantCode) {
        return tenantRepository.findByTenantCode(tenantCode);
    }
    
    public List<Project> getTenantProjects(@NonNull Long tenantId) {
        return projectRepository.findByTenantId(tenantId);
    }
    
    public List<User> getTenantUsers(@NonNull Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }
    
    public Project createProject(@NonNull Long tenantId, @NonNull String name, String description, String configJson) {
        Project project = new Project();
        project.setTenantId(tenantId);
        project.setName(name);
        project.setDescription(description);
        project.setStatus("ACTIVE");
        project.setConfigJson(configJson);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        Project saved = projectRepository.save(project);
        log.info("Project created: {} in tenant {}", name, tenantId);
        return saved;
    }
    
    // 为Controller添加的方法
    public List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
    
    public org.springframework.data.domain.Page<Tenant> findAll(org.springframework.data.domain.Pageable pageable) {
        return tenantRepository.findAll(pageable);
    }
    
    public Tenant findById(Long id) {
        return tenantRepository.findById(id).orElse(null);
    }
    
    public Tenant save(Tenant tenant) {
        return tenantRepository.save(tenant);
    }
    
    public void deleteById(Long id) {
        tenantRepository.deleteById(id);
    }
    
    public Tenant findByTenantCode(String tenantCode) {
        return tenantRepository.findByTenantCode(tenantCode).orElse(null);
    }
}
