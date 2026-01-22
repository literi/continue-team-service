package plugin.team.coreserver.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import plugin.team.coreserver.domain.entity.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    
    List<UserRole> findByRoleId(Long roleId);
    
    List<UserRole> findByTenantId(Long tenantId);
    
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);
}