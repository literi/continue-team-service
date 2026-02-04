package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, String> {
}