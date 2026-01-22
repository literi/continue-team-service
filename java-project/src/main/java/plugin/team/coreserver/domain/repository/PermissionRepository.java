package plugin.team.coreserver.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.team.coreserver.domain.entity.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermCode(String permCode);
    
    List<Permission> findByTenantId(Long tenantId);
    
    List<Permission> findByParentId(Long parentId);
    
    List<Permission> findByPermType(Integer permType);
}