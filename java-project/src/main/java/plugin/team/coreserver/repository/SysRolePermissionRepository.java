package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, String> {
}