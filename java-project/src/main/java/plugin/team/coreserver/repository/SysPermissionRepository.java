package plugin.team.coreserver.repository;

import plugin.team.coreserver.domain.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SysPermissionRepository extends JpaRepository<SysPermission, String> {

	@Query(value = "SELECT p.* FROM sys_permission p JOIN sys_role_permission rp ON rp.permission_id = p.id " +
			"WHERE rp.role_id IN (:roleIds) AND p.status = true",
			nativeQuery = true)
	List<SysPermission> listByRoleId(@Param("roleIds") Collection<String> roleIds);
}