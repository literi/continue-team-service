package plugin.team.coreserver.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import plugin.team.coreserver.domain.entity.ConfigItem;

@Repository
public interface ConfigItemRepository extends JpaRepository<ConfigItem, Long> {
    
    List<ConfigItem> findByTenantIdAndScopeAndStatus(Long tenantId, String scope, String status);
    
    List<ConfigItem> findByTenantIdAndProjectIdAndScopeAndStatus(Long tenantId, Long projectId, String scope, String status);
    
    List<ConfigItem> findByTenantIdAndProjectIdAndUserIdAndScopeAndStatus(Long tenantId, Long projectId, Long userId, String scope, String status);
    
    Optional<ConfigItem> findByTenantIdAndConfigKeyAndScopeAndStatus(Long tenantId, String configKey, String scope, String status);
    
    List<ConfigItem> findByConfigKeyAndScopeOrderByVersionDesc(String configKey, String scope);
    
    // ConfigService所需的方法
    List<ConfigItem> findByTenantId(Long tenantId);
    List<ConfigItem> findByProjectId(Long projectId);
    Optional<ConfigItem> findByConfigKey(String configKey);
    List<ConfigItem> findByScope(String scope);
}
