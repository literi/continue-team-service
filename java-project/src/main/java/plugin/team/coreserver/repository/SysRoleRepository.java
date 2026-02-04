package plugin.team.coreserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import plugin.team.coreserver.domain.SysRole;

import java.util.List;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, String> {

    SysRole findByRoleCode(String roleCode);

    @Query(value = "SELECT r.* FROM sys_role r JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = :userId AND r.tenant_id = :tenantId AND r.status = true",
            nativeQuery = true)
    List<SysRole> listByUserId(@Param("userId") String userId, @Param("tenantId") String tenantId);
}