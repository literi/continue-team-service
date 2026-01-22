package plugin.team.coreserver.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.team.coreserver.domain.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleCode(String roleCode);
    
    List<Role> findByTenantId(Long tenantId);
    
    Optional<Role> findByRoleCodeAndTenantId(String roleCode, Long tenantId);
}