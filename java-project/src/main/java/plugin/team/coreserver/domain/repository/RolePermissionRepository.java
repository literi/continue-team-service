package plugin.team.coreserver.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.team.coreserver.domain.entity.RolePermission;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
    
    List<RolePermission> findByPermId(Long permId);
    
    List<RolePermission> findByTenantId(Long tenantId);
    
    Optional<RolePermission> findByRoleIdAndPermId(Long roleId, Long permId);
}